package org.scarlettparker.videogameslifeserver.commands.life;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.scarlettparker.videogameslifeserver.objects.TPlayer;

import java.util.Objects;

import static org.scarlettparker.videogameslifeserver.manager.ConfigManager.playerExists;
import static org.scarlettparker.videogameslifeserver.utils.WorldUtils.handleRevive;

public class GiveLife implements CommandExecutor{

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        Player receiver = Bukkit.getPlayer(args[0]);

        // for cleanliness
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You must be a player to run this command.");
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Incorrect usage. Correct usage: /givelife player");
            return true;
        }

        if (!playerExists(args[0]) || receiver == null) {
            sender.sendMessage(ChatColor.RED + "Specified player does not exist/is not online.");
            return true;
        } else {
            if (Objects.equals(receiver, sender)) {
                sender.sendMessage(ChatColor.RED + "You cannot give yourself lives.");
                return true;
            }
        }

        // sender and receiver
        TPlayer sPlayer = new TPlayer(sender.getName());
        TPlayer rPlayer = new TPlayer(receiver.getName());

        if (sPlayer.getLives() < 1) {
            sender.sendMessage(ChatColor.RED + "You are dead. You cannot give lives.");
            return true;
        }

        if (rPlayer.isZombie()) {
            sender.sendMessage(ChatColor.RED + "You may not send lives to previously revived players.");
            return true;
        }

        if (sPlayer.getLives() == 1 && (args.length != 2 || !Objects.equals(args[1], "confirm"))) {
            sender.sendMessage("Are you sure you want to give this player a life?" +
                    ChatColor.RED + " Doing so will permanently kill you.");
            sender.sendMessage("Type" + ChatColor.RED + " /givelife [name] confirm "
                    + ChatColor.WHITE + "to give the receiver a life.");
            return true;
        }

        int receiverLives = rPlayer.getLives();
        int senderLives = sPlayer.getLives();

        receiverLives += 1;
        senderLives -= 1;

        if (receiverLives > 4) {
            sender.sendMessage(ChatColor.RED + "Players are not allowed to have more than 4 lives at any given time.");
            return true;
        }

        if (senderLives == 0) {
            ((Player) sender).setHealth(0);
        }

        // player has been revived (mathematically obviously)
        if (receiverLives == 1) {
            handleRevive(receiver);
            rPlayer.setZombie(true);
        }

        rPlayer.setLives(receiverLives);
        sPlayer.setLives(senderLives);

        // player gets confirmation message
        sender.sendMessage(ChatColor.GREEN + "Successfully sent a life to "
                + receiver.getName() + ".");
        receiver.sendMessage( ChatColor.GREEN + sender.getName() + " has given you a life!");

        // awesome effect moment
        receiver.playEffect(EntityEffect.TOTEM_RESURRECT);
        ((Player) sender).playEffect(EntityEffect.TOTEM_RESURRECT);

        return true;
    }
}
