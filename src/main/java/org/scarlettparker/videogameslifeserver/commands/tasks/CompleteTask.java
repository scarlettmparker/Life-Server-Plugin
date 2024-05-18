package org.scarlettparker.videogameslifeserver.commands.tasks;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.scarlettparker.videogameslifeserver.manager.ConfigManager;
import org.scarlettparker.videogameslifeserver.tasks.Task;

import java.util.Objects;
import static org.scarlettparker.videogameslifeserver.commands.tasks.StartTasks.playerTasks;
import static org.scarlettparker.videogameslifeserver.manager.TaskManager.removeBook;
import static org.scarlettparker.videogameslifeserver.manager.TaskManager.updatePlayerFile;

public class CompleteTask implements CommandExecutor {
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            System.err.println("Only players can execute this command!");
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Incorrect usage! Correct usage: /completetask PASSWORD");
            return true;
        }

        if (!Objects.equals(args[0], "J30JVDXNL")) {
            sender.sendMessage(ChatColor.RED + "Incorrect password! Please right click the sign to use this command!");
            return true;
        }

        // get player and relevant data
        Player p = Bukkit.getPlayer(sender.getName());
        String playerName = sender.getName();
        String[] playerData = ConfigManager.getPlayerData(playerName).split(",");

        if (Objects.equals(playerData[3], "-1")) {
            p.sendMessage(ChatColor.RED + "You have no active task to complete! Select a new task with /newtask [normal/hard]");
            return true;
        }

        int sessionTasks = Integer.parseInt(playerData[5]);
        if (sessionTasks == -1) {
            p.sendMessage(ChatColor.RED + "You were late to joining the session. You must fail this task before choosing another.");
            return true;
        }

        p.sendMessage(ChatColor.GREEN + "Congratulations on completing your task!"
                + ChatColor.WHITE + " Select a new task with: " + ChatColor.GREEN + "/newtask [normal/hard]");

        Bukkit.broadcastMessage(playerName + " has" + ChatColor.GREEN + " completed their task" + ChatColor.WHITE + ": "
                + ChatColor.WHITE + playerTasks.get(p.getName()).getDescription());

        Task currentTask = playerTasks.get(p.getName());
        currentTask.setAvailable(true);

        // get user info to update
        int tokens = Integer.parseInt(Objects.requireNonNull(playerData[6]));
        int numLives = Integer.parseInt(Objects.requireNonNull(playerData[1]));
        int difficulty = currentTask.getDifficulty();

        sessionTasks += 1;
        playerData[5] = String.valueOf(sessionTasks);

        int tokensIncrease = 0;

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

        // update tokens and announce new tokens
        tokens += tokensIncrease;
        p.sendMessage(ChatColor.WHITE + "You have gained " + ChatColor.GOLD + tokensIncrease + " tokens "
                + ChatColor.WHITE + "for completing your task.");

        playerData[6] = String.valueOf(tokens);

        // remove player data from hash
        playerTasks.remove(p.getName());
        playerData[3] = "-1";

        // update player info
        updatePlayerFile();
        removeBook(p);
        ConfigManager.writeToPlayerBase(playerName, playerData);

        // red players constantly take new tasks
        if (difficulty == 2 && numLives == 1) {
            p.performCommand("newtask normal J30JVDXNL");
        }

        return true;
    }
}