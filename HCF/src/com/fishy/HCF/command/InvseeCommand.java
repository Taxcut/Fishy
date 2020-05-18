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

public class InvseeCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "This command can be used only by players.");
			return true;
		}

		Player player = (Player) sender;
		
		if (args.length == 0 || args.length > 1) {
			player.sendMessage(ChatColor.RED + "Usage: /invsee <player>");
			return true;
		}

		Player target = Bukkit.getPlayer(args[0]);

		if (target == null) {
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cPlayer with name or UUID with '" + args[0] + "' not found."));
			return true;
		}

		player.openInventory(target.getInventory());
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
