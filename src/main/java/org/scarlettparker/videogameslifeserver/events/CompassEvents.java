package org.scarlettparker.videogameslifeserver.events;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.scarlettparker.videogameslifeserver.objects.TPlayer;

import java.util.*;

public class CompassEvents implements Listener {
    static Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("VideoGamesLifeServer");
    private final Map<UUID, Player> trackingMap = new HashMap<>();

    @EventHandler
    public void onCompassRightClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item.getType() == Material.COMPASS) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null && meta.getPersistentDataContainer().has(new NamespacedKey(plugin, "trackingCompass"), PersistentDataType.BYTE)) {
                if (hasTagTask(player)) {
                    openTrackingMenu(player);
                } else {
                    player.sendMessage(ChatColor.RED + "You cannot use this compass.");
                }
            }
        }
    }

    private void openTrackingMenu(Player player) {
        Inventory menu = Bukkit.createInventory(player, 9, "Select a player to locate:");

        // populate inventory with player heads of those who have the tag task
        for (Player onlinePlayer : Bukkit.getServer().getOnlinePlayers()) {
            if (onlinePlayer != player && hasTagTask(onlinePlayer)) {
                ItemStack playerHead = createPlayerHead(onlinePlayer.getName());
                menu.addItem(playerHead);
            }
        }

        player.openInventory(menu);

        // set tracking selection
        new BukkitRunnable() {
            @Override
            public void run() {
                trackingMap.put(player.getUniqueId(), null);
            }
        }.runTaskLater(plugin, 20L); // delay 1 second before allowing selection
    }

    @EventHandler
    public void onPlayerSelection(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory clickedInventory = event.getClickedInventory();

        if (clickedInventory != null && clickedInventory.equals(player.getOpenInventory().getTopInventory())) {
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem != null && clickedItem.getType() == Material.PLAYER_HEAD) {
                String playerName = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName());
                Player selectedPlayer = Bukkit.getPlayerExact(playerName);

                if (selectedPlayer != null && hasTagTask(selectedPlayer)) {
                    trackingMap.put(player.getUniqueId(), selectedPlayer);
                    startTracking(player, selectedPlayer);
                    player.sendMessage(ChatColor.GREEN + "You are now tracking: " + selectedPlayer.getName());
                    player.closeInventory(); // close the inventory after selecting
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_CELEBRATE, 2, 1);
                } else {
                    player.sendMessage(ChatColor.RED + "Invalid selection. Please choose a valid player.");
                }
            }
        }
    }

    private void startTracking(Player tracker, Player target) {
        ItemStack compass = tracker.getInventory().getItemInMainHand();
        if (compass != null && compass.getType() == Material.COMPASS) {
            // set the compass target to the selected player's location
            tracker.setCompassTarget(target.getLocation());

            ItemMeta meta = compass.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(ChatColor.YELLOW + "Tracking Compass");
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "Currently Tracking: " + target.getName());
                meta.setLore(lore);
                compass.setItemMeta(meta);
            }
        }
    }

    private boolean hasTagTask(Player player) {
        TPlayer tempPlayer = new TPlayer(player.getName());
        return Objects.equals(tempPlayer.getCurrentTask(), "tag");
    }

    private ItemStack createPlayerHead(String playerName) {
        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
        ItemMeta meta = playerHead.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.AQUA + playerName);
            playerHead.setItemMeta(meta);
        }
        return playerHead;
    }
}
