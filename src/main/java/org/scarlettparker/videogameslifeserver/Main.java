package org.scarlettparker.videogameslifeserver;
import org.bukkit.plugin.java.JavaPlugin;
import org.scarlettparker.videogameslifeserver.commands.GiveLife;
import org.scarlettparker.videogameslifeserver.commands.SetLife;
import org.scarlettparker.videogameslifeserver.commands.StartLife;
import org.scarlettparker.videogameslifeserver.events.LifeEvents;

import java.util.Objects;

public final class Main extends JavaPlugin {
    LifeEvents lifeEvents = new LifeEvents();
    @Override
    public void onEnable() {
        System.out.println("VGS Life Plugin Activated.");
        getServer().getPluginManager().registerEvents(lifeEvents, this);
        Objects.requireNonNull(getCommand("startlife")).setExecutor(new StartLife());
        Objects.requireNonNull(getCommand("setlife")).setExecutor(new SetLife());
        Objects.requireNonNull(getCommand("givelife")).setExecutor(new GiveLife());
    }

    @Override
    public void onDisable() {
        System.out.println("VGS Life Plugin Deactivated.");
    }
}
