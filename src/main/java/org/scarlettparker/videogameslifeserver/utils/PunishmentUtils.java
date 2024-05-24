package org.scarlettparker.videogameslifeserver.utils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.scarlettparker.videogameslifeserver.objects.Punishment;

public class PunishmentUtils {
    public static void applyPunishment(String punishment, Player player, boolean tellPlayer) {
        // only show when player joins
        if (tellPlayer) {
            Punishment tempPunishment = new Punishment(punishment);
            player.sendMessage(ChatColor.RED + "Reminder: You are currently being punished with "
                    + tempPunishment.getDescription());
        }
    }
}
