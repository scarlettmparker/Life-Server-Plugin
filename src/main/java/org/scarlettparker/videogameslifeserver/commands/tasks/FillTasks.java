package org.scarlettparker.videogameslifeserver.commands.tasks;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.scarlettparker.videogameslifeserver.objects.TPlayer;

import java.util.Objects;

import static org.bukkit.Bukkit.getServer;
import static org.scarlettparker.videogameslifeserver.manager.ConfigManager.jsonFileExists;
import static org.scarlettparker.videogameslifeserver.manager.ConfigManager.taskFile;
import static org.scarlettparker.videogameslifeserver.utils.WorldUtils.getAllPlayers;

public class FillTasks implements CommandExecutor {
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

        if (args.length != 0) {
            sender.sendMessage(ChatColor.RED + "Incorrect usage. Correct usage: /filltasks");
            return true;
        }

        for (Player p : getAllPlayers()) {
            TPlayer tempPlayer = new TPlayer(p.getName());
            // give players tasks if they are still in the game
            if (tempPlayer.getLives() > 0 && Objects.equals(tempPlayer.getCurrentTask(), "-1")) {
                getServer().dispatchCommand(getServer().getConsoleSender(), "newtask " + tempPlayer.getName() + " normal");
                sender.sendMessage("Given task " + ChatColor.YELLOW + tempPlayer.getCurrentTask() +
                        ChatColor.WHITE + " to player " + ChatColor.YELLOW + p.getName() + ChatColor.WHITE + ".");
            }
        }
        return true;
    }
}
