package com.fishy.hcf.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.fishy.hcf.HCF;

import net.md_5.bungee.api.ChatColor;

public class FlyCommand implements CommandExecutor {

	public FlyCommand(HCF plugin) {
		
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			if (sender.hasPermission("command.fly") || sender.hasPermission("command.fly.other")) {
				if (args.length == 0) {
					if (sender.hasPermission("command.fly")) {
						Player player = (Player) sender;

						if(player.isFlying()) {
							player.setAllowFlight(false);
							player.setFlying(false);
						} else {
							player.setAllowFlight(true);
							player.setFlying(true);
						}
						
						player.sendMessage((ChatColor.WHITE + "Your flight has been set to %status%.").replace("%status%", player.isFlying() ? "true" : "false"));
					} else {
						sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
						return true;
					}
				} else if (args.length == 1) {
					if (sender.hasPermission("command.fly.other")) {
						Player target = Bukkit.getPlayer(args[0]);

						if (target == null) {
		                    sender.sendMessage(ChatColor.RED + "That player is currently offline.");
							return true;
						}
						
						if(target.isFlying()) {
							target.setAllowFlight(false);
							target.setFlying(false);
						} else {
							target.setAllowFlight(true);
							target.setFlying(true);
						}
						
						sender.sendMessage((ChatColor.WHITE + target.getName() + "'s flight has been set to %status%").replace("%module%", target.getDisplayName() + "'s Flight").replace("%status%",
										(target.isFlying() ? "true" : "false")));
					} else {
						sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
						return true;
					}
				} else {
					sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <player>");
					return true;
				}
			}
		}
		return false;
	}

}
