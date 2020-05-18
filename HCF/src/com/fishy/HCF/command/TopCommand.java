package com.fishy.hcf.command;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import com.fishy.hcf.util.base.BukkitUtils;

import net.md_5.bungee.api.ChatColor;

public class TopCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "You must be a player");
			return false;
		}
		
		Player entity = (Player) sender;
		
        Location destination = BukkitUtils.getHighestLocation(entity.getLocation());
        if (destination != null) {
        	entity.teleport(destination.add(0.5, 1.0, 0.5), PlayerTeleportEvent.TeleportCause.PLUGIN);
        	sender.sendMessage(ChatColor.WHITE + "You have been teleported to the highest point.");
        	return false;
        } else {
        	sender.sendMessage(ChatColor.RED + "There is no highest point above you.");
        	return false;
        }
	}
}
