package org.scarlettparker.videogameslifeserver;
import org.bukkit.plugin.java.JavaPlugin;
import org.scarlettparker.videogameslifeserver.commands.life.GiveLife;
import org.scarlettparker.videogameslifeserver.commands.life.SetLife;
import org.scarlettparker.videogameslifeserver.commands.life.StartLife;
import org.scarlettparker.videogameslifeserver.commands.tasks.*;
import org.scarlettparker.videogameslifeserver.events.LifeEvents;

import java.util.Objects;

public final class Main extends JavaPlugin {
    LifeEvents lifeEvents = new LifeEvents();

    @Override
    public void onEnable() {
        System.out.println("VGS Life Plugin Activated.");
        getServer().getPluginManager().registerEvents(lifeEvents, this);

        // life commands
        Objects.requireNonNull(getCommand("startlife")).setExecutor(new StartLife());
        Objects.requireNonNull(getCommand("setlife")).setExecutor(new SetLife());
        Objects.requireNonNull(getCommand("givelife")).setExecutor(new GiveLife());

        // task commands
        Objects.requireNonNull(getCommand("starttasks")).setExecutor(new StartTasks());
        Objects.requireNonNull(getCommand("sessiontasks")).setExecutor(new StartTasks());
        Objects.requireNonNull(getCommand("newtask")).setExecutor(new NewTask());
        Objects.requireNonNull(getCommand("whattask")).setExecutor(new WhatTask());
        Objects.requireNonNull(getCommand("completetask")).setExecutor(new CompleteTask());
        Objects.requireNonNull(getCommand("failtask")).setExecutor(new FailTask());
    }

    @Override
    public void onDisable() {
        System.out.println("VGS Life Plugin Deactivated.");
    }
}
