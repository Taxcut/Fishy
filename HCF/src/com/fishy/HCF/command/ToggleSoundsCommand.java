package com.fishy.hcf.command;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.fishy.hcf.HCF;
import com.fishy.hcf.user.FactionUser;
import com.fishy.hcf.util.MessageEvent;

public class ToggleSoundsCommand implements CommandExecutor, Listener {

	private final HCF plugin;

	public ToggleSoundsCommand(HCF plugin) {
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
			return true;
		}
		Player player = (Player) sender;
		FactionUser baseUser = this.plugin.getUserManager().getUser(player.getUniqueId());
		boolean newMessagingSounds = !baseUser.isMessagingSounds() || args.length >= 2 && Boolean.parseBoolean(args[1]);
		baseUser.setMessagingSounds(newMessagingSounds);
		sender.sendMessage(ChatColor.RED + "Messaging sounds are now "
				+ (newMessagingSounds ? new StringBuilder().append(ChatColor.GREEN).append("on").toString()
						: new StringBuilder().append(ChatColor.RED).append("off").toString())
				+ ChatColor.RED + '.');
		return true;
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerMessage(MessageEvent event) {
		Player recipient = event.getRecipient();
		FactionUser recipientUser = this.plugin.getUserManager().getUser(recipient.getUniqueId());
		if (recipientUser.isMessagingSounds()) {
			recipient.playSound(recipient.getLocation(), Sound.ORB_PICKUP, 1.0f, 1.0f);
		}
	}
}
