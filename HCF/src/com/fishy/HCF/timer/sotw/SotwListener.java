package com.fishy.hcf.timer.sotw;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;

import com.fishy.hcf.HCF;

public class SotwListener implements Listener {

    private HCF plugin;

    public SotwListener(HCF plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player && event.getCause() != EntityDamageEvent.DamageCause.SUICIDE && plugin.getSotwTimer().getSotwRunnable() != null) {
            if (!HCF.getPlugin().getUserManager().getUser(event.getEntity().getUniqueId()).isSOTW()) {
                return;
            }
            event.setCancelled(true);

        }
     }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (plugin.getSotwTimer().getSotwRunnable() != null) {
            if (!HCF.getPlugin().getUserManager().getUser(event.getEntity().getUniqueId()).isSOTW()) {
                return;
            }
            event.setCancelled(true);

        }
     }
    
/*    @EventHandler
    public void onBowEvent(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player && plugin.getSotwTimer().getSotwRunnable() != null) {
            Player victim = (Player) event.getEntity();
            if (!HCF.getPlugin().getUserManager().getUser(event.getEntity().getUniqueId()).isSOTW()) {
                return;
            }
            victim.sendMessage(ChatColor.RED + "You cannot shoot bows whilst your " + ChatColor.YELLOW.toString() + ChatColor.BOLD + "SOTW Timer" + ChatColor.RED + " is active. " + ChatColor.GRAY + "(/sotw enable)");
            event.setCancelled(true);
        }
    }*/
    
/*    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onMove(PlayerMoveEvent event) {
    	Player player = event.getPlayer();
    	World world = Bukkit.getWorld("world");
    	if (plugin.getSotwTimer().getSotwRunnable() != null) {
    		if (player.getLocation().getBlockY() < -25) {
            	if (!HCF.getPlugin().getUserManager().getUser(event.getPlayer().getUniqueId()).isSOTW()) {
                	return;
            	}
            	player.teleport(world.getSpawnLocation().add(0.5, 0, 0.5));
    		}
    	}
    }*/

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getCause() != EntityDamageEvent.DamageCause.SUICIDE && plugin.getSotwTimer().getSotwRunnable() != null) {
            if (((event.getDamager() instanceof Player)) && ((event.getEntity() instanceof Player)))
            {
                Player damager = (Player)event.getDamager();
                if (!HCF.getPlugin().getUserManager().getUser(event.getDamager().getUniqueId()).isSOTW()) {
                    return;
                }
                damager.sendMessage(ChatColor.RED + "You cannot attack other players whilst your " + ChatColor.GREEN.toString() + ChatColor.BOLD + "SOTW Timer" + ChatColor.RED + " is active. " + ChatColor.GRAY + "(/sotw enable)");
                event.setCancelled(true);
            } else {
                return;
            }
        } else {
            return;
        }
    }
}
