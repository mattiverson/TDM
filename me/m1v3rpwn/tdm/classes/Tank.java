/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.m1v3rpwn.tdm.classes;

import me.m1v3rpwn.tdm.main.Util;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author m1v3rpwn
 */
public class Tank extends TDMClass {

    public Tank(Util u) {
        util = u;
        name = "tank";
        armor = new ItemStack[]{
            new ItemStack(Material.DIAMOND_BOOTS),
            new ItemStack(Material.DIAMOND_LEGGINGS),
            new ItemStack(Material.DIAMOND_CHESTPLATE),
            null};
        ItemStack sword = new ItemStack(Material.WOOD_SWORD);
        ItemMeta meta = sword.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Attack Redirect Sword");
        sword.setItemMeta(meta);
        inventory = new ItemStack[]{sword, util.MEDPACK};
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void attackRedirect(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player) || !(e.getEntity() instanceof Player)) {
            return;
        }
        Player p = (Player) e.getDamager();
        Player p1 = (Player) e.getEntity();
        if (!me.m1v3rpwn.tdm.main.Main.me.players.contains(p)) {
            return;
        }
        if (!me.m1v3rpwn.tdm.main.Main.me.players.contains(p1)) {
            return;
        }
//            Prevents team killing.
        if (util.isOnRed(p) == util.isOnRed(p1)) {
            e.setCancelled(true);
        } else {
            for (Entity en : p.getNearbyEntities(5, 5, 5)) {
//                    If the player attacked was near a tank of the same team, the tank takes the part of the hit for them.
                if (en instanceof Player && util.main.curclasses.get((Player) en).equals("tank") && ((Player) en).getItemInHand().getType().equals(Material.WOOD_SWORD) && !((Player) en).equals(p1) && !util.main.curclasses.get(p1).equals(util.main.curclasses.get((Player) en)) && ((Player) en).isBlocking()) {
                    Player t = (Player) en;
                    e.setDamage(e.getDamage() * 0.5);
                    t.damage(e.getDamage() * 1.25);
//                        Informs the players as to what has happened.
                    p.sendMessage(ChatColor.RED + "You attack was deflected by an enemy tank!");
                    p1.sendMessage(ChatColor.GREEN + t.getName() + " deflected an attack for you!");
                    t.sendMessage(ChatColor.GREEN + "You deflected an attack for " + p1.getName() + "!");
//                        Technically an aimbot, this code changes the attacker to looking at the tank, so they can target the tank first.
                    Location l = p.getLocation();
                    double d = p.getLocation().getX() - t.getLocation().getX();
                    double d1 = p.getLocation().getZ() - t.getLocation().getZ();
                    double d2 = d1 / d;
                    if (p.getLocation().getX() < t.getLocation().getX()) {
                        d2 = Math.toDegrees(Math.atan(d2));
                        d2 -= 90;
                    } else {
                        d2 = Math.toDegrees(Math.atan(d2));
                        d2 += 90;
                    }

                    l.setYaw((float) d2);
                    d2 = Math.sqrt(d * d + d1 * d1);
                    d = t.getLocation().getY() - p.getLocation().getY();
                    d2 = d / d2;
                    d2 = -Math.toDegrees(Math.atan(d2));
                    l.setPitch((float) d2);
                    p.teleport(l);
                    break;
                }
            }
        }
    }
}
