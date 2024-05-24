package org.scarlettparker.videogameslifeserver.utils;

import org.bukkit.entity.Player;

public interface CustomEffect {
    void apply(Player player);
    int getDuration();
}