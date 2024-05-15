package org.scarlettparker.videogameslifeserver.manager;

import org.scarlettparker.videogameslifeserver.tasks.Task;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TaskManager {
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
}
