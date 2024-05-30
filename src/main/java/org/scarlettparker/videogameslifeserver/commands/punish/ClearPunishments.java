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
        Player player = Bukkit.getPlayer(args[0]);

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


        if (!playerExists(args[0]) || player == null) {
            sender.sendMessage(ChatColor.RED + "Specified player is not online.");
            return true;
        }

        TPlayer tempPlayer = new TPlayer(player.getName());

        // clear any punishments that aren't potion effects
        if (tempPlayer.hasPunishment("fragile1")) {
            unregisterFragility(player);
        }
        if (tempPlayer.hasPunishment("knockback")) {
            unregisterKnockback(player);
        }
        if (tempPlayer.hasPunishment("hearts6")) {
            player.setMaxHealth(20.0);
        }

        tempPlayer.setPunishments(new String[0]);

        player.sendMessage(ChatColor.GREEN + "You have been cleared of your curse(s).");
        return true;
    }
}
