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

public class WhatTask implements CommandExecutor {
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            System.err.println("Only players can execute this command!");
            return true;
        }

        Player p = Bukkit.getPlayer(sender.getName());

        if (args.length > 1) {
            p.sendMessage(ChatColor.RED + "Incorrect usage! Correct usage: /whattask");
            return true;
        }

        // get player data to find task
        String playerName = p.getName();
        String[] playerData = ConfigManager.getPlayerData(playerName).split(",");
        int taskID = Integer.parseInt(Objects.requireNonNull(playerData[3]));

        // if player has no task currently
        if (taskID == -1) {
            p.sendMessage(ChatColor.RED + "You have no active task! Select a new task with /newtask [normal/hard]");
            return true;
        }

        int taskDifficulty = playerTasks.get(p.getName()).getDifficulty();

        ChatColor messageColor = null;
        String difficultyText = null;

        // formatting for different difficulties
        if (taskDifficulty == 0) {
            messageColor = ChatColor.GREEN;
            difficultyText = "Normal";
        } else if (taskDifficulty == 1) {
            messageColor = ChatColor.YELLOW;
            difficultyText = "Hard";
        } else if (taskDifficulty == 2) {
            messageColor = ChatColor.RED;
            difficultyText = "Red";
        }

        // send the player task information
        p.sendMessage(messageColor + "Your current task is: "
                + ChatColor.WHITE + playerTasks.get(p.getName()).getDescription());
        p.sendMessage("Your current task's difficulty is: " + messageColor + difficultyText);

        return true;
    }
}
