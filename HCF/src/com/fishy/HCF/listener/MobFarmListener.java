package com.fishy.hcf.listener;

import org.bukkit.Material;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.ImmutableList;

import net.md_5.bungee.api.ChatColor;

public class MobFarmListener implements Listener {
	
	public static final ImmutableList<Material> ALLOWED = ImmutableList.of(
            Material.DIAMOND_PICKAXE, Material.GOLD_PICKAXE, Material.STONE_PICKAXE, Material.WOOD_PICKAXE, Material.IRON_PICKAXE,
            Material.DIAMOND_AXE, Material.IRON_AXE, Material.GOLD_AXE, Material.STONE_AXE, Material.WOOD_AXE,
            Material.DIAMOND_SPADE, Material.IRON_SPADE, Material.GOLD_SPADE, Material.STONE_SPADE, Material.WOOD_SPADE
			);

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent event){
        LivingEntity entity = event.getEntity();
        if(entity instanceof Animals || entity instanceof Creature) {
            if (entity.getKiller() != null) {
                Player killer = entity.getKiller();
                killer.giveExp((int) Math.round(event.getDroppedExp() * 1));
                if (!killer.getInventory().addItem(event.getDrops().toArray(new ItemStack[event.getDrops().size()])).isEmpty()) {
                    killer.sendMessage(ChatColor.RED + "Your inventory is full.");
                }
                event.getDrops().clear();
                event.setDroppedExp(0);
            }
        }
    }
}
