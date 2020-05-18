package com.fishy.hcf.listener;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import com.fishy.hcf.HCF;
import com.fishy.hcf.faction.type.Faction;

public class GlowstoneListener implements Listener {

	private final HCF plugin;

	public GlowstoneListener(HCF plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();

		Faction faction = plugin.getFactionManager().getFactionAt(block);
		if (block.getType().equals(Material.GLOWSTONE)
				&& block.getLocation().getWorld().getName().equalsIgnoreCase("world_nether")) {
			if (faction.getName().equalsIgnoreCase("Glowstone")) {
				event.setCancelled(true);
				block.setType(Material.AIR);
				player.getInventory().addItem(new ItemStack(Material.GLOWSTONE));
			}
		}
	}

}
