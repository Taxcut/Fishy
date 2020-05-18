package com.fishy.hcf.command;

import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.fishy.hcf.HCF;
import com.fishy.hcf.util.Color;

public class VanishCommand implements CommandExecutor, TabCompleter {
	private final HCF vanish = HCF.getPlugin();

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (player.hasPermission("hcf.command.vanish")) {
				if (args.length > 0) {
					player.sendMessage(Color.translate("&cUsage: /" + label));
				} else {
					if (vanish.getStaffModeListener().isVanished(player)) {
						vanish.getStaffModeListener().setVanished(player, false);
						player.sendMessage(Color.translate("&5� &dYou are &cno longer &dvanished."));
					} else {
						vanish.getStaffModeListener().setVanished(player, true);
						player.sendMessage(Color.translate("&5� &dYou are &enow &dvanished."));
					}
				}
			} else {
				sender.sendMessage(ChatColor.RED +"You do not have permission to use this command.");
			}
		} else {
			sender.sendMessage(Color.translate("&cYou can not execute this command on console."));
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if (args.length > 1) {
			return Collections.emptyList();
		}
		return null;
	}
}