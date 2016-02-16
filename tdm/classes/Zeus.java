/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.m1v3rpwn.tdm.classes;

import java.util.HashMap;
import me.m1v3rpwn.tdm.main.Util;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

/**
 *
 * @author m1v3rpwn
 */
public class Zeus extends TDMClass {

    HashMap<String, Integer> zeusrunnables = new HashMap<>();

    public Zeus(Util u) {
        util = u;
        name = "zeus";
        armor = new ItemStack[]{
            new ItemStack(Material.LEATHER_BOOTS),
            new ItemStack(Material.LEATHER_LEGGINGS),
            new ItemStack(Material.LEATHER_CHESTPLATE),
            null};

        LeatherArmorMeta meta = (LeatherArmorMeta) armor[0].getItemMeta();
        meta.setColor(Color.SILVER);
        armor[0].setItemMeta(meta);
        armor[1].setItemMeta(meta);
        armor[2].setItemMeta(meta);

        inventory = new ItemStack[]{
            new ItemStack(Material.BLAZE_ROD),
            util.MEDPACK,
            new ItemStack(Material.DIAMOND_HOE)};

        ItemMeta imeta = inventory[0].getItemMeta();
        imeta.setDisplayName(ChatColor.DARK_AQUA + "Zeus's Lightning Bolt");
        inventory[0].setItemMeta(imeta.clone());

        imeta.setDisplayName(ChatColor.YELLOW + "Lightning cooldown meter");
        inventory[2].setItemMeta(imeta.clone());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void launchLightning(PlayerInteractEvent e) {
        if (!me.m1v3rpwn.tdm.main.Main.me.players.contains(e.getPlayer())) {
            return;
        }
        if (!util.main.curclasses.get(e.getPlayer()).equals("zeus")) {
            return;
        }
        if (!e.getItem().getType().equals(Material.BLAZE_ROD) || e.getPlayer().getInventory().getItem(e.getPlayer().getInventory().first(Material.DIAMOND_HOE)).getDurability() >= 20) {
            return;
        }
        if (e.getPlayer().getTargetBlock(null, 0).isEmpty()) {
            return;
        }
        e.getPlayer().getInventory().getItem(e.getPlayer().getInventory().first(Material.DIAMOND_HOE)).setDurability((short) 1520);

        Entity bolt = e.getPlayer().getWorld().strikeLightningEffect(e.getPlayer().getTargetBlock(null, 0).getLocation());
        boolean red = util.isOnRed(e.getPlayer());
        for (Entity e1 : bolt.getNearbyEntities(3, 50, 3)) {
            if (e1 instanceof Player) {
                Player p = (Player) e1;
                if (red == util.isOnRed(p)) {
                    continue;
                }
                switch (util.main.curclasses.get(p)) {
//                                     The rather strange damage system for lightning, it either instakills if the target's health is below the threshold or does a fixed percent of the player's health, shown beneath the line.
                    case "sniper":
                        p.damage(p.getHealth() < 8 ? 10000 : p.getHealth() * 0.625, e.getPlayer());
//                                        45%
                        break;
                    case "zeus":
                        p.damage(3);
                        break;
                    case "brewer":
                        p.damage(p.getHealth() < 6 ? 10000 : p.getHealth() * 0.43478, e.getPlayer());
//                                        40%
                        break;
                    case "fighter":
                        p.damage(p.getHealth() < 5 ? 10000 : p.getHealth() * 1.71875, e.getPlayer());
//                                        55%
                        break;
                    case "climber":
                        p.damage(p.getHealth() < 5 ? 10000 : p.getHealth() * 1.375, e.getPlayer());
//                                        55%
                        break;
                    case "priest":
                        p.damage(p.getHealth() < 7 ? 10000 : p.getHealth() * 0.757576, e.getPlayer());
//                                        45%
                        break;
                    case "tank":
                        p.damage(p.getHealth() < 9 ? 10000 : p.getHealth() * 1.75, e.getPlayer());
                    //35%
                    }
            }
        }
        final BukkitTask can = new ZeusRunnable(e.getPlayer(), this).runTaskTimer(util.main, 1, 1);
        new BukkitRunnable() {
            @Override
            public void run() {
                can.cancel();
            }
        }.runTaskLater(util.main, 78);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void noHoeFighting(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player && util.main.curclasses.get((Player) e.getDamager()).equals("zeus") && ((Player) e.getDamager()).getItemInHand().getType().equals(Material.DIAMOND_HOE)) {
            e.setCancelled(true);
        }
    }
}

class ZeusRunnable extends BukkitRunnable {

    Player p;
    Zeus z;

    public ZeusRunnable(Player pl, Zeus ze) {
        p = pl;
        z = ze;
    }

    @Override
    public void run() {

        if (p.getInventory().first(Material.DIAMOND_HOE) > -1) {
            ItemStack is = p.getInventory().getItem(p.getInventory().first(Material.DIAMOND_HOE));
            is.setDurability((short) (is.getDurability() - 19));
            p.getInventory().setItem(p.getInventory().first(Material.DIAMOND_HOE), is);
            if (is.getDurability() == 0) {
                cancel();
                z.zeusrunnables.remove(p.getName());
            }
        } else {
            cancel();
            z.zeusrunnables.remove(p.getName());
        }
    }
}
