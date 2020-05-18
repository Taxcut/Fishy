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

public class TeleportCommand implements CommandExecutor {

	public boolean isDouble(String s) {
		  try{
		  Double.parseDouble(s);
		  return true;
		  } catch(NumberFormatException e) {
		  return false;
		  }
		}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (cmd.getName().equalsIgnoreCase("teleport")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED +"You do not have permission to use this command.");
				return true;
			}

			if (args.length == 1) {
				Player player = (Player) sender;
				Player target = Bukkit.getPlayer(args[0]);
				if (target == null) {
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cPlayer with name or UUID with '" + args[0] + "' not found."));
					return true;
				}
				player.teleport(target);
				sender.sendMessage("�fYou have been teleported to �f" + target.getName() + "�f.");
				return true;
			}

			if (args.length == 2) {
				if (!sender.hasPermission("hcf.command.teleport.others") && !sender.hasPermission("hcf.command.*") && !sender.hasPermission("*")) {
					sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
					return true;
				}
				Player player = Bukkit.getPlayer(args[0]);
				Player target = Bukkit.getPlayer(args[1]);
				if (player == null) {
				sender.sendMessage("�cPlayer " + args[0] + " offline.");
					return true;
				}
				if (target == null) {
					sender.sendMessage("�cPlayer " + args[1] + " offline.");
					return true;
				}
				player.teleport(target);
				sender.sendMessage("�fYou have teleported �f" + player.getName() + " �fto �f" + target.getName() + "�f.");
				return true;
			}

			sender.sendMessage("�cUsage: /teleport <player> <player>");
			return true;
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
