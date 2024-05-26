package org.scarlettparker.videogameslifeserver.commands.tasks;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.scarlettparker.videogameslifeserver.objects.Punishment;
import org.scarlettparker.videogameslifeserver.objects.TPlayer;
import org.scarlettparker.videogameslifeserver.objects.Task;

import java.util.Arrays;
import java.util.Objects;

import static org.scarlettparker.videogameslifeserver.manager.ConfigManager.jsonFileExists;
import static org.scarlettparker.videogameslifeserver.manager.ConfigManager.taskFile;
import static org.scarlettparker.videogameslifeserver.manager.PunishmentManager.assignRandomPunishment;
import static org.scarlettparker.videogameslifeserver.utils.FragilityListener.unregister;
import static org.scarlettparker.videogameslifeserver.utils.PunishmentUtils.applyPunishment;
import static org.scarlettparker.videogameslifeserver.utils.WorldUtils.removeBook;

public class FailTask implements CommandExecutor {
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        // must be an operator to use the command
        if (sender instanceof Player && !sender.hasPermission("vgs.tasks.failtask")) {
            sender.sendMessage(ChatColor.RED + "You must be an operator to run this command.");
            return true;
        }

        if (args.length > 2 || args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Incorrect usage. Correct usage: /failtask player");
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
            sender.sendMessage(ChatColor.RED + "Spectators cannot fail tasks.");
            return true;
        }

        String currentTaskID = tempPlayer.getCurrentTask();
        Task tempTask = new Task(currentTaskID);

        if (Objects.equals(tempPlayer.getCurrentTask(), "-1")) {
            sender.sendMessage(ChatColor.RED + "Player has no active task to fail."
                    + " Start a new task by right clicking a sign at spawn.");
            return true;
        }

        tempTask.setCompleted(true);
        tempTask.setAvailable(true);

        // so it doesnt show up when session starts

        if (args.length < 2 || !Objects.equals(args[1], "dbg")) {
            Bukkit.getPlayer(args[0]).sendMessage(ChatColor.RED + "You have failed your task."
                    + ChatColor.WHITE + " Select a new task by right clicking a sign at spawn.");
        }

        // tell everyone the user has failed their task
        Bukkit.broadcastMessage(playerName + " has" + ChatColor.RED + " failed their task" + ChatColor.WHITE + ": "
                + ChatColor.WHITE + tempTask.getPlayerDescription());

        tempPlayer.setSessionTasks(tempPlayer.getSessionTasks() + 1);
        tempPlayer.setCurrentTask("-1");

        int punishment = 0;
        int difficulty = tempTask.getDifficulty();

        String difficultyType = "";
        ChatColor difficultyColor = null;

        // give user different number of tokens based on task
        if (difficulty == 1) {
            punishment = 1;
            difficultyType = "hard ";
            difficultyColor = ChatColor.GOLD;
        } else if (difficulty == 2) {
            punishment = 2;
            difficultyType = "red ";
            difficultyColor = ChatColor.RED;
        } else if (difficulty == 3) {
            punishment = 1;
            difficultyType = "Raven ";
            difficultyColor = ChatColor.DARK_AQUA;
        }

        removeBook(Bukkit.getPlayer(args[0]));
        ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();

        // if it's a hard task, continuous new tasks1!!!
        if (tempPlayer.getLives() == 1) {
            Bukkit.dispatchCommand(console, "newtask " + tempPlayer.getName() + " normal");
        }

        // players don't get punished in this instance
        if (punishment == 0) {
            return true;
        }

        // give random punishment and find final in list
        if (punishment == 2 && tempPlayer.getPunishments().length != 0) {
            if (tempPlayer.getTokens() > 0) {
                Bukkit.getPlayer(args[0]).sendMessage(ChatColor.RED +
                        "Because you are already cursed, as a red life, you shall lose a token instead.");

                tempPlayer.setTokens(tempPlayer.getTokens() - 1);
                Bukkit.getPlayer(args[0]).sendMessage(ChatColor.RED + "You now have "
                        + tempPlayer.getTokens() + " tokens.");
            }
            return true;
        }

        // if player can't be assigned a random punishment, do nothing
        if (!assignRandomPunishment(tempPlayer, punishment)) {
            return true;
        }

        String newPunishment = tempPlayer.getPunishments()[tempPlayer.getPunishments().length - 1];
        Punishment tempPunishment = new Punishment(newPunishment);

        // send message to correct player (i had definitely not previously done it wrong)
        Bukkit.getPlayer(args[0]).sendMessage("Because you failed a " + difficultyColor + difficultyType
                + ChatColor.WHITE + "task, you have been cursed with "
                + difficultyColor + tempPunishment.getDescription());

        // i love making variables i should do it more
        applyPunishment(tempPunishment.getName(), Bukkit.getPlayer(args[0]), false);

        return true;
    }
}
