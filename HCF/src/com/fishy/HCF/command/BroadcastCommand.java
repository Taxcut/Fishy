package com.fishy.hcf.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class BroadcastCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
			if (!sender.hasPermission("hcf.command.broadcast") && !sender.hasPermission("hcf.command.*") && !sender.hasPermission("*")) {
				sender.sendMessage(ChatColor.RED +"You do not have permission to use this command.");
				return true;
			}

			if (args.length == 0) {
				sender.sendMessage("�cUsage: /broadcast <msg>");
				return true;
			}
			StringBuilder str = new StringBuilder();

			for (int i = 0; i < args.length; i++) {
				str.append(args[i] + " ");
			}
			String msg = str.toString();

			Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', msg.toString()));
		return true;
	}
}
