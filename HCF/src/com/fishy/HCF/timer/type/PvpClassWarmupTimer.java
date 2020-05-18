package com.fishy.hcf.timer.type;

import com.fishy.hcf.HCF;
import com.fishy.hcf.classes.PvpClass;
import java.util.concurrent.*;

import com.fishy.hcf.timer.PlayerTimer;
import com.fishy.hcf.timer.TimerCooldown;
import com.fishy.hcf.util.base.*;

import org.bukkit.scheduler.*;
import org.bukkit.plugin.*;
import org.bukkit.*;
import javax.annotation.*;
import com.google.common.base.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.bukkit.event.inventory.*;
import org.bukkit.entity.*;
import java.util.*;

public class PvpClassWarmupTimer extends PlayerTimer implements Listener
{
    protected final Map<UUID, PvpClass> classWarmups;
    private final HCF plugin;

    public PvpClassWarmupTimer(final HCF plugin) {
        super("Class Warmup", TimeUnit.SECONDS.toMillis(3L), false);
        this.classWarmups = new HashMap<UUID, PvpClass>();
        this.plugin = plugin;
        new BukkitRunnable() {
            public void run() {
                for (final Player player : Bukkit.getOnlinePlayers()) {
                    PvpClassWarmupTimer.this.attemptEquip(player);
                }
            }
        }.runTaskLater((Plugin)plugin, 10L);
    }

    @Override
    public void onDisable(final Config config) {
        super.onDisable(config);
        this.classWarmups.clear();
    }

    public String getScoreboardPrefix() {
        return ChatColor.GREEN + ChatColor.BOLD.toString();
    }

    @Override
    public TimerCooldown clearCooldown(@Nullable final Player player, final UUID playerUUID) {
        final TimerCooldown runnable = super.clearCooldown(player, playerUUID);
        if (runnable != null) {
            this.classWarmups.remove(playerUUID);
        }
        return runnable;
    }

    public void handleExpiry(@Nullable final Player player, final UUID userUUID) {
        final PvpClass pvpClass = this.classWarmups.remove(userUUID);
        if (player != null) {
            Preconditions.checkNotNull((Object)pvpClass, "Attempted to equip a class for %s, but nothing was added", new Object[] { player.getName() });
            this.plugin.getPvpClassManager().setEquippedClass(player, pvpClass);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerKick(final PlayerQuitEvent event) {
        this.plugin.getPvpClassManager().setEquippedClass(event.getPlayer(), null);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        event.setJoinMessage((String)null);
        this.attemptEquip(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onEquipmentSet(final EquipmentSetEvent event) {
        final HumanEntity humanEntity = event.getHumanEntity();
        if (humanEntity instanceof Player) {
            this.attemptEquip((Player)humanEntity);
        }
    }

    private void attemptEquip(final Player player) {
        PvpClass current = this.plugin.getPvpClassManager().getEquippedClass(player);
        if (current != null) {
            if (current.isApplicableFor(player)) {
                return;
            }
            this.plugin.getPvpClassManager().setEquippedClass(player, null);
        }
        else if ((current = this.classWarmups.get(player.getUniqueId())) != null) {
            if (current.isApplicableFor(player)) {
                return;
            }
            this.clearCooldown(player);
        }
        final Collection<PvpClass> pvpClasses = this.plugin.getPvpClassManager().getPvpClasses();
        for (final PvpClass pvpClass : pvpClasses) {
            if (pvpClass.isApplicableFor(player)) {
                this.classWarmups.put(player.getUniqueId(), pvpClass);
                this.setCooldown(player, player.getUniqueId(), pvpClass.getWarmupDelay(), false);
                break;
            }
        }
    }
}
