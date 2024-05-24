package org.scarlettparker.videogameslifeserver.commands.tasks;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.scarlettparker.videogameslifeserver.objects.TPlayer;
import org.scarlettparker.videogameslifeserver.objects.Task;

import java.util.Objects;

public class WhatTask implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You must be a player to run this command.");
            return true;
        }

        if (args.length > 0) {
            sender.sendMessage(ChatColor.RED + "Incorrect usage! Correct usage: /whattask");
            return true;
        }

        TPlayer tempPlayer = new TPlayer(sender.getName());

        if (tempPlayer.getTasks().length == 0) {
            sender.sendMessage(ChatColor.RED
                    + "Tasks not yet initialized. Make sure to run /startlife and then /starttasks.");
            return true;
        }

        // because perhaps some people will try to be funny
        if (tempPlayer.getLives() <= 0) {
            sender.sendMessage(ChatColor.RED + "You are a spectator. You cannot complete new tasks.");
            return true;
        }

        Task tempTask = new Task(tempPlayer.getCurrentTask());

        if (Objects.equals(tempPlayer.getCurrentTask(), "-1")) {
            sender.sendMessage(ChatColor.RED
                    + "Yo have no active task! Players can select a new task by clicking a sign at spawn.");
            return true;
        }

        int taskDifficulty = tempTask.getDifficulty();

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
        } else if (taskDifficulty == 3) {
            messageColor = ChatColor.DARK_AQUA;
            difficultyText = "Raven";
        }

        // send the player task information
        sender.sendMessage(messageColor + "Your current task is: "
                + ChatColor.WHITE + tempTask.getPlayerDescription());
        sender.sendMessage("Your current task's difficulty is: " + messageColor + difficultyText);

        return true;
    }
}
