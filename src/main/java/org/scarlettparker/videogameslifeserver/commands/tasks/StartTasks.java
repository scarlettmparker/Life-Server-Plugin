package org.scarlettparker.videogameslifeserver.commands.tasks;

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
import static org.scarlettparker.videogameslifeserver.manager.PunishmentManager.generatePunishments;
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

        if (!jsonFileExists(playerFile)) {
            sender.sendMessage(ChatColor.RED
                    + "Player file not yet initialized. Make sure to run /startlife and then /starttasks.");
            return true;
        }

        if (args.length > 0 && !Objects.equals(args[0], "reset")) {
            sender.sendMessage(ChatColor.RED + "Invalid arguments. Correct usage: /starttasks [reset]");
            return true;
        }

        if ((args.length > 0 && Objects.equals(args[0], "reset"))
                || !jsonFileExists(taskFile) || !jsonFileExists(punishFile)) {
            ConfigManager.createJsonFile(taskFile);
            generateTasks();
            ConfigManager.createJsonFile(punishFile);
            generatePunishments();

            // get all players
            JsonObject allPlayers = returnAllObjects(playerFile);
            ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
            for (String key : allPlayers.keySet()) {
                JsonElement element = allPlayers.get(key);
                if (element.isJsonObject()) {
                    JsonObject jsonObject = element.getAsJsonObject();
                    // get player by name
                    if (jsonObject.has("name")) {
                        String name = jsonObject.get("name").getAsString();
                        TPlayer tempPlayer = new TPlayer(name);

                        // reset player and task stuff
                        tempPlayer.setCurrentTask("-1");
                        tempPlayer.setTokens(0);
                        tempPlayer.setTasks(new String[0]);

                        // clear punishments in case they have one
                        if (tempPlayer.getPunishments().length != 0) {
                            Bukkit.dispatchCommand(console, "clearpunishments " + tempPlayer.getName());
                        }
                    }
                }
            }

            sender.sendMessage(ChatColor.GREEN
                    + "Successfully created task file. Please run the command again with no arguments to distribute tasks.");
            return true;
        }

        handleNewSession();
        sender.sendMessage("Assigning tasks to " + getAllPlayers().size() + " player(s)...");

        // distribute both normal and red tasks
        doTaskDistribution(getAllPlayers(), 0);

        sender.sendMessage(ChatColor.GREEN +
                "Successfully assigned tasks to all players. Players will receive their books shortly.");
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
                            tempPlayer.setSessionTasks(0);
                        } else {
                            tempPlayer.setSessionTasks(0);
                            Task tempTask = new Task(tempPlayer.getCurrentTask());

                            // create temporary task and set player description back so it displays correctly please
                            if (!Objects.equals(tempTask.getName(), "-1")
                                    && tempTask.getDifficulty() != 2) {
                                Bukkit.dispatchCommand(console, "failtask " + tempPlayer.getName() + " dbg");
                                tempPlayer.setNextTask("-1");
                            }
                        }
                    }
                }
            }
        }
    }
}
