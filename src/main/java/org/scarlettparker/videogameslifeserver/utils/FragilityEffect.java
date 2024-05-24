package org.scarlettparker.videogameslifeserver.utils;
import org.bukkit.entity.Player;

public class FragilityEffect implements CustomEffect {
    private final int duration; // in ticks
    private final int amplifier;

    public FragilityEffect(int duration, int amplifier) {
        this.duration = duration;
        this.amplifier = amplifier;
    }

    @Override
    public void apply(Player player) {
        FragilityListener.register(player, this.duration, this.amplifier);
    }

    @Override
    public int getDuration() {
        return duration;
    }
}