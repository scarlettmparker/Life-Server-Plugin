package org.scarlettparker.videogameslifeserver.utils;

import org.bukkit.entity.Player;

public interface CustomEffect {
    void applyFragility(Player player);
    void applyKnockback(Player player);
    int getDuration();
    String getType();
}