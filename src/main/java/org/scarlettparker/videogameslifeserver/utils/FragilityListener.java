package org.scarlettparker.videogameslifeserver.utils;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public class FragilityListener implements Listener {
    private static final HashMap<UUID, Integer> fragilityPlayers = new HashMap<>();
    private static Plugin plugin;

    public static void register(Player player, int duration, int amplifier) {
        if (plugin == null) {
            plugin = JavaPlugin.getProvidingPlugin(FragilityListener.class);
        }
        if (!fragilityPlayers.containsKey(player.getUniqueId())) {
            fragilityPlayers.put(player.getUniqueId(), amplifier);
        }


        plugin.getServer().getPluginManager().registerEvents(new FragilityListener(), plugin);
    }

    public static void unregister(Player player) {
        if (plugin == null) {
            plugin = JavaPlugin.getProvidingPlugin(FragilityListener.class);
        }
        fragilityPlayers.remove(player.getUniqueId());

        plugin.getServer().getPluginManager().registerEvents(new FragilityListener(), plugin);
    }

    @EventHandler
    public void onHit(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (fragilityPlayers.containsKey(player.getUniqueId())) {
                int amplifier = fragilityPlayers.get(player.getUniqueId());
                double damage = event.getDamage();
                event.setDamage(damage * amplifier * 1.35);
            }
        }
    }
}