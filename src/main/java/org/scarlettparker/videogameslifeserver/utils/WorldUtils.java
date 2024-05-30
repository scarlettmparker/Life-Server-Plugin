package org.scarlettparker.videogameslifeserver.utils;

import org.bukkit.*;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.scarlettparker.videogameslifeserver.objects.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class WorldUtils {
    static Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("VideoGamesLifeServer");

    public static List<Player> getAllPlayers() {
        return new ArrayList<>(Bukkit.getOnlinePlayers());
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
            colorPrefix = "&2";

            // wonderful formatting based on no. lives
            if (lives == 1) {
                lifeColor = ChatColor.RED;
                colorPrefix = "&c";
            } else if (lives == 2) {
                lifeColor = ChatColor.YELLOW;
                colorPrefix = "&e";
            } else if (lives == 3) {
                lifeColor = ChatColor.GREEN;
                colorPrefix = "&a";
            }
        } else {
            title = "DEAD";
            contrastColor = ChatColor.GRAY;
            lifeColor = ChatColor.GRAY;
            colorPrefix = "&7";
        }

        newName = lifeColor + p.getName() + contrastColor + " [" + lifeColor + title + contrastColor + "]";
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

    public static String manageReceiverDescription(String description, Player p) {
        List<Player> allPlayers = getAllPlayers();
        if (allPlayers.size() > 1) {
            allPlayers.remove(p.getPlayer());
        }
        int random = new Random().nextInt(allPlayers.size());
        Player pickedPlayer = allPlayers.get(random);
        return description.replace("{receiver}", pickedPlayer.getName());
    }

    public static String manageSenderDescription(String description, Player p) {
        return description.replace("{sender}", p.getName());
    }

    public static void giveTaskBook(Task task, Player p) {
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK, 1);
        setBookMeta(book, task);

        // if the player's inventory is full
        if (p.getInventory().firstEmpty() == -1) {
            p.getLocation().getWorld().dropItemNaturally(p.getLocation(), book);
            p.sendMessage(ChatColor.RED + "Your inventory is full! A book has been dropped with your task.");
            p.performCommand("whattask");
        } else {
            p.getInventory().addItem(book);
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

                    this.cancel(); // Cancel the task
                    return;
                }

                p.sendTitle(ChatColor.YELLOW + String.valueOf(time), "", 0, 21, 0);
                p.playNote(p.getLocation(), Instrument.BELL, Note.flat(0, Note.Tone.C));

                time--;
            }
        };

        runnable.runTaskTimer(plugin, 60L, 20L);
    }

    private static void setBookMeta(ItemStack book, Task task) {
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
            difficultyText = "Raven";
        }

        // format the book properly
        meta.setTitle("Your Task");
        meta.setAuthor("VGS Life Series");

        // split description into segments of 268 characters
        String playerDescription = task.getPlayerDescription();
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

        // trolling raven raven because it's funny
        if (task.getDifficulty() == 3) {
            pages = new ArrayList<>();
            pages.add("Task Difficulty: " + messageColor + difficultyText + "\n"
                    + ChatColor.BLACK + "Hello, " + ChatColor.RED + "RavingRaven" + ChatColor.BLACK
                    + ".\nAs you may be aware, you illegally gave nether wart to some players in the previous season. "
                    + "Unfortunately, we can't let you get away with that without punishment, so we have a special task"
                    + " just for you.\n\n");

            pages.add(ChatColor.BLACK + "Your task is to " + ChatColor.RED + "give nether wart that you have collected"
                    + " by yourself to a player of your choice " + ChatColor.BLACK +"before the end of the session"
                    + ChatColor.BLACK + ".\nLuckily, there are some perks to your task! No yellow player is allowed to"
                    + " guess your task, but you still must not reveal your task to anybody, or you fail.\n");
            pages.add(ChatColor.RED + "Failing this task will result in "
                    + "a hefty punishment" + ChatColor.BLACK + ", so chop chop!\n\n"
                    + "Much love, the VGS committee.\n\nEnjoy your season!");

            meta.setPages(pages);
        }

        book.setItemMeta(meta);
    }

    public static void removeBook(Player player) {
        Inventory inv = player.getInventory();
        for (ItemStack is : inv) {
            // skip over non items
            if (is == null || is.equals(Material.AIR)) {
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
