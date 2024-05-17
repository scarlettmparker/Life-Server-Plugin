package org.scarlettparker.videogameslifeserver.commands.shop;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.scarlettparker.videogameslifeserver.manager.ConfigManager;

import java.util.Objects;

public class Tokens implements CommandExecutor {
    // TODO: Tell user how many tokens they have.
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender instanceof ConsoleCommandSender) {
            System.err.println("Only players can use this command!");
            return true;
        }

        Player p = Bukkit.getPlayer(sender.getName());

        // get token information from player
        String[] playerData = ConfigManager.getPlayerData(sender.getName()).split(",");
        String tokens = Objects.requireNonNull(playerData[6]);

        p.sendMessage(ChatColor.WHITE + "You have " + ChatColor.GOLD + tokens + " tokens" + ChatColor.WHITE + ".");

        return true;
    }
}
