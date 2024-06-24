package org.scarlettparker.videogameslifeserver.events;
import org.bukkit.*;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;
import org.scarlettparker.videogameslifeserver.objects.Death;
import org.scarlettparker.videogameslifeserver.objects.TPlayer;
import org.scarlettparker.videogameslifeserver.utils.InstantFirework;

import java.util.Objects;

import static org.scarlettparker.videogameslifeserver.commands.life.StartLife.createPlayer;
import static org.scarlettparker.videogameslifeserver.manager.ConfigManager.*;
import static org.scarlettparker.videogameslifeserver.utils.WorldUtils.getAllPlayers;
import static org.scarlettparker.videogameslifeserver.utils.WorldUtils.setPlayerName;

public class LifeEvents implements Listener {
    @EventHandler
    public void playerJoinEvent(PlayerJoinEvent event) {
        // check if player exists in file
        if (playerFile.exists() && !playerExists(event.getPlayer().getName())) {
            createPlayer(event.getPlayer());

            // for players that have joined for the first time
            TPlayer temp = new TPlayer(event.getPlayer().getName());
            temp.setSessionTasks(-1);

            // new players aren't allowed to join

            /*ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
            Bukkit.dispatchCommand(console, "kick " + event.getPlayer().getName() + " Server is closed to new players.");
            Bukkit.dispatchCommand(console, "ban " + event.getPlayer().getName() + " Server is closed to new players.");*/

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

            // change death message
            String deathMessage = event.getDeathMessage();
            for (Player p : getAllPlayers()) {
                if (deathMessage.contains(p.getName())) {
                    if (tempPlayer.getLives() < 1) {
                        deathMessage = "";
                        break;
                    }
                    deathMessage = deathMessage.replace(p.getName(), p.getDisplayName());
                }
            }

            // broadcast the death message to everyone
            if (!Objects.equals(deathMessage, "")) { Bukkit.broadcastMessage(deathMessage); }

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
    public void playerDamageEvent(EntityResurrectEvent event) {
        if (event.getEntity() instanceof Player player) {
            EquipmentSlot totemHand = event.getHand();
            if (totemHand != null) {
                ItemStack totem = player.getInventory().getItem(totemHand);
                ItemMeta meta = totem.getItemMeta();
                String metaData = meta.getPersistentDataContainer().get(new NamespacedKey("videogameslifeserver", "itemmeta"), PersistentDataType.STRING);
                if (totem.getType() == Material.TOTEM_OF_UNDYING) {
                    System.out.println(metaData);
                    if (metaData != null) {
                        if (!metaData.equals("shoptotem")) {
                            event.setCancelled(true);
                        }
                    } else {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void playerRespawnEvent(PlayerRespawnEvent event) {
        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("VideoGamesLifeServer");
        Player player = event.getPlayer();
        TPlayer tempPlayer = new TPlayer(player.getName());
        int lives = tempPlayer.getLives();

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
