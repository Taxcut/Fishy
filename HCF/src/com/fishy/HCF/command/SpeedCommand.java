package com.fishy.hcf.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.fishy.hcf.HCF;

import net.md_5.bungee.api.ChatColor;
import net.minecraft.util.org.apache.commons.lang3.math.NumberUtils;

public class SpeedCommand implements CommandExecutor {

	private final HCF plugin;

	public SpeedCommand(HCF plugin) {
		this.plugin = plugin;
	}
	
	String message = ChatColor.WHITE + "Set %type% speed for %player% to %speed%.";

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			if (sender.hasPermission("command.speed")) {
				if (args.length == 0 || args.length > 2) {
					sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <speed 1-10> [player]");
					return true;
				}

				int speed = 0;
				float level = 0.0F;

				if (NumberUtils.isNumber(args[0])) {
					speed = Integer.valueOf(args[0]);

					switch (speed) {
					case 1:
						level = 0.1F;
						break;
					case 2:
						level = 0.2F;
						break;
					case 3:
						level = 0.3F;
						break;
					case 4:
						level = 0.4F;
						break;
					case 5:
						level = 0.5F;
						break;
					case 6:
						level = 0.6F;
						break;
					case 7:
						level = 0.7F;
						break;
					case 8:
						level = 0.8F;
						break;
					case 9:
						level = 0.9F;
						break;
					case 10:
						level = 1.0F;
						break;
					default:
						double closer = Math.abs(speed - 1) < Math.abs(speed - 10) ? 1 : 10;
						if (closer == 10)
							level = 1.0F;
						else
							level = 0.1F;
						break;
					}

				}

				if (args.length == 1) {
					// self
					Player player = (Player) sender;

					if (player.isFlying()) {
						player.setFlySpeed(level);
						player.sendMessage(message.toString().replace("%player%", player.getName())
								.replace("%speed%", "" + speed).replace("%type%", "flying"));
					} else {
						player.setWalkSpeed(level);
						player.sendMessage(message.toString().replace("%player%", player.getName())
								.replace("%speed%", "" + speed).replace("%type%", "walking"));
					}
				} else if (args.length == 2) {
					// target
					Player target = plugin.getServer().getPlayer(args[1]);

					if (target == null) {
						sender.sendMessage(ChatColor.RED + "That player is not online.");
						return true;
					}

					if (target.isFlying()) {
						target.setFlySpeed(level);
						sender.sendMessage(message.toString().replace("%player%", target.getName())
								.replace("%speed%", "" + speed).replace("%type%", "flying"));
					} else {
						target.setWalkSpeed(level);
						sender.sendMessage(message.toString().replace("%player%", target.getName())
								.replace("%speed%", "" + speed).replace("%type%", "walking"));
					}
				}
			} else {
				sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
				return true;
			}
		}
		return false;
	}

}
