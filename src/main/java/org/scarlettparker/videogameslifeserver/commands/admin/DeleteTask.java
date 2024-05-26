package org.scarlettparker.videogameslifeserver.commands.admin;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.scarlettparker.videogameslifeserver.manager.ConfigManager;
import org.scarlettparker.videogameslifeserver.objects.Task;

import java.util.Objects;

import static org.scarlettparker.videogameslifeserver.manager.ConfigManager.*;

public class DeleteTask implements CommandExecutor {
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        // must be an operator to use the command
        if (sender instanceof Player && !sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "You must be an operator to run this command.");
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Incorrect usage. Correct usage: /deletetask taskid");
            return true;
        }

        if (!jsonFileExists(taskFile)) {
            sender.sendMessage(ChatColor.RED
                    + "Task file not yet initialized. Make sure to run /startlife and then /starttasks.");
            return true;
        }

        Task tempTask = new Task(args[0]);

        if (Objects.equals(tempTask.getDescription(), "")) {
            sender.sendMessage(ChatColor.RED + "No such task exists.");
            return true;
        }

        // delete task and also double check it exists for some reason
        boolean taskDeleted = ConfigManager.deleteJsonObjectByName(taskFile, args[0]);

        if (taskDeleted) {
            sender.sendMessage(ChatColor.GREEN + "Task " + args[0] + " has been deleted successfully.");
        } else {
            sender.sendMessage(ChatColor.RED + "Task deletion failed. Perhaps this task doesn't exist?");
        }


        return true;
    }
}