package org.scarlettparker.videogameslifeserver.events;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.scarlettparker.videogameslifeserver.objects.TPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.scarlettparker.videogameslifeserver.manager.ConfigManager.playerExists;
import static org.scarlettparker.videogameslifeserver.manager.ConfigManager.playerFile;

public class TagEvents implements Listener {
    static Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("VideoGamesLifeServer");
    public static final Map<Player, BukkitRunnable> activeTasks = new HashMap<>();

    private void registerPlayer(Player p) {
        if (playerFile.exists() && playerExists(p.getName())) {
            TPlayer tempPlayer = new TPlayer(p.getName());
            if (Objects.equals(tempPlayer.getCurrentTask(), "tag")) {
                // send message above hotbar for tagging status
                if (!tempPlayer.getTagged()) {
                    sendActionBarMessageForDuration(p, ChatColor.GREEN
                            + "You are currently not tagged.", 2 * 60 * 60); // 2 hours
                } else {
                    sendActionBarMessageForDuration(p, ChatColor.RED
                            + "You are currently tagged.", 2 * 60 * 60);
                }
            }
        }
    }

    @EventHandler
    public void playerJoinEvent(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        registerPlayer(p);
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player whoWasHit && e.getDamager() instanceof Player whoHit) {
            TPlayer gotHitPlayer = new TPlayer(whoWasHit.getName());
            TPlayer hittingPlayer = new TPlayer(whoHit.getName());

            if (Objects.equals(gotHitPlayer.getCurrentTask(), "tag")
                    && Objects.equals(hittingPlayer.getCurrentTask(), "tag")) {
                // can only tag players if their hand is empty
                if (hittingPlayer.getTagged() && !gotHitPlayer.getTagged()
                        && whoHit.getInventory().getItemInMainHand().getType() == Material.AIR) {
                    hittingPlayer.setTagged(false);
                    gotHitPlayer.setTagged(true);

                    sendActionBarMessageForDuration(whoHit, ChatColor.GREEN
                            + "You are currently not tagged.", 2 * 60 * 60); // 2 hours
                    sendActionBarMessageForDuration(whoWasHit, ChatColor.RED
                            + "You are currently tagged.", 2 * 60 * 60);
                }
            }
        }
    }

    public static void sendActionBarMessageForDuration(Player player, String message, int durationSeconds) {
        // cancel any existing task for this player
        if (activeTasks.containsKey(player)) {
            activeTasks.get(player).cancel();
        }

        // create a new task for sending the action bar message
        BukkitRunnable task = new BukkitRunnable() {
            int secondsPassed = 0;
            final int interval = 2;

            @Override
            public void run() {
                if (secondsPassed >= durationSeconds) {
                    this.cancel();
                    return;
                }
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
                secondsPassed += interval;
            }
        };

        // schedule the task to run every 'interval' seconds
        task.runTaskTimer(plugin, 0, 2 * 20);
        activeTasks.put(player, task);
    }
}
