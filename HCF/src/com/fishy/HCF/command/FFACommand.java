package com.fishy.hcf.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class FFACommand implements CommandExecutor{

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player.hasPermission("hcf.command.staffmode")) {
				player.sendMessage(ChatColor.RED + "Players have been given the FFA potion effects.");
			} else {
				player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999, 1));
				player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 999999, 0));
				player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 999999, 0));
			}
		}
		sender.sendMessage(ChatColor.GREEN + "You have given all players potion effects.");
		return true;
	}
}
