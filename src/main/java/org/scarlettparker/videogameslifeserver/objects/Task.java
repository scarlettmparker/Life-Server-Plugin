package org.scarlettparker.videogameslifeserver.objects;

import static org.scarlettparker.videogameslifeserver.manager.ConfigManager.*;

public class Task {
    private String name;
    private String playerDescription;

    public Task(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDifficulty() {
        Object difficulty = getJsonObjectAttribute(taskFile, String.valueOf(name), "difficulty");
        return difficulty instanceof Integer ? (int) difficulty : 0; // default value is 0
    }

    public boolean getCompleted() {
        Object completed = getJsonObjectAttribute(taskFile, String.valueOf(name), "completed");
        return completed instanceof Boolean && (boolean) completed; // default value is false
    }

    public boolean getAvailable() {
        Object available = getJsonObjectAttribute(taskFile, String.valueOf(name), "available");
        return !(available instanceof Boolean) || (boolean) available; // default value is false
    }

    public String getDescription() {
        Object description = getJsonObjectAttribute(taskFile, String.valueOf(name), "description");
        return description instanceof String ? (String) description : ""; // default value is 0
    }

    public String getPlayerDescription() {
        Object playerDescription = getJsonObjectAttribute(taskFile, String.valueOf(name), "playerDescription");
        return playerDescription instanceof String ? (String) playerDescription : ""; // default value is 0
    }

    public String getReceiver() {
        Object receiver = getJsonObjectAttribute(taskFile, String.valueOf(name), "receiver");
        return receiver instanceof String ? (String) receiver : ""; // default value is 0
    }

    public void setID(String id) {
        this.name = id;
    }

    public void setDifficulty(int difficulty) {
        setJsonObjectAttribute(taskFile, String.valueOf(name), "difficulty", difficulty);
    }

    public void setCompleted(boolean completed) {
        setJsonObjectAttribute(taskFile, String.valueOf(name), "completed", completed);
    }

    public void setAvailable(boolean available) {
        setJsonObjectAttribute(taskFile, String.valueOf(name), "available", available);
    }

    public void setDescription(String description) {
        setJsonObjectAttribute(taskFile, String.valueOf(name), "description", description);
    }

    public void setPlayerDescription(String playerDescription) {
        setJsonObjectAttribute(taskFile, String.valueOf(name), "playerDescription", playerDescription);
    }

    public void setReceiver(String receiver) {
        setJsonObjectAttribute(taskFile, String.valueOf(name), "receiver", receiver);
    }
}
