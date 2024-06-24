package org.scarlettparker.videogameslifeserver;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.scarlettparker.videogameslifeserver.commands.punish.*;
import org.scarlettparker.videogameslifeserver.commands.life.GiveLife;
import org.scarlettparker.videogameslifeserver.commands.life.SetLife;
import org.scarlettparker.videogameslifeserver.commands.life.StartLife;
import org.scarlettparker.videogameslifeserver.commands.shop.*;
import org.scarlettparker.videogameslifeserver.commands.tasks.*;
import org.scarlettparker.videogameslifeserver.events.*;
import org.scarlettparker.videogameslifeserver.objects.TPlayer;
import org.scarlettparker.videogameslifeserver.utils.FragilityListener;
import org.scarlettparker.videogameslifeserver.utils.KnockbackListener;
import org.scarlettparker.videogameslifeserver.utils.PunishmentUtils;

import java.util.Objects;

import static org.scarlettparker.videogameslifeserver.utils.PunishmentUtils.applyPunishment;
import static org.scarlettparker.videogameslifeserver.utils.WorldUtils.getAllPlayers;

public final class Main extends JavaPlugin {
    // for registering events
    LifeEvents lifeEvents = new LifeEvents();
    InventoryEvents inventoryEvents = new InventoryEvents();
    PunishmentEvents punishmentEvents = new PunishmentEvents();

    @Override
    public void onEnable() {
        Bukkit.getLogger().info("Enabling VGS Plugin");

        getServer().getPluginManager().registerEvents(lifeEvents, this);
        getServer().getPluginManager().registerEvents(inventoryEvents, this);
        getServer().getPluginManager().registerEvents(punishmentEvents, this);

        getServer().getPluginManager().registerEvents(new CompassEvents(), this);
        getServer().getPluginManager().registerEvents(new TagEvents(), this);
        getServer().getPluginManager().registerEvents(new PunishmentUtils(), this);
        getServer().getPluginManager().registerEvents(new FragilityListener(), this);
        getServer().getPluginManager().registerEvents(new KnockbackListener(), this);

        // life commands
        Objects.requireNonNull(getCommand("givelife")).setExecutor(new GiveLife());
        Objects.requireNonNull(getCommand("setlife")).setExecutor(new SetLife());
        Objects.requireNonNull(getCommand("startlife")).setExecutor(new StartLife());

        // token commands
        Objects.requireNonNull(getCommand("tokens")).setExecutor(new Tokens());
        Objects.requireNonNull(getCommand("gift")).setExecutor(new Gift());
        Objects.requireNonNull(getCommand("settokens")).setExecutor(new SetTokens());
        Objects.requireNonNull(getCommand("setvillagershop")).setExecutor(new SetVillagerShop());
        Objects.requireNonNull(getCommand("shop")).setExecutor(new Shop());

        Shop shop = new Shop();
        getServer().getPluginManager().registerEvents(shop, this);

        // task commands
        AddTask addTaskCommand = new AddTask(this);
        Objects.requireNonNull(getCommand("addtask")).setExecutor(addTaskCommand);
        Objects.requireNonNull(getCommand("cleartask")).setExecutor(new ClearTask());
        Objects.requireNonNull(getCommand("completetask")).setExecutor(new CompleteTask());
        Objects.requireNonNull(getCommand("currenttask")).setExecutor(new CurrentTask());
        Objects.requireNonNull(getCommand("deletetask")).setExecutor(new DeleteTask());

        EditTask editTaskCommand = new EditTask(this);
        Objects.requireNonNull(getCommand("edittask")).setExecutor(editTaskCommand);

        Objects.requireNonNull(getCommand("endtag")).setExecutor(new EndTag());
        Objects.requireNonNull(getCommand("excludeplayer")).setExecutor(new ExcludePlayer());
        Objects.requireNonNull(getCommand("excludetask")).setExecutor(new ExcludeTask());
        Objects.requireNonNull(getCommand("failtask")).setExecutor(new FailTask());
        Objects.requireNonNull(getCommand("forcetask")).setExecutor(new ForceTask());
        Objects.requireNonNull(getCommand("filltasks")).setExecutor(new FillTasks());
        Objects.requireNonNull(getCommand("listtasks")).setExecutor(new ListTasks());
        Objects.requireNonNull(getCommand("newtask")).setExecutor(new NewTask());
        Objects.requireNonNull(getCommand("setpriority")).setExecutor(new SetPriority());
        Objects.requireNonNull(getCommand("settask")).setExecutor(new SetTask());
        Objects.requireNonNull(getCommand("starttasks")).setExecutor(new StartTasks());
        Objects.requireNonNull(getCommand("whattask")).setExecutor(new WhatTask());

        // punishment commands
        Objects.requireNonNull(getCommand("clearpunishments")).setExecutor(new ClearPunishments());
        Objects.requireNonNull(getCommand("setpunishment")).setExecutor(new SetPunishment());

        applyEffects();
    }

    @Override
    public void onDisable() {
        Bukkit.getLogger().info("Disabling VGS Plugin");
    }

    // in case of server reloading
    private void applyEffects() {
        for (Player p : getAllPlayers()) {
            TPlayer tempPlayer = new TPlayer(p.getName());
            for (String s : tempPlayer.getPunishments()) {
                applyPunishment(s, p, true);
            }
        }
    }
}
