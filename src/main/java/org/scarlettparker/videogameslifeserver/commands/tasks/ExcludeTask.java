package org.scarlettparker.videogameslifeserver.commands.tasks;

import com.google.gson.Gson;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.scarlettparker.videogameslifeserver.objects.Task;

import java.util.Objects;

import static org.scarlettparker.videogameslifeserver.manager.ConfigManager.*;

public class ExcludeTask implements CommandExecutor {
    private final Gson gson = new Gson();

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        // must be an operator to use the command
        if (sender instanceof Player && !sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "You must be an operator to run this command.");
            return true;
        }

        if (args.length > 1) {
            sender.sendMessage(ChatColor.RED + "Incorrect usage. Correct usage: /excludetask taskid");
            return true;
        } else {
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

            if (tempTask.getExcluded()) {
                tempTask.setExcluded(false);
                sender.sendMessage(ChatColor.GREEN + "Task " + args[0] + " is now allowed. Players can now be "
                        + "given this task.");
            } else {
                tempTask.setExcluded(true);
                sender.sendMessage(ChatColor.YELLOW + "Task " + args[0] + " is now excluded. Players will not be "
                        + "given this task.");
            }
        }

        return true;
    }
}