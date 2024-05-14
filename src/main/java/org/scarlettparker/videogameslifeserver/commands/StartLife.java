package org.scarlettparker.videogameslifeserver.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.scarlettparker.videogameslifeserver.ConfigManager;
import org.scarlettparker.videogameslifeserver.LifeManager;

import java.util.ArrayList;
import java.util.List;

public class StartLife implements CommandExecutor {
    LifeManager lifeManager = new LifeManager();
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        int numLives;

        if (sender instanceof Player) {
            Player p = (Player) sender;
            p.sendMessage(ChatColor.RED + "You cannot use this command as a player. Please run this command from the console.");
            return true;
        }

        if (args.length != 1) {
            System.out.println("Incorrect arguments. Command usage: /startlife lives");
            return true;
        } else {
            try {
                // ensure correct format (integer) is used
                numLives = Integer.parseInt(args[0]);
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
            System.out.println(numLives + " lives being assigned to each player...");

            ConfigManager.createPlayerBase();
            for (Player p : getAllPlayers()) {
                // add players with lives to config
                ConfigManager.getPlayerBase().set(p.getName(), numLives);
            }
            // copy everything into config
            ConfigManager.getPlayerBase().options().copyDefaults(true);
            ConfigManager.savePlayerBase();

            System.out.println("Lives successfully assigned to all players.");
            for (Player p : getAllPlayers()) {
                lifeManager.setPlayerName(p);
            }
        }
        return true;
    }

    private List<Player> getAllPlayers() {
        return new ArrayList<>(Bukkit.getOnlinePlayers());
    }
}
