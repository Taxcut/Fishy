package com.fishy.hcf.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public class CopyInvCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "You must be a player");
			return false;
		}
		if (args.length != 1) {
			sender.sendMessage(ChatColor.RED + "Usage: /copyinv <player>");
			return false;
		}
		
		Player player = (Player) sender;
		Player target = Bukkit.getServer().getPlayerExact(args[0]);
		if (target == null) {
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cPlayer with name or UUID with '" + args[0] + "' not found."));
		} else {
            player.getInventory().setContents(target.getInventory().getContents());
            player.getInventory().setArmorContents(target.getInventory().getArmorContents());
            player.sendMessage(ChatColor.WHITE + "You have copied the inventory of " + ChatColor.GREEN + target.getName());
		}	
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
