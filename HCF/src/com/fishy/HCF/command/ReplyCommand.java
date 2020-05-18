package com.fishy.hcf.command;

import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.fishy.hcf.HCF;
import com.fishy.hcf.user.FactionUser;
import com.fishy.hcf.util.MessageEvent;
import com.google.common.collect.Sets;

public class ReplyCommand implements CommandExecutor {

	private static final long VANISH_REPLY_TIMEOUT = TimeUnit.SECONDS.toMillis(45);
	private final HCF plugin;

	public ReplyCommand(HCF plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player target;
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "This command is only executable for players.");
			return true;
		}
		Player player = (Player) sender;
		UUID uuid = player.getUniqueId();
		FactionUser baseUser = this.plugin.getUserManager().getUser(uuid);
		UUID lastReplied = baseUser.getLastRepliedTo();
		target = lastReplied == null ? null : Bukkit.getPlayer(lastReplied);
		if (args.length < 1) {
			sender.sendMessage(ChatColor.RED + "Usage: /reply <text...>");
			if (lastReplied != null && player.canSee(target) && Bukkit.getPlayer(lastReplied).isOnline()) {
				sender.sendMessage(ChatColor.RED + "You are in a conversation with " + target.getName() + '.');
			}
			return true;
		}
		long millis = System.currentTimeMillis();
		if (target == null || !player.canSee(target)
				&& millis - baseUser.getLastReceivedMessageMillis() > VANISH_REPLY_TIMEOUT) {
			sender.sendMessage(ChatColor.RED + "There is no player to reply to.");
			return true;
		}
		String message = StringUtils.join(args, ' ');
		HashSet<Player> recipients = Sets.newHashSet(target);
		MessageEvent playerMessageEvent = new MessageEvent(player, recipients, message, false);
		Bukkit.getPluginManager().callEvent(playerMessageEvent);
		if (!playerMessageEvent.isCancelled()) {
			playerMessageEvent.send();
		}
		return true;
	}
}
