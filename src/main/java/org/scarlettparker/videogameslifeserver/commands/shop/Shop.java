package org.scarlettparker.videogameslifeserver.commands.shop;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;
import org.scarlettparker.videogameslifeserver.objects.TPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Shop implements CommandExecutor, Listener {
    static Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("VideoGamesLifeServer");

    private final Map<ItemStack, Integer> itemValues = new HashMap<>();
    private final List<ItemStack> itemOrder = new ArrayList<>();

    public Shop() {
        addItem(createItem(Material.APPLE, 3, "1 Token", "Apple"), 1);
        addItem(createItem(Material.CARROT, 16, "1 Token", "Carrot"), 1);
        addItem(createItem(Material.POTATO, 16, "1 Token", "Potato"), 1);
        addItem(createItem(Material.BAKED_POTATO, 16, "2 Tokens", "Baked Potato"), 2);
        addItem(createItem(Material.GOLDEN_APPLE, 1, "2 Tokens", "Golden Apple"), 2);

        fillBlankSlots(4);

        addItem(createItem(Material.LEATHER, 16, "2 Tokens", "Leather"), 2);
        addItem(createItem(Material.BAMBOO, 16, "1 Token", "Bamboo"), 1);
        addItem(createItem(Material.EXPERIENCE_BOTTLE, 64, "1 Token", "Bottle o' Enchanting"), 1);

        fillBlankSlots(6);

        addItem(createItem(Material.IRON_BLOCK, 1, "1 Token", "Iron Block"), 1);
        addItem(createEnchantedBook(1, "7 Tokens", Enchantment.MENDING, 1), 7);
        addItem(createEnchantedBook(1, "5 Tokens", Enchantment.DURABILITY, 3), 5);

        fillBlankSlots(5);

        // on the side
        addItem(createItem(Material.OCELOT_SPAWN_EGG, 1, "2 Tokens", "Ocelot Spawn Egg"), 2);

        // normal
        addItem(createItem(Material.NETHERITE_SCRAP, 1, "3 Tokens", "Netherite Scrap"), 3);
        addItem(createItem(Material.DIAMOND, 3, "2 Tokens", "Diamond"), 2);
        addItem(createItem(Material.LAPIS_LAZULI, 32, "1 Token", "Lapis Lazuli"), 1);
        addItem(createItem(Material.BLAZE_ROD, 8, "5 Tokens", "Blaze Rod"), 5);
        addItem(createItem(Material.NETHER_WART, 3, "5 Tokens", "Nether Wart"), 5);

        fillBlankSlots(3);

        // on the side
        addItem(createItem(Material.WOLF_SPAWN_EGG, 2, "5 Tokens", "Wolf Spawn Egg"), 5);

        // normal
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

        fillBlankSlots(1);

        addItem(createItem(Material.TOTEM_OF_UNDYING, 1, "45 Tokens", "Totem of Undying"), 45);
    }

    private void addItem(ItemStack item, int tokenCost) {
        itemValues.put(item, tokenCost);
        itemOrder.add(item);
    }

    private void fillBlankSlots(int quantity) {
        for (int i = 0; i < quantity; i++) {
            addItem(createItem(Material.AIR, 0, "", ""), 0);
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "You must be a player to run this command.");
            return true;
        }

        if (!sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "You must be an operator to run this command.");
            return true;
        }

        if (args.length > 0) {
            sender.sendMessage(ChatColor.RED + "Incorrect usage! Correct usage: /whattask");
            return true;
        }

        TPlayer tempPlayer = new TPlayer(player.getName());

        player.openInventory(shopInventory(tempPlayer));
        tempPlayer.setShopping(true);

        return true;
    }

    @EventHandler
    public void onVillagerClick(PlayerInteractEntityEvent e) {
        if (e.getRightClicked().getType() == EntityType.VILLAGER) {
            // get villager and ensure name matches
            Villager villager = (Villager) e.getRightClicked();
            NamespacedKey key = new NamespacedKey(plugin, "shop_villager");
            if (villager.getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
                String value = villager.getPersistentDataContainer().get(key, PersistentDataType.STRING);
                if ("forum_marketplace".equals(value)) {
                    e.setCancelled(true);

                    // get person who right clicked and open the shop
                    Player player = e.getPlayer();
                    TPlayer tempPlayer = new TPlayer(player.getName());
                    player.openInventory(shopInventory(tempPlayer));
                    tempPlayer.setShopping(true);
                }
            }
        }
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

    private ItemStack createItem(Material material, int quantity, String tokenCost, String displayName) {
        ItemStack itemStack = new ItemStack(material, quantity);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setDisplayName(ChatColor.RESET + displayName);
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GOLD + tokenCost);
            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);
        }
        return itemStack;
    }

    private ItemStack createPotionItem(Material material, int quantity, String tokenCost,
                                       PotionType potionType, boolean extended, boolean upgraded) {
        ItemStack itemStack = new ItemStack(material, quantity);
        PotionMeta potionMeta = (PotionMeta) itemStack.getItemMeta();
        if (potionMeta != null) {
            potionMeta.setBasePotionData(new PotionData(potionType, extended, upgraded));
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GOLD + tokenCost); // token cost as lore
            potionMeta.setLore(lore);

            itemStack.setItemMeta(potionMeta);
        }
        return itemStack;
    }

    private ItemStack createEnchantedBook(int quantity, String tokenCost, Enchantment enchantment, int level) {
        ItemStack itemStack = new ItemStack(Material.ENCHANTED_BOOK, quantity);
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) itemStack.getItemMeta();
        meta.addStoredEnchant(enchantment, level, true);
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GOLD + tokenCost);
        meta.setLore(lore);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

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
                    int tokenCost = entry.getValue();
                    int quantity = clickedItem.getAmount();

                    // if players inventory is full
                    if (player.getInventory().firstEmpty() == -1) {
                        player.sendMessage(ChatColor.RED + "Your inventory is full! "
                                + "Please empty a slot before buying an item.");
                    } else {
                        if (tempPlayer.getTokens() >= tokenCost) {
                            tempPlayer.setTokens(tempPlayer.getTokens() - tokenCost);

                            // create the item to give based on the type and quantity
                            ItemStack itemToGive = shopItem.clone();

                            itemToGive.setAmount(quantity);
                            ItemMeta itemMeta = itemToGive.getItemMeta();
                            itemMeta.setDisplayName(null);
                            itemToGive.setItemMeta(itemMeta);

                            if (itemToGive.getType() == Material.TOTEM_OF_UNDYING) {
                                itemMeta.getPersistentDataContainer().set(new NamespacedKey("videogameslifeserver", "itemmeta"), PersistentDataType.STRING, "shoptotem");
                                itemToGive.setItemMeta(itemMeta);
                            }

                            player.getInventory().addItem(itemToGive);

                            player.sendMessage(ChatColor.GREEN + "You purchased " + quantity + " "
                                    + shopItem.getType().name().replace("_", " ").toLowerCase()
                                    + "(s) for " + tokenCost + " tokens!");

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
