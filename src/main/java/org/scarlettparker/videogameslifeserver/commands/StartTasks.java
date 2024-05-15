package org.scarlettparker.videogameslifeserver.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.scarlettparker.videogameslifeserver.manager.ConfigManager;
import org.scarlettparker.videogameslifeserver.tasks.Task;

import java.util.*;

import static org.scarlettparker.videogameslifeserver.manager.TaskManager.*;

public class StartTasks implements CommandExecutor {
    public Task[] tasks = {};
    public HashMap<Player, Task> playerTasks = new HashMap();

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            p.sendMessage(ChatColor.RED + "You cannot use this command as a player. Please run this command from the console.");
            return true;
        }

        if (args.length > 0) {
            System.err.println("Incorrect usage! Usage is /starttasks");
            return true;
        }

        // initialize all tasks
        tasks = generateTasks();
        distributeTasks();

        // debugging to print off tasks
        for (Map.Entry<Player, Task> entry : playerTasks.entrySet()) {
            Player player = entry.getKey();
            Task task = entry.getValue();
            System.out.println("Player: " + player.getName() + ", Task: " + task.getID() + ", Description: " + task.getDescription());
        }
        return true;
    }

    private void distributeTasks() {
        for (Player p : getAllPlayers()) {
            String playerName = p.getName();
            String[] playerData = ConfigManager.getPlayerData(playerName).split(",");
            int numLives = Integer.parseInt(Objects.requireNonNull(playerData[1]));

            Task assignedTask = null;
            if (numLives == 1) {
                List<Task> difficultyTwoTasks = filterTasksByDifficulty(tasks, 2);
                assignedTask = getRandomTask(difficultyTwoTasks);
            } else if (numLives > 1) {
                List<Task> difficultyZeroTasks = filterTasksByDifficulty(tasks, 0);
                assignedTask = getRandomTask(difficultyZeroTasks);
            }

            if (assignedTask == null) {
                System.err.println("No more tasks left! Cannot assign task to " + p.getName());
            } else {
                playerTasks.put(p, assignedTask);
                assignedTask.setAvailable(false);
            }
        }
    }

    private List<Player> getAllPlayers() {
        return new ArrayList<>(Bukkit.getOnlinePlayers());
    }
}
