package com.fishy.hcf.command;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MassayCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
			if (args.length == 0) {
				sender.sendMessage("�cUsage: /massay <msg>");
				return true;
			}
			
			for (Player player : Bukkit.getOnlinePlayers()) {
				player.chat(StringUtils.join(args, ' ', 0, args.length));
			}
		return true;
	}
}
