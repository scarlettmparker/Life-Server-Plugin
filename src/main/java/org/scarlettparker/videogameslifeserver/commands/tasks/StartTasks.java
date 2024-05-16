package org.scarlettparker.videogameslifeserver.commands.tasks;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.NotNull;
import org.scarlettparker.videogameslifeserver.manager.ConfigManager;
import org.scarlettparker.videogameslifeserver.tasks.Task;

import java.util.*;

import static org.scarlettparker.videogameslifeserver.manager.TaskManager.*;

public class StartTasks implements CommandExecutor {
    private static Task[] tasks = {};
    public static HashMap<Player, Task> playerTasks = new HashMap();

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        // just in case it has already been set up
        playerTasks.clear();

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

        ItemStack book = new ItemStack(Material.WRITTEN_BOOK, 1);

        // debugging to print off tasks
        int playerCount = 0;
        for (Map.Entry<Player, Task> entry : playerTasks.entrySet()) {

            Player player = entry.getKey();
            Task task = entry.getValue();

            setBookMeta(book, task);

            // check if inventory is empty
            if (player.getInventory().firstEmpty() == -1) {
                // drop book and tell user their task just in case
                player.getLocation().getWorld().dropItemNaturally(player.getLocation(), book);
                player.sendMessage(ChatColor.RED + "Your inventory is full! A book has been dropped with your task.");
                player.performCommand("whattask");
            } else {
                player.getInventory().addItem(book);
            }
            playerCount += 1;
        }

        System.out.println("Successfully given tasks to " + playerCount + " players.");
        return true;
    }

    private void distributeTasks() {
        for (Player p : getAllPlayers()) {
            String playerName = p.getName();
            String[] playerData = ConfigManager.getPlayerData(playerName).split(",");
            int numLives = Integer.parseInt(Objects.requireNonNull(playerData[1]));

            Task assignedTask = null;

            if (numLives == 1) {
                // assign task based on difficulty and update player
                List<Task> task = filterTasksByDifficulty(tasks, 2);
                assignedTask = getRandomTask(task);
                playerData[3] = "2";
            } else if (numLives > 1) {
                List<Task> task = filterTasksByDifficulty(tasks, 0);
                assignedTask = getRandomTask(task);
                playerData[3] = "0";
            }

            // update player info
            ConfigManager.writeToPlayerBase(playerName, playerData);

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

    public static Task[] getTasks() {
        return tasks;
    }

    public static void setBookMeta(ItemStack book, Task task) {
        BookMeta meta = (BookMeta) book.getItemMeta();

        ChatColor messageColor = null;
        String difficultyText = null;

        // set the colour based on difficulty
        if (task.getDifficulty() == 0) {
            messageColor = ChatColor.GREEN;
            difficultyText = "Normal";
        } else if (task.getDifficulty() == 1) {
            messageColor = ChatColor.YELLOW;
            difficultyText = "Hard";
        } else if (task.getDifficulty() == 2) {
            messageColor = ChatColor.RED;
            difficultyText = "Red";
        }

        // format the book properly
        meta.setTitle(messageColor + "Your Task");
        meta.setAuthor("VGS Life Series");
        meta.setPages("Task Difficulty: " + messageColor + difficultyText
                + "\n" + ChatColor.BLUE + task.getDescription());

        book.setItemMeta(meta);
    }
}
