package org.scarlettparker.videogameslifeserver.commands.admin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.scarlettparker.videogameslifeserver.objects.Punishment;
import org.scarlettparker.videogameslifeserver.objects.TPlayer;
import org.scarlettparker.videogameslifeserver.utils.CustomEffect;

import java.util.Arrays;
import java.util.Objects;

import static org.scarlettparker.videogameslifeserver.manager.ConfigManager.*;
import static org.scarlettparker.videogameslifeserver.utils.FragilityListener.unregister;
import static org.scarlettparker.videogameslifeserver.utils.PunishmentUtils.applyPunishment;
import static org.scarlettparker.videogameslifeserver.utils.PunishmentUtils.getCustomEffect;

public class SetPunishment implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        // for cleanliness
        String playerName;

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

        if (!playerExists(args[0]) || Bukkit.getPlayer(args[0]) == null) {
            sender.sendMessage(ChatColor.RED + "Specified player is not online.");
            return true;
        } else {
            playerName = args[0];
        }

        Punishment tempPunishment = new Punishment(args[1]);

        if (Objects.equals(tempPunishment.getDescription(), "")) {
            sender.sendMessage(ChatColor.RED + "No such punishment exists.");
            return true;
        }

        // get player and add punishments
        TPlayer tempPlayer = new TPlayer(playerName);

        tempPlayer.addPunishment(args[1]);
        applyPunishment(args[1], Bukkit.getPlayer(args[0]), false);

        Bukkit.getPlayer(args[0]).sendMessage(ChatColor.RED + "You have been punished with " +
                tempPunishment.getDescription());

        return true;
    }
}
