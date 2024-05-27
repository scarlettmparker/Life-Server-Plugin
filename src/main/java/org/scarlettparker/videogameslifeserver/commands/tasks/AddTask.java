package org.scarlettparker.videogameslifeserver.commands.tasks;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.scarlettparker.videogameslifeserver.objects.Task;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.scarlettparker.videogameslifeserver.manager.ConfigManager.*;
import static org.scarlettparker.videogameslifeserver.manager.TaskManager.convertTaskToJson;

public class AddTask implements CommandExecutor, Listener {
    private final JavaPlugin plugin;
    private final Map<Player, TaskCreationState> taskCreationStateMap = new HashMap<>();

    public AddTask(JavaPlugin plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    private enum TaskCreationStep {
        ENTER_ID,
        ENTER_DESCRIPTION,
        ENTER_DIFFICULTY,
        COMPLETED
    }

    private static class TaskCreationState {
        private TaskCreationStep step;
        private String taskId;
        private String description;
        private int difficulty;

        public TaskCreationState() {
            this.step = TaskCreationStep.ENTER_ID;
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;
        if (!player.isOp()) {
            player.sendMessage(ChatColor.RED + "You must be an operator to run this command.");
            return true;
        }

        taskCreationStateMap.put(player, new TaskCreationState());
        player.sendMessage(ChatColor.DARK_AQUA + "Enter the new task name (type 'cancel' to cancel):");
        return true;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (!taskCreationStateMap.containsKey(player)) {
            return;
        }

        event.setCancelled(true);
        TaskCreationState state = taskCreationStateMap.get(player);
        String message = event.getMessage();

        if (message.equalsIgnoreCase("cancel")) {
            taskCreationStateMap.remove(player);
            player.sendMessage(ChatColor.RED + "Task creation cancelled.");
            return;
        }

        // to prevent the file from dying
        if (message.contains(":")) {
            player.sendMessage(ChatColor.RED + "Colons are not allowed in task creation. Please try again without using colons.");
            return;
        }

        switch (state.step) {
            case ENTER_ID:
                // this is what we call being lazy
                if (message.indexOf(' ') >= 0) {
                    player.sendMessage(ChatColor.RED + "Task names may not contain spaces.");
                } else if (jsonFileExists(taskFile) && !Objects.equals(new Task(message).getDescription(), "")) {
                    player.sendMessage(ChatColor.RED + "Task with chosen name already exists. Choose a different name.");
                } else {
                    state.taskId = message;
                    state.step = TaskCreationStep.ENTER_DESCRIPTION;
                    player.sendMessage(ChatColor.GREEN + "Name: " + message);
                    player.sendMessage(ChatColor.DARK_AQUA + "Enter the task description (type 'cancel' to cancel):");
                }
                break;
            case ENTER_DESCRIPTION:
                state.description = message;
                state.step = TaskCreationStep.ENTER_DIFFICULTY;
                player.sendMessage(ChatColor.GREEN + "Description: " + message);
                player.sendMessage(ChatColor.DARK_AQUA + "Enter the task difficulty (0, 1, 2 or normal, hard, red) (type 'cancel' to cancel):");
                break;
            case ENTER_DIFFICULTY:
                int difficulty;
                switch (message.toLowerCase()) {
                    case "0":
                    case "normal":
                        difficulty = 0;
                        break;
                    case "1":
                    case "hard":
                        difficulty = 1;
                        break;
                    case "2":
                    case "red":
                        difficulty = 2;
                        break;
                    default:
                        player.sendMessage(ChatColor.RED + "Invalid difficulty. Valid difficulties: 0 (normal), 1 (hard), 2 (red).");
                        return;
                }
                state.difficulty = difficulty;
                state.step = TaskCreationStep.COMPLETED;

                Task newTask = new Task(state.taskId);

                // add json task to file
                String json = convertTaskToJson(newTask);
                addJsonObject(taskFile, json);

                // set task attributes
                newTask.setDescription(state.description);
                newTask.setPlayerDescription(state.description);
                newTask.setAvailable(true);
                newTask.setCompleted(false);
                newTask.setExcluded(false);
                newTask.setDifficulty(state.difficulty);

                player.sendMessage(ChatColor.GREEN + "Difficulty: " + difficulty);
                player.sendMessage(ChatColor.GREEN + "Task successfully created. This task can be assigned with: " +
                        "/settask player " + state.taskId + ". It may also be randomly assigned unless excluded.");
                taskCreationStateMap.remove(player);
                break;
            default:
                break;
        }
    }
}