package org.scarlettparker.videogameslifeserver.utils;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.scarlettparker.videogameslifeserver.Main;

public class CustomParticleEffect {

    public static void playTotemEffect(Player player) {
        Location location = player.getLocation();
        playTotemAnimation(location);
        playHexagonAnimation(location);
    }

    private static void playTotemAnimation(Location location) {
        new BukkitRunnable() {
            double t = 0;

            @Override
            public void run() {
                t += 0.1;

                // totem particle effect around the player
                location.getWorld().spawnParticle(Particle.TOTEM, location.clone().add(0, 1, 0), 20, 0.5, 1, 0.5, 0.1);

                if (t > 3) {
                    this.cancel();
                }
            }
        }.runTaskTimer(Main.getPlugin(Main.class), 0, 1);
    }

    private static void playHexagonAnimation(Location location) {
        new BukkitRunnable() {
            double t = 0;
            final Location center = location.clone().add(0, 1, 0); // Center point at eye level

            @Override
            public void run() {
                t += 0.1;

                // hexagonal particle effect spinning around the player
                for (int i = 0; i < 6; i++) {
                    double angle = Math.toRadians((i * 60) + (t * 60));
                    double x = Math.cos(angle) * 1.5;
                    double z = Math.sin(angle) * 1.5;
                    Location particleLoc = center.clone().add(x, 0, z);
                    particleLoc.getWorld().spawnParticle(Particle.SPELL_WITCH, particleLoc, 1, 0, 0, 0, 0);
                }

                // disintegration effect, slowly decreasing particle density
                for (int i = 0; i < 6; i++) {
                    double angle = Math.toRadians((i * 60) + (t * 60));
                    double x = Math.cos(angle) * 1.5;
                    double z = Math.sin(angle) * 1.5;
                    Location particleLoc = center.clone().add(x, 0, z);

                    // decrease density over time so it disperses
                    double density = Math.max(0, 1 - (t / 3.0));
                    particleLoc.getWorld().spawnParticle(Particle.CRIT, particleLoc, (int) (10 * density), 0, 0, 0, 0);
                }

                if (t > 3) {
                    this.cancel();
                }
            }
        }.runTaskTimer(Main.getPlugin(Main.class), 0, 1);
    }
}