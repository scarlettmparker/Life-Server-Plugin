package org.scarlettparker.videogameslifeserver.commands.tasks;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.scarlettparker.videogameslifeserver.objects.Task;

import java.util.Objects;

import static org.scarlettparker.videogameslifeserver.manager.ConfigManager.*;
import static org.scarlettparker.videogameslifeserver.manager.ConfigManager.playerExists;

public class SetPriority implements CommandExecutor {
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        int priority;
        // must be an operator to use the command
        if (sender instanceof Player && !sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "You must be an operator to run this command.");
            return true;
        }

        if (args.length != 2) {
            sender.sendMessage(ChatColor.RED + "Incorrect usage. Correct usage: /setpriority taskid priority");
            return true;
        }

        if (!jsonFileExists(playerFile)) {
            sender.sendMessage(ChatColor.RED
                    + "Player file not yet initialized. Make sure to run /startlife and then /starttasks.");
            return true;
        }

        try {
            priority = Integer.parseInt(args[1]);
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "Please enter a valid integer for task priority.");
            return true;
        }

        if (priority < 0) {
            sender.sendMessage(ChatColor.RED + "Task priority cannot be negative. Default is 0.");
            return true;
        }

        Task tempTask = new Task(args[0]);

        if (Objects.equals(tempTask.getDescription(), "")) {
            sender.sendMessage(ChatColor.RED + "No such task exists.");
            return true;
        }

        // set task priority
        if (tempTask.getPriority() == priority) {
            sender.sendMessage(ChatColor.YELLOW + "Task priority unchanged.");
            return true;
        }

        tempTask.setPriority(priority);
        sender.sendMessage(ChatColor.GREEN + "Task " + tempTask.getName() + " priority successfully "
            + "changed to " + priority + ".");


        return true;
    }
}