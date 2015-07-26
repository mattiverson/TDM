/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.m1v3rpwn.tdm.main;

import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author m1v3rpwn
 */
public class PlayerEvents implements Listener {

    Main plugin;
    public static HashMap<String, Integer> healrunnables = new HashMap<>(), zeusrunnables = new HashMap<>();
    ArrayList<String> chatters = new ArrayList<>(), overflowchatters = new ArrayList<>();
    String finalkill = "";

    public PlayerEvents(Main instance) {
        plugin = instance;
    }

    @EventHandler
    public void rightClick(final PlayerInteractEvent e) {
        if (!plugin.players.contains(e.getPlayer())) {
            return;
        }
        if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (e.getItem() != null && e.getItem().getType().equals(Material.COOKIE)) {
//                Heals the player when they use their medpack.
                e.setCancelled(true);
                if (e.getPlayer().getHealth() < 20 && e.getPlayer().getExp() == 0.99f) {
                    e.getPlayer().setExp(0.04f);

                    if (e.getPlayer().getHealth() > 18) {
                        e.getPlayer().setHealth(20);
                    } else {
                        e.getPlayer().setHealth(e.getPlayer().getHealth() + 2);
                    }
                    healrunnables.put(e.getPlayer().getName(), plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new HealCooldownRunnable(e.getPlayer(), this), 1, 1));
                }
            }
        }
    }

    public void PlayerInit(Player p) {
        plugin.players.add(p);
        plugin.getServer().dispatchCommand(p, "fighter");
        p.setLevel(0);
        p.setScoreboard(plugin.eloscores.getScoreboard());
        if (plugin.maingame != -1) {
            if (plugin.redTeam.getSize() >= plugin.blueTeam.getSize()) {
                plugin.blueTeam.addPlayer(p);
                p.sendMessage("You are on the " + ChatColor.BLUE + "Blue " + ChatColor.WHITE + "team!");
            } else {
                plugin.redTeam.addPlayer(p);
                p.sendMessage("You are on the " + ChatColor.DARK_RED + "Red " + ChatColor.WHITE + "team!");
            }
        }
//        Gives the player a starting elo of 1000 if they aren't in the system already.
        FileConfiguration elos = YamlConfiguration.loadConfiguration(plugin.dat);
        if (elos.contains(p.getName())) {
            plugin.realelo.getScore(p).setScore(elos.getInt(p.getName()));
        } else {
            plugin.realelo.getScore(p).setScore(1000);
            elos.set(p.getName(), 1000);
        }
        plugin.eloscores.getScore(Bukkit.getOfflinePlayer(plugin.util.getTeamColor(p) + p.getName())).setScore(plugin.realelo.getScore(p).getScore());
    }

    @EventHandler
    public void storeRating(PlayerQuitEvent e) {
        if (!plugin.players.contains(e.getPlayer())) {
            return;
        }
        if (plugin.blueTeam.hasPlayer(e.getPlayer())) {
            plugin.blueTeam.removePlayer(e.getPlayer());
        } else {
            plugin.redTeam.removePlayer(e.getPlayer());
        }
        plugin.players.remove(e.getPlayer());

        FileConfiguration elos = YamlConfiguration.loadConfiguration(plugin.dat);
        elos.set(e.getPlayer().getName(), plugin.realelo.getScore(e.getPlayer()).getScore());
    }

    @EventHandler
    public void changeRatings(PlayerDeathEvent e) {
        if (!plugin.players.contains(e.getEntity())) {
            return;
        }
        e.getDrops().clear();
        e.setDeathMessage(null);
//        Increments the death count and resets the streak of the killed player
        plugin.deaths.getScore(e.getEntity()).setScore(plugin.deaths.getScore(e.getEntity()).getScore() + 1);
        plugin.streak.getScore(e.getEntity()).setScore(0);
        if (plugin.redTeam.hasPlayer(e.getEntity())) {
//            Increments blue team's score
            plugin.teamScores.getScore(Bukkit.getOfflinePlayer("blue")).setScore(plugin.teamScores.getScore(Bukkit.getOfflinePlayer("blue")).getScore() + 1);
//            Checks to see if this kill ended the game
            if (plugin.teamScores.getScore(Bukkit.getOfflinePlayer("blue")).getScore() == plugin.objkills) {
                plugin.overyet = true;
                if (e.getEntity().getKiller() != null) {
                    finalkill = "The " + ChatColor.BLUE + "Blue " + ChatColor.DARK_GREEN + "team's final point was scored by " + e.getEntity().getKiller().getName() + " (elo " + plugin.realelo.getScore(e.getEntity().getKiller()).getScore() + "), who killed " + e.getEntity().getName() + "(elo " + plugin.realelo.getScore(e.getEntity()).getScore() + ")!";
                } else {
                    finalkill = "The " + ChatColor.BLUE + "Blue " + ChatColor.DARK_GREEN + "team's final point was " + e.getEntity().getName() + "'s (elo " + plugin.realelo.getScore(e.getEntity()).getScore() + ") death!";
                }
            }
        } else if (plugin.blueTeam.hasPlayer(e.getEntity())) {
//            Increments red team's score
            plugin.teamScores.getScore(Bukkit.getOfflinePlayer("red")).setScore(plugin.teamScores.getScore(Bukkit.getOfflinePlayer("red")).getScore() + 1);
            //            Checks to see if this kill ended the game
            if (plugin.teamScores.getScore(Bukkit.getOfflinePlayer("red")).getScore() == plugin.objkills) {
                plugin.overyet = true;
                if (e.getEntity().getKiller() != null) {
                    finalkill = "The " + ChatColor.DARK_RED + "Red " + ChatColor.DARK_GREEN + "team's final point was scored by " + e.getEntity().getKiller().getName() + " (elo " + plugin.realelo.getScore(e.getEntity().getKiller()).getScore() + "), who killed " + e.getEntity().getName() + "(elo " + plugin.realelo.getScore(e.getEntity()).getScore() + ")!";
                } else {
                    finalkill = "The " + ChatColor.DARK_RED + "Red " + ChatColor.DARK_GREEN + "team's final point was " + e.getEntity().getName() + "'s (elo " + plugin.realelo.getScore(e.getEntity()).getScore() + ") death!";
                }
            }
        }
        if (e.getEntity().getKiller() != null) {
//            Updates the stats of the player credited with the kill.
            plugin.kills.getScore(e.getEntity().getKiller()).setScore(plugin.kills.getScore(e.getEntity().getKiller()).getScore() + 1);
            plugin.streak.getScore(e.getEntity().getKiller()).setScore(plugin.streak.getScore(e.getEntity().getKiller()).getScore() + 1);
//            Updates both players' elo ratings.
            int killerRating = plugin.realelo.getScore(e.getEntity().getKiller()).getScore();
            int victimRating = plugin.realelo.getScore(e.getEntity()).getScore();
            int ratingChange = 16 - (int) (killerRating / 25 - victimRating / 25);
            if (ratingChange > 0) {
                plugin.realelo.getScore(e.getEntity().getKiller()).setScore(killerRating + ratingChange);
                plugin.realelo.getScore(e.getEntity()).setScore(victimRating - ratingChange);
            }
//            A somewhat unusual death message.
            e.getEntity().sendMessage(ChatColor.YELLOW + "You were " + ChatColor.RED + "" + ChatColor.UNDERLINE + "" + ChatColor.BOLD + "violently murdered " + ChatColor.RESET + "" + ChatColor.YELLOW + "by " + e.getEntity().getKiller().getName() + " (elo -" + ratingChange + ")");
            e.getEntity().getKiller().sendMessage(ChatColor.YELLOW + "You " + ChatColor.RED + "" + ChatColor.UNDERLINE + "" + ChatColor.BOLD + "violently murdered " + ChatColor.RESET + ChatColor.YELLOW + e.getEntity().getName() + " (elo +" + ratingChange + ")");
        } else {
            plugin.realelo.getScore(e.getEntity()).setScore(plugin.realelo.getScore(e.getEntity()).getScore() - 5);
            e.getEntity().sendMessage(ChatColor.YELLOW + "You were " + ChatColor.RED + "" + ChatColor.UNDERLINE + "" + ChatColor.BOLD + "violently murdered!");
        }
//        If the recently deceased player was a priest, destroys their faith towers.
        if (plugin.buildings.containsValue(e.getEntity())) {
            for (Block b : plugin.buildings.keySet()) {
                if (plugin.buildings.get(b).equals(e.getEntity())) {
                    b.setTypeId(0);
                    b.getLocation().add(0, 1, 0).getBlock().setTypeId(0);
                }
            }
        }
    }

    @EventHandler
    public void noTeamKill(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
            Player p = (Player) e.getDamager();
            Player p1 = (Player) e.getEntity();
            if (!plugin.players.contains(p) || !plugin.players.contains(p1)) {
                return;
            }
            if (plugin.util.isOnRed(p) == plugin.util.isOnRed(p1)) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void noDrops(EntityDeathEvent e) {
        if (!(e.getEntity() instanceof Player)) {
            return;
        }
        if (!plugin.players.contains((Player) e.getEntity())) {
            return;
        }
        e.setDroppedExp(0);
        e.getDrops().clear();
    }

    @EventHandler
    public void resetPlayer(final PlayerRespawnEvent e) {
//        Teleports a player back to the match, after a slight delay.
        if (!plugin.players.contains(e.getPlayer())) {
            return;
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                plugin.getServer().dispatchCommand(e.getPlayer(), plugin.curclasses.get(e.getPlayer()));
                e.getPlayer().setFallDistance(0);
            }
        }.runTaskLater(plugin, 20);

    }

    @EventHandler
    public void noDrop(PlayerDropItemEvent e) {
        if (!plugin.players.contains(e.getPlayer())) {
            return;
        }
        e.setCancelled(true);
    }

    @EventHandler
    public void noPlace(BlockPlaceEvent e) {
        if (!plugin.players.contains(e.getPlayer())) {
            return;
        }
        if (!e.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void noBreak(BlockBreakEvent e) {
        if (!plugin.players.contains(e.getPlayer())) {
            return;
        }
        if (!e.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void chatProcessor(PlayerChatEvent e) {
        if (!plugin.players.contains(e.getPlayer())) {
            return;
        }
//        The server's chat system, as well as kicking players who spam (three messages in one five-second sending cycle).
        if (plugin.newchat < 4) {
            plugin.chat[0] = plugin.chat[1];
            plugin.chat[1] = plugin.chat[2];
            plugin.chat[2] = plugin.chat[3];
            plugin.chat[3] = plugin.util.getTeamColor(e.getPlayer()) + "<" + e.getPlayer().getName() + ChatColor.RESET + plugin.util.getTeamColor(e.getPlayer()) + ">" + ChatColor.RESET + e.getMessage();
            if (chatters.contains(e.getPlayer().getName())) {
                if (chatters.contains(e.getPlayer().getName() + "(1)")) {
                    plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "kick " + e.getPlayer().getName() + " " + ChatColor.RED + "You have been kicked for spamming.");
                    plugin.chatspam = true;
                } else {
                    chatters.add(e.getPlayer().getName() + "(1)");
                }
            } else {
                chatters.add(e.getPlayer().getName());
            }
        } else {
            plugin.chatoverflow.add(plugin.util.getTeamColor(e.getPlayer()) + "<" + ChatColor.WHITE + e.getPlayer().getName() + plugin.util.getTeamColor(e.getPlayer()) + ">" + ChatColor.RESET + ":" + e.getMessage());
            if (overflowchatters.contains(e.getPlayer().getName())) {
                if (overflowchatters.contains(e.getPlayer().getName() + "(1)") && overflowchatters.size() < 4) {
                    plugin.chatoverflow.remove(overflowchatters.indexOf(e.getPlayer().getName()));
                    plugin.chatoverflow.remove(overflowchatters.indexOf(e.getPlayer().getName() + "(1)"));
                    plugin.chatoverflow.remove(plugin.chatoverflow.size() - 1);

                }
            } else {
                overflowchatters.add(e.getPlayer().getName());
            }

        }
        e.setCancelled(true);
    }

    @EventHandler
    public void noBeaconInventory(InventoryOpenEvent e) {
//        Prevents players from opening beacon inventories.
        if (!(e.getPlayer() instanceof Player) || !plugin.players.contains((Player) e.getPlayer())) {
            return;
        }
        if (!(e.getInventory() instanceof PlayerInventory)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player) || !plugin.players.contains((Player) e.getWhoClicked())) {
            return;
        }
        if (e.getSlotType().equals(SlotType.ARMOR)) {
            e.setCancelled(true);
        }
    }
}
// The runnable used whenever someone takes a bite of that delicious cookie and regains up to a heart. This runnable will show the cooldown in the player's experience bar, and cancel itself when it finishes.

class HealCooldownRunnable extends BukkitRunnable {

    Player p;
    PlayerEvents e;

    public HealCooldownRunnable(Player pl, PlayerEvents e) {
        p = pl;
        this.e = e;
    }

    @Override
    public void run() {
        if (p.getExp() > 0.93f) {
            p.setExp(0.99f);

        } else {
            p.setExp(p.getExp() + 0.05f);
        }
        if (p.getExp() == 0.99f) {
            cancel();
            e.healrunnables.remove(p.getName());
        }
    }
}
