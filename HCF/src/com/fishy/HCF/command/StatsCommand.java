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

import com.fishy.hcf.HCF;
import com.fishy.hcf.util.base.BukkitUtils;

public class StatsCommand implements CommandExecutor
{
    public boolean onCommand(CommandSender sender, Command command, String arg, String[] args) {
    	
		if (args.length == 0) {
			sender.sendMessage("�cUsage: /stats <player>");
			return true;
		}
    	
    	
        Player player = (Player)sender;
		Player target = Bukkit.getPlayer(args[0]);
    	
		if (target == null) {
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cPlayer with name or UUID with '" + args[0] + "' not found."));
			return true;
		}
		
        player.sendMessage(ChatColor.DARK_GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
        player.sendMessage(ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + target.getName() + "'s Stats");
        player.sendMessage("");
        player.sendMessage(ChatColor.WHITE + "Kills: " + ChatColor.GREEN + HCF.getPlugin().getUserManager().getUser(target.getUniqueId()).getKills());
        player.sendMessage(ChatColor.WHITE + "Deaths: " + ChatColor.RED + HCF.getPlugin().getUserManager().getUser(target.getUniqueId()).getDeaths());
        player.sendMessage(ChatColor.DARK_GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
        return false;
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
