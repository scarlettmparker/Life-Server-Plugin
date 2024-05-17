package org.scarlettparker.videogameslifeserver.manager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.scarlettparker.videogameslifeserver.tasks.Task;

import java.io.*;
import java.util.*;

import static org.scarlettparker.videogameslifeserver.commands.tasks.StartTasks.playerTasks;

public class TaskManager {
    private static final File pTaskFile = new File(Objects.requireNonNull(Bukkit.getServer().getPluginManager()
            .getPlugin("VideoGamesLifeServer")).getDataFolder(), "playerTasks.txt");

    public static Task[] generateTasks() {
        String path = "tasks/tasks.txt";
        InputStream is = TaskManager.class.getClassLoader().getResourceAsStream(path);

        if (is == null) {
            System.err.println("Input stream is null. Resource not found.");
            return new Task[0];
        }

        List<Task> taskList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String line;
            int taskID = 0;

            while ((line = reader.readLine()) != null) {
                String[] attributes = line.split(":");

                // set attributes of the temporary task
                String tempDescription = attributes[0];
                int tempDifficulty = Integer.parseInt(attributes[1]);

                taskList.add(generateTask(taskID, tempDifficulty, true, tempDescription));
                taskID += 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return taskList.toArray(new Task[0]);
    }

    public static Task generateTask(int id, int difficulty, boolean available, String description) {
        Task task = new Task();

        // i love object oriented programming
        task.setID(id);
        task.setDescription(description);
        task.setAvailable(available);
        task.setDifficulty(difficulty);

        return task;
    }

    public static List<Task> filterTasksByDifficulty(Task[] tasks, int difficulty) {
        List<Task> filteredTasks = new ArrayList<>();
        for (Task task : tasks) {
            if (task.getDifficulty() == difficulty && task.getAvailable()) {
                filteredTasks.add(task);
            }
        }
        return filteredTasks;
    }

    public static Task getRandomTask(List<Task> taskList) {
        if (taskList.isEmpty()) {
            return null;
        }
        Random rand = new Random();
        return taskList.get(rand.nextInt(taskList.size()));
    }

    public static void setBookMeta(ItemStack book, Task task) {
        BookMeta meta = (BookMeta) book.getItemMeta();

        ChatColor messageColor = null;
        String difficultyText = null;

        // set the colour based on difficulty
        if (task.getDifficulty() == 0) {
            messageColor = ChatColor.GREEN;
            difficultyText = "Normal";
        } else if (task.getDifficulty() == 1 || task.getDifficulty() == 3) {
            messageColor = ChatColor.GOLD;
            difficultyText = "Hard";
        } else if (task.getDifficulty() == 2) {
            messageColor = ChatColor.RED;
            difficultyText = "Red";
        }

        // format the book properly
        meta.setTitle("Your Task");
        meta.setAuthor("VGS Life Series");
        meta.setPages("Task Difficulty: " + messageColor + difficultyText
                + "\n" + ChatColor.BLACK + task.getDescription());


        // trolling raving raven
        if (task.getID() == 500) {
            List<String> pages = new ArrayList<>();
            pages.add("Task Difficulty: " + messageColor + difficultyText + "\n"
                    + ChatColor.BLACK + "Hello " + ChatColor.RED + "RavingRaven" + ChatColor.BLACK
                    + ".\nAs you may be aware, you illegally gave nether wart to some players in the previous season. "
                    + "Unfortunately, we can't let you get away with that without punishment, so we have a special task"
                    + " just for you.\n\n");

            pages.add(ChatColor.BLACK + "Your task is to " + ChatColor.RED + "give nether wart that you have collected"
                    + " to a player of your choice " + ChatColor.BLACK +"before the end of the session"
                    + ChatColor.BLACK + ".\nLuckily, there are some perks to your task! No yellow player is allowed to"
                    + " guess your task, but you still must not reveal your task to anybody, or you fail.\n");
            pages.add(ChatColor.RED + "Failing this task will result in "
                    + "a hefty punishment" + ChatColor.BLACK + ", so chop chop!\n\n"
                    + "Much love, the VGS committee. Enjoy your season!");

            meta.setPages(pages);
        }

        book.setItemMeta(meta);
    }

    public static void removeBook(Player player) {
        Inventory inv = player.getInventory();
        for (ItemStack is : inv) {
            // skip over non items
            if (is == null || is.equals(Material.AIR)) {
                continue;
            }
            if (!is.hasItemMeta()) {
                continue;
            }
            if (is.getType() != Material.WRITTEN_BOOK) {
                continue;
            }
            // get book info and remove from inventory
            BookMeta meta = (BookMeta) is.getItemMeta();
            if (Objects.equals(meta.getAuthor(), "VGS Life Series")) {
                is.setAmount(0);
            }
        }
    }

    public static void updatePlayerFile() {
        BufferedWriter bf = null;
        try {
            bf = new BufferedWriter(new FileWriter(pTaskFile));
            for (Map.Entry<String, Task> entry : playerTasks.entrySet()) {
                String player = entry.getKey();
                Task task = entry.getValue();
                bf.write(player + ":" + formatTask(task));
                bf.newLine();
            }
            bf.flush();
        } catch (Exception e) {
            // file writing error
        } finally {
            try {
                if (bf != null) {
                    bf.close();
                }
            } catch (Exception e) {
                // do nothing
            }
        }
    }

    public static void loadPlayerFile() {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(pTaskFile));
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(":", 2);
                if (parts.length == 2) {
                    String player = String.valueOf(parts[0]);
                    Task task = parseTask(parts[1]);
                    if (player != null && task != null) {
                        playerTasks.put(player, task);
                    }
                }
            }
        } catch (Exception e) {
            // file reading error
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (Exception e) {
                // do nothing
            }
        }
    }

    private static Task parseTask(String taskString) {
        String[] taskParts = taskString.split(":", 5);
        if (taskParts.length != 5) {
            return null; // Invalid task string
        }
        Task task = new Task();
        try {
            task.setID(Integer.parseInt(taskParts[0]));
            task.setDifficulty(Integer.parseInt(taskParts[1]));
            task.setAvailable(Boolean.parseBoolean(taskParts[2]));
            task.setDescription(taskParts[3]);
            task.setReceiver(taskParts[4]);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return null;
        }
        return task;
    }

    private static String formatTask(Task task) {
        return task.getID() + ":" +
                task.getDifficulty() + ":" +
                task.getAvailable() + ":" +
                task.getDescription() + ":" +
                task.getReceiver();
    }

}
