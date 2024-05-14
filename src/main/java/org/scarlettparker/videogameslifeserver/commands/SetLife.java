package org.scarlettparker.videogameslifeserver.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.scarlettparker.videogameslifeserver.manager.ConfigManager;
import org.scarlettparker.videogameslifeserver.manager.LifeManager;

public class SetLife implements CommandExecutor {
    LifeManager lifeManager = new LifeManager();
    StartLife startLife = new StartLife();

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        int numLives;

        if (sender instanceof Player) {
            Player p = (Player) sender;
            p.sendMessage(ChatColor.RED + "You cannot use this command as a player. Please run this command from the console.");
            return true;
        }

        if (args.length != 2) {
            System.err.println("Incorrect arguments. Command usage: /setlife player lives");
            return true;
        } else {
            try {
                numLives = Integer.parseInt(args[1]);
                if (numLives < 1 || numLives > 99) {
                    System.err.println("Number of lives must be between 1 and 99.");
                    return true;
                }
            } catch(Exception e) {
                System.err.println("Please enter a valid integer for lives.");
                return true;
            }
        }
        if (sender instanceof ConsoleCommandSender) {
            if (!ConfigManager.findPlayerBase()) {
                System.err.println("Config file not found! Please run /startlife lives first.");
                return true;
            }
            try {
                String[] playerData = ConfigManager.getPlayerData(args[0]).split(",");
                lifeManager.updateLives(playerData, numLives);
            } catch(Exception e) {
                // in case player doesn't exist, make a new player
                startLife.createNewPlayer(args[0], numLives, 0, "false");
            }

        }
        System.out.println("Success!");
        return true;
    }
}
