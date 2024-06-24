package org.scarlettparker.videogameslifeserver.utils;

import org.bukkit.*;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.scarlettparker.videogameslifeserver.objects.TPlayer;
import org.scarlettparker.videogameslifeserver.objects.Task;

import java.util.*;

public class WorldUtils {
    static Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("VideoGamesLifeServer");

    public static List<Player> getAllPlayers() {
        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        Collections.shuffle(players);
        return players;
    }

    public static void setPlayerName(Player p, int lives) {
        String title;
        String newName;
        String colorPrefix;
        ChatColor lifeColor;
        ChatColor contrastColor;

        ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
        String command;

        if (lives >= 1) {
            title = String.valueOf(lives);
            contrastColor = ChatColor.WHITE;
            lifeColor = ChatColor.DARK_GREEN;
            colorPrefix = "&5&2";

            // wonderful formatting based on no. lives
            if (lives == 1) {
                lifeColor = ChatColor.RED;
                colorPrefix = "&2&c";
            } else if (lives == 2) {
                lifeColor = ChatColor.YELLOW;
                colorPrefix = "&3&e";
            } else if (lives == 3) {
                lifeColor = ChatColor.GREEN;
                colorPrefix = "&4&a";
            }
        } else {
            title = "DEAD";
            contrastColor = ChatColor.GRAY;
            lifeColor = ChatColor.GRAY;
            colorPrefix = "&1&7";
        }

        newName = lifeColor + p.getName() + contrastColor
                + " [" + lifeColor + title + contrastColor + "]";
        command = "nte player " + p.getName() + " prefix " + colorPrefix;


        // custom name AND tag name now displays
        Bukkit.dispatchCommand(console, command);

        p.setDisplayName(newName);
        p.setPlayerListName(newName);
        p.setCustomName(newName);
    }

    public static void handleFinalDeath(String name) {
        Player tempPlayer = Bukkit.getPlayer(name);
        Location location = tempPlayer.getLocation();
        World world = location.getWorld();
        Inventory inv = tempPlayer.getInventory();

        for (ItemStack is : inv) {
            try {
                world.dropItem(location, is);
            } catch(Exception e) {
                // do nothing
            }
        }

        clearOnFinalDeath(tempPlayer, world, location);

        Bukkit.broadcastMessage(ChatColor.RED + name + " has lost all of their lives."
                + ChatColor.WHITE + " They are now permanently dead unless" + ChatColor.BLUE
                + " revived by another player" + ChatColor.WHITE + ".");
        tempPlayer.setGameMode(GameMode.SPECTATOR);
    }

    public static void handleRevive(Player p) {
        Bukkit.broadcastMessage(p.getName() + ChatColor.BLUE + " has been revived" + ChatColor.WHITE
                + ", and now must " +  ChatColor.RED + "kill at least 1 player per session "
                + ChatColor.WHITE + "to remain in the game.");

        p.setGameMode(GameMode.SURVIVAL);
        p.teleport(Bukkit.getWorld("world").getSpawnLocation());
        p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_CELEBRATE, 2, 1);
    }

    private static void clearOnFinalDeath(Player player, World world, Location location) {
        // clear player inventory in case they are revived
        ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
        String command = "clear " + player.getName();
        Bukkit.dispatchCommand(console, command);

        world.strikeLightningEffect(location).setSilent(true);
        // play the lightning sound for everyone
        for (Player p : world.getPlayers()) {
            Location tempLocation = p.getPlayer().getLocation();
            p.getPlayer().playSound(tempLocation, Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 1, 0);
        }
    }

    public static void giveTaskBook(Task task, Player p) {
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK, 1);
        TPlayer tempPlayer = new TPlayer(p.getName());
        setBookMeta(book, task, tempPlayer);

        // if the player's inventory is full
        if (p.getInventory().firstEmpty() == -1) {
            p.getLocation().getWorld().dropItemNaturally(p.getLocation(), book);
            p.sendMessage(ChatColor.RED + "Your inventory is full! A book has been dropped with your task.");
            p.performCommand("whattask");
            if (Objects.equals(task.getName(), "tag")) {
                p.sendMessage(ChatColor.RED + "Your inventory is full! A tracking compass has been dropped. Right click to locate a player (does not auto-update).");
                p.getLocation().getWorld().dropItemNaturally(p.getLocation(), createTrackingCompass());
            }
        } else {
            p.getInventory().addItem(book);
            if (Objects.equals(task.getName(), "tag")) {
                p.getInventory().addItem(createTrackingCompass());
                p.sendMessage(ChatColor.YELLOW + "You've received a tracking compass! Right click to locate a player (does not auto-update).");
            }
        }
    }

    public static void bookCountdown(Task task, Player p) {
        p.sendTitle("Receiving task in...", "", 10, 40, 10);

        BukkitRunnable runnable = new BukkitRunnable() {
            int time = 5;

            @Override
            public void run() {
                if (time == 0) {
                    // give book and make sure it doesn't keep giving the book
                    giveTaskBook(task, p);
                    p.playSound(p.getLocation(), Sound.ITEM_TOTEM_USE, 1, 1);
                    CustomParticleEffect.playTotemEffect(p);

                    this.cancel(); // cancel the task
                    return;
                }

                p.sendTitle(ChatColor.YELLOW + String.valueOf(time), "", 0, 21, 0);
                p.playNote(p.getLocation(), Instrument.BELL, Note.flat(0, Note.Tone.C));

                time--;
            }
        };

        runnable.runTaskTimer(plugin, 60L, 20L);
    }

    private static void setBookMeta(ItemStack book, Task task, TPlayer tempPlayer) {
        BookMeta meta = (BookMeta) book.getItemMeta();

        ChatColor messageColor = null;
        String difficultyText = null;

        // set the colour based on difficulty
        if (task.getDifficulty() == 0) {
            messageColor = ChatColor.GREEN;
            difficultyText = "Normal";
        } else if (task.getDifficulty() == 1) {
            messageColor = ChatColor.GOLD;
            difficultyText = "Hard";
        } else if (task.getDifficulty() == 2) {
            messageColor = ChatColor.RED;
            difficultyText = "Red";
        } else if (task.getDifficulty() == 3) {
            messageColor = ChatColor.DARK_AQUA;
            difficultyText = "Shiny";
        }

        // format the book properly
        meta.setTitle("Your Task");
        meta.setAuthor("VGS Life Series");

        // split description into segments of 268 characters
        String playerDescription = tempPlayer.getTaskDescription();
        List<String> pages = new ArrayList<>();
        StringBuilder currentPage = new StringBuilder("Task Difficulty: " + messageColor
                + difficultyText + ChatColor.BLACK + "\n");

        int charCount = currentPage.length();
        for (String word : playerDescription.split("\\s+")) {
            if (charCount + word.length() + 1 <= 268) {
                // add word to current page
                currentPage.append(word).append(" ");
                charCount += word.length() + 1;
            } else {
                // start the new page
                pages.add(currentPage.toString());
                currentPage = new StringBuilder();
                currentPage.append(word).append(" ");
                charCount = word.length() + 1;
            }
        }

        if (currentPage.length() > 0) {
            pages.add(currentPage.toString());
        }

        meta.setPages(pages);

        // i love hard coding stuff
        if (Objects.equals(task.getName(), "president")) {
            pages = new ArrayList<>();

            char c[] = tempPlayer.getTaskDescription().toCharArray();
            c[0] = Character.toLowerCase(c[0]);
            String taskDescription = new String(c);

            pages.add("Task Difficulty: " + messageColor + difficultyText + "\n"
                    + ChatColor.BLACK + "Good afternoon, " + tempPlayer.getName() + ", and congratulations on making "
                    + "your way to the primaries!\n\nYou see, you're quite the hot-shot politican, and are ready to "
                    + ChatColor.DARK_AQUA + "run for president of the server!\n");

            pages.add(ChatColor.BLACK + "However, the other party has gotten quite popular, too -"
                    + " so you must" + ChatColor.DARK_AQUA + " win over the members of the server through a session long campaign."
                    + ChatColor.BLACK + "\n\nYour full task description can be found on the next page.");

            pages.add(ChatColor.BLACK + "Your task is to " + taskDescription + "\n\nYou should both meet up and"
                    + ChatColor.DARK_AQUA + " discuss a time to hold the vote at spawn"
                    + ChatColor.BLACK + ", near the end of the session, after your campaigns.");

            pages.add(ChatColor.BLACK + "As you're of such high esteem, " + ChatColor.DARK_AQUA
                    + "no yellow players are allowed to guess your task." + ChatColor.BLACK + "\n\nHowever, "
                    + "you are " + ChatColor.DARK_AQUA + "not allowed to manually fail this task.\n\n"
                    + ChatColor.BLACK + "To reiterate, " + ChatColor.RED + "you are NOT allowed to manually fail this task.");

            pages.add(ChatColor.BLACK + "You will not be punished if you are not elected, though winning the election"
                    + " will" + ChatColor.DARK_AQUA + " award you a large prize" + ChatColor.BLACK
                    + ", along with a " + ChatColor.DARK_AQUA + "special rare item.\n\n"
                    + ChatColor.BLACK + "Good luck, future president! Your country salutes you.");
        }

        if (Objects.equals(task.getName(), "trivia")) {
            pages = new ArrayList<>();
            pages.add("Task Difficulty: " + messageColor + difficultyText + "\n"
                    + ChatColor.BLACK + "Your task is to build a place to host a trivia game and get at least 3 "
                    + "players to participate. The questions should be based on the previous life server (examples "
                    + "are given in the book if you didn't participate), but can be about any part of the server you "
                    + "want.");
            pages.add("The winner should also be announced to the chat, as they will be given a prize as well. You "
                    + "may build the place to host the trivia game wherever you like on the server, it just must be "
                    + "on the surface. Turn to the next page for some example questions. Scoring is up to your discretion.");
            pages.add("1. Name at least 2 players who won the previous life server.\n\n"
                    + "Acceptable answers: Harry (OrcaHedral), Jon (SpectralBlue), Scarlett (scarwe), Dylan (OperationDusty),"
                    + " \nLucy (UndeadP0WER), Tal (Tal_Doesnt_Exist), Ollie (FizzyKinkajou).");
            pages.add("2. What caused Harry (OrcaHedral)'s first death?\n\n"
                    + "Answer: His ender pearl glitched in the end, causing him to fall into the void.");
            pages.add("3. Who had the most kills in the previous life server?\n\n"
                    + "Acceptable answers: Evil Harry (Spectralwraith), Chris (LightingLord)");
            pages.add("4. What was the weapon that William (TerrariaTrees) used to attack Scarlett (scarwe) with called?\n\n"
                    + "Answer: Scarlett Slayer (The Scarlett Slayer is also acceptable).");
        }

        meta.setPages(pages);
        book.setItemMeta(meta);
    }

    private static ItemStack createTrackingCompass() {
        ItemStack compass = new ItemStack(Material.COMPASS);
        ItemMeta meta = compass.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + "Tracking Compass");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Use this compass to track");
        lore.add(ChatColor.GRAY + "other players.");
        meta.setLore(lore);

        // set a custom tag to identify this as a tracking compass
        meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "trackingCompass"), PersistentDataType.BYTE, (byte) 1);

        compass.setItemMeta(meta);
        return compass;
    }

    public static void removeBook(Player player) {
        Inventory inv = player.getInventory();
        for (ItemStack is : inv) {
            // skip over non items
            if (is == null || is.getType().equals(Material.AIR)) {
                continue;
            }
            if (!is.hasItemMeta()) {
                continue;
            }
            if (is.getType() != Material.WRITTEN_BOOK) {
                continue;
            }
            // get book info and remove from inventory
            BookMeta meta = (BookMeta) is.getItemMeta();
            if (Objects.equals(meta.getAuthor(), "VGS Life Series")) {
                is.setAmount(0);
            }
        }
    }
}
