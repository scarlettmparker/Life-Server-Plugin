package org.scarlettparker.videogameslifeserver.commands.admin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.scarlettparker.videogameslifeserver.objects.TPlayer;

import java.util.Objects;

import static org.scarlettparker.videogameslifeserver.manager.ConfigManager.*;
import static org.scarlettparker.videogameslifeserver.utils.WorldUtils.handleRevive;

public class SetLife implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        // for cleanliness
        int lives;
        String playerName;

        // must be an operator to use the command
        if (sender instanceof Player && !sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "You must be an operator to run this command.");
            return true;
        }

        if (args.length != 2) {
            sender.sendMessage(ChatColor.RED + "Incorrect usage. Correct usage: /setlife player lives");
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

        try {
            lives = Integer.parseInt(args[1]);
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "Please enter a valid integer for number of lives.");
            return true;
        }

        if (lives < 1) {
            sender.sendMessage(ChatColor.RED + "You can not set a player's lives to below 1.");
            return true;
        }

        TPlayer tempPlayer = new TPlayer(playerName);

        // if player is revived (don't actually make them a zombie functionally as it's an admin command)
        if (tempPlayer.getLives() == 0) {
            handleRevive(Objects.requireNonNull(Bukkit.getPlayer(tempPlayer.getName())));
        }

        // update lives and display
        tempPlayer.setLives(lives);
        Bukkit.getPlayer(args[0]).sendMessage(ChatColor.GREEN + "You have been set to " + lives + " lives.");

        ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
        if (lives == 1 && Objects.equals(tempPlayer.getCurrentTask(), "-1")) {
            Bukkit.getPlayer(args[0]).sendMessage(ChatColor.RED + "As you're now a red life, you will be given "
                    + "continuous red tasks from now on.");

            // run the newtask command so player gets red task
            Bukkit.dispatchCommand(console, "newtask " + tempPlayer.getName() + " normal");
        }

        return true;
    }
}
