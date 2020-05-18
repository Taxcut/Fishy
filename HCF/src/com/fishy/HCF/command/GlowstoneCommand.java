package com.fishy.hcf.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.fishy.hcf.HCF;
import com.fishy.hcf.util.SerializableLocation;
import com.fishy.hcf.util.base.BukkitUtils;
import com.sk89q.worldedit.bukkit.selections.Selection;

public class GlowstoneCommand implements CommandExecutor {

	private final HCF plugin;

	public GlowstoneCommand(HCF plugin) {
		this.plugin = plugin;
		new GlowstoneRunnable().runTaskTimer(plugin, 0L, 20 * 60 * 15);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!sender.hasPermission("command.glowstone")) {
			sender.sendMessage(ChatColor.RED + "You do not have permission for this command.");
			return true;
		}

		if (args.length == 0 || args.length > 1) {
            sender.sendMessage(ChatColor.DARK_GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
            sender.sendMessage(ChatColor.GOLD.toString() + ChatColor.BOLD + "Glowstone Mountain Help");
            sender.sendMessage("");
            sender.sendMessage("�e/glowstone set �7- �fSets the location for Glowstone Mountain. �7(Make sure you have made a selection with WorldEdit)");
            sender.sendMessage("�e/glowstone reset �7- �fResets Glowstone Mountain.");
            sender.sendMessage(ChatColor.DARK_GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
				return true;
		}

		if (args[0].equalsIgnoreCase("reset"))
			this.resetGlowstoneMountain();
		else if (args[0].equalsIgnoreCase("set")) {
			Player player = (Player) sender;
			Selection selection = this.plugin.getWorldEdit().getSelection(player);

			if (selection == null) {
				player.sendMessage(ChatColor.RED + "You need to make a selection first!");
				return true;
			}

			SerializableLocation min = new SerializableLocation(selection.getMinimumPoint()),
					max = new SerializableLocation(selection.getMaximumPoint());

			plugin.getConfig().set("Location.glowstone.min", min);
			plugin.getConfig().set("Location.glowstone.max", max);
			plugin.saveConfig();

			player.sendMessage(ChatColor.GREEN + "Set glowstone mountain location!");
		}

		return false;
	}

	public void resetGlowstoneMountain() {
		if (plugin.getConfig().get("Location.glowstone.min") == null
				|| plugin.getConfig().get("Location.glowstone.max") == null)
			return;

		Location min = ((SerializableLocation) plugin.getConfig().get("Location.glowstone.min")).getLocation();
		Location max = ((SerializableLocation) plugin.getConfig().get("Location.glowstone.max")).getLocation();

		if (min == null || max == null)
			return;

		int minY = min.getBlockY(), maxY = max.getBlockY();
		Block block = null;

		for (; minY <= maxY; minY++) {
			for (int x = min.getBlockX(); x <= max.getBlockX(); x++) {
				for (int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
					block = min.getWorld().getBlockAt(x, minY, z);
					
					if(!block.getChunk().isLoaded()) {
						block.getChunk().load();
						continue;
					}
						
					block.setType(Material.GLOWSTONE);
				}
			}
		}
		Bukkit.broadcastMessage(
				ChatColor.translateAlternateColorCodes('&', "&eThe &6&lGlowstone Mountain &ehas been &6&lreset&e."));
		Bukkit.broadcastMessage(
				ChatColor.translateAlternateColorCodes('&', "&eThe &6&lGlowstone Mountain &ewill reset in &615 minutes&e."));
	}

	public class GlowstoneRunnable extends BukkitRunnable {

		@Override
		public void run() {
			GlowstoneCommand.this.resetGlowstoneMountain();
		}

	}

}
