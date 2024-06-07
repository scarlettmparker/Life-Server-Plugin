package org.scarlettparker.videogameslifeserver.commands.shop;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.scarlettparker.videogameslifeserver.objects.TPlayer;

import java.util.Objects;

import static org.scarlettparker.videogameslifeserver.manager.ConfigManager.playerExists;

public class Gift implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "This command is disabled to players until the third session.");
            return true;
        }

        if (args.length != 2) {
            sender.sendMessage(ChatColor.RED + "Incorrect usage. Correct usage: /gift player tokens");
            return true;
        }

        int tokens;
        Player receiver = Bukkit.getPlayer(args[0]);

        try {
            tokens = Integer.parseInt(args[1]);
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "Please enter a valid integer for number of tokens.");
            return true;
        }

        if (!playerExists(args[0]) || receiver == null) {
            sender.sendMessage(ChatColor.RED + "Specified player does not exist/is not online.");
            return true;
        } else {
            if (Objects.equals(receiver, sender)) {
                sender.sendMessage(ChatColor.RED + "You cannot gift yourself tokens.");
                return true;
            }
        }

        if (tokens < 1) {
            sender.sendMessage(ChatColor.RED + "You must gift at least 1 token.");
            return true;
        }

        // sender and receiver
        TPlayer sPlayer = new TPlayer(sender.getName());
        TPlayer rPlayer = new TPlayer(receiver.getName());

        if (!(sender instanceof Player)) {
            rPlayer.setTokens(rPlayer.getTokens() + tokens);
            sender.sendMessage("Successfully gifted " + ChatColor.GOLD + tokens
                    + " tokens" + ChatColor.WHITE + " to " + receiver.getName() + ".");
            receiver.sendMessage(sender.getName() + " has gifted you " + ChatColor.GOLD
                    + tokens + " tokens" + ChatColor.WHITE + ".");
            return true;
        }

        if (sPlayer.getLives() < 1) {
            sender.sendMessage(ChatColor.RED + "You are dead. You cannot gift tokens.");
            return true;
        }

        if (sPlayer.getTokens() - tokens < 0) {
            sender.sendMessage(ChatColor.RED + "You don't have enough tokens to gift. You currently have "
                + sPlayer.getTokens() + " tokens.");
            return true;
        }

        // update player tokens with some basic addition
        sPlayer.setTokens(sPlayer.getTokens() - tokens);
        rPlayer.setTokens(rPlayer.getTokens() + tokens);

        // player gets confirmation message
        sender.sendMessage("Successfully gifted " + ChatColor.GOLD + tokens
                + " tokens" + ChatColor.WHITE + " to " + receiver.getName() + ".");
        receiver.sendMessage(sender.getName() + " has gifted you " + ChatColor.GOLD
                + tokens + " tokens" + ChatColor.WHITE + ".");

        return true;
    }
}
