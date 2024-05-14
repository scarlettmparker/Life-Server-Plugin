package org.scarlettparker.videogameslifeserver.manager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Objects;

public class LifeManager {

    public void updateLives(String[] playerData, int numLives) {
        // update the new player life value
        playerData[1] = String.valueOf(numLives);
        ConfigManager.writeToPlayerBase(playerData[0], playerData);

        // update display name
        setPlayerName(Bukkit.getPlayer(playerData[0]));
    }

    public void setPlayerName(Player p) {
        try {
            // get number of lives from the config file
            String[] playerData = ConfigManager.getPlayerData(p.getName()).split(",");
            int numLives = Integer.parseInt(Objects.requireNonNull(playerData[1]));

            String newName;

            // formatting to look lovely
            if (numLives == 0) {
                newName = ChatColor.GRAY + p.getName() + " [DEAD]";
                p.setDisplayName(newName);
                p.setPlayerListName(newName);
                p.setCustomName(newName);
            } else if (numLives == 1) {
                newName = ChatColor.RED + p.getName()
                        + ChatColor.WHITE + " [" + ChatColor.RED + numLives
                        + ChatColor.WHITE + "]";
                p.setDisplayName(newName);
                p.setPlayerListName(newName);
                p.setCustomName(newName);
            } else if (numLives == 2) {
                newName = ChatColor.YELLOW + p.getName()
                        + ChatColor.WHITE + " [" + ChatColor.YELLOW + numLives
                        + ChatColor.WHITE + "]";
                p.setDisplayName(newName);
                p.setPlayerListName(newName);
                p.setCustomName(newName);
            } else if (numLives >= 3) {
                newName = ChatColor.GREEN + p.getName()
                        + ChatColor.WHITE + " [" + ChatColor.GREEN + numLives
                        + ChatColor.WHITE + "]";
                p.setDisplayName(newName);
                p.setPlayerListName(newName);
                p.setCustomName(newName);
            }
            p.setCustomNameVisible(true);
        } catch (Exception e) {
            // do a whole lot of nothing cause player doesnt exist
        }
    }
}
