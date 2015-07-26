/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.m1v3rpwn.tdm.classes;

import me.m1v3rpwn.tdm.main.Util;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author m1v3rpwn
 */
public class Climber extends TDMClass implements FreeClass {

    public Climber(Util u) {
        util = u;
        name = "climber";
        armor = new ItemStack[]{
            new ItemStack(Material.DIAMOND_BOOTS),
            new ItemStack(Material.IRON_LEGGINGS),
            new ItemStack(Material.IRON_CHESTPLATE),
            null};
        
        ItemStack pick = new ItemStack(Material.DIAMOND_PICKAXE);
        ItemMeta im = pick.getItemMeta();
        im.setDisplayName(ChatColor.BLUE + "Climber's pickaxe");
        pick.setItemMeta(im);
        
        inventory = new ItemStack[]{
            new ItemStack(Material.DIAMOND_SWORD),
            util.MEDPACK,
            pick};
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void climbWall(PlayerInteractEvent e) {
        if (!me.m1v3rpwn.tdm.main.Main.me.players.contains(e.getPlayer())) {
            return;
        }
        if (e.getAction().equals(Action.LEFT_CLICK_BLOCK) && util.main.curclasses.get(e.getPlayer()).equals("climber") && e.getItem().getType().equals(Material.DIAMOND_PICKAXE) && e.getPlayer().getTargetBlock(null, 0).getLocation().add(0, 1, 0).getBlock().getTypeId() != 0 && e.getPlayer().getTargetBlock(null, 0).getLocation().add(0, 1, 0).getBlock().getTypeId() != 138) {
            e.getPlayer().setVelocity(e.getPlayer().getVelocity().setY(0.7));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void noFallDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) {
            return;
        }
        if (!me.m1v3rpwn.tdm.main.Main.me.players.contains((Player)e.getEntity())) {
            return;
        }
        if (e.getCause().equals(EntityDamageEvent.DamageCause.FALL) && e.getEntity() instanceof Player && util.main.curclasses.containsKey((Player)e.getEntity()) && util.main.curclasses.get(((Player) e.getEntity())).equals("climber")) {
            e.setDamage(0);
        }
    }
}
