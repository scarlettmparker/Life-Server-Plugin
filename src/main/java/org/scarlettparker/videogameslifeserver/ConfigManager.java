package org.scarlettparker.videogameslifeserver;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Objects;

public class ConfigManager {
    // files for player lives etc
    private static final File playerFile = new File(Objects.requireNonNull(Bukkit.getServer().getPluginManager()
                .getPlugin("VideoGamesLifeServer")).getDataFolder(), "playerbase.yml");
    private static final FileConfiguration customFile = YamlConfiguration.loadConfiguration(playerFile);

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

    public static FileConfiguration getPlayerBase() {
        return customFile;
    }

    public static void savePlayerBase() {
        try {
            customFile.save(playerFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
