package org.scarlettparker.videogameslifeserver.commands.life;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.scarlettparker.videogameslifeserver.manager.ConfigManager;
import org.scarlettparker.videogameslifeserver.manager.LifeManager;

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
                createNewPlayer(p.getName(), numLives, 0, -1, "false");
            }

            // for some debug messages
            int playerCount = 0;
            for (Player p : getAllPlayers()) {
                lifeManager.setPlayerName(p);
                playerCount += 1;
            }

            System.out.println("Successfully given " + args[0] + " lives to " + playerCount + " players.");
        }
        return true;
    }

    private List<Player> getAllPlayers() {
        return new ArrayList<>(Bukkit.getOnlinePlayers());
    }

    public void createNewPlayer(String playerName, int numLives, int deaths, int activeTaskID, String isZombie) {
        String[] data = new String[]{String.valueOf(numLives), String.valueOf(deaths),
                String.valueOf(activeTaskID), String.valueOf(isZombie)};
        ConfigManager.writeToPlayerBase(playerName, data);
    }
}
