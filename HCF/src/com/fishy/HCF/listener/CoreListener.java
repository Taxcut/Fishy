package com.fishy.hcf.listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.Furnace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import com.fishy.hcf.HCF;

public class CoreListener implements Listener {

    private static final String DEFAULT_WORLD_NAME = "world";

    private final HCF plugin;

    public CoreListener(HCF plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        event.setRespawnLocation(Bukkit.getWorld(CoreListener.DEFAULT_WORLD_NAME).getSpawnLocation().add(0.5, 5, 0.5));
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();
            BlockState state = block.getState();
            if(state instanceof Furnace) {
                ((Furnace) state).setCookSpeedMultiplier(10.0);
            }

        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerSpawn(PlayerSpawnLocationEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPlayedBefore()) {
            plugin.getEconomyManager().addBalance(player.getUniqueId(), HCF.getPlugin().getConfig().getInt("STARTING_BALANCE"));
            player.teleport(new Location(player.getWorld(), 0, 73, 0));        
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (player.getWorld().getEnvironment() == World.Environment.NETHER && event.getBlock().getState() instanceof CreatureSpawner &&
                !player.hasPermission(ProtectionListener.PROTECTION_BYPASS_PERMISSION)) {

            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You cannot break spawners in the nether.");
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
            if (player.getWorld().getEnvironment() == World.Environment.NETHER && event.getItemInHand() != null && event.getItemInHand().getType() == Material.BED) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "You cannot place beds in the nether.");
            }
            if (player.getWorld().getEnvironment() == World.Environment.NETHER && event.getBlock().getState() instanceof CreatureSpawner &&
                    !player.hasPermission(ProtectionListener.PROTECTION_BYPASS_PERMISSION)) {

                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "You cannot place spawners in the nether.");
            }
    }
    
	@EventHandler
	void onTarg(EntityTargetEvent event) {
		if ((event.getEntityType() == EntityType.CREEPER) || (event.getEntityType() == EntityType.ZOMBIE) || (event.getEntityType() == EntityType.BLAZE) || 
				(event.getEntityType() == EntityType.SPIDER) || (event.getEntityType() == EntityType.CAVE_SPIDER) || (event.getEntityType() == EntityType.SLIME) || 
				(event.getEntityType() == EntityType.WITCH) || (event.getEntityType() == EntityType.SKELETON)) {
			event.setCancelled(true);
		}
	}

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        plugin.getVisualiseHandler().clearVisualBlocks(player, null, null, false);
        plugin.getUserManager().getUser(player.getUniqueId()).setShowClaimMap(false);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        plugin.getVisualiseHandler().clearVisualBlocks(player, null, null, false);
        plugin.getUserManager().getUser(player.getUniqueId()).setShowClaimMap(false);
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
    	event.setCancelled(true);
    }

    /*@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onChunkUnload(ChunkUnloadEvent event) {
        plugin.getVisualiseHandler().clearVisualBlocks(event.getChunk());
    }*/
}
