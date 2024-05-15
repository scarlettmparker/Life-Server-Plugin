package org.scarlettparker.videogameslifeserver.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.scarlettparker.videogameslifeserver.manager.ConfigManager;
import org.scarlettparker.videogameslifeserver.tasks.Task;

import java.util.List;
import java.util.Objects;

import static org.scarlettparker.videogameslifeserver.commands.StartTasks.getTasks;
import static org.scarlettparker.videogameslifeserver.commands.StartTasks.playerTasks;
import static org.scarlettparker.videogameslifeserver.manager.TaskManager.filterTasksByDifficulty;
import static org.scarlettparker.videogameslifeserver.manager.TaskManager.getRandomTask;

public class NewTask implements CommandExecutor {
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            System.err.println("Only players can execute this command!");
            return true;
        }

        // get the player and their info
        Player p = Bukkit.getPlayer(sender.getName());
        String playerName = p.getName();
        String[] playerData = ConfigManager.getPlayerData(playerName).split(",");

        int numLives = Integer.parseInt(Objects.requireNonNull(playerData[1]));
        int taskID = Integer.parseInt(Objects.requireNonNull(playerData[3]));
        int difficulty = 0;

        if (args.length > 1) {
            p.sendMessage(ChatColor.RED + "Incorrect usage! Correct usage: /newtask [normal/hard]");
            return true;
        }

        if (args.length > 0 && numLives == 1) {
            p.sendMessage(ChatColor.RED + "You cannot select the difficulty of the task as a red player. Correct usage: /newtask.");
            return true;
        }

        if (taskID != -1) {
            p.sendMessage(ChatColor.RED + "You already have an active task! Complete it to begin another!");
            return true;
        }

        if (numLives == 1) {
            difficulty = 2;
        }

        // big ball of mud the sequel, i'm gonna go off and eat 2 pizzas after this.
        try {
            if (!args[0].isEmpty()) {
                if (args[0].equalsIgnoreCase("hard")) {
                    difficulty = 1;
                } else if (!(args[0].equalsIgnoreCase("normal"))){
                    p.sendMessage(ChatColor.RED + "Incorrect usage! Correct usage: /newtask [normal/hard]");
                    return true;
                }
            }
        } catch (Exception e) {
            // do nothing
        }

        // filter task by difficulty and give to player
        List<Task> task = filterTasksByDifficulty(getTasks(), difficulty);
        Task assignedTask = getRandomTask(task);
        playerData[3] = String.valueOf(difficulty);

        ConfigManager.writeToPlayerBase(playerName, playerData);

        if (assignedTask == null) {
            System.err.println("No more tasks left! Cannot assign task!");
        } else {
            playerTasks.put(p, assignedTask);
            assignedTask.setAvailable(false);
            p.sendMessage("Your task is: " + assignedTask.getDescription());
        }

        return true;
    }
}
