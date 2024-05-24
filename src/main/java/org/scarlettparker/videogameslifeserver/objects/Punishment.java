package org.scarlettparker.videogameslifeserver.objects;

import static org.scarlettparker.videogameslifeserver.manager.ConfigManager.*;

public class Punishment {
    String name;

    public Punishment(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        Object description = getJsonObjectAttribute(punishFile, String.valueOf(name), "description");
        return description instanceof String ? (String) description : ""; // default value is 0
    }

    public int getDifficulty() {
        Object difficulty = getJsonObjectAttribute(punishFile, String.valueOf(name), "difficulty");
        return difficulty instanceof Integer ? (int) difficulty : 0; // default value is 0
    }

    public void setDescription(String description) {
        setJsonObjectAttribute(punishFile, String.valueOf(name), "description", description);
    }

    public void setDifficulty(int difficulty) {
        setJsonObjectAttribute(punishFile, String.valueOf(name), "difficulty", difficulty);
    }
}
