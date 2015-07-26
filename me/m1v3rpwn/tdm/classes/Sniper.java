/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.m1v3rpwn.tdm.classes;

import me.m1v3rpwn.tdm.main.Util;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import me.m1v3rpwn.tdm.main.Main;
import org.bukkit.ChatColor;
import org.bukkit.event.EventPriority;

/**
 *
 * @author m1v3rpwn
 */
public class Sniper extends TDMClass implements FreeClass {

    public Sniper(Util u) {
        util = u;
        name = "sniper";
        armor = new ItemStack[]{
            new ItemStack(Material.CHAINMAIL_BOOTS),
            new ItemStack(Material.LEATHER_LEGGINGS),
            new ItemStack(Material.LEATHER_CHESTPLATE),
            null};
        
        ItemStack bow = new ItemStack(Material.BOW, Short.MIN_VALUE);
        bow.addEnchantment(Enchantment.ARROW_KNOCKBACK, 1);
        bow.addEnchantment(Enchantment.ARROW_INFINITE, 1);
        bow.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
        
        inventory = new ItemStack[]{
            new ItemStack(Material.STONE_SWORD),
            util.MEDPACK,
            bow,
            new ItemStack(Material.ARROW)};
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void doubleDamage(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Arrow)) {
            return;
        }
        if (!(e.getEntity() instanceof Player)) {
            return;
        }
        if (!(((Arrow) e.getDamager()).getShooter() instanceof Player)) {
            return;
        }
        Player p = (Player) ((Arrow) e.getDamager()).getShooter();
        Player p1 = (Player) e.getEntity();
        if (!me.m1v3rpwn.tdm.main.Main.me.players.contains(p)) {
            return;
        }
        if (!me.m1v3rpwn.tdm.main.Main.me.players.contains(p1)) {
            return;
        }
        if (util.isOnRed(p) == util.isOnRed(p1)) {
            e.setDamage(0);
            return;
        }
        if (util.main.curclasses.get(p).equals("sniper")) {
            e.setDamage(e.getDamage() * 1.9);
        }
    }
}
