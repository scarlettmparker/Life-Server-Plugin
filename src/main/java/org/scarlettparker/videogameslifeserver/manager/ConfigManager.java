package org.scarlettparker.videogameslifeserver.manager;
import org.bukkit.Bukkit;
import java.io.*;
import java.util.Objects;
import java.util.StringJoiner;

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
            PrintWriter pw = new PrintWriter(new FileWriter(tempFile));

            BufferedReader br = new BufferedReader(new FileReader(playerFile));
            String line;
            boolean found = false;

            while ((line = br.readLine()) != null) {
                if (line.startsWith(playerName)) {
                    // if player exists replace data
                    StringJoiner joiner = new StringJoiner(",");
                    for (String attribute : attributes) {
                        String s = escapeSpecialCharacters(attribute);
                        joiner.add(s);
                    }
                    pw.println(joiner);
                    found = true;
                } else {
                    pw.println(line);
                }
            }

            // if no player exists write a new line
            if (!found) {
                StringJoiner joiner = new StringJoiner(",");
                joiner.add(playerName);
                for (String attribute : attributes) {
                    String s = escapeSpecialCharacters(attribute);
                    joiner.add(s);
                }
                pw.println(joiner);
            }

            br.close();
            pw.flush();
            pw.close();

            playerFile.delete();
            tempFile.renameTo(playerFile);
        } catch(Exception e) {
            // do not much
        }
    }

    private static String escapeSpecialCharacters(String data) {
        if (data == null) {
            throw new IllegalArgumentException("Input data cannot be null");
        }
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
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
