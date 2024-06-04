package org.scarlettparker.videogameslifeserver.manager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.scarlettparker.videogameslifeserver.objects.TPlayer;
import org.scarlettparker.videogameslifeserver.objects.Task;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

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
                tempTask.setPlayerDescription(attributes[1]);
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
    private static void distributeTasksToPlayers(List<Player> players, List<String> normalIDs, List<String> redIDs) {
        Random randomTask = new Random();

        for (Player p : players) {
            TPlayer tempPlayer = new TPlayer(p.getName());
            int playerLives = tempPlayer.getLives();

            if (playerLives <= 0) {
                continue;
            }

            List<String> taskIDs = (playerLives >= 2) ? normalIDs : redIDs;
            assignTaskToPlayer(randomTask, p, tempPlayer, taskIDs);
        }
    }

    // assign a task to a player
    private static void assignTaskToPlayer(Random randomTask, Player player, TPlayer tPlayer, List<String> taskIDs) {
        if (!Objects.equals(tPlayer.getNextTask(), "-1")) {
            Task nextTask = new Task(tPlayer.getNextTask());
            nextTask.setExcluded(true);

            // in case {receiver} has been entered weirdly
            nextTask.setPlayerDescription(manageReceiverDescription(
                    manageSenderDescription(nextTask.getDescription(), player), player));

            // give player task and corresponding book
            addTaskToPlayer(tPlayer, nextTask.getName());
            removeBook(player);
            bookCountdown(nextTask, player);
        } else {
            Task currentTask = new Task(tPlayer.getCurrentTask());
            if (!taskIDs.isEmpty() && currentTask.getDifficulty() != 2) {
                // get random task in list
                int randomIndex = randomTask.nextInt(taskIDs.size());
                String randomID = taskIDs.get(randomIndex);
                Task tempTask = new Task(randomID);

                // temporary id set to remove from
                List<String> tempIDs = taskIDs;
                if (Arrays.asList(tempTask.getExcludedPlayers()).contains(tPlayer.getName())) {
                    while (!tempIDs.isEmpty()) {
                        // reset the task
                        randomIndex = randomTask.nextInt(taskIDs.size());
                        randomID = tempIDs.get(randomIndex);

                        tempTask = new Task(randomID);
                        tempIDs.remove(randomIndex);
                    }
                }

                if (tempIDs.isEmpty()) {
                    player.sendMessage(ChatColor.RED + "There are currently no tasks available for you. Please annoy "
                            + "the admins into creating new tasks.");
                    return;
                }

                taskIDs.remove(randomIndex);
                tempTask.setExcluded(true);

                // in case {receiver} has been entered weirdly
                tempTask.setPlayerDescription(manageReceiverDescription(
                        manageSenderDescription(tempTask.getDescription(), player), player));

                // give player task and corresponding book
                addTaskToPlayer(tPlayer, randomID);
                removeBook(player);
                bookCountdown(tempTask, player);
            } else {
                // because tasks can't be duplicated ig
                if (Objects.equals(tPlayer.getCurrentTask(), "-1")) {
                    player.sendMessage(ChatColor.RED + "There are currently no tasks available for you. Please annoy "
                            + "the admins into creating new tasks.");
                }
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
}
