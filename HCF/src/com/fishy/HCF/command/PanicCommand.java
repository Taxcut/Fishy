package com.fishy.hcf.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.fishy.hcf.HCF;

public class PanicCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "freeze " + commandSender.getName());
        
        if ((!HCF.getPlugin().getFreezeListener().isFrozen((Player) commandSender))) {
            for (Player staff : Bukkit.getServer().getOnlinePlayers()) {
            	if (staff.hasPermission("panic.see")) {
            		staff.sendMessage(ChatColor.DARK_RED.toString() + ChatColor.BOLD + "[Panic] " + ChatColor.GRAY + commandSender.getName() + ChatColor.WHITE + " has left panic mode.");
            	}
            }	
        } else {
            for (Player staff : Bukkit.getServer().getOnlinePlayers()) {
            	if (staff.hasPermission("panic.see")) {
            		staff.sendMessage(ChatColor.DARK_RED.toString() + ChatColor.BOLD + "[Panic] " + ChatColor.GRAY + commandSender.getName() + ChatColor.WHITE + " has entered panic mode.");
            	}
            }	
        }
        
        commandSender.sendMessage(ChatColor.GREEN + "You have entered panic mode. Do not abuse this command as it will get you banned. If you would like to be taken out of panic mode, use /panic again.");
        
        return false;
    }
}
