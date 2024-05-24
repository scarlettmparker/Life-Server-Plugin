package org.scarlettparker.videogameslifeserver.manager;

import com.google.gson.*;
import org.bukkit.Bukkit;
import org.scarlettparker.videogameslifeserver.objects.Death;
import org.scarlettparker.videogameslifeserver.objects.Punishment;
import org.scarlettparker.videogameslifeserver.objects.Task;
import java.io.*;
import java.util.Objects;
import java.util.logging.Logger;

public class ConfigManager {
    public static final File playerFile = new File(Objects.requireNonNull(Bukkit.getServer().getPluginManager()
            .getPlugin("VideoGamesLifeServer")).getDataFolder(), "playerbase.json");
    public static final File taskFile = new File(Objects.requireNonNull(Bukkit.getServer().getPluginManager()
            .getPlugin("VideoGamesLifeServer")).getDataFolder(), "taskbase.json");
    public static final File punishFile = new File(Objects.requireNonNull(Bukkit.getServer().getPluginManager()
            .getPlugin("VideoGamesLifeServer")).getDataFolder(), "punishments.json");

    private static final Logger LOGGER = Logger.getLogger(ConfigManager.class.getName());
    private static final Gson gson = new Gson();

    public static void createJsonFile(File jsonFile) {
        if (!jsonFile.exists()) {
            try {
                jsonFile.createNewFile();
            } catch (IOException e) {
                LOGGER.severe("Failed to create new file: " + e.getMessage());
            }
        } else {
            // delete file because we want new lives
            if (!jsonFile.delete()) {
                LOGGER.severe("Failed to delete existing file: " + jsonFile.getAbsolutePath());
            }
            try {
                jsonFile.createNewFile();
            } catch (IOException e) {
                LOGGER.severe("Failed to create new file: " + e.getMessage());
            }
        }
    }

    public static boolean jsonFileExists(File jsonFile) {
        return jsonFile.exists();
    }

    public static void addJsonObject(File jsonFile, String json) {
        JsonObject newJsonObject = gson.fromJson(json, JsonObject.class);
        JsonObject existingJsonObject = readJsonFile(jsonFile);

        if (existingJsonObject != null) {
            existingJsonObject.add(newJsonObject.get("name").getAsString(), newJsonObject);
            writeJsonToFile(jsonFile, existingJsonObject);
        } else {
            JsonObject jsonObject = new JsonObject();
            jsonObject.add(newJsonObject.get("name").getAsString(), newJsonObject);
            writeJsonToFile(jsonFile, jsonObject);
        }
    }

    public static void setJsonObjectAttribute(File jsonFile, String jsonObjectName, String attribute, Object value) {
        JsonObject jsonObject = readJsonFile(jsonFile);
        if (jsonObject != null) {
            JsonObject targetObject = jsonObject.getAsJsonObject(jsonObjectName);
            if (targetObject != null) {
                targetObject.add(attribute, gson.toJsonTree(value));
                writeJsonToFile(jsonFile, jsonObject);
            }
        }
    }

    public static Object getJsonObjectAttribute(File jsonFile, String jsonObjectName, String attribute) {
        JsonObject jsonObject = readJsonFile(jsonFile);
        if (jsonObject != null && jsonObject.has(jsonObjectName)) {
            JsonElement attributeValue = jsonObject.getAsJsonObject(jsonObjectName).get(attribute);
            if (attributeValue != null) {
                try {
                    if (attributeValue.isJsonPrimitive()) {
                        JsonPrimitive primitive = attributeValue.getAsJsonPrimitive();
                        if (primitive.isNumber()) {
                            return primitive.getAsInt();
                        } else if (primitive.isBoolean()) {
                            return primitive.getAsBoolean();
                        } else if (primitive.isString()) {
                            return primitive.getAsString();
                        }
                    } else if (attributeValue.isJsonArray()) {
                        if ("deaths".equals(attribute)) {
                            return gson.fromJson(attributeValue, Death[].class);
                        } else {
                            return gson.fromJson(attributeValue, String[].class);
                        }
                    } else if (attributeValue.isJsonObject()) {
                        if ("currentTask".equals(attribute)) {
                            return gson.fromJson(attributeValue, Task.class);
                        } else if ("punishment".equals(attribute)) {
                            return gson.fromJson(attributeValue, Punishment.class);
                        }
                    }
                } catch (JsonSyntaxException e) {
                    LOGGER.severe("Failed to deserialize JSON attribute: " + e.getMessage());
                }
            }
        }
        return null;
    }

    private static JsonObject readJsonFile(File jsonFile) {
        try (FileReader reader = new FileReader(jsonFile)) {
            return gson.fromJson(reader, JsonObject.class);
        } catch (IOException e) {
            LOGGER.severe("Failed to read JSON file: " + e.getMessage());
            return null;
        }
    }

    private static void writeJsonToFile(File jsonFile, JsonObject jsonObject) {
        try (FileWriter writer = new FileWriter(jsonFile)) {
            Gson gsonPretty = new GsonBuilder().setPrettyPrinting().create();
            gsonPretty.toJson(jsonObject, writer);
        } catch (IOException e) {
            LOGGER.severe("Failed to write JSON file: " + e.getMessage());
        }
    }

    public static int totalFileObjects(File jsonFile) {
        JsonObject jsonObject = readJsonFile(jsonFile);
        if (jsonObject != null) {
            return jsonObject.size();
        } else {
            return 0;
        }
    }

    public static JsonObject returnAllObjects(File jsonFile) {
        return readJsonFile(jsonFile);
    }

    public static boolean playerExists(String playerName) {
        return getJsonObjectAttribute(playerFile, playerName, "name") != null;
    }
}
