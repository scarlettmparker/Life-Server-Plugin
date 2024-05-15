package org.scarlettparker.videogameslifeserver.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.scarlettparker.videogameslifeserver.manager.ConfigManager;

import java.util.Objects;

import static org.scarlettparker.videogameslifeserver.commands.StartTasks.playerTasks;

public class CompleteTask implements CommandExecutor {
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
            p.sendMessage(ChatColor.RED + "Incorrect usage! Correct usage: /completetask");
            return true;
        }

        if (Objects.equals(playerData[3], "-1")) {
            p.sendMessage(ChatColor.RED + "You have no active task to complete! Select a new task with /newtask [normal/hard]");
            return true;
        }

        p.sendMessage(ChatColor.GREEN + "Congratulations on completing your task!"
                + ChatColor.WHITE + " Select a new task with " + ChatColor.BLUE + "/newtask [normal/hard]");

        Bukkit.broadcastMessage(playerName + " has completed their task: " + ChatColor.BLUE + playerTasks.get(p).getDescription());

        // remove player data from hash
        playerTasks.remove(p);
        playerData[3] = "-1";

        // update player info
        ConfigManager.writeToPlayerBase(playerName, playerData);

        return true;
    }
}