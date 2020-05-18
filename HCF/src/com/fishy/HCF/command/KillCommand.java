package com.fishy.hcf.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.fishy.hcf.HCF;

import net.md_5.bungee.api.ChatColor;

public class KillCommand implements CommandExecutor {
	
	public void CommandKill(HCF plugin) {
	
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender instanceof Player) {
			if(sender.hasPermission("command.kill")) {
				if(args.length == 0 || args.length > 1) {
					sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <player>");
					return true;
				}
				Player target = Bukkit.getPlayer(args[0]);
				
				if(target == null) {
                    sender.sendMessage(ChatColor.RED + "That player is currently offline.");
					return true;
				}
				
				target.setHealth(0.0F);
				sender.sendMessage((ChatColor.WHITE + "You have killed %player%.").replace("%player%", target.getName()));
			} else {
				sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
				return true;
			}
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
