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
import java.util.Objects;

import static org.scarlettparker.videogameslifeserver.manager.ConfigManager.*;
import static org.scarlettparker.videogameslifeserver.manager.TaskManager.doTaskDistribution;
import static org.scarlettparker.videogameslifeserver.utils.WorldUtils.*;

public class SetTask implements CommandExecutor {
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        // for cleanliness
        String playerName;

        // must be an operator to use the command
        if (sender instanceof Player && !sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "You must be an operator to run this command.");
            return true;
        }

        if (args.length != 2) {
            sender.sendMessage(ChatColor.RED + "Incorrect usage. Correct usage: /settask player taskid");
            return true;
        }

        if (!jsonFileExists(playerFile)) {
            sender.sendMessage(ChatColor.RED
                    + "Player file not yet initialized. Make sure to run /startlife and then /starttasks.");
            return true;
        }

        if (!playerExists(args[0]) || Bukkit.getPlayer(args[0]) == null) {
            sender.sendMessage(ChatColor.RED + "Specified player is not online.");
            return true;
        } else {
            playerName = args[0];
        }

        Task tempTask = new Task(args[1]);

        TPlayer tempPlayer = new TPlayer(playerName);
        Player player = Bukkit.getPlayer(playerName);

        ArrayList<Player> tempPlayers = new ArrayList<>();
        tempPlayers.add(player);

        // give random task based on difficulty
        if (Objects.equals(args[1], "normal") || Objects.equals(args[1], "hard")) {
            int difficulty = Objects.equals(args[1], "normal") ? 0 : 1;
            doTaskDistribution(tempPlayers, difficulty);
            return true;
        }

        if (Objects.equals(tempTask.getDescription(), "")) {
            sender.sendMessage(ChatColor.RED + "No such task exists.");
            return true;
        }

        tempTask.setExcluded(true);
        tempTask.setPlayerDescription(manageReceiverDescription(tempTask.getPlayerDescription(), player));
        tempTask.setPlayerDescription(manageSenderDescription(tempTask.getPlayerDescription(), player));
        tempPlayer.setCurrentTask(tempTask.getName());

        // make sure to give the correct book
        removeBook(player);
        giveTaskBook(tempTask, player);

        Bukkit.getPlayer(args[0]).sendMessage(ChatColor.GREEN +
                "Your task has been set to: " + tempTask.getName());

        return true;
    }
}
