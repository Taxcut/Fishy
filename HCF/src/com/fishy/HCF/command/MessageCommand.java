package com.fishy.hcf.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

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
import com.fishy.hcf.util.base.BukkitUtils;

public class MessageCommand implements CommandExecutor {

	public MessageCommand(HCF plugin) {
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "This command is only executable for players.");
			return true;
		}
		if (args.length < 2) {
			sender.sendMessage(ChatColor.RED + "Usage: /message <playerName> <text...>");
			return true;
		}
		Player player = (Player) sender;
		Player target = BukkitUtils.playerWithNameOrUUID(args[0]);
		if (target == null || !player.canSee(target)) {
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cPlayer with name or UUID with '" + args[0] + "' not found."));
			return true;
		}

		FactionUser user = HCF.getPlugin().getUserManager().getUser(target.getUniqueId());
		if (!user.isMessagesVisible() && !player.hasPermission("hcf.command.staffmode")) {
			sender.sendMessage(ChatColor.RED + "You cannot private message that player as it has messages toggled.");
			return true;
		}
		String message = StringUtils.join(args, ' ', 1, args.length);
		Set<Player> recipients = Collections.singleton(target);
		MessageEvent playerMessageEvent = new MessageEvent(player, recipients, message, false);
		Bukkit.getPluginManager().callEvent(playerMessageEvent);
		if (!playerMessageEvent.isCancelled()) {
			playerMessageEvent.send();
		}
		return true;
	}
	
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> results = new ArrayList<>();
        for (Player target : Bukkit.getOnlinePlayers()) {
            results.add(target.getName());
        }
        return results;
    }
}
