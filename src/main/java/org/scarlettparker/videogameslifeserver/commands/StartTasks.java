package org.scarlettparker.videogameslifeserver.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.scarlettparker.videogameslifeserver.tasks.Task;
import java.util.Arrays;

import static org.scarlettparker.videogameslifeserver.manager.TaskManager.generateTasks;

public class StartTasks implements CommandExecutor {
    public Task[] tasks = {};

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            p.sendMessage(ChatColor.RED + "You cannot use this command as a player. Please run this command from the console.");
            return true;
        }

        if (args.length > 0) {
            System.err.println("Incorrect usage! Usage is /starttasks");
            return true;
        }

        tasks = generateTasks();
        System.out.println(Arrays.toString(tasks));
        return true;
    }
}
