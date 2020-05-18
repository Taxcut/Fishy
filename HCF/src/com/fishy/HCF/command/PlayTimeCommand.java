package com.fishy.hcf.command;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.fishy.hcf.HCF;
import com.fishy.hcf.util.base.BukkitUtils;

public class PlayTimeCommand implements CommandExecutor {

	@Override
	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		OfflinePlayer target;

		if (args.length >= 1) {
			target = BukkitUtils.offlinePlayerWithNameOrUUID(args[0]);
		} else {
			if (!(sender instanceof Player)) {
				sender.sendMessage("You can only use this if you are a player!");
				return true;
			}
			
			target = (OfflinePlayer) sender;
		}

		if ((!target.hasPlayedBefore()) && (!target.isOnline())) {
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cPlayer with name or UUID with '" + args[0] + "' not found."));
			return true;
		}
        sender.sendMessage(ChatColor.GREEN + target.getName() + ChatColor.WHITE + " has been playing for " + ChatColor.LIGHT_PURPLE + DurationFormatUtils.formatDurationWords(HCF.getPlugin().getPlayTimeManager().getTotalPlayTime(target.getUniqueId()), true, true) + ChatColor.WHITE + " this map.");
		//sender.sendMessage(ChatColor.translateAlternateColorCodes('&', ("").replace("%time%", DurationFormatUtils.formatDurationWords(HCF.getPlugin().getPlayTimeManager().getTotalPlayTime(target.getUniqueId()), true, true)).replace("%target%", target.getName())));
		return true;
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		return args.length == 1 ? null : Collections.emptyList();
	}

}