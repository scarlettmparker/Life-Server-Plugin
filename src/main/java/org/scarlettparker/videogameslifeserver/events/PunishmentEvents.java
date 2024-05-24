package org.scarlettparker.videogameslifeserver.events;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.scarlettparker.videogameslifeserver.objects.TPlayer;
import static org.scarlettparker.videogameslifeserver.manager.ConfigManager.playerFile;
import static org.scarlettparker.videogameslifeserver.utils.PunishmentUtils.applyPunishment;

public class PunishmentEvents implements Listener {
    @EventHandler
    public void playerJoinEvent(PlayerJoinEvent event) {
        // check if player exists in file
        if (playerFile.exists()) {
            TPlayer tempPlayer = new TPlayer(event.getPlayer().getName());
            // if player is currently punished
            tempPlayer.getPunishments();
            for (String punishment : tempPlayer.getPunishments()) {
                applyPunishment(punishment, Bukkit.getPlayer(event.getPlayer().getName()), true);
            }
        }
    }
}
