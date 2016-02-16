/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.m1v3rpwn.tdm.main;

import me.m1v3rpwn.tdm.classes.Sniper;
import me.m1v3rpwn.tdm.classes.Tank;
import me.m1v3rpwn.tdm.classes.TDMClass;
import me.m1v3rpwn.tdm.classes.Brewer;
import me.m1v3rpwn.tdm.classes.Zeus;
import me.m1v3rpwn.tdm.classes.Climber;
import me.m1v3rpwn.tdm.classes.Priest;
import me.m1v3rpwn.tdm.classes.Fighter;
import java.util.HashMap;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

/**
 *
 * @author m1v3rpwn
 */
public class Util {

    public Main main;
    
    public final HashMap<String, TDMClass> classes = new HashMap<>();

    public final ItemStack MEDPACK = new ItemStack(Material.COOKIE);

    public final ItemStack RED_HEAL = new ItemStack(Material.REDSTONE_BLOCK),
            BLUE_HEAL = new ItemStack(Material.DIAMOND_BLOCK),
            RED_SPEED = new ItemStack(Material.WOOL, 1, (short) 14),
            BLUE_SPEED = new ItemStack(Material.WOOL, 1, (short) 11),
            RED_STRENGTH = new ItemStack(Material.NETHERRACK),
            BLUE_STRENGTH = new ItemStack(Material.LAPIS_BLOCK);

    public final ItemStack RED_HELMET = new ItemStack(Material.LEATHER_HELMET), BLUE_HELMET = new ItemStack(Material.LEATHER_HELMET);

    public Util(Main m) {
        main = m;
        classes.put("sniper", new Sniper(this));
        classes.put("brewer", new Brewer(this));
        classes.put("fighter", new Fighter(this));
        classes.put("priest", new Priest(this));
        classes.put("tank", new Tank(this));
        classes.put("zeus", new Zeus(this));
        classes.put("climber", new Climber(this));

        ItemMeta meta = MEDPACK.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "Medpack");
        MEDPACK.setItemMeta(meta);

        meta = RED_HEAL.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "Healing Faith Tower");
        RED_HEAL.setItemMeta(meta);
        meta.setDisplayName(ChatColor.RED + "Strength Faith Tower");
        RED_STRENGTH.setItemMeta(meta);
        meta.setDisplayName(ChatColor.RED + "Speed Faith Tower");
        RED_SPEED.setItemMeta(meta);
        meta.setDisplayName(ChatColor.BLUE + "Healing Faith Tower");
        BLUE_HEAL.setItemMeta(meta);
        meta.setDisplayName(ChatColor.BLUE + "Strength Faith Tower");
        BLUE_STRENGTH.setItemMeta(meta);
        meta.setDisplayName(ChatColor.BLUE + "Speed Faith Tower");
        BLUE_SPEED.setItemMeta(meta);

        LeatherArmorMeta lameta = (LeatherArmorMeta) RED_HELMET.getItemMeta();
        lameta.setColor(Color.RED);
        RED_HELMET.setItemMeta(lameta);
        lameta.setColor(Color.BLUE);
        BLUE_HELMET.setItemMeta(lameta);
    }

    public boolean isOnRed(Player p) {
        if (main.redTeam.getPlayers().contains(p)) {
            return true;
        }
        return false;
    }

    public ChatColor getTeamColor(Player p) {
        if (isOnRed(p)) {
            return ChatColor.RED;
        } else {
            return ChatColor.BLUE;
        }
    }

    public ItemStack getHelmet(Player p) {
        if (isOnRed(p)) {
            return RED_HELMET;
        } else {
            return BLUE_HELMET;
        }
    }

    public ItemStack getMedpack() {
        return MEDPACK;
    }
}
