package org.scarlettparker.videogameslifeserver.events;
import org.bukkit.*;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.scarlettparker.videogameslifeserver.objects.Death;
import org.scarlettparker.videogameslifeserver.objects.TPlayer;
import org.scarlettparker.videogameslifeserver.utils.InstantFirework;

import java.util.Objects;

import static org.scarlettparker.videogameslifeserver.commands.admin.StartLife.createPlayer;
import static org.scarlettparker.videogameslifeserver.manager.ConfigManager.*;
import static org.scarlettparker.videogameslifeserver.utils.WorldUtils.setPlayerName;

public class LifeEvents implements Listener {
    @EventHandler
    public void playerJoinEvent(PlayerJoinEvent event) {
        // check if player exists in file
        if (playerFile.exists() && !playerExists(event.getPlayer().getName())) {
            createPlayer(event.getPlayer());
        } else {
            TPlayer tempPlayer = new TPlayer(event.getPlayer().getName());
            int lives = tempPlayer.getLives();
            setPlayerName(event.getPlayer(), lives);
        }
        event.getPlayer().setNoDamageTicks(0);
    }

    @EventHandler
    public void playerDeathEvent(PlayerDeathEvent event) {
        if (playerFile.exists() && playerExists(event.getPlayer().getName())) {
            long unixTime = System.currentTimeMillis() / 1000L;
            Death death = new Death(unixTime, event.getDeathMessage());

            TPlayer tempPlayer = new TPlayer(event.getPlayer().getName());
            Death[] currentDeaths = tempPlayer.getDeaths();
            Death[] tempDeaths = new Death[currentDeaths.length + 1];

            // copy old deaths into new array
            System.arraycopy(currentDeaths, 0, tempDeaths, 0, currentDeaths.length);

            // add new death to last position and update
            tempDeaths[currentDeaths.length] = death;
            tempPlayer.setDeaths(tempDeaths);

            // update lives and used to set name properly
            int lives = tempPlayer.getLives();
            lives -= 1;

            if (lives < 0) {
                lives = 0;
            }

            tempPlayer.setLives(lives);
            setPlayerName(event.getPlayer(), lives);

            // to send console commands
            ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
            if (tempPlayer.getPunishments().length != 0) {
                Bukkit.dispatchCommand(console, "clearpunishments " + tempPlayer.getName());
            }
        }

        Location location = event.getPlayer().getLocation();

        // set off firework at player location
        FireworkEffect fireworkEffect = FireworkEffect.builder().flicker(false).trail(true)
                .with(FireworkEffect.Type.BALL).withColor(Color.WHITE).withFade(Color.GRAY).build();
        new InstantFirework(fireworkEffect, location, "deathfirework");
    }

    @EventHandler
    public void playerRespawnEvent(PlayerRespawnEvent event) {
        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("VideoGamesLifeServer");
        Player player = event.getPlayer();
        TPlayer tempPlayer = new TPlayer(player.getName());
        int lives = tempPlayer.getLives();

        ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
        if (lives == 1 && Objects.equals(tempPlayer.getCurrentTask(), "-1")) {
            player.sendMessage(ChatColor.RED + "As you're now a red life, you will be given "
                    + "continuous red tasks from now on.");

            // run the newtask command when the player respawns
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "newtask " + player.getName() + " normal");
            }, 10); // WHAT IS WRONG WITH BUKKIT
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Firework) {
            Firework firework = (Firework) event.getDamager();
            String metaData = firework.getPersistentDataContainer().get(new NamespacedKey("videogameslifeserver", "fireworkmeta"), PersistentDataType.STRING);
            // make sure player aren't damaged by fireworks
            if (metaData != null && metaData.equals("deathfirework")) {
                event.setCancelled(true);
            }
        }
    }
}
