package com.fishy.hcf.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.fishy.hcf.HCF;
import com.fishy.hcf.user.FactionUser;

public class ToggleMessagesCommand implements CommandExecutor {

	private final HCF plugin;

	public ToggleMessagesCommand(HCF plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "This command is only executable for players.");
			return true;
		}
		Player player = (Player) sender;
		FactionUser baseUser = this.plugin.getUserManager().getUser(player.getUniqueId());
		boolean newToggled = !baseUser.isMessagesVisible();
		baseUser.setMessagesVisible(newToggled);
		sender.sendMessage(ChatColor.RED + "You have turned private messages "
				+ (newToggled ? new StringBuilder().append(ChatColor.GREEN).append("on").toString()
						: new StringBuilder().append(ChatColor.RED).append("off").toString())
				+ ChatColor.RED + '.');
		return true;
	}
}
