package com.fishy.hcf.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.fishy.hcf.HCF;

import net.md_5.bungee.api.ChatColor;

public class ClearCommand implements CommandExecutor {

	private final HCF plugin;

	public ClearCommand(HCF plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;

			if (!player.hasPermission("command.clear")) {
				player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
				return true;
			}

			if (args.length > 1) {
				player.sendMessage(ChatColor.RED.toString() + '/' + label + " [player]");
				return true;
			}

			if (args.length == 0) {
				try {
					int items = clearInventory(player);

					player.sendMessage(ChatColor.WHITE + "You have cleared your inventory.");
				} catch (NullPointerException exception) {
				}
			} else if (args.length == 1) {
				Player target = plugin.getServer().getPlayer(args[0]);

				if (target == null) {
					player.sendMessage(ChatColor.RED + "That player is offline.");
					return true;
				}

				try {
					int items = clearInventory(target);

					player.sendMessage(ChatColor.WHITE + "You have cleared " + target.getName() + "'s inventory.");
				} catch (NullPointerException exception) {
				}
			}

		}
		return false;
	}
	
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> results = new ArrayList<>();
        for (Player target : Bukkit.getOnlinePlayers()) {
            if (!results.contains(target.getName())) {
                results.add(target.getName());
            }
        }
        return results;
    }

	private int clearInventory(Player player) throws NullPointerException {
		if (player != null && player.isOnline()) {
			int items = 0;

			for (ItemStack item : player.getInventory().getContents())
				if (item != null && item.getType() != Material.AIR)
					items = items + item.getAmount();

			player.getInventory().clear();
			player.getInventory().setArmorContents(null);
			player.updateInventory();

			return items;
		}
		throw new NullPointerException();
	}

}
