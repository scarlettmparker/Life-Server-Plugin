package org.scarlettparker.videogameslifeserver.commands.tasks;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.scarlettparker.videogameslifeserver.objects.TPlayer;
import org.scarlettparker.videogameslifeserver.objects.Task;
import java.util.*;

import static org.scarlettparker.videogameslifeserver.manager.TaskManager.doTaskDistribution;

public class NewTask implements CommandExecutor {
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        // must be an operator to use the command
        if (sender instanceof Player && !sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "You must be an operator to run this command.");
            return true;
        }

        if (args.length != 2) {
            sender.sendMessage(ChatColor.RED + "Incorrect usage. Correct usage: /newtask player difficulty");
            return true;
        }

        int difficulty = 0;

        if (!args[1].equals("normal") && !args[1].equals("hard")) {
            sender.sendMessage(ChatColor.RED + "Incorrect usage. Correct usage: /newtask player [normal/hard]");
            return true;
        }

        if (!args[1].equals("normal")) { difficulty = 1; }

        if (Bukkit.getPlayer(args[0]) == null) {
            sender.sendMessage(ChatColor.RED + "Invalid player/player is not online.");
            return true;
        }

        String playerName = args[0];
        TPlayer tempPlayer = new TPlayer(playerName);

        if (tempPlayer.getSessionTasks() >= 2 && tempPlayer.getLives() != 1) {
            sender.sendMessage(ChatColor.RED + "Player has already attempted 2 tasks this session."
                    + " As a non-red player, players cannot take on more than 2 tasks a session.");
            return true;
        }

        // because perhaps some people will try to be funny
        if (tempPlayer.getLives() <= 0) {
            sender.sendMessage(ChatColor.RED + "Spectators cannot complete new tasks.");
            return true;
        }

        if (tempPlayer.getLives() == 1) {
            difficulty = 2;
        }

        if (tempPlayer.getTasks() == null) {
            sender.sendMessage(ChatColor.RED
                    + "Tasks not yet initialized. Make sure to run /startlife and then /starttasks.");
            return true;
        }

        String currentTaskID = tempPlayer.getCurrentTask();
        Task tempTask = new Task(currentTaskID);

        if (!tempTask.getCompleted()) {
            sender.sendMessage(ChatColor.RED + "You already have an active task! Complete it to begin another!");
            return true;
        }

        // such a weird way of doing things but i guess we'll see if it can be used for multi player tasks
        ArrayList<Player> tempPlayers = new ArrayList<>();
        tempPlayers.add(Bukkit.getPlayer(args[0]));

        doTaskDistribution(tempPlayers, difficulty);
        return true;
    }
}
