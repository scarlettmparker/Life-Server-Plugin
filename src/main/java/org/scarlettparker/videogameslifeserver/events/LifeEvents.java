package org.scarlettparker.videogameslifeserver.events;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.scarlettparker.videogameslifeserver.manager.ConfigManager;
import org.scarlettparker.videogameslifeserver.manager.LifeManager;
import org.scarlettparker.videogameslifeserver.utils.InstantFirework;
import java.util.Objects;

public class LifeEvents implements Listener {
    LifeManager lifeManager = new LifeManager();

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        // TODO: Implement custom Player Death
        String playerName = event.getPlayer().getName();
        String[] playerData = ConfigManager.getPlayerData(playerName).split(",");

        int numLives = Integer.parseInt(Objects.requireNonNull(playerData[1]));
        numLives -= 1;

        lifeManager.updateLives(playerData, numLives);

        Location location = event.getPlayer().getLocation();
        World world = location.getWorld();

        FireworkEffect fireworkEffect = FireworkEffect.builder().flicker(false).trail(true)
                .with(FireworkEffect.Type.BALL).withColor(Color.WHITE).withFade(Color.GRAY).build();
        new InstantFirework(fireworkEffect, location);

        // for perma deaths :(
        if (numLives == 0) {
            world.strikeLightningEffect(location).setSilent(true);
            event.getPlayer().setGameMode(GameMode.SPECTATOR);
            Bukkit.broadcastMessage(ChatColor.RED + playerName + " has lost all of their lives." + ChatColor.WHITE +
                    " They are now permanently dead unless" + ChatColor.BLUE + " revived by another player"
                    + ChatColor.WHITE + ".");

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
        event.getPlayer().setNoDamageTicks(0);
    }
}
