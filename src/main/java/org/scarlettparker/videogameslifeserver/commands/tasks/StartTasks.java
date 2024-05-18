package org.scarlettparker.videogameslifeserver.commands.tasks;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.scarlettparker.videogameslifeserver.manager.ConfigManager;
import org.scarlettparker.videogameslifeserver.tasks.Task;

import java.io.*;
import java.util.*;

import static org.bukkit.Bukkit.getName;
import static org.scarlettparker.videogameslifeserver.manager.ConfigManager.writeToPlayerBase;
import static org.scarlettparker.videogameslifeserver.manager.TaskManager.*;

public class StartTasks implements CommandExecutor {
    public static Task[] tasks = {};
    public static HashMap<String, Task> playerTasks = new HashMap();
    private static boolean chatDisabled = false;
    private String commandLabel = "starttasks";

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        commandLabel = label;

        if (sender instanceof Player) {
            Player p = (Player) sender;
            p.sendMessage(ChatColor.RED + "You cannot use this command as a player. Please run this command from the console.");
            return true;
        }

        if (args.length > 0) {
            System.err.println("Incorrect usage! Usage is /starttasks");
            return true;
        }

        if (commandLabel.equalsIgnoreCase("starttasks")) {
            playerTasks.clear();
            createTaskFile();
            tasks = generateTasks();
        }

        // initialize all tasks
        distributeTasks();

        ItemStack book = new ItemStack(Material.WRITTEN_BOOK, 1);

        // debugging to print off tasks
        int playerCount = 0;
        for (Map.Entry<String, Task> entry : playerTasks.entrySet()) {

            Player player = Bukkit.getPlayer(entry.getKey());
            Task task = entry.getValue();

            setBookMeta(book, task);
            String[] playerData = ConfigManager.getPlayerData(entry.getKey()).split(",");

            // reset session tasks :3
            playerData[5] = "0";

            if (player == null) {
                playerData[5] = "-1";
            }

            writeToPlayerBase(entry.getKey(), playerData);

            // check if inventory is empty
            try {
                if (player.getInventory().firstEmpty() == -1) {
                    // drop book and tell user their task just in case
                    player.getLocation().getWorld().dropItemNaturally(player.getLocation(), book);
                    player.sendMessage(ChatColor.RED + "Your inventory is full! A book has been dropped with your task.");
                    player.performCommand("whattask");
                } else {
                    player.getInventory().addItem(book);
                }
            } catch (Exception e) {
                //do not much
            }
            playerCount += 1;
        }

        // update file and write to it blah blah
        updatePlayerFile();
        System.out.println("Successfully given tasks to " + playerCount + " players.");
        return true;
    }

    private void distributeTasks() {
        setChatDisabled(true);
        for (Player p : getAllPlayers()) {
            removeBook(p);
            String playerName = p.getName();
            String[] playerData = ConfigManager.getPlayerData(playerName).split(",");
            int numLives = Integer.parseInt(Objects.requireNonNull(playerData[1]));


            if (commandLabel.equalsIgnoreCase("sessiontasks")) {

                p.performCommand("failtask J30JVDXNL");
            }

            // reset player tokens
            if (commandLabel.equalsIgnoreCase("starttasks")) {
                playerData[6] = "0";
            }

            Task assignedTask = null;
            removeBook(p);

            if (commandLabel.equalsIgnoreCase("starttasks") && p.getName().equals("scarwe")) {
                assignedTask = new Task();

                assignedTask.setDifficulty(3);
                assignedTask.setDescription("Give nether wart that you have collected to a player of your choice.");
                assignedTask.setAvailable(true);
                assignedTask.setID(500);
                playerData[3] = "3";
            } else {
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
            }

            if (assignedTask.getDescription().contains("{receiver}")) {
                List<Player> allPlayers = getAllPlayers();
                allPlayers.remove(p.getPlayer());
                int random = new Random().nextInt(allPlayers.size());
                Player pickedPlayer = getAllPlayers().get(random);
                assignedTask.setDescription(assignedTask.getDescription().replace("{receiver}", pickedPlayer.getName()));
                System.out.println(assignedTask.getDescription());
            }
            if (assignedTask.getDescription().contains("{sender}")) {
                assignedTask.setDescription(assignedTask.getDescription().replace("{sender}", p.getName()));
            }

            // update player info
            writeToPlayerBase(playerName, playerData);

            playerTasks.put(p.getName(), assignedTask);
            assignedTask.setAvailable(false);
        }
        setChatDisabled(false);
    }

    public static Task[] getTasks() {
        return tasks;
    }

    public static List<Player> getAllPlayers() {
        return new ArrayList<>(Bukkit.getOnlinePlayers());
    }


    public static boolean getChatDisabled() {
        return chatDisabled;
    }

    public static void setChatDisabled(boolean disabled) {
        chatDisabled = disabled;
    }
}
