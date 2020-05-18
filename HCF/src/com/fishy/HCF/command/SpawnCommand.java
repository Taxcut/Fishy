package com.fishy.hcf.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class SpawnCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("spawn")) {
			if (!(sender instanceof Player)) {
				return true;
			}
			if (!sender.hasPermission("hcf.command.spawn")) {
				sender.sendMessage(ChatColor.RED + "This server does not have a spawn command, so you must travel to spawn. The coordinates of spawn are 0, 0.");
				return true;
			}

			if (args.length == 0) {
				Player player = (Player) sender;
				World world = player.getWorld();
				Location spon = world.getSpawnLocation().clone().add(0.5, 0.5, 0.5);
				player.teleport(spon);
				player.sendMessage(ChatColor.GREEN + "You have been teleported to spawn.");
			}
			if(args.length == 1) {
				Player target = Bukkit.getPlayer(args[0]);

				if(target == null) {
					sender.sendMessage(ChatColor.RED + "That player is currently offline.");
					return true;
				}

				World world = target.getWorld();
				Location spon = world.getSpawnLocation().clone().add(0.5, 0.5, 0.5);
				target.teleport(spon);
				sender.sendMessage(ChatColor.GREEN + target.getName() + " has been teleported to spawn.");
			}
		}
		return false;
	}

}
