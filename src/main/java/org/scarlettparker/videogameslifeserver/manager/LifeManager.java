package org.scarlettparker.videogameslifeserver.manager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
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
            ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();

            String newName = "";
            String command = "";

            // formatting to look lovely
            if (numLives == 0) {
                newName = ChatColor.GRAY + p.getName() + " [DEAD]";
                command = "nte player " + p.getName() + " prefix &7";
            } else if (numLives == 1) {
                newName = ChatColor.RED + p.getName()
                        + ChatColor.WHITE + " [" + ChatColor.RED + numLives
                        + ChatColor.WHITE + "]";
                command = "nte player " + p.getName() + " prefix &c";
            } else if (numLives == 2) {
                newName = ChatColor.YELLOW + p.getName()
                        + ChatColor.WHITE + " [" + ChatColor.YELLOW + numLives
                        + ChatColor.WHITE + "]";
                command = "nte player " + p.getName() + " prefix &e";
            } else if (numLives == 3) {
                newName = ChatColor.GREEN + p.getName()
                        + ChatColor.WHITE + " [" + ChatColor.GREEN + numLives
                        + ChatColor.WHITE + "]";
                command = "nte player " + p.getName() + " prefix &a";
            } else if (numLives >= 4) {
                newName = ChatColor.DARK_GREEN + p.getName()
                        + ChatColor.WHITE + " [" + ChatColor.DARK_GREEN + numLives
                        + ChatColor.WHITE + "]";
                command = "nte player " + p.getName() + " prefix &2";
            }
            // custom name AND tag name now displays
            Bukkit.dispatchCommand(console, command);

            p.setDisplayName(newName);
            p.setPlayerListName(newName);
            p.setCustomName(newName);
        } catch (Exception e) {
            // do a whole lot of nothing cause player doesnt exist
        }
    }
}
