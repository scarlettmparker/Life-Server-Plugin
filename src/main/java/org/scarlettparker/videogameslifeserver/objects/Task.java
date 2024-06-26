package org.scarlettparker.videogameslifeserver.objects;

import static org.scarlettparker.videogameslifeserver.manager.ConfigManager.*;

public class Task {
    private String name;

    public Task(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDifficulty() {
        Object difficulty = getJsonObjectAttribute(taskFile, String.valueOf(name), "difficulty");
        return difficulty instanceof Integer ? (int) difficulty : 0; // default value is 0
    }

    public boolean getExcluded() {
        Object excluded = getJsonObjectAttribute(taskFile, String.valueOf(name), "excluded");
        return !(excluded instanceof Boolean) || (boolean) excluded; // default value is false
    }

    public String[] getExcludedPlayers() {
        Object excludedPlayers = getJsonObjectAttribute(taskFile, name, "excludedPlayers");
        return excludedPlayers instanceof String[] ? (String[]) excludedPlayers : new String[0]; // default value is empty array
    }

    public String getDescription() {
        Object description = getJsonObjectAttribute(taskFile, String.valueOf(name), "description");
        return description instanceof String ? (String) description : ""; // default value is 0
    }

    public int getPriority() {
        Object priority = getJsonObjectAttribute(taskFile, String.valueOf(name), "priority");
        return priority instanceof Integer ? (Integer) priority : 0; // default value is 0
    }

    public int getReward() {
        Object reward = getJsonObjectAttribute(taskFile, String.valueOf(name), "reward");
        return reward instanceof Integer ? (Integer) reward : 0; // default value is 0
    }

    public void setDifficulty(int difficulty) {
        setJsonObjectAttribute(taskFile, String.valueOf(name), "difficulty", difficulty);
    }

    public void setExcluded(boolean excluded) {
        setJsonObjectAttribute(taskFile, String.valueOf(name), "excluded", excluded);
    }

    public void setExcludedPlayers(String[] tasks) {
        setJsonObjectAttribute(taskFile, name, "excludedPlayers", tasks);
    }

    public void setDescription(String description) {
        setJsonObjectAttribute(taskFile, String.valueOf(name), "description", description);
    }

    public void setPriority(int priority) {
        setJsonObjectAttribute(taskFile, String.valueOf(name), "priority", priority);
    }

    public void setReward(int reward) {
        setJsonObjectAttribute(taskFile, String.valueOf(name), "reward", reward);
    }
}
