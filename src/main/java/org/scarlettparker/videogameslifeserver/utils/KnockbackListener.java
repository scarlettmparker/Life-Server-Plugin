package org.scarlettparker.videogameslifeserver.utils;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.UUID;

public class KnockbackListener implements Listener {
    private static final HashMap<UUID, Integer> knockbackPlayers = new HashMap<>();
    private static Plugin plugin;

    public static void registerKnockback(Player player, int amplifier) {
        if (plugin == null) {
            plugin = JavaPlugin.getProvidingPlugin(KnockbackListener.class);
        }

        if (!knockbackPlayers.containsKey(player.getUniqueId())) {
            knockbackPlayers.put(player.getUniqueId(), amplifier);
        }
    }

    public static void unregisterKnockback(Player player) {
        if (plugin == null) {
            plugin = JavaPlugin.getProvidingPlugin(KnockbackListener.class);
        }
        knockbackPlayers.remove(player.getUniqueId());
    }

    @EventHandler
    public void onHit(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (knockbackPlayers.containsKey(player.getUniqueId())) {
                Vector knockbackDirection = null;

                if (event instanceof EntityDamageByEntityEvent entityEvent) {
                    Entity damager = entityEvent.getDamager();

                    // if the player has been hit by a projectile
                    if (damager instanceof Projectile projectile) {
                        ProjectileSource shooter = projectile.getShooter();
                        if (shooter instanceof Entity) {
                            // set knockback direction to direction the projectile was firing
                            knockbackDirection = player.getLocation().toVector()
                                    .subtract(((Entity) shooter).getLocation().toVector()).normalize();
                        }
                    } else {
                        knockbackDirection = player.getLocation().toVector()
                                .subtract(damager.getLocation().toVector()).normalize();
                    }
                }

                if (knockbackDirection != null) {
                    Vector horizontalKnockback = new Vector(knockbackDirection.getX(),
                            0, knockbackDirection.getZ()).normalize().multiply(7.5);

                    // scale the vertical component by 2 so player doesn't go FLYING
                    double verticalKnockback = knockbackDirection.getY();
                    if (verticalKnockback < 0) {
                        verticalKnockback = 0; // prevent downward knockback
                    } else {
                        verticalKnockback *= 2;
                    }

                    // Combine the horizontal and adjusted vertical components
                    Vector totalKnockback = horizontalKnockback.add(new Vector(0, verticalKnockback, 0));

                    player.setVelocity(totalKnockback);
                }
            }
        }
    }
}