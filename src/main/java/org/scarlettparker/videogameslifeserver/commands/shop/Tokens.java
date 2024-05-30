package org.scarlettparker.videogameslifeserver.commands.shop;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.scarlettparker.videogameslifeserver.objects.TPlayer;

public class Tokens implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You must be a player to run this command.");
            return true;
        }

        if (args.length > 0) {
            sender.sendMessage(ChatColor.RED + "Incorrect usage! Correct usage: /tokens");
            return true;
        }

        TPlayer tempPlayer = new TPlayer(sender.getName());

        // because perhaps some people will try to be funny
        if (tempPlayer.getLives() <= 0) {
            sender.sendMessage(ChatColor.RED + "You are a spectator. You can stop worrying about the market now.");
            return true;
        }

        sender.sendMessage(ChatColor.WHITE + "You have "
                + ChatColor.GOLD + tempPlayer.getTokens() + " tokens" + ChatColor.WHITE + ".");

        return true;
    }
}
