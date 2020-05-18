package com.fishy.hcf.listener;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Squid;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import com.fishy.hcf.HCF;

/**
 * Listener that limits the amount of entities in one chunk.
 */
public class EntityLimitListener implements Listener {

    private static final int MAX_CHUNK_GENERATED_ENTITIES = 25;
    private static final int MAX_NATURAL_CHUNK_ENTITIES = 25;

    private final HCF plugin;
    
    private final static long TPS_CLEAR_DELAY = TimeUnit.SECONDS.toMillis(5L);
    private long lastTPSRun = 0L;

    public EntityLimitListener(HCF plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onCreatureSpawn1(CreatureSpawnEvent event){
        if(shouldClear(0)){
            for(Entity entity : plugin.getServer().getWorlds().get(0).getEntities()){
                if(entity instanceof Player || entity instanceof Projectile || entity instanceof ItemFrame ||
                        entity instanceof Cow || entity instanceof Pig || entity instanceof Minecart || entity instanceof Chicken){
                    continue;
                }

                if(entity instanceof Item){
                    Item item = (Item) entity;

                    if(item.getItemStack() != null && item.getItemStack().getType().name().contains("DIAMOND") && !item.getItemStack().getEnchantments().isEmpty()){
                        continue;
                    }
                }

                entity.remove();
            }
        }
    }

    private boolean shouldClear(int Player){
        if(!(plugin.getSotwTimer().getSotwRunnable() == null) && plugin.getServer().spigot().getTPS()[0] <= 18 &&
                lastTPSRun + TPS_CLEAR_DELAY < System.currentTimeMillis()){
            lastTPSRun = System.currentTimeMillis();
            return true;
        }

        Collection<? extends Player> onlinePlayers = plugin.getServer().getOnlinePlayers();
        int entities = plugin.getServer().getWorlds().get(0).getEntities().size() - Player;

        return Player >= 200 && entities >= 3500 || Player >= 100 && entities >= 4000;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onCreatureSpawn(CreatureSpawnEvent event) {

    	Entity entity = event.getEntity();
        if (entity instanceof Squid) {
            event.setCancelled(true);
            return;
        }

        if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.SLIME_SPLIT) { // allow slimes to always split
            switch (event.getSpawnReason()) {
                case NATURAL:
                    if (event.getLocation().getChunk().getEntities().length > MAX_NATURAL_CHUNK_ENTITIES) {
                        event.setCancelled(true);
                    }
                    break;
                case CHUNK_GEN:
                    if (event.getLocation().getChunk().getEntities().length > MAX_CHUNK_GENERATED_ENTITIES) {
                        event.setCancelled(true);
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
