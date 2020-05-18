package com.fishy.hcf.listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.metadata.FixedMetadataValue;

import com.fishy.hcf.HCF;

public class FoundDiamondsListener implements Listener{
	
	private final HCF plugin;
	
	public FoundDiamondsListener(final HCF plugin) {
		this.plugin = plugin;
	}

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event){
        if(event.getBlock().getType() == Material.DIAMOND_ORE){
            event.getBlock().setMetadata("fd", new FixedMetadataValue(plugin, true));
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event){
        if(event.getBlock().getType() == Material.DIAMOND_ORE && !event.getBlock().hasMetadata("fd")){
            int count = 0;
            for(int x = -5; x < 5; x++){
                for(int y = -5; y < 5; y++){
                    for(int z = -5; z < 5; z++){
                        Block block = event.getBlock().getLocation().add(x, y, z).getBlock();
                        if(block.getType() == Material.DIAMOND_ORE && !block.hasMetadata("fd")){
                            block.setMetadata("fd", new FixedMetadataValue(plugin, null));
                            count++;
                        }
                    }
                }
            }

            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&f[&b*&f] &b%player% &ffound &bdiamonds&f [&b" + count + "&f]").replace("%player%", event.getPlayer().getName()));
        }
    }
}
