package com.fishy.hcf.classes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.fishy.hcf.HCF;
import com.fishy.hcf.classes.archer.ArcherClass;
import com.fishy.hcf.classes.bard.BardClass;
import com.fishy.hcf.classes.event.PvpClassEquipEvent;
import com.fishy.hcf.classes.event.PvpClassUnequipEvent;
import com.fishy.hcf.classes.type.MinerClass;
import com.fishy.hcf.classes.type.ReaperClass;
import com.fishy.hcf.classes.type.RogueClass;

public class PvpClassManager implements Listener {
    private final Map<UUID, PvpClass> equippedClassMap = new HashMap<>();

    private final List<PvpClass> pvpClasses = new ArrayList<>();

    public PvpClassManager(HCF plugin) {
        pvpClasses.add(new ArcherClass(plugin));
        pvpClasses.add(new BardClass(plugin));
        pvpClasses.add(new MinerClass(plugin));
        pvpClasses.add(new RogueClass(plugin));
        if (HCF.getPlugin().getConfig().getBoolean("KITMAP")) {
        	pvpClasses.add(new ReaperClass(plugin));
        }
        
        
        Bukkit.getPluginManager().registerEvents(this, plugin);
        for (PvpClass pvpClass : pvpClasses) {
            if (pvpClass instanceof Listener) {
                plugin.getServer().getPluginManager().registerEvents((Listener) pvpClass, plugin);
            }
        }
    }

    public void onDisable() {
        for (Map.Entry<UUID, PvpClass> entry : new HashMap<>(equippedClassMap).entrySet()) {
            this.setEquippedClass(Bukkit.getPlayer(entry.getKey()), null);
        }

        this.pvpClasses.clear();
        this.equippedClassMap.clear();
    }
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        setEquippedClass(event.getEntity(), null);
    }
    public Collection<PvpClass> getPvpClasses() {
        return pvpClasses;
    }
    public PvpClass getEquippedClass(Player player) {
        synchronized (equippedClassMap) {
            return equippedClassMap.get(player.getUniqueId());
        }
    }	

    public boolean hasClassEquipped(Player player, PvpClass pvpClass) {
        return getEquippedClass(player) == pvpClass;
    }
    public void setEquippedClass(Player player, @Nullable PvpClass pvpClass) {
        if (pvpClass == null) {
            PvpClass equipped = this.equippedClassMap.remove(player.getUniqueId());
            if (equipped != null) {
                equipped.onUnequip(player);
                Bukkit.getPluginManager().callEvent(new PvpClassUnequipEvent(player, equipped));
            }
        } else if (pvpClass.onEquip(player) && pvpClass != this.getEquippedClass(player)) {
            equippedClassMap.put(player.getUniqueId(), pvpClass);
            Bukkit.getPluginManager().callEvent(new PvpClassEquipEvent(player, pvpClass));
        }
    }
}
