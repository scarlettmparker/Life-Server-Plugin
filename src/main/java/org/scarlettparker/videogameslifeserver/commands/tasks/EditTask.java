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

import static org.scarlettparker.videogameslifeserver.manager.ConfigManager.jsonFileExists;
import static org.scarlettparker.videogameslifeserver.manager.ConfigManager.taskFile;

public class EditTask implements CommandExecutor, Listener {
    private final JavaPlugin plugin;
    private final Map<Player, TaskCreationState> taskCreationStateMap = new HashMap<>();

    public EditTask(JavaPlugin plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    private enum TaskCreationStep {
        ENTER_ID,
        ENTER_DESCRIPTION,
        ENTER_DIFFICULTY,
        ENTER_TOKENS,
        COMPLETED
    }

    private static class TaskCreationState {
        private TaskCreationStep step;
        private String taskId;
        private String description;
        private int difficulty;
        private int tokens;

        public TaskCreationState() {
            this.step = TaskCreationStep.ENTER_ID;
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
            return true;
        }

        if (!player.isOp()) {
            player.sendMessage(ChatColor.RED + "You must be an operator to run this command.");
            return true;
        }

        taskCreationStateMap.put(player, new TaskCreationState());
        player.sendMessage(ChatColor.DARK_AQUA + "Enter the task name to edit (type 'cancel' to cancel):");
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
            player.sendMessage(ChatColor.RED + "Editing task cancelled.");
            return;
        }

        if (message.contains(":")) {
            player.sendMessage(ChatColor.RED + "No task attributes can contain colons. Please try again without using colons.");
            return;
        }

        switch (state.step) {
            case ENTER_ID:
                if (jsonFileExists(taskFile) && Objects.equals(new Task(message).getDescription(), "")) {
                    player.sendMessage(ChatColor.RED + "Task with specified name does not exist. Please enter a valid task name.");
                } else {
                    state.taskId = message;
                    state.step = TaskCreationStep.ENTER_DESCRIPTION;
                    player.sendMessage(ChatColor.GREEN + "Name: " + message);
                    player.sendMessage(ChatColor.DARK_AQUA + "Enter the new task description (type 'cancel' to cancel, 'none' to leave unchanged):");
                    player.sendMessage( "Current description: " + ChatColor.DARK_AQUA + (new Task(message).getDescription()));
                }
                break;
            case ENTER_DESCRIPTION:
                if (message.equalsIgnoreCase("none")) {
                    state.description = null; // indicate that the description should remain unchanged
                    player.sendMessage(ChatColor.YELLOW + "Description left unchanged.");
                } else {
                    state.description = message;
                    player.sendMessage(ChatColor.GREEN + "Description: " + message);
                }
                state.step = TaskCreationStep.ENTER_DIFFICULTY;
                player.sendMessage(ChatColor.DARK_AQUA + "Enter the new task difficulty (0, 1, 2, 3 or normal, hard, red, special) (type 'cancel' to cancel, 'none' to leave unchanged):");
                break;
            case ENTER_DIFFICULTY:
                int difficulty;
                if (message.equalsIgnoreCase("none")) {
                    state.difficulty = -1; // indicate that the description should remain unchanged
                    player.sendMessage(ChatColor.YELLOW + "Description left unchanged.");
                } else {
                    switch (message.toLowerCase()) {
                        case "0", "normal" -> difficulty = 0;
                        case "1", "hard" -> difficulty = 1;
                        case "2", "red" -> difficulty = 2;
                        case "3", "special" -> difficulty = 3;
                        default -> {
                            player.sendMessage(ChatColor.RED + "Invalid difficulty. Valid difficulties: 0 (normal), 1 (hard), 2 (red), or 3 (special).");
                            return;
                        }
                    }
                    state.difficulty = difficulty;
                    player.sendMessage(ChatColor.GREEN + "Difficulty: " + message);
                }
                state.step = TaskCreationStep.ENTER_TOKENS;
                player.sendMessage(ChatColor.DARK_AQUA + "Enter the new token reward (type 'cancel' to cancel, 'none' to leave unchanged):");
                break;
            case ENTER_TOKENS:
                if (message.equalsIgnoreCase("none")) {
                    state.tokens = -1; // indicate that the tokens should remain unchanged
                    player.sendMessage(ChatColor.YELLOW + "Token reward left unchanged.");
                } else {
                    int tokens;
                    try {
                        tokens = Integer.parseInt(message);
                        state.tokens = tokens;
                    } catch (Exception e) {
                        player.sendMessage(ChatColor.RED + "Please enter a valid integer for number of tokens.");
                        return;
                    }

                    if (tokens == 0) {
                        if (state.difficulty == 0) {
                            state.tokens = 6;
                        } else if (state.difficulty == 1) {
                            state.tokens = 15;
                        } else if (state.difficulty == 2) {
                            state.tokens = 3;
                        } else if (state.difficulty == 3) {
                            state.tokens = 11;
                        }
                    }

                    if (state.tokens < 0) {
                        player.sendMessage(ChatColor.RED + "Token reward cannot be below 1.");
                        return;
                    }

                    player.sendMessage(ChatColor.GREEN + "Token reward: " + state.tokens);
                }

                state.step = TaskCreationStep.COMPLETED;

                Task editedTask = new Task(state.taskId);

                // edit attributes of current task
                if (state.description != null) {
                    editedTask.setDescription(state.description);
                }

                if (state.difficulty != -1) {
                    editedTask.setDifficulty(state.difficulty);
                }

                if (state.tokens != -1) {
                    editedTask.setReward(state.tokens);
                }

                if (state.description == null && state.difficulty == -1 && state.tokens == -1) {
                    player.sendMessage(ChatColor.YELLOW + "No changes made to task.");
                } else {
                    player.sendMessage(ChatColor.GREEN + "Task successfully edited.");
                }
                taskCreationStateMap.remove(player);
                break;
            default:
                break;
        }
    }
}
