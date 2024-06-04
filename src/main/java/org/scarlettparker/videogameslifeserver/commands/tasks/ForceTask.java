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

import java.util.Objects;

import static org.scarlettparker.videogameslifeserver.manager.ConfigManager.*;

public class ForceTask implements CommandExecutor {
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        // must be an operator to use the command
        if (sender instanceof Player && !sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "You must be an operator to run this command.");
            return true;
        }

        if (args.length != 2) {
            sender.sendMessage(ChatColor.RED + "Incorrect usage. Correct usage: /forcetask player taskid");
            return true;
        }

        if (!jsonFileExists(playerFile)) {
            sender.sendMessage(ChatColor.RED
                    + "Player file not yet initialized. Make sure to run /startlife and then /starttasks.");
            return true;
        }

        if (!playerExists(args[0])) {
            sender.sendMessage(ChatColor.RED + "Specified player does not exist in the player file.");
            return true;
        }

        Task tempTask = new Task(args[1]);
        TPlayer tempPlayer = new TPlayer(args[0]);

        if (Objects.equals(tempTask.getDescription(), "")) {
            sender.sendMessage(ChatColor.RED + "No such task exists.");
            return true;
        }

        tempPlayer.setNextTask(args[1]);
        sender.sendMessage(ChatColor.GREEN +
                "Set " + tempPlayer.getName() + "'s next task to: " + tempTask.getName());

        return true;
    }
}