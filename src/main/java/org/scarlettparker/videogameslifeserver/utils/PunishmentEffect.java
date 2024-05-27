package org.scarlettparker.videogameslifeserver.utils;
import org.bukkit.entity.Player;

public class PunishmentEffect implements CustomEffect {
    private final int duration; // in ticks
    private final int amplifier;
    private final String type;

    public PunishmentEffect(String type, int duration, int amplifier) {
        this.type = type;
        this.duration = duration;
        this.amplifier = amplifier;
    }

    @Override
    public void applyFragility(Player player) {
        FragilityListener.register(player, this.amplifier);
    }

    public void applyKnockback(Player player) {
        KnockbackListener.registerKnockback(player, this.amplifier);
    }

    @Override
    public int getDuration() {
        return duration;
    }

    public String getType() {
        return type;
    }
}