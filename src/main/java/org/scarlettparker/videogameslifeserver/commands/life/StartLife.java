package org.scarlettparker.videogameslifeserver.commands.life;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.scarlettparker.videogameslifeserver.manager.ConfigManager;
import org.scarlettparker.videogameslifeserver.objects.Death;
import org.scarlettparker.videogameslifeserver.objects.TPlayer;

import java.util.Objects;

import static org.scarlettparker.videogameslifeserver.manager.ConfigManager.*;
import static org.scarlettparker.videogameslifeserver.utils.FragilityListener.unregisterFragility;
import static org.scarlettparker.videogameslifeserver.utils.KnockbackListener.unregisterKnockback;
import static org.scarlettparker.videogameslifeserver.utils.WorldUtils.getAllPlayers;
import static org.scarlettparker.videogameslifeserver.utils.WorldUtils.setPlayerName;

public class StartLife implements CommandExecutor {
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        // must be an operator to use the command
        if (sender instanceof Player && !sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "You must be an operator to run this command.");
            return true;
        }

        // just some verification stuff in case
        if (jsonFileExists(playerFile) && (args.length != 1 || !Objects.equals(args[0], "confirm"))) {
            sender.sendMessage(ChatColor.RED + "Player file already exists. Please type /startlife confirm "
            + "to confirm you want to reset.");
            return true;
        }

        sender.sendMessage("3 lives being assigned to " + getAllPlayers().size() + " player(s)...");
        ConfigManager.createJsonFile(playerFile);

        setGameRules();
        sender.sendMessage("Game rules successfully initialized.");

        // create players and clear any effects just in case
        for (Player p : getAllPlayers()) {
            createPlayer(p);
            unregisterFragility(p);
            unregisterKnockback(p);
            p.setMaxHealth(20.0);
        }

        sender.sendMessage(ChatColor.GREEN + "Lives successfully assigned to all players. "
                + "New players will automatically be assigned lives.");
        return true;
    }

    // helper function that can be reused
    public static void createPlayer(Player p) {
        TPlayer tempPlayer = new TPlayer(p.getName());

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json;
        try {
            json = ow.writeValueAsString(tempPlayer);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        // add json player before other information
        addJsonObject(playerFile, json);

        // set attributes for player
        tempPlayer.setLives(3);
        tempPlayer.setDeaths(new Death[]{});
        tempPlayer.setSessionTasks(0);
        tempPlayer.setTokens(0);
        tempPlayer.setZombie(false);
        tempPlayer.setShopping(false);

        // display the name correctly
        setPlayerName(p, tempPlayer.getLives());
    }

    private void setGameRules() {
        ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();

        // set the game rules for the server to run properly
        Bukkit.dispatchCommand(console, "gamerule logAdminCommands false");
        Bukkit.dispatchCommand(console, "gamerule sendCommandFeedback false");
        Bukkit.dispatchCommand(console, "gamerule keepInventory true");
        Bukkit.dispatchCommand(console, "gamerule showDeathMessages true");
        Bukkit.dispatchCommand(console, "difficulty normal");
    }
}
