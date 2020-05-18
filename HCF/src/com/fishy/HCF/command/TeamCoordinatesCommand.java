package com.fishy.hcf.command;

import com.fishy.hcf.faction.type.PlayerFaction;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.fishy.hcf.HCF;

public class TeamCoordinatesCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
		if (!(sender instanceof Player)) {
			return true;
		}

		Player player = (Player) sender;
		PlayerFaction playerFaction = HCF.getPlugin().getFactionManager().getPlayerFaction(player);

		if (playerFaction == null) {
			player.sendMessage(ChatColor.RED + "You don't have a faction.");
			return true;
		}
		String coordinates = player.getLocation().getBlockX() + ", " + player.getLocation().getBlockY() +  ", " + player.getLocation().getBlockZ();
		playerFaction.broadcast(ChatColor.translateAlternateColorCodes('&', "&a(Faction) " + sender.getName() + "&7: &f[" + coordinates + "]")); 
		return false;
	}
}
