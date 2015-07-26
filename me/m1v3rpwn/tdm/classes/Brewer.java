/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.m1v3rpwn.tdm.classes;

import me.m1v3rpwn.tdm.main.Util;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

/**
 *
 * @author m1v3rpwn
 */
public class Brewer extends TDMClass {

    public Brewer(Util u) {
        util = u;
        name = "brewer";
        armor = new ItemStack[]{
            new ItemStack(Material.LEATHER_BOOTS),
            null,
            null,
            null};
        
        ItemStack resist = new Potion(PotionType.FIRE_RESISTANCE).splash().toItemStack(4);
        PotionMeta pm = (PotionMeta) resist.getItemMeta();
        pm.addCustomEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 400, 1), true);
        resist.setItemMeta(pm);
        
        ItemStack strength = new Potion(PotionType.STRENGTH).splash().toItemStack(5);
        pm = (PotionMeta) strength.getItemMeta();
        pm.addCustomEffect(new PotionEffect(PotionEffectType.SPEED, 7200, 1), true);
        pm.addCustomEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 7200, 0), true);
        strength.setItemMeta(pm);
        
        inventory = new ItemStack[]{
            new ItemStack(Material.WOOD_SWORD),
            util.MEDPACK,
            resist,
            new Potion(PotionType.REGEN, 1, true, true).splash().toItemStack(3),
            new Potion(PotionType.POISON, 3, true).splash().toItemStack(4),
            strength,
            new Potion(PotionType.WEAKNESS, 2, true).splash().toItemStack(4)};
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void nerfStrength(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player) || !(e.getDamager() instanceof Player)) {
            return;
        }
        if (!me.m1v3rpwn.tdm.main.Main.me.players.contains((Player)e.getEntity())) {
            return;
        }
        if (!me.m1v3rpwn.tdm.main.Main.me.players.contains((Player)e.getDamager())) {
            return;
        }
        if (((Player) e.getDamager()).hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
            e.setDamage(e.getDamage() * 0.65);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void brewerVulnerable(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player) || !(e.getDamager() instanceof Player)) {
            return;
        }
        if (!me.m1v3rpwn.tdm.main.Main.me.players.contains((Player)e.getEntity())) {
            return;
        }
        if (!me.m1v3rpwn.tdm.main.Main.me.players.contains((Player)e.getDamager())) {
            return;
        }
        Player p = (Player) e.getEntity();
        if (util.main.curclasses.get(p).equals("brewer")) {
            e.setDamage(e.getDamage() * 1.5);
        }
        
    }
}
