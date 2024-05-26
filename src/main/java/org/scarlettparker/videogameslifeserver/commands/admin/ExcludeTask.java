package org.scarlettparker.videogameslifeserver.commands.admin;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.scarlettparker.videogameslifeserver.objects.Task;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.Objects;

import static org.scarlettparker.videogameslifeserver.manager.ConfigManager.*;

public class ExcludeTask implements CommandExecutor {
    private final Gson gson = new Gson();

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        // must be an operator to use the command
        if (sender instanceof Player && !sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "You must be an operator to run this command.");
            return true;
        }

        boolean excluded = args.length == 1 && Objects.equals(args[0], "excluded");

        if (!excluded && args.length == 1) {
            sender.sendMessage(ChatColor.RED + "Incorrect usage. Correct usage: /listtasks [excluded]");
            return true;
        }

        if (!jsonFileExists(taskFile)) {
            sender.sendMessage(ChatColor.RED + "Task file not yet initialized. Make sure to run /startlife and then /starttasks.");
            return true;
        }

        BaseComponent[] messageComponents = TextComponent.fromLegacyText(org.bukkit.ChatColor.GREEN + "List of task IDs: ");

        // retrieve all objects from task file
        JsonObject taskJson = returnAllObjects(taskFile);
        if (taskJson != null && !taskJson.entrySet().isEmpty()) {
            for (String taskName : taskJson.keySet()) {
                // get the task object from json
                Task task = gson.fromJson(taskJson.get(taskName), Task.class);
                if (excluded == task.getExcluded()) {
                    // create a text component for the task name
                    TextComponent taskComponent = new TextComponent(taskName + ", ");
                    taskComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            new ComponentBuilder(task.getDescription()).color(org.bukkit.ChatColor.WHITE.asBungee()).create()));
                    taskComponent.setColor(net.md_5.bungee.api.ChatColor.YELLOW);
                    messageComponents = ArrayUtils.addAll(messageComponents, taskComponent);
                }
            }
        } else {
            BaseComponent[] noTasksMessage = TextComponent.fromLegacyText(org.bukkit.ChatColor.YELLOW + "No tasks found.");
            messageComponents = ArrayUtils.addAll(messageComponents, noTasksMessage);
        }

        ((Player) sender).spigot().sendMessage(messageComponents);
        return true;
    }
}