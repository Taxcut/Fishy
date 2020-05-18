package com.fishy.hcf.listener.fixes;

import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;

public class MobFixes implements Listener {
    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent e) {
        if (e.getEntity() instanceof Horse || (e.getEntity() instanceof Ghast || (e.getEntity() instanceof Slime || (e.getEntity() instanceof Skeleton && ((Skeleton) e.getEntity()).getSkeletonType() == Skeleton.SkeletonType.WITHER)))) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onEndermanDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Enderman || event.getDamager() instanceof MagmaCube || event.getDamager() instanceof Creeper || event.getDamager() instanceof Slime) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onCreeperExplode(EntityExplodeEvent e) {
        if (e.getEntity() instanceof Creeper) {
            e.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityExplode(ExplosionPrimeEvent e) {
        if (e.getEntity() instanceof Creeper) {
            e.setCancelled(true);
        }
    }
}
