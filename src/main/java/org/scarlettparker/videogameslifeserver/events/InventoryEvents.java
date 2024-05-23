package org.scarlettparker.videogameslifeserver.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.scarlettparker.videogameslifeserver.objects.TPlayer;

public class InventoryEvents implements Listener {
    @EventHandler
    public void detectInventoryClose(InventoryCloseEvent event) {
        // has to not be shopping if any inventory is closed
        TPlayer tempPlayer = new TPlayer(event.getPlayer().getName());
        tempPlayer.setShopping(false);
    }

    @EventHandler
    public void checkMovedItem(InventoryClickEvent event) {
        // so players dont accidentally remove items from their inventory
        TPlayer tempPlayer = new TPlayer(event.getWhoClicked().getName());
        if (tempPlayer.getShopping() && event.getInventory().getType() == InventoryType.PLAYER) {
            event.setCancelled(true);
        }
    }
}
