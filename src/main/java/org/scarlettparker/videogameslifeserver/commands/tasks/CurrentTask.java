package org.scarlettparker.videogameslifeserver.commands.tasks;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.scarlettparker.videogameslifeserver.objects.TPlayer;

import java.util.Objects;

import static org.scarlettparker.videogameslifeserver.manager.ConfigManager.jsonFileExists;
import static org.scarlettparker.videogameslifeserver.manager.ConfigManager.taskFile;

public class CurrentTask implements CommandExecutor {
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender instanceof Player && !sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "You must be an operator to run this command.");
            return true;
        }

        if (!jsonFileExists(taskFile)) {
            sender.sendMessage(ChatColor.RED
                    + "Tasks not yet initialized. Make sure to run /startlife and then /starttasks.");
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Incorrect usage. Correct usage: /currenttask player");
            return true;
        }

        TPlayer tempPlayer = new TPlayer(args[0]);
        if (Objects.equals(tempPlayer.getCurrentTask(), "-1")) {
            sender.sendMessage(ChatColor.YELLOW + "Player has no active task.");
        } else {
            sender.sendMessage(ChatColor.YELLOW + args[0] + "'s task is: " + ChatColor.WHITE + tempPlayer.getTaskDescription());
        }

        return true;
    }
}
