package org.scarlettparker.videogameslifeserver.manager;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.bukkit.Bukkit;
import java.io.*;
import java.util.*;

/*
    CSV File Structure:
    playerName, lives, deaths(...time:message:etc), tasks(...time:task:etc), isZombie, livesGained, hasUsedExtraLife
*/

public class ConfigManager {
    // files for player lives etc
    private static final File playerFile = new File(Objects.requireNonNull(Bukkit.getServer().getPluginManager()
                .getPlugin("VideoGamesLifeServer")).getDataFolder(), "playerbase.csv");

    // finds and generates new player file
    public static void createPlayerBase() {
        if (!playerFile.exists()) {
            try {
                playerFile.createNewFile();
            } catch (Exception e) {
                // do nothing
            }
        } else {
            // delete file because we want new lives
            playerFile.delete();
            try {
                playerFile.createNewFile();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean findPlayerBase() {
        return playerFile.exists();
    }

    public static void writeToPlayerBase(String playerName, String[] attributes) {
        try {
            File tempFile = new File(playerFile.getAbsolutePath() + ".tmp");

            for (int i = 0; i < attributes.length; i++) {
                attributes[i] = ""+attributes[i];
            }

            CsvMapper cm = new CsvMapper();
            MappingIterator<List<String>> it = cm.readerForListOf(String.class)
                    .with(CsvParser.Feature.WRAP_AS_ARRAY)
                    .with(CsvParser.Feature.SKIP_EMPTY_LINES)
                    .readValues(playerFile);
            List<List<String>> all = it.readAll();
            List<String> player = all.stream()
                    .filter(entry -> entry.get(0).equals(playerName))
                    .findFirst()
                    .orElse(null);

            if (player == null) {
                List<String> playerValues = new ArrayList<>(Arrays.stream(attributes).toList());
                playerValues.add(0, playerName);
                all.add(playerValues);
            }
            else {
                for (int i = 0; i < attributes.length; i++) {
                    player.set(i+1, attributes[i]);
                }
            }

            CsvSchema schema = cm.schemaFor(String.class).withoutQuoteChar();
            cm.writer(schema).withDefaultPrettyPrinter().writeValue(tempFile, all);

            playerFile.delete();
            tempFile.renameTo(playerFile);
        } catch(Exception e) {
            // do not much
        }
    }

    public static String getPlayerData(String playerName) {
        try (BufferedReader br = new BufferedReader(new FileReader(playerFile))){
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith(playerName)) {
                    return line;
                }
            }
        } catch (Exception e) {
            // do nothing
        }
        return "";
    }
}
