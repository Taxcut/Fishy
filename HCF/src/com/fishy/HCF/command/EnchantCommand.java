package com.fishy.hcf.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

import com.fishy.hcf.HCF;

import net.md_5.bungee.api.ChatColor;
import net.minecraft.util.org.apache.commons.lang3.math.NumberUtils;

public class EnchantCommand implements CommandExecutor {

	private final HCF plugin;

	public EnchantCommand(HCF plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			if (sender.hasPermission("command.enchant")) {
				if (args.length < 2 || args.length > 3) {
					sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <player> <enchantment> <level>");
					return true;
				}

				if (args.length == 3) {
					Player target = plugin.getServer().getPlayer(args[0]);

					if (target == null) {
	                    sender.sendMessage(ChatColor.RED + "That player is currently offline.");
						return true;
					}

					Enchantment enchantment = Enchantment.getByName(args[1].toUpperCase());

					int level = -1;
					if (NumberUtils.isNumber(args[2]))
						level = Integer.valueOf(args[2]);

					if (level == -1) {
						sender.sendMessage(ChatColor.RED + "Please pick a valid enchant level!");
						return true;
					}
					
					if (enchantment == null) {
						sender.sendMessage(ChatColor.RED + "Couldn't find enchantment " + args[0].toUpperCase() + ".");
						return true;
					}

					target.getItemInHand().addUnsafeEnchantment(enchantment, level);
					sender.sendMessage((ChatColor.WHITE + "Enchanted %player% with %enchantment% %level%.").replace("%player%", target.getName())
							.replace("%enchantment%", enchantment.getName()).replace("%level%", "" + level));
				} else if (args.length == 2) {
					Enchantment enchantment = Enchantment.getByName(args[0].toUpperCase());

					int level = -1;
					if (NumberUtils.isNumber(args[1]))
						level = Integer.valueOf(args[1]);

					if (level == -1) {
						sender.sendMessage(ChatColor.RED + "Please pick a valid enchant level!");
						return true;
					}

					if (enchantment == null) {
						sender.sendMessage(ChatColor.RED + "Couldn't find enchantment " + args[0].toUpperCase() + ".");
						return true;
					}

					((Player) sender).getItemInHand().addUnsafeEnchantment(enchantment, level);
					sender.sendMessage((ChatColor.WHITE + "Enchanted %player% with %enchantment% %level%.").replace("%player%", ((Player) sender).getDisplayName())
									.replace("%enchantment%", enchantment.getName()).replace("%level%", "" + level));
				}

			} else {
				sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
				return true;
			}
		}
		return false;
	}


}
