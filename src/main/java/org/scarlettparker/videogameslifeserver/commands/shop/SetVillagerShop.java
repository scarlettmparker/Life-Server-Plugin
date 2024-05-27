package org.scarlettparker.videogameslifeserver.commands.shop;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;

public class SetVillagerShop implements CommandExecutor {
    static Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("VideoGamesLifeServer");
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You must be a player to run this command.");
            return true;
        }

        if (!sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "You must be an operator to run this command.");
            return true;
        }

        Player player = (Player) sender;

        // ensure the player is looking at a villager
        if (player.getTargetEntity(5) instanceof Villager) {
            Villager villager = (Villager) player.getTargetEntity(5);
            // set the metadata
            NamespacedKey key = new NamespacedKey(plugin, "shop_villager");
            villager.getPersistentDataContainer().set(key, PersistentDataType.STRING, "forum_marketplace");
            player.sendMessage(ChatColor.GREEN + "Villager is now a shop villager.");
            return true;
        } else {
            player.sendMessage(ChatColor.RED + "You are not looking at a villager.");
            return true;
        }
    }
}
