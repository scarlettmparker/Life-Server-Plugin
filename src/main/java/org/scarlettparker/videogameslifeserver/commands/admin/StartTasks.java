package org.scarlettparker.videogameslifeserver.commands.admin;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.scarlettparker.videogameslifeserver.manager.ConfigManager;
import org.scarlettparker.videogameslifeserver.objects.TPlayer;
import org.scarlettparker.videogameslifeserver.objects.Task;

import java.util.Objects;

import static org.scarlettparker.videogameslifeserver.manager.ConfigManager.*;
import static org.scarlettparker.videogameslifeserver.manager.TaskManager.doTaskDistribution;
import static org.scarlettparker.videogameslifeserver.manager.TaskManager.generateTasks;
import static org.scarlettparker.videogameslifeserver.utils.WorldUtils.getAllPlayers;

public class StartTasks implements CommandExecutor {
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        // must be an operator to use the command
        if (sender instanceof Player && !sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "You must be an operator to run this command.");
            return true;
        }

        handleNewSession();

        Bukkit.getLogger().info("Creating and assigning tasks to " + getAllPlayers().size() + " players...");
        ConfigManager.createJsonFile(taskFile);

        generateTasks();
        
        // distribute both normal and red tasks
        doTaskDistribution(getAllPlayers(), 0);

        return true;
    }


    private static void handleNewSession() {
        JsonObject allPlayers = returnAllObjects(playerFile);
        ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();

        // iterate through all players and set session tasks to 0
        if (allPlayers != null) {
            for (String key : allPlayers.keySet()) {
                JsonElement element = allPlayers.get(key);
                if (element.isJsonObject()) {
                    JsonObject jsonObject = element.getAsJsonObject();
                    if (jsonObject.has("name")) {
                        String name = jsonObject.get("name").getAsString();
                        TPlayer tempPlayer = new TPlayer(name);
                        if (Bukkit.getPlayer(name) == null) {
                            tempPlayer.setSessionTasks(-1);
                        } else {
                            tempPlayer.setSessionTasks(0);

                            // create temporary task and set player description back so it displays correctly please
                            if (!Objects.equals(tempPlayer.getCurrentTask(), "-1") && tempPlayer.getLives() > 1) {
                                Task tempTask = new Task(tempPlayer.getCurrentTask());
                                tempTask.setPlayerDescription(Objects.requireNonNull(getJsonObjectAttribute(taskFile,
                                        tempPlayer.getCurrentTask(), "playerDescription")).toString());
                                Bukkit.dispatchCommand(console, "failtask " + tempPlayer.getName() + " dbg");
                            }
                        }
                    }
                }
            }
        }
    }
}