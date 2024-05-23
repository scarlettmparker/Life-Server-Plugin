package org.scarlettparker.videogameslifeserver.manager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.bukkit.entity.Player;
import org.scarlettparker.videogameslifeserver.objects.TPlayer;
import org.scarlettparker.videogameslifeserver.objects.Task;

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
            int taskName = 0;

            while ((line = reader.readLine()) != null) {
                String[] attributes = line.split(":");
                Task tempTask = new Task(String.valueOf(taskName));

                String json = convertTaskToJson(tempTask);

                // add json task to file
                addJsonObject(taskFile, json);

                // set task attributes
                tempTask.setDescription(attributes[0]);
                tempTask.setPlayerDescription(attributes[0]);
                tempTask.setDifficulty(Integer.parseInt(attributes[1]));
                tempTask.setAvailable(true);
                tempTask.setCompleted(false);

                taskName += 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // convert a task object to json string
    private static String convertTaskToJson(Task task) {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        try {
            return ow.writeValueAsString(task);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    // distribute tasks to players based on difficulty
    public static void doTaskDistribution(List<Player> players, int difficulty) {
        List<Integer> normalIDs = new ArrayList<>();
        List<Integer> redIDs = new ArrayList<>();

        // gather task ids by difficulty
        for (int i = 0; i < totalFileObjects(taskFile); i++) {
            int taskDifficulty = (int) getJsonObjectAttribute(taskFile, String.valueOf(i), "difficulty");
            boolean available = (boolean) getJsonObjectAttribute(taskFile, String.valueOf(i), "available");

            if (available) {
                if (taskDifficulty == difficulty) {
                    normalIDs.add(i);
                } else if (taskDifficulty == 2) {
                    redIDs.add(i);
                }
            }
        }

        distributeTasksToPlayers(players, normalIDs, redIDs);
    }

    // distribute tasks to players
    private static void distributeTasksToPlayers(List<Player> players, List<Integer> normalIDs, List<Integer> redIDs) {
        Random randomTask = new Random();

        for (Player p : players) {
            TPlayer tempPlayer = new TPlayer(p.getName());
            int playerLives = tempPlayer.getLives();

            if (playerLives <= 0) {
                continue;
            }

            List<Integer> taskIDs = (playerLives > 1) ? normalIDs : redIDs;
            assignTaskToPlayer(randomTask, p, tempPlayer, taskIDs);
        }
    }

    // assign a task to a player
    private static void assignTaskToPlayer(Random randomTask, Player player, TPlayer tPlayer, List<Integer> taskIDs) {
        if (!taskIDs.isEmpty()) {
            int randomIndex = randomTask.nextInt(taskIDs.size());
            int randomID = taskIDs.get(randomIndex);
            taskIDs.remove(randomIndex);

            Task tempTask = new Task(String.valueOf(randomID));
            tempTask.setAvailable(false);

            // since tasks may involve other players names on them
            if (tempTask.getDescription().contains("{receiver}")) {
                tempTask.setPlayerDescription(manageReceiverDescription(tempTask.getDescription(), player));
            }

            if (tempTask.getDescription().contains("{sender}")) {
                tempTask.setPlayerDescription(manageSenderDescription(tempTask.getDescription(), player));
            }

            // give player task and corresponding book
            addTaskToPlayer(tPlayer, randomID);
            removeBook(player);
            giveTaskBook(tempTask, player);
        }
    }

    // add a task to a player's task list
    private static void addTaskToPlayer(TPlayer tPlayer, int taskID) {
        String[] currentTasks = tPlayer.getTasks();
        String[] tempTasks = Arrays.copyOf(currentTasks, currentTasks.length + 1);
        tempTasks[currentTasks.length] = String.valueOf(taskID);
        tPlayer.setTasks(tempTasks);
    }
}