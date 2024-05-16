package org.scarlettparker.videogameslifeserver.commands.tasks;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.scarlettparker.videogameslifeserver.manager.ConfigManager;

import java.util.Objects;

import static org.scarlettparker.videogameslifeserver.commands.tasks.StartTasks.playerTasks;

public class FailTask implements CommandExecutor {
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            System.err.println("Only players can execute this command!");
            return true;
        }

        // get player and relevant data
        Player p = Bukkit.getPlayer(sender.getName());
        String playerName = sender.getName();
        String[] playerData = ConfigManager.getPlayerData(playerName).split(",");

        if (args.length > 0) {
            p.sendMessage(ChatColor.RED + "Incorrect usage! Correct usage: /failtask");
            return true;
        }

        if (Objects.equals(playerData[3], "-1")) {
            p.sendMessage(ChatColor.RED + "You have no active task to fail! Select a new task with /newtask [normal/hard]");
            return true;
        }

        p.sendMessage(ChatColor.RED + "You have failed your task."
                + ChatColor.WHITE + " You can select a new task with: " + ChatColor.GREEN + "/newtask [normal/hard]");

        Bukkit.broadcastMessage(playerName + " has" + ChatColor.RED + " failed their task" + ChatColor.WHITE + ": "
                + ChatColor.WHITE + playerTasks.get(p).getDescription());

        int punishment = 0;

        if (playerTasks.get(p).getDifficulty() == 1) {
            // Player has failed a hard task
            p.sendMessage("Because you failed a " + ChatColor.RED + "hard " + ChatColor.WHITE
                    + "task, you have been punished with: " + ChatColor.RED + punishment);
            punishment = 1;
        }

        if (playerTasks.get(p).getDifficulty() == 2) {
            // Player has failed a red player task
            p.sendMessage("Because you failed a " + ChatColor.RED + "red " + ChatColor.WHITE
                    + "task, you have been punished with: " + ChatColor.RED + punishment);
            punishment = 2;
        }

        // remove player data from hash
        playerTasks.remove(p);
        playerData[3] = "-1";

        // update player info
        ConfigManager.writeToPlayerBase(playerName, playerData);

        return true;
    }
}
