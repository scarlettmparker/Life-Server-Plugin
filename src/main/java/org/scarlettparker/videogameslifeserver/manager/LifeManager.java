package org.scarlettparker.videogameslifeserver.manager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Objects;

public class LifeManager {

    public void updateLives(String[] playerData, int numLives) {
        // update the new player life value
        playerData[1] = String.valueOf(numLives);
        ConfigManager.writeToPlayerBase(playerData[0], playerData);

        // update display name
        setPlayerName(Bukkit.getPlayer(playerData[0]));
    }

    public void setPlayerName(Player p) {
        try {
            // get number of lives from the config file
            String[] playerData = ConfigManager.getPlayerData(p.getName()).split(",");
            int numLives = Integer.parseInt(Objects.requireNonNull(playerData[1]));
            ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();

            String newName;
            String command = "";

            // formatting to look lovely
            if (numLives == 0) {
                newName = ChatColor.GRAY + p.getName() + " [DEAD]";
                p.setDisplayName(newName);
                p.setPlayerListName(newName);
                p.setCustomName(newName);
                command = "nte player " + p.getName() + " prefix &7";
            } else if (numLives == 1) {
                newName = ChatColor.RED + p.getName()
                        + ChatColor.WHITE + " [" + ChatColor.RED + numLives
                        + ChatColor.WHITE + "]";
                p.setDisplayName(newName);
                p.setPlayerListName(newName);
                p.setCustomName(newName);
                command = "nte player " + p.getName() + " prefix &c";
            } else if (numLives == 2) {
                newName = ChatColor.YELLOW + p.getName()
                        + ChatColor.WHITE + " [" + ChatColor.YELLOW + numLives
                        + ChatColor.WHITE + "]";
                p.setDisplayName(newName);
                p.setPlayerListName(newName);
                p.setCustomName(newName);
                command = "nte player " + p.getName() + " prefix &e";
            } else if (numLives >= 3) {
                newName = ChatColor.GREEN + p.getName()
                        + ChatColor.WHITE + " [" + ChatColor.GREEN + numLives
                        + ChatColor.WHITE + "]";
                p.setDisplayName(newName);
                p.setPlayerListName(newName);
                p.setCustomName(newName);
                setPlayerNameTag(p, ChatColor.RED + p.getName() + "aaaahh");
                command = "nte player " + p.getName() + " prefix &a";
            }
            // custom name AND tag name now displays
            p.setCustomNameVisible(true);
            Bukkit.dispatchCommand(console, command);
        } catch (Exception e) {
            // do a whole lot of nothing cause player doesnt exist
        }
    }

    public static void setPlayerNameTag(Player player, String name) {
        try {
            Method getHandle = player.getClass().getMethod("getHandle");
            Object entityPlayer = getHandle.invoke(player);
            boolean gameProfileExists = false;
            try {
                Class.forName("net.minecraft.util.com.mojang.authlib.GameProfile");
                gameProfileExists = true;
            } catch (ClassNotFoundException ignored) {

            }
            try {
                Class.forName("com.mojang.authlib.GameProfile");
                gameProfileExists = true;
            } catch (ClassNotFoundException ignored) {

            }
            if (!gameProfileExists) {
                Field nameField = entityPlayer.getClass().getSuperclass().getDeclaredField("name");
                nameField.setAccessible(true);
                nameField.set(entityPlayer, name);
            } else {
                Object profile = entityPlayer.getClass().getMethod("getProfile").invoke(entityPlayer);
                Field ff = profile.getClass().getDeclaredField("name");
                ff.setAccessible(true);
                ff.set(profile, name);
            }
            if (Bukkit.class.getMethod("getOnlinePlayers", new Class<?>[0]).getReturnType() == Collection.class) {
                Collection<? extends Player> players = (Collection<? extends Player>) Bukkit.class.getMethod("getOnlinePlayers").invoke(null);
                for (Player p : players) {
                    p.hidePlayer(player);
                    p.showPlayer(player);
                }
            } else {
                Player[] players = ((Player[]) Bukkit.class.getMethod("getOnlinePlayers").invoke(null));
                for (Player p : players) {
                    p.hidePlayer(player);
                    p.showPlayer(player);
                }
            }
        } catch (Exception e) {
        }
    }
}
