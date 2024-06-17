package org.scarlettparker.videogameslifeserver.commands.tasks;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Instrument;
import org.bukkit.Note;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.scarlettparker.videogameslifeserver.objects.TPlayer;

import java.util.Objects;

import static org.scarlettparker.videogameslifeserver.events.TagEvents.activeTasks;

public class EndTag implements CommandExecutor {
    static Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("VideoGamesLifeServer");

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        // must be an operator to use the command
        if (!sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "You must be an operator to run this command.");
            return true;
        }
        String message = ChatColor.RED + "Tag will end in ";

        // send initial messages to players with the tag task
        for (Player p : Bukkit.getOnlinePlayers()) {
            TPlayer tempPlayer = new TPlayer(p.getName());
            if (Objects.equals(tempPlayer.getCurrentTask(), "tag")) {
                sendMessageWithDelay(p, tempPlayer, message + "5 minutes.", 0, -1); // immediately show 5 minutes
                sendMessageWithDelay(p, tempPlayer, message + "1 minute.", 4 * 60 * 20, -1); // 4 minutes later

                // countdown from 50 seconds to 1 second
                int delay = (4 * 60 * 20) + (50 * 20); // 4 minutes + 50 seconds
                for (int i = 10; i >= 0; i--) {
                    sendMessageWithDelay(p, tempPlayer, message + i + " seconds.", delay, i);
                    delay += 20; // increment delay for each second (20 ticks = 1 second)
                }
            }
        }

        return true;
    }

    private void sendMessageWithDelay(Player player, TPlayer tempPlayer, String message, int delayTicks, int i) {
        ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (i != 0) {
                player.sendMessage(message);
                player.playNote(player.getLocation(), Instrument.BELL, Note.flat(0, Note.Tone.C));
            } else {
                if (Objects.equals(tempPlayer.getCurrentTask(), "tag")) {
                    if (tempPlayer.getTagged()) {
                        Bukkit.dispatchCommand(console, "failtask " + tempPlayer.getName());
                        Bukkit.dispatchCommand(console, "setpunishment " + tempPlayer.getName() + " knockback");
                    } else {
                        Bukkit.dispatchCommand(console, "completetask " + tempPlayer.getName());
                    }
                }
                activeTasks.clear();
            }
        }, delayTicks);
    }
}
