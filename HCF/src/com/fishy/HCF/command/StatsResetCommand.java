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

public class StatsResetCommand implements CommandExecutor
{
    public boolean onCommand(CommandSender sender, Command command, String arg, String[] args) {
    	
		if (args.length == 0) {
			sender.sendMessage(ChatColor.RED + "Usage: /statsreset <player>");
			return true;
		}
    	
    	
        Player player = (Player)sender;
		Player target = BukkitUtils.playerWithNameOrUUID(args[0]);
    	
		if (target == null) {
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cPlayer with name or UUID with '" + args[0] + "' not found."));
			return true;
		}
		HCF.getPlugin().getEconomyManager().setBalance(target.getUniqueId(), HCF.getPlugin().getConfig().getInt("STARTING_BALANCE"));
		HCF.getPlugin().getUserManager().getUser(target.getUniqueId()).setKills(0);
		HCF.getPlugin().getUserManager().getUser(target.getUniqueId()).setDeaths(0);
		HCF.getPlugin().getUserManager().getUser(target.getUniqueId()).setSOTW(true);
		HCF.getPlugin().getDeathbanManager().setLives(player.getUniqueId(), 0);
        player.sendMessage(ChatColor.GREEN + "You have reset the stats of " + target.getName() + ".");
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
