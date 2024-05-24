package org.scarlettparker.videogameslifeserver.commands.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.scarlettparker.videogameslifeserver.manager.ConfigManager;
import org.scarlettparker.videogameslifeserver.objects.Death;
import org.scarlettparker.videogameslifeserver.objects.TPlayer;
import static org.scarlettparker.videogameslifeserver.manager.ConfigManager.*;
import static org.scarlettparker.videogameslifeserver.utils.WorldUtils.getAllPlayers;
import static org.scarlettparker.videogameslifeserver.utils.WorldUtils.setPlayerName;

public class StartLife implements CommandExecutor {
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        // must be an operator to use the command
        if (sender instanceof Player && !sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "You must be an operator to run this command.");
            return true;
        }

        sender.sendMessage("3 lives being assigned to each player...");
        ConfigManager.createJsonFile(playerFile);

        // create players
        for (Player p : getAllPlayers()) {
            createPlayer(p);
        }

        sender.sendMessage("All done :3");
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
}
