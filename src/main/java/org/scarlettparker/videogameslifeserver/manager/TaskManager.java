package org.scarlettparker.videogameslifeserver.manager;

import org.scarlettparker.videogameslifeserver.tasks.Task;

import java.io.BufferedReader;
import java.io.FileReader;

public class TaskManager {
    public static Task[] generateTasks() {
        String path = "tasks/tasks.txt";
        Task[] tasks = {};

        try {
            // start reading the file
            BufferedReader reader = new BufferedReader(new FileReader(path));
            String line;
            int taskID = 0;

            while ((line = reader.readLine()) != null) {
                String[] attributes = line.split(":");

                // set attributes of the temporary task
                int tempId = taskID;
                String tempDescription = attributes[0];
                int tempDifficulty = Integer.parseInt(attributes[1]);

                tasks = pushTask(tasks, generateTask(tempId, tempDifficulty, true, tempDescription));
                taskID += 1;
            }
        } catch (Exception e) {
            // don't do much
        }
        return tasks;
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

    private static Task[] pushTask(Task[] array, Task push) {
        Task[] longer = new Task[array.length + 1];
        System.arraycopy(array, 0, longer, 0, array.length);
        longer[array.length] = push;
        return longer;
    }
}
