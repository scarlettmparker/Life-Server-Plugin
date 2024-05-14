package org.scarlettparker.videogameslifeserver;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Objects;

public class LifeEvents implements Listener {
    LifeManager lifeManager = new LifeManager();

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        // TODO: Implement custom Player Death
        String playerName = event.getPlayer().getName();
        int numLives = Integer.parseInt(Objects.requireNonNull(
                ConfigManager.getPlayerBase().getString(playerName)));
        numLives -= 1;

        lifeManager.updateLives(playerName, numLives);

        Location location = event.getPlayer().getLocation();
        World world = location.getWorld();

        FireworkEffect fireworkEffect = FireworkEffect.builder().flicker(false).trail(true).with(FireworkEffect.Type.BALL).withColor(Color.WHITE).withFade(Color.GRAY).build();
        new InstantFirework(fireworkEffect, location);

        // for perma deaths :(
        if (numLives == 0) {
            world.strikeLightningEffect(location);
            event.getPlayer().setGameMode(GameMode.SPECTATOR);
            Bukkit.broadcastMessage(ChatColor.RED + playerName + " has lost all of their lives.");

            // play the lightning sound for everyone
            for (Player p : world.getPlayers()) {
                Location tempLocation = p.getPlayer().getLocation();
                p.getPlayer().playSound(tempLocation, Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 1, 0);
            }
        }
    }

    @EventHandler
    public void playerJoinEvent(PlayerJoinEvent event) {
        lifeManager.setPlayerName(event.getPlayer());
    }
}
