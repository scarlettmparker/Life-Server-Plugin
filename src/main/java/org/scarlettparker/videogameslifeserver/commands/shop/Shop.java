package org.scarlettparker.videogameslifeserver.commands.shop;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;
import org.scarlettparker.videogameslifeserver.objects.TPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Shop implements CommandExecutor, Listener {

    private final Map<ItemStack, Integer> itemValues = new HashMap<>();
    private final List<ItemStack> itemOrder = new ArrayList<>();

    public Shop() {
        addItem(createItem(Material.APPLE, 3, "1 Token"), 1);
        addItem(createItem(Material.CARROT, 16, "1 Token"), 1);
        addItem(createItem(Material.POTATO, 16, "1 Token"), 1);
        addItem(createItem(Material.BAKED_POTATO, 16, "2 Tokens"), 2);
        addItem(createItem(Material.GOLDEN_APPLE, 1, "2 Tokens"), 2);

        addItem(createItem(Material.AIR, 0, ""), 0);
        addItem(createItem(Material.AIR, 0, ""), 0);
        addItem(createItem(Material.AIR, 0, ""), 0);
        addItem(createItem(Material.AIR, 0, ""), 0);

        addItem(createItem(Material.SUGAR_CANE, 16, "1 Token"), 1);
        addItem(createItem(Material.BAMBOO, 16, "1 Token"), 1);
        addItem(createItem(Material.EXPERIENCE_BOTTLE, 64, "1 Token"), 1);

        addItem(createItem(Material.AIR, 0, ""), 0);
        addItem(createItem(Material.AIR, 0, ""), 0);
        addItem(createItem(Material.AIR, 0, ""), 0);
        addItem(createItem(Material.AIR, 0, ""), 0);
        addItem(createItem(Material.AIR, 0, ""), 0);
        addItem(createItem(Material.AIR, 0, ""), 0);

        addItem(createItem(Material.IRON_BLOCK, 1, "1 Token"), 1);
        addItem(createItem(Material.INFESTED_STONE, 12, "1 Token"), 1);
        addItem(createMendingBook(1, "7 Tokens"), 7);

        addItem(createItem(Material.AIR, 0, ""), 0);
        addItem(createItem(Material.AIR, 0, ""), 0);
        addItem(createItem(Material.AIR, 0, ""), 0);
        addItem(createItem(Material.AIR, 0, ""), 0);
        addItem(createItem(Material.AIR, 0, ""), 0);
        addItem(createItem(Material.AIR, 0, ""), 0);

        addItem(createItem(Material.NETHERITE_SCRAP, 1, "3 Tokens"), 3);
        addItem(createItem(Material.WOLF_SPAWN_EGG, 2, "5 Tokens"), 5);
        addItem(createItem(Material.DIAMOND, 3, "2 Tokens"), 2);
        addItem(createItem(Material.BLAZE_ROD, 16, "8 Tokens"), 8);

        addItem(createItem(Material.AIR, 0, ""), 0);
        addItem(createItem(Material.AIR, 0, ""), 0);
        addItem(createItem(Material.AIR, 0, ""), 0);
        addItem(createItem(Material.AIR, 0, ""), 0);
        addItem(createItem(Material.AIR, 0, ""), 0);

        addItem(createPotionItem(Material.POTION, 1, "2 Tokens",
                PotionType.INVISIBILITY, false, false), 2);
        addItem(createPotionItem(Material.POTION, 1, "2 Tokens",
                PotionType.SPEED, false, false), 2);
        addItem(createPotionItem(Material.POTION, 1, "2 Tokens",
                PotionType.FIRE_RESISTANCE, true, false), 2);

        addItem(createPotionItem(Material.SPLASH_POTION, 1, "2 Tokens",
                PotionType.POISON, true, false), 2);
        addItem(createPotionItem(Material.SPLASH_POTION, 1, "2 Tokens",
                PotionType.INSTANT_DAMAGE, false, true), 2);
        addItem(createPotionItem(Material.SPLASH_POTION, 1, "2 Tokens",
                PotionType.WEAKNESS, true, false), 2);
        addItem(createPotionItem(Material.SPLASH_POTION, 1, "2 Tokens",
                PotionType.SLOWNESS, true, false), 2);

        addItem(createItem(Material.AIR, 0, ""), 0);
        addItem(createItem(Material.TOTEM_OF_UNDYING, 1, "45 Tokens"), 45);
    }

    private void addItem(ItemStack item, int quantity) {
        itemValues.put(item, quantity);
        itemOrder.add(item);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You must be a player to run this command.");
            return true;
        }

        if (args.length > 0) {
            sender.sendMessage(ChatColor.RED + "Incorrect usage! Correct usage: /whattask");
            return true;
        }

        Player player = (Player) sender;
        TPlayer tempPlayer = new TPlayer(player.getName());

        player.openInventory(shopInventory(tempPlayer));
        tempPlayer.setShopping(true);

        return true;
    }

    private Inventory shopInventory(TPlayer tempPlayer) {
        // show player number of tokens in shop
        String tokenText = (tempPlayer.getTokens() == 1) ? " token" : " tokens";

        Inventory shopInventory = Bukkit.createInventory(null, 45, "You have "
                + ChatColor.DARK_AQUA + tempPlayer.getTokens() + tokenText + ChatColor.BLACK + ".");

        int index = 0;
        for (ItemStack item : itemOrder) {
            if (item.getType() != Material.AIR) {
                shopInventory.setItem(index, item);
            }
            index++;
        }

        return shopInventory;
    }

    private ItemStack createItem(Material material, int tokenCost, String displayName) {
        ItemStack itemStack = new ItemStack(material, tokenCost);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setDisplayName(ChatColor.GREEN + displayName);
            itemStack.setItemMeta(itemMeta);
        }
        return itemStack;
    }

    private ItemStack createPotionItem(Material material, int tokenCost, String displayName,
                                       PotionType potionType, boolean extended, boolean upgraded) {
        ItemStack itemStack = new ItemStack(material, tokenCost);
        PotionMeta potionMeta = (PotionMeta) itemStack.getItemMeta();
        potionMeta.setBasePotionData(new PotionData(potionType, extended, upgraded));
        potionMeta.setDisplayName(ChatColor.GREEN + displayName);
        itemStack.setItemMeta(potionMeta);
        return itemStack;
    }

    private ItemStack createMendingBook(int tokenCost, String displayName) {
        ItemStack itemStack = new ItemStack(Material.ENCHANTED_BOOK, tokenCost);
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) itemStack.getItemMeta();
        meta.addStoredEnchant(Enchantment.MENDING, 1, true);
        meta.setDisplayName(ChatColor.GREEN + displayName);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();
        TPlayer tempPlayer = new TPlayer(player.getName());

        if (tempPlayer.getShopping() && event.getView().getTitle().startsWith("You have")) {
            event.setCancelled(true);
            ItemStack clickedItem = event.getCurrentItem();

            if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

            // find the token value and quantity of the clicked item
            for (Map.Entry<ItemStack, Integer> entry : itemValues.entrySet()) {
                ItemStack shopItem = entry.getKey();
                if (clickedItem.isSimilar(shopItem)) {
                    // get values from shop
                    int tokenValue = entry.getValue();
                    int quantity = clickedItem.getAmount();

                    // if players inventory is full
                    if (player.getInventory().firstEmpty() == -1) {
                        player.sendMessage(ChatColor.RED + "Your inventory is full! "
                                + "Please empty a slot before buying an item.");
                    } else {
                        if (tempPlayer.getTokens() >= tokenValue) {
                            tempPlayer.setTokens(tempPlayer.getTokens() - tokenValue);

                            // create the item to give based on the type and quantity
                            ItemStack itemToGive = shopItem.clone();

                            itemToGive.setAmount(quantity);
                            ItemMeta itemMeta = itemToGive.getItemMeta();
                            itemMeta.setDisplayName(null);
                            itemToGive.setItemMeta(itemMeta);

                            player.getInventory().addItem(itemToGive);

                            player.sendMessage(ChatColor.GREEN + "You purchased " + quantity + " "
                                    + shopItem.getType().name().replace("_", " ").toLowerCase()
                                    + "(s) for " + tokenValue + " tokens!");

                            // reset shop title so it shows new token value
                            player.openInventory(shopInventory(tempPlayer));
                            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_CELEBRATE, 2, 1);

                            tempPlayer.setShopping(true);
                        } else {
                            player.sendMessage(ChatColor.RED + "You don't have enough tokens for this item.");
                        }
                    }
                    break;
                }
            }
        }
    }
}