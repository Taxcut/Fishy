package com.fishy.hcf.command;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.brawlspigot.util.chatcolor.CC;

public class FeedCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(CC.translate(CC.translate("&cYou're not a player you idiot.")));
			return true;

		}
		Player player = (Player) sender;
		if (!(player.hasPermission("hcf.command.feed"))) {
			player.sendMessage(CC.translate("&cNo Permission."));
			return true;
		}
		if (args.length == 0) {
			player.setFoodLevel(20);
			player.sendMessage(CC.translate("&5� &dYou have been &5&lfed&d."));
			return true;
		}

		if (args.length == 1) {
			if (!(player.hasPermission("hcf.command.feed.others"))) {
				player.sendMessage(CC.translate("&cNo Permission."));
				return true;
			}
			Player player2 = Bukkit.getPlayer(args[0]);
			if (player2 == null) {
				player.sendMessage(CC.translate("&cThat player is not online!"));
				return true;
			}
			player2.setFoodLevel(20);
			player2.sendMessage(CC.translate("&5� &dYou have been fed by &5&l" + player.getName() + "&d."));
			return true;

		}

		return false;
	}

}
