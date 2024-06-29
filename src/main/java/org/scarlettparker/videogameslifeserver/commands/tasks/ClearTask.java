package org.scarlettparker.videogameslifeserver.commands.tasks;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.scarlettparker.videogameslifeserver.objects.TPlayer;

import java.util.Objects;

import static org.scarlettparker.videogameslifeserver.manager.ConfigManager.*;
import static org.scarlettparker.videogameslifeserver.utils.WorldUtils.removeBook;

public class ClearTask implements CommandExecutor {
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        // must be an operator to use the command
        if (sender instanceof Player && !sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "You must be an operator to run this command.");
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Incorrect usage. Correct usage: /cleartask player");
            return true;
        }

        // iterate through all players and set session tasks to 0
        if (Objects.equals(args[0], "all")) {
            JsonObject allPlayers = returnAllObjects(playerFile);
            for (String key : allPlayers.keySet()) {
                JsonElement element = allPlayers.get(key);
                if (element.isJsonObject()) {
                    JsonObject jsonObject = element.getAsJsonObject();
                    if (jsonObject.has("name")) {
                        String name = jsonObject.get("name").getAsString();
                        TPlayer tempPlayer = new TPlayer(name);

                        // ensure player exists to remove book
                        if (Bukkit.getPlayer(name) != null) {
                            removeBook(Objects.requireNonNull(Bukkit.getPlayer(name)));
                        }

                        if (!Objects.equals(tempPlayer.getCurrentTask(), "-1")) {
                            // clear current task and set session tasks to 0 so they may attempt more
                            sender.sendMessage("Successfully cleared task " + ChatColor.YELLOW + tempPlayer.getCurrentTask()
                                    + ChatColor.WHITE + " from player " + ChatColor.YELLOW + tempPlayer.getName()
                                    + ChatColor.WHITE + ".");
                            tempPlayer.setCurrentTask("-1");
                        }
                        tempPlayer.setSessionTasks(0);
                    }
                }
            }
            return true;
        }

        // for cleanliness
        Player player = Bukkit.getPlayer(args[0]);

        if (!jsonFileExists(playerFile)) {
            sender.sendMessage(ChatColor.RED
                    + "Player file not yet initialized. Make sure to run /startlife and then /starttasks.");
            return true;
        }

        if (!playerExists(args[0]) || player == null) {
            sender.sendMessage(ChatColor.RED + "Specified player is not online.");
            return true;
        }

        TPlayer tempPlayer = new TPlayer(player.getName());
        removeBook(player);
        tempPlayer.setCurrentTask("-1");

        sender.sendMessage(ChatColor.GREEN + "Successfully cleared " + tempPlayer.getName() + "'s task.");
        Bukkit.getPlayer(tempPlayer.getName()).sendMessage(ChatColor.GREEN + "Your task has been cleared.");
        return true;
    }
}
