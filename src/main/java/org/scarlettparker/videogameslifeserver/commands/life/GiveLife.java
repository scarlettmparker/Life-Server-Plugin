package org.scarlettparker.videogameslifeserver.commands.life;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.scarlettparker.videogameslifeserver.manager.ConfigManager;
import org.scarlettparker.videogameslifeserver.manager.LifeManager;
import org.scarlettparker.videogameslifeserver.utils.InstantFirework;

import java.util.HashMap;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class GiveLife implements CommandExecutor {
    HashMap<String, Boolean> canSendLife = new HashMap<>();
    HashMap<String, Player> tempSender = new HashMap<>();
    LifeManager lifeManager = new LifeManager();

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender instanceof ConsoleCommandSender) {
            System.err.println("Only players can use this command!");
            return true;
        }

        Player s = Bukkit.getPlayer(sender.getName());
        Player r;

        String[] senderData = ConfigManager.getPlayerData(sender.getName()).split(",");
        String[] receiverData = null;

        if (args.length != 1) {
            s.sendMessage(ChatColor.RED + "Incorrect arguments. Command usage: /givelife player");
            return true;
        }
        if (!Objects.equals(args[0], "confirm")) {
            try {
                r = Bukkit.getPlayer(args[0]);
                receiverData = ConfigManager.getPlayerData(r.getName()).split(",");
            } catch (Exception e) {
                s.sendMessage(ChatColor.RED + "Player is not online!");
                return true;
            }
        } else {
            if (canSendLife.containsKey(s.getName())) {
                r = tempSender.get(s.getName());
                receiverData = ConfigManager.getPlayerData(r.getName()).split(",");

                int receiverLives = Integer.parseInt(receiverData[1]);
                receiverLives += 1;

                s.sendMessage(ChatColor.GREEN + "Successfully sent a life to " + r.getDisplayName());
                s.setHealth(0.0);

                // update lives and empty lists so it can't be run again
                lifeManager.updateLives(receiverData, receiverLives);
                lifeManager.updateLives(senderData, 0);

                canSendLife.remove(s.getName());
                tempSender.remove(s.getName());

                s.playEffect(EntityEffect.TOTEM_RESURRECT);
                r.playEffect(EntityEffect.TOTEM_RESURRECT);

                return true;
            } else {
                s.sendMessage(ChatColor.RED + "Nothing to confirm!");
                return true;
            }
        }
        if (!ConfigManager.findPlayerBase()) {
            s.sendMessage(ChatColor.RED + "Config file not found! Please run /startlife lives from the console first.");
            return true;
        }
        if (sender.getName().equals(args[0])) {
            s.sendMessage(ChatColor.RED + "You can't give yourself lives!");
            return true;
        }

        // get current players lives
        int numLives = Integer.parseInt(Objects.requireNonNull(senderData[1]));

        if (numLives == 0) {
            s.sendMessage(ChatColor.RED + "You are dead. You have no lives to send.");
            return true;
        }

        if (numLives == 1) {
            s.sendMessage("Are you sure you want to give this player a life?" +
                    ChatColor.RED + " Doing so will permanently kill you.");
            s.sendMessage("[VGS Life Server] Type" + ChatColor.RED + " /givelife confirm " + ChatColor.WHITE +
                    "within the next 30 seconds to confirm that you want to give this player a life.");

            canSendLife.put(s.getName(), false);
            tempSender.put(s.getName(), r);

            Timer timer = new Timer();

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    canSendLife.remove(s.getName());
                    tempSender.remove(s.getName());
                }
            }, 30000); // remove confirm list after 30 seconds

            // remove player from map after 30 seconds
            return true;
        }

        numLives -= 1;

        lifeManager.updateLives(senderData, numLives);

        int receiverLives = Integer.parseInt(Objects.requireNonNull(receiverData[1]));

        if (receiverLives == 0) {
            // bring player back to survival and announce
            r.setGameMode(GameMode.SURVIVAL);
            r.teleport(Bukkit.getWorld("world").getSpawnLocation());
            Bukkit.broadcastMessage(r.getName() + ChatColor.BLUE + " has been revived" + ChatColor.WHITE
                    + ", and now must " +  ChatColor.RED + "kill at least 1 player per session "
                    + ChatColor.WHITE + "to remain in the game.");

            // play a firework when revived because yay
            Location location = r.getPlayer().getLocation();
            FireworkEffect fireworkEffect = FireworkEffect.builder().flicker(false).trail(true)
                    .with(FireworkEffect.Type.BALL).withColor(Color.WHITE).withFade(Color.GRAY).build();
            new InstantFirework(fireworkEffect, location);

            // and a villager celebration!
            r.getPlayer().playSound(location, Sound.ENTITY_VILLAGER_CELEBRATE, 2, 1);

            // update receiver data stuff to mark that player is a zombie
            receiverData[3] = "true";
        } else {
            r.playEffect(EntityEffect.TOTEM_RESURRECT);
        }

        receiverLives += 1;

        lifeManager.updateLives(receiverData, receiverLives);

        // player gets confirmation message
        s.sendMessage(ChatColor.GREEN + "Successfully sent a life to " + r.getDisplayName() + ChatColor.GREEN + ".");
        r.sendMessage(s.getDisplayName() + "" + ChatColor.GREEN + " has given you a life!");

        // totems cause yummy
        s.playEffect(EntityEffect.TOTEM_RESURRECT);
        return true;
    }
}
