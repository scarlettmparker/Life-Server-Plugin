package org.scarlettparker.videogameslifeserver.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;
import org.scarlettparker.videogameslifeserver.objects.Punishment;
import org.scarlettparker.videogameslifeserver.objects.TPlayer;
import java.util.Arrays;
import java.util.Objects;

public class PunishmentUtils implements Listener {
    static Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("VideoGamesLifeServer");
    private static final int duration = 30;

    public static void applyPunishment(String punishment, Player player, boolean tellPlayer) {
        // only show when player joins
        if (tellPlayer) {
            Punishment tempPunishment = new Punishment(punishment);
            player.sendMessage(ChatColor.RED + "Reminder: You are currently cursed with "
                    + tempPunishment.getDescription());
        }

        CustomEffect customPunishment = getCustomEffect(punishment);
        TPlayer tempPlayer = new TPlayer(player.getName());

        // if the punishment is active
        if (customPunishment != null && !(customPunishment instanceof PunishmentEffect)) {
            Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
                @Override
                public void run() {
                    if (Bukkit.getPlayer(player.getName()) != null
                            && Arrays.asList(tempPlayer.getPunishments()).contains(punishment)) {
                        customPunishment.applyFragility(player);
                    }
                }
            }, 0, 20);
        } else if (customPunishment != null) {
            if (Objects.equals(customPunishment.getType(), "fragile")) {
                customPunishment.applyFragility(player);
            } else if (Objects.equals(customPunishment.getType(), "knockback")) {
                customPunishment.applyKnockback(player);
            }
        } else {
            player.setMaxHealth(12.0);
        }
    }

    public static @Nullable CustomEffect getCustomEffect(String punishment) {
        CustomEffect customPunishment = null;

        if (Objects.equals(punishment, "weak2")) {
            customPunishment = new PotionEffectWrapper(new PotionEffect(PotionEffectType.WEAKNESS, duration, 1));
        } else if (Objects.equals(punishment, "weak1")) {
            customPunishment = new PotionEffectWrapper(new PotionEffect(PotionEffectType.WEAKNESS, duration, 0));
        } else if (Objects.equals(punishment, "fragile1")) {
            customPunishment = new PunishmentEffect("fragile", 1, 1);
        } else if (Objects.equals(punishment, "knockback")) {
            customPunishment = new PunishmentEffect("knockback", 1, 1);
        }

        return customPunishment;
    }
}
