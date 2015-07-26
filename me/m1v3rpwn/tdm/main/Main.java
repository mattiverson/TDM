/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.m1v3rpwn.tdm.main;

import me.m1v3rpwn.tdm.classes.TDMClass;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

/**
 *
 * @author m1v3rpwn
 */
public class Main extends JavaPlugin {
//    If DEMOMODE is true, all players will have access to all classes.

    public static Main me;
    public Commands com;
    public HashMap<Player, String> curclasses = new HashMap<>();
    public HashMap<Block, Player> buildings = new HashMap<>();
    public ArrayList<Player> players = new ArrayList<>();
    public ArrayList<String> ignoreTips;
    public Location bluespawn = new Location(null, 1000, 70, 0), redspawn = new Location(null, 1100, 70, 0);
    public ArrayList<String> chatoverflow = new ArrayList<>();
    public String[] hud = new String[3], chat = {"", "", "", ""}, safechat = new String[4], tips = {"Map maker", "These hints can be enabled/disabled using /hintsoff and /hintson!", "Zeus has to wait 4 seconds between lightning strikes!", "Brewers take 50% more melee damage!", "Arrows will always fire straight!", "Right click with your Medpack to regenerate 1 heart!", "Your Medpack's cooldown is represented by your experience bar!", "Strength Potions only add 50% to attack, not 130%!", "You cannot break a faith tower by breaking the beacon!", "Climbers won't climb while breaking a faith tower!"};
    public boolean chatspam = false, specials = false, overyet = false;
    public final String chatLine = "--------------------------------------------------------";
    public int objkills = 100, newchat = 0, pregame = -1, maingame = -1, postgame = -1;
    public File dat = new File("plugins/M1TDM.yml");
    public PlayerEvents listen = new PlayerEvents(this);
    public Util util = new Util(this);
    public Objective eloscores, realelo, teamScores, kills, deaths, streak;
    public Scoreboard teamScoreboard;
    public Team redTeam, blueTeam;

    @Override
    public void onEnable() {
        me = this;
//        prepares scoreboard
        eloscores = Bukkit.getScoreboardManager().getNewScoreboard().registerNewObjective("eloscores", "dummy");
        eloscores.setDisplaySlot(DisplaySlot.SIDEBAR);
        eloscores.setDisplayName(ChatColor.AQUA + "Top Ratings");
        
        teamScoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

        redTeam = teamScoreboard.registerNewTeam("Red");
        redTeam.setPrefix(ChatColor.RED + "");
        redTeam.setAllowFriendlyFire(false);

        blueTeam = teamScoreboard.registerNewTeam("Blue");
        blueTeam.setPrefix(ChatColor.BLUE + "");
        blueTeam.setAllowFriendlyFire(false);

        teamScores = teamScoreboard.registerNewObjective("teamScores", "dummy");
        kills = teamScoreboard.registerNewObjective("kills", "dummy");
        deaths = teamScoreboard.registerNewObjective("deaths", "dummy");
        streak = teamScoreboard.registerNewObjective("streak", "dummy");
        realelo = teamScoreboard.registerNewObjective("realelo", "dummy");
//        prepares stats and registers scoreboard with players
        for (Player p : players) {
            curclasses.put(p, "fighter");
            deaths.getScore(p).setScore(0);
            kills.getScore(p).setScore(0);
            streak.getScore(p).setScore(0);
            p.setScoreboard(eloscores.getScoreboard());
        }
//        registers events to handlers
        this.getServer().getPluginManager().registerEvents(listen, this);
        for (TDMClass cl : util.classes.values()) {
            this.getServer().getPluginManager().registerEvents(cl, this);
        }
//        sets the world for respawn locations
        bluespawn.setWorld(Bukkit.getWorlds().get(0));
        redspawn.setWorld(Bukkit.getWorlds().get(0));
//        creates data file if it doesn't exist
        if (!dat.exists()) {
            try {
                dat.createNewFile();
                FileConfiguration data = YamlConfiguration.loadConfiguration(dat);
                data.save(dat);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
//            loads in data if data file does exist
        } else {
            FileConfiguration data = YamlConfiguration.loadConfiguration(dat);
            for (Player p : players) {
                int elorate = data.getInt(p.getName());
                eloscores.getScore(p).setScore(elorate);
            }
        }
//        starts the runnable to update scoreboard elos and animate them.
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player p : players) {
                    int real = realelo.getScore(p).getScore();
                    int disp = eloscores.getScore(p).getScore();
                    if (real == disp) {
                        continue;
                    }
                    if (disp - real > 1) {
                        eloscores.getScore(p).setScore(disp - 2);
                    } else if (disp - real < -1) {
                        eloscores.getScore(p).setScore(disp + 2);
                    } else {
                        eloscores.getScore(p).setScore(real);
                    }
                }
            }
        }.runTaskTimer(this, 10, 10);
//        sets the pregame, and thus the whole game, in motion.
        preGame();
    }

    @Override
    public void onDisable() {
//        Updates everyone's elo rating.
        FileConfiguration config = YamlConfiguration.loadConfiguration(dat);
        for (Player p: players) {
            config.set(p.getName(), realelo.getScore(p).getScore());
        }
        try {
//            Bukkit.broadcastMessage(config.saveToString());
            config.save(dat);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player p = (Player) sender;
        if (com.hasCommand(label)) {
            return com.executeCommand(p, command, label, args);
        }
        return false;
    }

    public void preGame() {
        final long starttime = System.currentTimeMillis() + 30000;
        List<World> worlds = Bukkit.getWorlds();
//        Selects the world to play.
        for (World w : Bukkit.getWorlds()) {
            if (!w.getEnvironment().equals(Environment.NORMAL)) {
                worlds.remove(w);
            }
        }
        final World w = worlds.get((int) (Math.random() * worlds.size()));
        w.setPVP(false);
        pregame = this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new BukkitRunnable() {
            @Override
            public void run() {
                for (Player p : players) {
                    p.sendMessage(ChatColor.GREEN + "The Match will start soon! Map: " + w.getName());
                    if (((int) ((starttime - System.currentTimeMillis()) / 1000)) < 10) {
                        p.sendMessage(ChatColor.GREEN + "Time until start: 0:0" + ((int) ((starttime - System.currentTimeMillis()) / 1000)) + ", Current class: " + ChatColor.DARK_AQUA + curclasses.get(p));
                    } else {
                        p.sendMessage(ChatColor.GREEN + "Time until start: 0:" + ((int) ((starttime - System.currentTimeMillis()) / 1000)) + ", Current class: " + ChatColor.DARK_AQUA + curclasses.get(p));
                    }
                    p.sendMessage(ChatColor.DARK_GREEN + tips[(int) (Math.random() * tips.length)]);
                    p.sendMessage(chat);
                    for (int i = 0; i < 3; i++) {
                        p.sendMessage("");
                    }
                }
            }
        }, 19, 19);
//        19 ticks instead of 20 to make sure that it doesn't skip a second if it lags a little.
        this.getServer().getScheduler().scheduleSyncDelayedTask(this, new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : players) {
                    kills.getScore(player).setScore(0);
                    deaths.getScore(player).setScore(0);
                    streak.getScore(player).setScore(0);
                }
                mainGame();
            }
        }, 600);
//        Gives all of the players a class.
        for (Player p : players) {
            this.getServer().dispatchCommand(p, "fighter");
        }
    }

    public void mainGame() {
        this.getServer().getScheduler().cancelTask(pregame);
        pregame = -1;
        bluespawn.getWorld().setPVP(true);
        for (Player p : players) {
//            Sorts players into teams.
            if (blueTeam.getSize() > redTeam.getSize()) {
                redTeam.addPlayer(p);
                p.teleport(redspawn);

                eloscores.getScore(Bukkit.getOfflinePlayer(ChatColor.BLUE + p.getName())).setScore(-10000);
                eloscores.getScore(Bukkit.getOfflinePlayer(ChatColor.BLUE + p.getName())).setScore(realelo.getScore(p).getScore());
                p.sendMessage("You are on the " + ChatColor.DARK_RED + "Red " + ChatColor.WHITE + "team!");
            } else {
                blueTeam.addPlayer(p);
                p.teleport(bluespawn);
                p.sendMessage("You are on the " + ChatColor.BLUE + "Blue " + ChatColor.WHITE + "team!");
            }
        }
//        Schedules main game runnable.
        maingame = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new BukkitRunnable() {
            @Override
            public void run() {
//                Part of the system for detecting spam.
                if (chatspam) {
                    chat = safechat;
                }
//                    If the game has ended, change to the post game runnable. Otherwise, send all of the players the hud.
                if (overyet) {
                        postGame();
                        return;
                }
                for (Player p : players) {
                    
//                        Builds the hud.
                        StringBuilder prog = new StringBuilder();
                        for (int i = 0; i < 25 * teamScores.getScore(Bukkit.getOfflinePlayer("blue")).getScore() / objkills; i++) {
                            prog.append("i!");
                        }
                        prog.append(ChatColor.WHITE);
                        for (int i = 0; i < 25 - (25 * teamScores.getScore(Bukkit.getOfflinePlayer("blue")).getScore() / objkills); i++) {
                            prog.append("i!");
                        }
                        StringBuilder sb = new StringBuilder();
                        sb.append(ChatColor.BLUE + "Blue:");
                        sb.append(prog.toString());
                        sb.append(" Kills: " + teamScores.getScore(Bukkit.getOfflinePlayer("blue")).getScore() + "/" + objkills);
                        hud[0] = sb.toString();
                        prog = new StringBuilder();
                        sb = new StringBuilder();
                        for (int i = 0; i < 25 * teamScores.getScore(Bukkit.getOfflinePlayer("red")).getScore() / objkills; i++) {
                            prog.append("i!");
                        }
                        prog.append(ChatColor.WHITE);
                        for (int i = 0; i < 25 - (25 * teamScores.getScore(Bukkit.getOfflinePlayer("red")).getScore() / objkills); i++) {
                            prog.append("i!");
                        }
                        sb.append(ChatColor.DARK_RED + "Red:");
                        sb.append(prog.toString());
                        sb.append(" Kills: " + teamScores.getScore(Bukkit.getOfflinePlayer("red")).getScore() + "/" + objkills);
                        hud[1] = sb.toString();
                        hud[2] = ChatColor.GREEN + "Kills: " + ChatColor.WHITE + kills.getScore(p).getScore() + ChatColor.GREEN + "( " + ChatColor.WHITE + streak.getScore(p).getScore() + ChatColor.GREEN + " in a row) Deaths: " + ChatColor.WHITE + deaths.getScore(p).getScore();
                        p.sendMessage(util.getTeamColor(p) + chatLine);
                        if (ignoreTips.contains(p.getName())) {
                            p.sendMessage(ChatColor.DARK_GREEN + tips[0]);
                        } else {
                            p.sendMessage(ChatColor.DARK_GREEN + tips[(int) (Math.random() * tips.length)]);
                        }
                        p.sendMessage(hud);
                        p.sendMessage(util.getTeamColor(p) + chatLine);
                        p.sendMessage(chat);

                    newchat = 0;
                }
//                Fills in the chat after sending the previous chat.
                if (chatoverflow.size() > 3) {
                    newchat = 4;
                    for (int i = 0; i < 4; i++) {
                        chat[ 3 - i] = chatoverflow.get(i);
                    }
                    for (int i = 4; i < chatoverflow.size(); i++) {
                        chatoverflow.set(i - 4, chatoverflow.get(i));
                    }
                    for (int i = -4; i < 0; i++) {
                        chatoverflow.remove(chatoverflow.size() + i);
                    }
                } else if (chatoverflow.size() > 0) {
                    for (String s : chatoverflow) {
                        chat[0] = chat[1];
                        chat[1] = chat[2];
                        chat[2] = chat[3];
                        chat[3] = s;
                        newchat++;
                    }
                }
                safechat = chat;
                listen.chatters = new ArrayList<>();
            }
        }, 100, 100);
//        Schedules the priest runnable.
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new BukkitRunnable() {
            @Override
            public void run() {
//                Loops through all of the faith towers (placed by Priest), and gives their effects to all players in range.
                for (Block b : buildings.keySet()) {
                    boolean isred = false;
                    switch (b.getType()) {
                        case REDSTONE_BLOCK:
                        case NETHERRACK:
                            isred = true;
                            break;
                        case DIAMOND_BLOCK:
                        case LAPIS_BLOCK:
                            isred = false;
                            break;
                        case WOOL:
                            isred = b.getData() == 14;
                            break;
                    }
                    Entity e = b.getWorld().spawnEntity(b.getLocation().subtract(0, b.getY() - 1, 0), EntityType.ARROW);
                    for (Entity pl : e.getNearbyEntities(5, 100, 5)) {
                        if (pl instanceof Player) {
                            if (redTeam.hasPlayer((Player) pl) == isred) {
                                switch (b.getType()) {
                                    case REDSTONE_BLOCK:
                                    case DIAMOND_BLOCK:
                                        if (!((Player) pl).isDead()) {
                                            if (((Player) pl).getHealth() < 20) {
                                                if (((Player) pl).getHealth() <= 17) {
                                                    ((Player) pl).setHealth(((Player) pl).getHealth() + 3);
                                                } else {
                                                    ((Player) pl).setHealth(20);
                                                }
                                            }
                                        }
                                        break;
                                    case NETHERRACK:
                                    case LAPIS_BLOCK:
                                        ((Player) pl).addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 30, 0));
                                        break;
                                    case WOOL:
                                        ((Player) pl).addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 30, 2), true);
                                        break;
                                }
                            } else {
                                switch (b.getType()) {
                                    case REDSTONE_BLOCK:
                                    case DIAMOND_BLOCK:
                                        ((Player) pl).setHealth(((Player) pl).getHealth() - 1);
                                        break;
                                    case NETHERRACK:
                                    case LAPIS_BLOCK:
                                        ((Player) pl).addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20, 0), true);
                                        break;
                                    case WOOL:
                                        ((Player) pl).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20, 1), true);
                                        break;
                                }
                            }
                        }
                    }
//                    A slightly more fun way to remove the entity.
                    e.setVelocity(e.getVelocity().setY(-25));
                }
            }
        }, 20, 20);
    }

    public void postGame() {
        this.getServer().getScheduler().cancelTask(maingame);
        maingame = -1;
        for (Block b : buildings.keySet()) {
            b.setTypeId(0);
            b.getLocation().add(0, 1, 0).getBlock().setTypeId(0);
            bluespawn.getWorld().setPVP(false);
        }
        postgame = this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new BukkitRunnable() {
            @Override
            public void run() {
                for (Player p : players) {
//                Sends postgame chat to all of the players.
                    p.sendMessage(ChatColor.GREEN + "Game Over! The next match will start shortly!");
                    p.sendMessage(ChatColor.DARK_GREEN + listen.finalkill);
                    p.sendMessage(ChatColor.DARK_AQUA + "Final Score: " + ChatColor.BLUE + "Blue: " + ChatColor.DARK_AQUA + new Integer(teamScores.getScore(Bukkit.getOfflinePlayer("blue")).getScore()).toString() + " Red: " + ChatColor.DARK_RED + "Red: " + ChatColor.DARK_AQUA + new Integer(teamScores.getScore(Bukkit.getOfflinePlayer("red")).getScore()).toString());
                    p.sendMessage(ChatColor.GREEN + "Your Stats | Kills: " + ChatColor.WHITE + kills.getScore(p).getScore() + ChatColor.GREEN + "( " + ChatColor.WHITE + streak.getScore(p).getScore() + ChatColor.GREEN + " in a row) Deaths: " + ChatColor.WHITE + deaths.getScore(p).getScore());
                }
            }
        }, 100, 100);
        this.getServer().getScheduler().scheduleSyncDelayedTask(this, new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.reload();
            }
        }, 300);
    }

}
