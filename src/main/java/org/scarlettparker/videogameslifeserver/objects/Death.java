package org.scarlettparker.videogameslifeserver.objects;

public class Death {
    private long time;
    private String deathMessage;

    public Death(long time, String deathMessage) {
        this.time = time;
        this.deathMessage = deathMessage;
    }

    public long getTime() {
        return this.time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getDeathMessage() {
        return this.deathMessage;
    }

    public void setDeathMessage(String deathMessage) {
        this.deathMessage = deathMessage;
    }
}
