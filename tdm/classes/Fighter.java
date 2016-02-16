/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.m1v3rpwn.tdm.classes;

import me.m1v3rpwn.tdm.main.Util;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 *
 * @author m1v3rpwn
 */
public class Fighter extends TDMClass implements FreeClass {

    public Fighter(Util u) {
        util = u;
        name = "fighter";
        armor = new ItemStack[]{
            new ItemStack(Material.IRON_BOOTS),
            new ItemStack(Material.DIAMOND_LEGGINGS),
            new ItemStack(Material.DIAMOND_CHESTPLATE),
            null};
        inventory = new ItemStack[]{new ItemStack(Material.DIAMOND_SWORD), util.MEDPACK};
    }

    @Override
    public void apply(Player p) {
        super.apply(p);
        p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0), true);
    }
}
