package com.fishy.hcf.command;

import com.fishy.hcf.HCF;

import net.md_5.bungee.api.ChatColor;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HidePlayerCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
    	Player target = Bukkit.getPlayer(args[0]);
		
		if (args.length == 0 || args.length > 1) {
			sender.sendMessage(ChatColor.RED + "Usage: /hideplayer <player>");
			return true;
		}
    	
		if (target == null) {
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cPlayer with name or UUID with '" + args[0] + "' not found."));
			return true;
		}
		
		if (HCF.getPlugin().getStaffModeListener().isVanished(target)) {
			sender.sendMessage(ChatColor.RED + "You cannot show this player because they are already vanished.");
		} else {
			for (Player all : Bukkit.getOnlinePlayers()) {
				all.hidePlayer(target);
			}
			sender.sendMessage(ChatColor.GREEN + "You have hidden " + target.getName() + '.');
		}
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