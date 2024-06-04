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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.scarlettparker.videogameslifeserver.manager.ConfigManager.*;
import static org.scarlettparker.videogameslifeserver.manager.TaskManager.doTaskDistribution;
import static org.scarlettparker.videogameslifeserver.utils.WorldUtils.*;

public class ExcludePlayer implements CommandExecutor {
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        // must be an operator to use the command
        if (sender instanceof Player && !sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "You must be an operator to run this command.");
            return true;
        }

        if (args.length != 2) {
            sender.sendMessage(ChatColor.RED + "Incorrect usage. Correct usage: /excludeplayer taskid player");
            return true;
        }

        if (!jsonFileExists(playerFile)) {
            sender.sendMessage(ChatColor.RED
                    + "Player file not yet initialized. Make sure to run /startlife and then /starttasks.");
            return true;
        }

        if (!playerExists(args[1])) {
            sender.sendMessage(ChatColor.RED + "Specified player does not exist in the player file.");
            return true;
        }

        Task tempTask = new Task(args[0]);

        if (Objects.equals(tempTask.getDescription(), "")) {
            sender.sendMessage(ChatColor.RED + "No such task exists.");
            return true;
        }

        // get list of excluded players
        String[] excludedPlayers = tempTask.getExcludedPlayers();

        if (!(Arrays.asList(excludedPlayers).contains(args[1]))) {
            // i love array copies they are so cool and awesome
            String[] tempExcluded = Arrays.copyOf(excludedPlayers, excludedPlayers.length + 1);
            tempExcluded[excludedPlayers.length] = args[1];
            tempTask.setExcludedPlayers(tempExcluded);

            sender.sendMessage(ChatColor.YELLOW + args[1] + " is now excluded from Task " + args[0]
                    + ". They will no longer be given this task.");
        } else {
            List<String> tempExcluded = new ArrayList<>(Arrays.asList(excludedPlayers));
            tempExcluded.remove(args[1]);
            tempTask.setExcludedPlayers(tempExcluded.toArray(new String[0]));

            sender.sendMessage(ChatColor.GREEN + args[1] + " is no longer excluded excluded from Task " + args[0]
                    + ". They can now be given this task.");
        }

        return true;
    }
}
