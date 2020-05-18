package com.fishy.hcf.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.fishy.hcf.HCF;

public class NickCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		if(!(sender instanceof Player)){
			sender.sendMessage(ChatColor.RED + "Only players can execute this command.");
			return true;
		}
		if(args.length < 1){
			sender.sendMessage(ChatColor.RED + "Usage: /nick <nickname/none>");
			return true;
		}
		Player player = (Player) sender;
		if (args[0].equals(player.getName()) || args[0].isEmpty() || args[0].length() > 16) {
			sender.sendMessage(ChatColor.RED + "Invalid nickname.");
			return true;
		}
		if (args[0].equals("none")) {
			sender.sendMessage(ChatColor.WHITE + "Your nickname has been reset.");
			HCF.getPlugin().getUserManager().getUser(player.getUniqueId()).setNick(null);
			return true;
		}
		HCF.getPlugin().getUserManager().getUser(player.getUniqueId()).setNick(args[0]);
		sender.sendMessage(ChatColor.WHITE + "Your nick is now " + ChatColor.GRAY + args[0]);
		return true;
	}
}

