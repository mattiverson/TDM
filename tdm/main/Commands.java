/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.m1v3rpwn.tdm.main;

import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

/**
 *
 * @author m1v3rpwn
 */
public class Commands {

    public static final ArrayList<String> commandList;
    private Main main;

    static {
        commandList = new ArrayList<>();
        commandList.add("switch");
        commandList.add("hintson");
        commandList.add("hintsoff");
        commandList.add("sniper");
            commandList.add("brewer");
            commandList.add("fighter");
            commandList.add("climber");
            commandList.add("tank");
            commandList.add("zeus");
            commandList.add("priest");
    }

    public Commands(Main m) {
        main = m;
    }

    public boolean hasCommand(String label) {
        return commandList.contains(label);
    }

    public boolean executeCommand(Player sender, Command com, String label, String[] args) {
        switch (label.toLowerCase()) {
            case "switch":
                return switchCommand(sender);
            case "hintson":
                return showHintsCommand(sender, true);
            case "hintsoff":
                return showHintsCommand(sender, false);
            case "sniper":
            case "brewer":
            case "fighter":
            case "climber":
            case "tank":
            case "zeus":
            case "priest":
                return classCommand(sender, label);
        }
        return true;
    }

    private boolean switchCommand(Player p) {
        if (main.buildings.containsValue(p)) {
            for (Block b : main.buildings.keySet()) {
                if (main.buildings.get(b).equals(p)) {
                    b.setTypeId(0);
                    b.getLocation().add(0, 1, 0).getBlock().setTypeId(0);
                }
            }
        }
        if (main.redTeam.hasPlayer(p)) {
            main.redTeam.removePlayer(p);
            main.blueTeam.addPlayer(p);
            p.teleport(main.bluespawn);
            p.setBedSpawnLocation(main.bluespawn, true);
        } else {
            main.blueTeam.removePlayer(p);
            main.redTeam.addPlayer(p);
            p.teleport(main.redspawn);
            p.setBedSpawnLocation(main.redspawn, true);
        }
        Bukkit.dispatchCommand(p, main.curclasses.get(p));
        return true;
    }

    private boolean showHintsCommand(Player p, boolean show) {
        if (show) {
            if (main.ignoreTips.contains(p.getName())) {
                main.ignoreTips.remove(p.getName());
                p.sendMessage(ChatColor.AQUA + "Hints enabled.");
            }
        } else {
            if (!main.ignoreTips.contains(p.getName())) {
                main.ignoreTips.add(p.getName());
                p.sendMessage(ChatColor.AQUA + "Hints disabled.");
            }
        }
        return true;
    }

    private boolean classCommand(Player p, String label) {
        main.util.classes.get(label.toLowerCase()).apply(p);
        return true;
    }
}
