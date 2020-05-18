package com.fishy.hcf.classes;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public abstract class PvpClass {

    public static final long DEFAULT_MAX_DURATION = TimeUnit.MINUTES.toMillis(8L);

    protected final Set<PotionEffect> passiveEffects = new HashSet<>();
    protected final String name;
    protected final long warmupDelay;

    public PvpClass(String name, long warmupDelay) {
        this.name = name;
        this.warmupDelay = warmupDelay;
    }
    public String getName() {
        return name;
    }
    public long getWarmupDelay() {
        return warmupDelay;
    }
    public boolean onEquip(Player player) {
        for (PotionEffect effect : passiveEffects) {
            player.addPotionEffect(effect, true);
        }

        player.sendMessage(ChatColor.GREEN + name + " class has been activated.");
        return true;
    }
    public void onUnequip(Player player) {
        for (PotionEffect effect : passiveEffects) {
            for (PotionEffect active : player.getActivePotionEffects()) {
                if (active.getDuration() > DEFAULT_MAX_DURATION && active.getType().equals(effect.getType()) && active.getAmplifier() == effect.getAmplifier()) {
                    player.removePotionEffect(effect.getType());
                    break;
                }
            }
        }

        player.sendMessage(ChatColor.RED + name + " class has been deactivated.");
    }
    public abstract boolean isApplicableFor(Player player);
}
