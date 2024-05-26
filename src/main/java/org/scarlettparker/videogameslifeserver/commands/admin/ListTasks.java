package org.scarlettparker.videogameslifeserver.commands.admin;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.scarlettparker.videogameslifeserver.objects.Task;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import static org.scarlettparker.videogameslifeserver.manager.ConfigManager.*;

public class ListTasks implements CommandExecutor {
    private final Gson gson = new Gson();

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender instanceof Player && !sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "You must be an operator to run this command.");
            return true;
        }

        boolean showAll = args.length == 0;
        boolean showExcluded = args.length == 1 && Objects.equals(args[0], "excluded");
        boolean showAllowed = args.length == 1 && Objects.equals(args[0], "allowed");

        if (!showAll && !showExcluded && !showAllowed) {
            sender.sendMessage(ChatColor.RED + "Incorrect usage. Correct usage: /listtasks [allowed|excluded]");
            return true;
        }

        BaseComponent[] messageComponents = generateMessageComponents(showAll, showExcluded, showAllowed);

        ((Player) sender).spigot().sendMessage(messageComponents);
        return true;
    }

    private BaseComponent[] generateMessageComponents(boolean showAll, boolean showExcluded, boolean showAllowed) {
        String listMessage = "";
        if (showExcluded) {
            listMessage = "List of excluded tasks: ";
        } else if (showAllowed) {
            listMessage = "List of non-excluded tasks: ";
        } else {
            listMessage = "List of tasks: ";
        }

        BaseComponent[] messageComponents = TextComponent.fromLegacyText(listMessage);

        JsonObject taskJson = returnAllObjects(taskFile);
        if (taskJson != null && !taskJson.entrySet().isEmpty()) {
            Map<String, Task> sortedTasks = sortTasksAlphabetically(taskJson);
            for (Map.Entry<String, Task> entry : sortedTasks.entrySet()) {
                Task task = entry.getValue();
                if ((showAll) || (showExcluded && task.getExcluded()) || (showAllowed && !task.getExcluded())) {
                    TextComponent taskComponent = createTaskComponent(entry.getKey(), task);
                    messageComponents = ArrayUtils.addAll(messageComponents, taskComponent);
                    messageComponents = ArrayUtils.addAll(messageComponents, TextComponent.fromLegacyText(", "));
                }
            }
        } else {
            BaseComponent[] noTasksMessage = TextComponent.fromLegacyText(org.bukkit.ChatColor.YELLOW + "No tasks found.");
            messageComponents = ArrayUtils.addAll(messageComponents, noTasksMessage);
        }

        return messageComponents;
    }

    private Map<String, Task> sortTasksAlphabetically(JsonObject taskJson) {
        Map<String, Task> sortedTasks = new TreeMap<>(new TaskNameComparator());
        for (Map.Entry<String, JsonElement> entry : taskJson.entrySet()) {
            sortedTasks.put(entry.getKey(), gson.fromJson(entry.getValue(), Task.class));
        }
        return sortedTasks;
    }

    private TextComponent createTaskComponent(String taskName, Task task) {
        ChatColor taskColor = determineTaskColor(task);
        TextComponent taskComponent = new TextComponent(taskName);
        taskComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder(task.getDescription()).color(org.bukkit.ChatColor.WHITE.asBungee()).create()));
        taskComponent.setColor(taskColor.asBungee());
        return taskComponent;
    }

    private ChatColor determineTaskColor(Task task) {
        int difficulty = task.getDifficulty();
        if (difficulty == 0) {
            return ChatColor.GREEN;
        } else if (difficulty == 1) {
            return ChatColor.YELLOW;
        } else if (difficulty == 2){
            return ChatColor.RED;
        } else {
            return ChatColor.DARK_AQUA;
        }
    }

    class TaskNameComparator implements Comparator<String> {
        @Override
        public int compare(String s1, String s2) {
            boolean isNumeric1 = s1.matches("\\d+");
            boolean isNumeric2 = s2.matches("\\d+");

            if (isNumeric1 && isNumeric2) {
                return Integer.compare(Integer.parseInt(s1), Integer.parseInt(s2));
            } else if (isNumeric1) {
                return -1;
            } else if (isNumeric2) {
                return 1;
            } else {
                return s1.compareToIgnoreCase(s2);
            }
        }
    }
}
