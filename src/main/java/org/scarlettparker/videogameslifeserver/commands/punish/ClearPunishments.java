package org.scarlettparker.videogameslifeserver.commands.punish;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.scarlettparker.videogameslifeserver.objects.TPlayer;

import java.util.Arrays;

import static org.scarlettparker.videogameslifeserver.manager.ConfigManager.*;
import static org.scarlettparker.videogameslifeserver.utils.FragilityListener.unregisterFragility;
import static org.scarlettparker.videogameslifeserver.utils.KnockbackListener.unregisterKnockback;

public class ClearPunishments implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        // for cleanliness
        String playerName;

        // must be an operator to use the command
        if (sender instanceof Player && !sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "You must be an operator to run this command.");
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Incorrect usage. Correct usage: /clearpunishments player");
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

        TPlayer tempPlayer = new TPlayer(playerName);

        // clear any punishments that aren't potion effects
        if (Arrays.asList(tempPlayer.getPunishments()).contains("fragile1")) {
            unregisterFragility(Bukkit.getPlayer(args[0]));
        }
        if (Arrays.asList(tempPlayer.getPunishments()).contains("knockback")) {
            unregisterKnockback(Bukkit.getPlayer(args[0]));
        }
        if (Arrays.asList(tempPlayer.getPunishments()).contains("hearts6")) {
            Bukkit.getPlayer(args[0]).setMaxHealth(20.0);
        }

        tempPlayer.setPunishments(new String[0]);

        Bukkit.getPlayer(args[0]).sendMessage(ChatColor.GREEN + "You have been cleared of your curse(s).");
        return true;
    }
}
