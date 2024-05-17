package org.scarlettparker.videogameslifeserver.commands.tasks;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.scarlettparker.videogameslifeserver.manager.ConfigManager;
import org.scarlettparker.videogameslifeserver.tasks.Task;

import java.util.List;
import java.util.Objects;

import static org.scarlettparker.videogameslifeserver.commands.tasks.StartTasks.*;
import static org.scarlettparker.videogameslifeserver.manager.TaskManager.*;

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

        // get useful information for edge cases
        int numLives = Integer.parseInt(Objects.requireNonNull(playerData[1]));
        int taskID = Integer.parseInt(Objects.requireNonNull(playerData[3]));
        int sessionTasks = Integer.parseInt(Objects.requireNonNull(playerData[5]));

        int difficulty = 0;

        if (args.length > 1) {
            p.sendMessage(ChatColor.RED + "Incorrect usage! Correct usage: /newtask [normal/hard]");
            return true;
        }

        if (args.length > 0 && numLives == 1) {
            p.sendMessage(ChatColor.RED + "You cannot select the difficulty of the task as a red player. Correct usage: /newtask");
            return true;
        }

        // if user has an active task
        if (taskID != -1) {
            p.sendMessage(ChatColor.RED + "You already have an active task! Complete it to begin another!");
            return true;
        }

        // limit for tasks per session
        if (sessionTasks > 1 && numLives != 1) {
            p.sendMessage(ChatColor.RED + "You have already attempted 2 tasks this session. As a non-red player, you cannot take on more than 2 tasks a session.");
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
        removeBook(p);

        // update necessary files nd whatnot
        ConfigManager.writeToPlayerBase(playerName, playerData);

        if (assignedTask == null) {
            System.err.println("No more tasks left! Cannot assign task!");
        } else {
            playerTasks.put(p.getName(), assignedTask);
            updatePlayerFile();
            assignedTask.setAvailable(false);

            // set up book stuff and add to inventory
            ItemStack book = new ItemStack(Material.WRITTEN_BOOK, 1);
            setBookMeta(book, assignedTask);

            // if the player's inventory is full
            if (p.getInventory().firstEmpty() == -1) {
                p.getLocation().getWorld().dropItemNaturally(p.getLocation(), book);
                p.sendMessage(ChatColor.RED + "Your inventory is full! A book has been dropped with your task.");
                p.performCommand("whattask");
            } else {
                p.getInventory().addItem(book);
            }
        }

        return true;
    }
}
