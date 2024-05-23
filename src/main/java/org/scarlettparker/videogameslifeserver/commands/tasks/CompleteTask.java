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

import java.util.Objects;

import static org.scarlettparker.videogameslifeserver.utils.WorldUtils.removeBook;

public class CompleteTask implements CommandExecutor {
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        // must be an operator to use the command
        if (sender instanceof Player && !sender.isOp()) {
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

        if (tempPlayer.getTasks() == null) {
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
