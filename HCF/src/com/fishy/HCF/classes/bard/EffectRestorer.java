package com.fishy.hcf.classes.bard;

import java.util.Collection;
import java.util.UUID;

import com.fishy.hcf.classes.event.PvpClassUnequipEvent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionEffectExpireEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.fishy.hcf.classes.PvpClass;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

public class EffectRestorer implements Listener {

    private final Table<UUID, PotionEffectType, PotionEffect> restores = HashBasedTable.create();

    public EffectRestorer(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPvpClassUnequip(PvpClassUnequipEvent event) {
        restores.rowKeySet().remove(event.getPlayer().getUniqueId());
    }

    public void setRestoreEffect(Player player, PotionEffect effect) {
        boolean shouldCancel = true;
        Collection<PotionEffect> activeList = player.getActivePotionEffects();
        for (PotionEffect active : activeList) {
            if (!active.getType().equals(effect.getType())) continue;

            // If the current potion effect has a higher amplifier, ignore this one.
            if (effect.getAmplifier() < active.getAmplifier()) {
                return;
            } else if (effect.getAmplifier() == active.getAmplifier()) {
                // If the current potion effect has a longer duration, ignore this one.
                if (effect.getDuration() < active.getDuration()) {
                    return;
                }
            }

            restores.put(player.getUniqueId(), active.getType(), active);
            shouldCancel = false;
            break;
        }

        // Cancel the previous restore.
        player.addPotionEffect(effect, true);
        if (shouldCancel && effect.getDuration() > BardClass.HELD_EFFECT_DURATION_TICKS && effect.getDuration() < PvpClass.DEFAULT_MAX_DURATION) {
            restores.remove(player.getUniqueId(), effect.getType());
        }
    }

    @EventHandler
    public void onPotionEffectExpire(PotionEffectExpireEvent event) {
        LivingEntity livingEntity = event.getEntity();
        if (livingEntity instanceof Player) {
            Player player = (Player) livingEntity;
            PotionEffect previous = restores.remove(player.getUniqueId(), event.getEffect().getType());
            if (previous != null) {
                event.setCancelled(true);
				player.addPotionEffect(previous, true);
            }
        }
    }
}
