/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.m1v3rpwn.tdm.classes;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import me.m1v3rpwn.tdm.main.Util;
import org.bukkit.block.Block;

/**
 *
 * @author m1v3rpwn
 */
public abstract class TDMClass implements org.bukkit.event.Listener {
    Util util;
    public ItemStack[] armor;
    public ItemStack[] inventory;
    public String name;

    public void apply(Player p) {
        if (!p.hasPermission("class." + name) && !p.hasPermission("class.*") && !(this instanceof FreeClass)) {
            p.sendMessage(ChatColor.GREEN + "You don't have this class yet! Order it today at http://thisisaurlolzplaceholder.net!");
            return;
        }
        util.main.curclasses.put(p, name);
        if (p.getHealth() < 20) {
            p.damage(1000);
            return;
        } else {
            p.setHealth(20);
        }
        p.setExp(0.99f);
        p.setLevel(0);
        p.setFoodLevel(17);
        p.setSaturation(1000f);
        p.teleport(p.getBedSpawnLocation());
        for (PotionEffect pe : p.getActivePotionEffects()) {
            p.removePotionEffect(pe.getType());
        }
        if (util.main.buildings.containsValue(p)) {
            for (Block b : util.main.buildings.keySet()) {
                if (util.main.buildings.get(b).equals(p)) {
                    b.setTypeId(0);
                    b.getLocation().add(0, 1, 0).getBlock().setTypeId(0);
                }
            }
        }
        PlayerInventory pi = p.getInventory();
        pi.clear();
        pi.setArmorContents(armor);
        pi.setHelmet(util.getHelmet(p));
        if (inventory.length > 0) {
            pi.addItem(inventory);
        }
        util.main.streak.getScore(p).setScore(0);
    }
}
