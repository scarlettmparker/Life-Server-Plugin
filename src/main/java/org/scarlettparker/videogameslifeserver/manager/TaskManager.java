package org.scarlettparker.videogameslifeserver.manager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.scarlettparker.videogameslifeserver.objects.TPlayer;
import org.scarlettparker.videogameslifeserver.objects.Task;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

import static org.scarlettparker.videogameslifeserver.manager.ConfigManager.*;
import static org.scarlettparker.videogameslifeserver.utils.WorldUtils.*;

public class TaskManager {
    public static void generateTasks() {
        String path = "tasks/tasks.txt";
        InputStream is = TaskManager.class.getClassLoader().getResourceAsStream(path);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] attributes = line.split(":");
                Task tempTask = new Task(attributes[0]);

                String json = convertTaskToJson(tempTask);

                // add json task to file
                addJsonObject(taskFile, json);

                // set task attributes
                tempTask.setDescription(attributes[1]);
                tempTask.setDifficulty(Integer.parseInt(attributes[2]));
                tempTask.setCompleted(false);
                tempTask.setExcluded(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // convert a task object to json string
    public static String convertTaskToJson(Task task) {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        try {
            return ow.writeValueAsString(task);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    // distribute tasks to players based on difficulty
    public static void doTaskDistribution(List<Player> players, int difficulty) {
        List<String> normalIDs = new ArrayList<>();
        List<String> redIDs = new ArrayList<>();

        // gather task ids by difficulty
        JsonObject allTasks = returnAllObjects(taskFile);

        for (String key : allTasks.keySet()) {
            JsonObject task = allTasks.getAsJsonObject(key);
            int taskDifficulty = task.get("difficulty").getAsInt();
            boolean excluded = task.get("excluded").getAsBoolean();

            // check if the task is excluded
            if (excluded) {
                continue;
            }

            if (taskDifficulty == difficulty || taskDifficulty == 3) {
                normalIDs.add(key);
            } else if (taskDifficulty == 2) {
                redIDs.add(key);
            }
        }

        distributeTasksToPlayers(players, normalIDs, redIDs);
    }

    // distribute tasks to players
    // yes i know this can be improved but i've done so much for this plugin i've lost interest kinda
    public static void distributeTasksToPlayers(List<Player> players, List<String> normalIDs, List<String> redIDs) {
        // separate players with forced tasks and without forced tasks
        List<Player> forcedTaskPlayers = new ArrayList<>();
        List<Player> nonForcedTaskPlayers = new ArrayList<>();

        for (Player p : players) {
            TPlayer tempPlayer = new TPlayer(p.getName());
            if (!tempPlayer.getNextTask().equals("-1")) {
                forcedTaskPlayers.add(p);
            } else {
                nonForcedTaskPlayers.add(p);
            }
        }

        // shuffle non-forced task players
        Collections.shuffle(nonForcedTaskPlayers);

        // create a final ordered list of players
        List<Player> orderedPlayers = new ArrayList<>(forcedTaskPlayers);
        orderedPlayers.addAll(nonForcedTaskPlayers);

        // Distribute tasks to players in the ordered list
        for (Player p : orderedPlayers) {
            TPlayer tempPlayer = new TPlayer(p.getName());
            int playerLives = tempPlayer.getLives();

            if (playerLives <= 0) {
                continue;
            }

            List<String> taskIDs = (playerLives >= 2) ? normalIDs : redIDs;

            // Group tasks by priority
            Map<Integer, List<String>> tasksByPriority = taskIDs.stream()
                    .collect(Collectors.groupingBy(taskID -> new Task(taskID).getPriority()));

            // Shuffle each priority group
            List<String> shuffledTaskIDs = new ArrayList<>();
            tasksByPriority.keySet().stream()
                    .sorted(Comparator.reverseOrder()) // higher priority tasks come first
                    .forEach(priority -> {
                        List<String> priorityTasks = tasksByPriority.get(priority);
                        Collections.shuffle(priorityTasks);
                        shuffledTaskIDs.addAll(priorityTasks);
                    });

            assignTaskToPlayer(p, tempPlayer, shuffledTaskIDs);
        }
    }

    // assign a task to a player
    private static void assignTaskToPlayer(Player player, TPlayer tPlayer, List<String> taskIDs) {
        if (!Objects.equals(tPlayer.getCurrentTask(), "-1")) {
            return;
        }

        if (!Objects.equals(tPlayer.getNextTask(), "-1")) {
            Task nextTask = new Task(tPlayer.getNextTask());
            nextTask.setExcluded(true);

            if (nextTask.getDescription().contains("{player}")) {
                // multi player tasks
                List<Player> onlinePlayers = getAllPlayers();
                manageMultiplePlayersDescription(tPlayer, nextTask, onlinePlayers);
            } else {
                // in case {receiver} has been entered weirdly
                tPlayer.setTaskDescription(manageReceiverDescription(
                        manageSenderDescription(nextTask.getDescription(), player), player));

                // give player task and corresponding book
                addTaskToPlayer(tPlayer, nextTask.getName());
                removeBook(player);
                bookCountdown(nextTask, player);
            }
        } else {
            Task currentTask = new Task(tPlayer.getCurrentTask());
            if (!taskIDs.isEmpty() && currentTask.getDifficulty() != 2) {
                Iterator<String> iterator = taskIDs.iterator();
                while (iterator.hasNext()) {
                    String taskID = iterator.next();
                    Task tempTask = new Task(taskID);

                    if (Arrays.asList(tempTask.getExcludedPlayers()).contains(tPlayer.getName())) {
                        continue;
                    }

                    // safely remove the taskID from the list
                    iterator.remove();
                    tempTask.setExcluded(true);

                    // manage description stuff
                    if (tempTask.getDescription().contains("{player}")) {
                        // multi player tasks
                        List<Player> onlinePlayers = getAllPlayers();
                        manageMultiplePlayersDescription(tPlayer, tempTask, onlinePlayers);
                    } else {
                        tPlayer.setTaskDescription(manageReceiverDescription(
                                manageSenderDescription(tempTask.getDescription(), player), player));
                        addTaskToPlayer(tPlayer, taskID);
                        removeBook(player);
                        bookCountdown(tempTask, player);
                    }
                    return;
                }
            } else if (Objects.equals(tPlayer.getCurrentTask(), "-1")) {
                player.sendMessage(ChatColor.RED + "There are currently no tasks available for you. Please annoy the admins into creating new tasks.");
            }
        }
    }


    // add a task to a player's task list
    private static void addTaskToPlayer(TPlayer tPlayer, String taskID) {
        String[] currentTasks = tPlayer.getTasks();
        String[] tempTasks = Arrays.copyOf(currentTasks, currentTasks.length + 1);
        tempTasks[currentTasks.length] = taskID;

        // set player attributes so program doesn't die itself
        tPlayer.setTasks(tempTasks);
        tPlayer.setCurrentTask(taskID);
    }

    public static String manageReceiverDescription(String description, Player p) {
        List<Player> allPlayers = getAllPlayers();
        if (allPlayers.size() > 1) {
            allPlayers.remove(p.getPlayer());
        }
        int random = new Random().nextInt(allPlayers.size());
        Player pickedPlayer = allPlayers.get(random);
        return description.replace("{receiver}", pickedPlayer.getName());
    }

    public static String manageSenderDescription(String description, Player p) {
        return description.replace("{sender}", p.getName());
    }

    public static void manageMultiplePlayersDescription(TPlayer tempPlayer, Task task, List<Player> onlinePlayers) {
        String description = task.getDescription();
        List<Player> eligiblePlayers = new ArrayList<>();
        List<Player> forcedPlayers = new ArrayList<>();
        Random random = new Random();

        // filter eligible players who have no current tasks and are not excluded
        for (Player player : onlinePlayers) {
            TPlayer tPlayer = new TPlayer(player.getName());
            if (!Arrays.asList(task.getExcludedPlayers()).contains(player.getName())) {
                // add to forced players list if they have this task as their next task~
                if (Objects.equals(tPlayer.getNextTask(), task.getName())) {
                    forcedPlayers.add(player);
                } else if (Objects.equals(tPlayer.getCurrentTask(), "-1")){
                    eligiblePlayers.add(player);
                }
            }
        }

        int playerTagsCount = description.split("\\{player\\}").length;

        // check if there are enough eligible players
        if (forcedPlayers.size() + eligiblePlayers.size() < playerTagsCount) {
            Bukkit.getPlayer(tempPlayer.getName()).sendMessage(ChatColor.RED
                    + "You were assigned a multiplayer task but there are not enough players online. "
                    + "Contact the admins to manually set your task.");
            return;
        }

        // add forced players first
        List<Player> involvedPlayers = new ArrayList<>(forcedPlayers);

        // select random unique eligible players if needed
        while (involvedPlayers.size() < playerTagsCount) {
            Player pickedPlayer = eligiblePlayers.get(random.nextInt(eligiblePlayers.size()));
            if (!involvedPlayers.contains(pickedPlayer)) {
                involvedPlayers.add(pickedPlayer);
            }
        }

        // assign the task to all involved players with the correct description
        for (int i = 0; i < involvedPlayers.size(); i++) {
            Player currentPlayer = involvedPlayers.get(i);
            TPlayer tPlayer = new TPlayer(currentPlayer.getName());
            String updatedDescription = description;

            // replace {player} tags with the names of other players
            List<Player> descriptionPlayers = new ArrayList<>(involvedPlayers);

            // update task description for the current player
            updatedDescription = updatedDescription.replaceFirst("\\{receiver}", descriptionPlayers.get(1).getName());
            descriptionPlayers.remove(i);

            for (int j = 0; j < playerTagsCount - 1; j++) {
                updatedDescription = updatedDescription.replaceFirst("\\{player}", descriptionPlayers.get(j).getName());
            }

            tPlayer.setTaskDescription(updatedDescription);
            addTaskToPlayer(tPlayer, task.getName());
        }

        // why is it in another loop??? I DUNNO
        for (Player player : involvedPlayers) {
            removeBook(player);
            bookCountdown(task, player);
        }

        // set next task for remaining eligible players
        for (Player player : eligiblePlayers) {
            if (!involvedPlayers.contains(player)) {
                TPlayer tPlayer = new TPlayer(player.getName());
                tPlayer.setNextTask("-1");
            }
        }

        task.setExcluded(true);
    }
}
