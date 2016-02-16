/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.m1v3rpwn.tdm.classes;

import me.m1v3rpwn.tdm.main.Util;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author m1v3rpwn
 */
public class Priest extends TDMClass {

    private ItemStack[] redInv, blueInv;

    public Priest(Util u) {
        util = u;
        name = "priest";
        armor = new ItemStack[]{
            new ItemStack(Material.IRON_BOOTS),
            new ItemStack(Material.GOLD_LEGGINGS),
            new ItemStack(Material.GOLD_CHESTPLATE),
            null};
        
        redInv = new ItemStack[]{
            new ItemStack(Material.GOLD_SWORD),
            util.MEDPACK,
            util.RED_HEAL,
            util.RED_STRENGTH,
            util.RED_SPEED};
        
        blueInv = new ItemStack[]{
            new ItemStack(Material.GOLD_SWORD),
            util.MEDPACK,
            util.BLUE_HEAL,
            util.BLUE_STRENGTH,
            util.BLUE_SPEED};
        
        inventory = new ItemStack[]{};
    }

    @Override
    public void apply(Player p) {
        super.apply(p);
        if (!p.hasPermission("class." + name) && !p.hasPermission("class.*") && !(this instanceof FreeClass)) {
            return;
        }
        if (util.isOnRed(p)) {
            p.getInventory().addItem(redInv);
        } else {
            p.getInventory().addItem(blueInv);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void placeTower(BlockPlaceEvent e) {
        if (!me.m1v3rpwn.tdm.main.Main.me.players.contains(e.getPlayer())) {
            return;
        }
        if (e.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
            return;
        }
        if (util.main.curclasses.get(e.getPlayer()).equals("priest") && util.main.maingame != -1) {
            util.main.buildings.put(e.getBlockPlaced(), e.getPlayer());
            e.getBlockPlaced().getLocation().add(0, 1, 0).getBlock().setType(Material.BEACON);
            e.setCancelled(false);
        } else {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void breakTower(BlockBreakEvent e)  {
        if (!me.m1v3rpwn.tdm.main.Main.me.players.contains(e.getPlayer())) {
            return;
        }
        if (e.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
            return;
        }
        if (util.main.buildings.containsKey(e.getBlock())) {
//                Determines the type of block that was broken, and returns it to its owner's inventory.
            switch (e.getBlock().getType()) {
                case REDSTONE_BLOCK:
                    util.main.buildings.get(e.getBlock()).getInventory().addItem(util.RED_HEAL);
                    break;
                case NETHERRACK:
                    util.main.buildings.get(e.getBlock()).getInventory().addItem(util.RED_STRENGTH);
                    break;
                case WOOL:
                    if (e.getBlock().getData() == 14) {
                        util.main.buildings.get(e.getBlock()).getInventory().addItem(util.RED_SPEED);
                    } else {
                        util.main.buildings.get(e.getBlock()).getInventory().addItem(util.BLUE_SPEED);
                    }
                    break;
                case LAPIS_BLOCK:
                    util.main.buildings.get(e.getBlock()).getInventory().addItem(util.BLUE_STRENGTH);
                    break;
                case DIAMOND_BLOCK:
                    util.main.buildings.get(e.getBlock()).getInventory().addItem(util.BLUE_HEAL);
                    break;
            }
            e.setCancelled(false);
            e.getBlock().getDrops().clear();
            util.main.buildings.remove(e.getBlock());
            e.getBlock().getLocation().add(0, 1, 0).getBlock().setTypeId(0);
        }
    }
}
