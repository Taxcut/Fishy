package com.fishy.hcf.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TphereCommand implements CommandExecutor {

    public boolean isDouble(String s) {
        try{
            Double.parseDouble(s);
            return true;
        } catch(NumberFormatException e) {
            return false;
        }
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

    	if (args.length == 1) {
    		Player player = (Player) sender;
            Player target = Bukkit.getPlayer(args[0]);
    		if (target == null) {
    			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cPlayer with name or UUID with '" + args[0] + "' not found."));
    			return true;
    		}
                target.teleport(player);
                player.sendMessage("�fYou have teleported " + target.getName() + " to your location.");
                return true;
            }
            sender.sendMessage("�cUsage: /tphere <player>");
            return true;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> results = new ArrayList<>();
        for (Player target : Bukkit.getOnlinePlayers()) {
            if (!results.contains(target.getName())) {
                results.add(target.getName());
            }
        }
        return results;
    }
}
