package org.scarlettparker.videogameslifeserver.commands.punish;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.scarlettparker.videogameslifeserver.objects.Punishment;
import org.scarlettparker.videogameslifeserver.objects.TPlayer;

import java.util.Objects;

import static org.scarlettparker.videogameslifeserver.manager.ConfigManager.*;
import static org.scarlettparker.videogameslifeserver.utils.PunishmentUtils.applyPunishment;

public class SetPunishment implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        // for cleanliness
        Player player = Bukkit.getPlayer(args[0]);

        // must be an operator to use the command
        if (sender instanceof Player && !sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "You must be an operator to run this command.");
            return true;
        }

        if (args.length != 2) {
            sender.sendMessage(ChatColor.RED + "Incorrect usage. Correct usage: /setpunishment player punishmentID");
            return true;
        }

        if (!jsonFileExists(punishFile) || !jsonFileExists(playerFile)) {
            sender.sendMessage(ChatColor.RED
                    + "Player file not yet initialized. Make sure to run /startlife and then /starttasks.");
            return true;
        }

        if (!playerExists(args[0]) || player == null) {
            sender.sendMessage(ChatColor.RED + "Specified player is not online.");
            return true;
        }

        Punishment tempPunishment = new Punishment(args[1]);

        if (Objects.equals(tempPunishment.getDescription(), "")) {
            sender.sendMessage(ChatColor.RED + "No such punishment exists.");
            return true;
        }

        // get player and add punishments
        TPlayer tempPlayer = new TPlayer(player.getName());

        tempPlayer.addPunishment(args[1]);
        applyPunishment(args[1], player, false);

        player.sendMessage(ChatColor.RED + "You have been punished with " +
                tempPunishment.getDescription());

        return true;
    }
}
