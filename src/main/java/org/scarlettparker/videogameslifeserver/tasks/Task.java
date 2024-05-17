package org.scarlettparker.videogameslifeserver.tasks;

public class Task {
    private int id;
    private int difficulty;
    private boolean available;
    private String description;
    private String receiver;

    public int getID() {
        return this.id;
    }

    public int getDifficulty() {
        return this.difficulty;
    }

    public boolean getAvailable() {
        return this.available;
    }

    public String getDescription() {
        return this.description;
    }

    public String getReceiver() {
        return this.receiver;
    }

    public void setID(int id) {
        this.id = id;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }
}
