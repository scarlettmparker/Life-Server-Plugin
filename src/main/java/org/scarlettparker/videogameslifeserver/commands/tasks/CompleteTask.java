package org.scarlettparker.videogameslifeserver.commands.tasks;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.scarlettparker.videogameslifeserver.objects.TPlayer;
import org.scarlettparker.videogameslifeserver.objects.Task;

import java.util.Arrays;
import java.util.Objects;

import static org.scarlettparker.videogameslifeserver.manager.ConfigManager.jsonFileExists;
import static org.scarlettparker.videogameslifeserver.manager.ConfigManager.taskFile;
import static org.scarlettparker.videogameslifeserver.utils.FragilityListener.unregisterFragility;
import static org.scarlettparker.videogameslifeserver.utils.KnockbackListener.unregisterKnockback;
import static org.scarlettparker.videogameslifeserver.utils.WorldUtils.removeBook;

public class CompleteTask implements CommandExecutor {
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        // must be an operator to use the command
        if (sender instanceof Player && !sender.hasPermission("vgs.tasks.completetask")) {
            sender.sendMessage(ChatColor.RED + "You must be an operator to run this command.");
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Incorrect usage. Correct usage: /completetask player");
            return true;
        }

        if (Bukkit.getPlayer(args[0]) == null) {
            sender.sendMessage(ChatColor.RED + "Invalid player/player is not online.");
            return true;
        }

        String playerName = args[0];
        TPlayer tempPlayer = new TPlayer(playerName);

        if (!jsonFileExists(taskFile)) {
            sender.sendMessage(ChatColor.RED
                    + "Tasks not yet initialized. Make sure to run /startlife and then /starttasks.");
            return true;
        }

        // because perhaps some people will try to be funny
        if (tempPlayer.getLives() <= 0) {
            sender.sendMessage(ChatColor.RED + "Spectators cannot complete tasks.");
            return true;
        }

        String currentTaskID = tempPlayer.getCurrentTask();
        Task tempTask = new Task(currentTaskID);

        if (Objects.equals(tempPlayer.getCurrentTask(), "-1")) {
            sender.sendMessage(ChatColor.RED + "Player has no active task to complete."
                    + " Start a new task by right clicking a sign at spawn.");
            return true;
        }

        tempTask.setCompleted(true);
        tempTask.setAvailable(true);

        Bukkit.getPlayer(args[0]).sendMessage(ChatColor.GREEN + "Congratulations on completing your task!"
                + ChatColor.WHITE + " Select a new task by right clicking a sign at spawn.");

        Bukkit.broadcastMessage(playerName + " has" + ChatColor.GREEN + " completed their task" + ChatColor.WHITE + ": "
                + ChatColor.WHITE + tempTask.getPlayerDescription());

        int tokensIncrease = 0;
        int difficulty = tempTask.getDifficulty();

        // give user different number of tokens based on task
        if (difficulty == 0) {
            tokensIncrease = 6;
        } else if (difficulty == 1) {
            tokensIncrease = 15;
        } else if (difficulty == 2) {
            tokensIncrease = 3;
        } else if (difficulty == 3) {
            tokensIncrease = 9;
        }

        // send message to correct player (i had definitely not previously done it wrong)
        Bukkit.getPlayer(args[0]).sendMessage(ChatColor.WHITE + "You have gained " + ChatColor.GOLD
                + tokensIncrease + " tokens " + ChatColor.WHITE + "for completing your task.");

        tempPlayer.setTokens(tempPlayer.getTokens() + tokensIncrease);
        tempPlayer.setSessionTasks(tempPlayer.getSessionTasks() + 1);
        tempPlayer.setCurrentTask("-1");

        // if player is currently punished
        if (tempPlayer.getPunishments().length != 0) {
            Bukkit.getPlayer(args[0]).sendMessage(ChatColor.GREEN + "You have been cured of your curse(s).");

            // clear the punishments if they have specific ones
            if (Arrays.asList(tempPlayer.getPunishments()).contains("fragile1")) {
                unregisterFragility(Bukkit.getPlayer(args[0]));
            }
            if (Arrays.asList(tempPlayer.getPunishments()).contains("knockback")) {
                unregisterKnockback(Bukkit.getPlayer(args[0]));
            }
            if (Arrays.asList(tempPlayer.getPunishments()).contains("hearts6")) {
                Bukkit.getPlayer(args[0]).setMaxHealth(20.0);
            }
            tempPlayer.setPunishments(new String[0]);
        }

        // if it's a hard task, continuous new tasks1!!!
        ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();

        // remove book from player
        removeBook(Bukkit.getPlayer(args[0]));

        if (tempPlayer.getLives() == 1) {
            Bukkit.dispatchCommand(console, "newtask " + tempPlayer.getName() + " normal");
        }

        return true;
    }
}
