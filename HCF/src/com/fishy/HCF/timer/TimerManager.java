package com.fishy.hcf.timer;

import java.util.LinkedHashSet;
import java.util.Set;

import com.fishy.hcf.events.EventTimer;
import com.fishy.hcf.timer.type.*;
import com.fishy.hcf.util.base.Config;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.fishy.hcf.HCF;

import lombok.Data;
import lombok.Getter;

@Data
public class TimerManager implements Listener {

    public SpawnTagTimer combatTimer;
    public LogoutTimer logoutTimer;
    public EnderpearlTimer enderPearlTimer;
    public EventTimer eventTimer;
    public GappleTimer gappleTimer;
    public ArcherTimer archerTimer;
    
    @Getter
    private final InvincibilityTimer invincibilityTimer;

    @Getter
    private final PvpClassWarmupTimer pvpClassWarmupTimer;

    @Getter
    private final StuckTimer stuckTimer;

    @Getter
    private final TeleportTimer teleportTimer;

    @Getter
    private final CrappleTimer appleTimer;

    @Getter
    private final RebootTimer autoRestartTimer;

    @Getter
    private final Set<Timer> timers = new LinkedHashSet<>();

    private final JavaPlugin plugin;
    private Config config;

    public TimerManager(HCF plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        autoRestartTimer = new RebootTimer();
        registerTimer(enderPearlTimer = new EnderpearlTimer(plugin));
        registerTimer(logoutTimer = new LogoutTimer());
        registerTimer(gappleTimer = new GappleTimer(plugin));
        registerTimer(stuckTimer = new StuckTimer());
        registerTimer(invincibilityTimer = new InvincibilityTimer(plugin));
        registerTimer(combatTimer = new SpawnTagTimer(plugin));
        registerTimer(teleportTimer = new TeleportTimer(plugin));
        registerTimer(eventTimer = new EventTimer(plugin));
        registerTimer(pvpClassWarmupTimer = new PvpClassWarmupTimer(plugin));
        registerTimer(appleTimer = new CrappleTimer(plugin));
        registerTimer(archerTimer = new ArcherTimer());
        reloadTimerData();
    }

    public void registerTimer(Timer timer) {
        timers.add(timer);
        if (timer instanceof Listener) {
            plugin.getServer().getPluginManager().registerEvents((Listener) timer, plugin);
        }
    }

    public void unregisterTimer(Timer timer) {
        timers.remove(timer);
    }
    public void reloadTimerData() {
        config = new Config(plugin, "timer");
        for (Timer timer : timers) {
            timer.load(config);
        }
    }
    public void saveTimerData() {
        for (Timer timer : timers) {
            timer.onDisable(config);
        }

        config.save();
    }
}
