package com.fishy.hcf.events.tracker;

import com.fishy.hcf.HCF;
import com.fishy.base.GuavaCompat;
import com.fishy.hcf.events.CaptureZone;
import com.fishy.hcf.events.EventTimer;
import com.fishy.hcf.events.EventType;
import com.fishy.hcf.events.faction.ConquestFaction;
import com.fishy.hcf.events.faction.EventFaction;
import com.fishy.hcf.faction.event.FactionRemoveEvent;
import com.fishy.hcf.faction.type.Faction;
import com.fishy.hcf.faction.type.PlayerFaction;
import com.fishy.hcf.util.ConcurrentValueOrderedMap;
import com.fishy.hcf.util.DurationFormatter;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Deprecated
public class ConquestTracker implements EventTracker, Listener {
    private static final long MINIMUM_CONTROL_TIME_ANNOUNCE = TimeUnit.SECONDS.toMillis(5L);
    public static final long DEFAULT_CAP_MILLIS = TimeUnit.SECONDS.toMillis(30L);

    private final ConcurrentValueOrderedMap<PlayerFaction, Integer> factionPointsMap = new ConcurrentValueOrderedMap<>();
    private final HCF plugin;
    
    public static String prefix = ChatColor.translateAlternateColorCodes('&', "&5&lCONQUEST &8� &f");

    public ConquestTracker(HCF plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onFactionRemove(FactionRemoveEvent event) {
        Faction faction = event.getFaction();
        if (faction instanceof PlayerFaction) {
            this.factionPointsMap.remove((PlayerFaction) faction);
        }
    }
    public ConcurrentValueOrderedMap<PlayerFaction, Integer> getFactionPointsMap() {
        return this.factionPointsMap;
    }

    public int getPoints(PlayerFaction faction) {
        return GuavaCompat.firstNonNull(this.factionPointsMap.get(faction), 0);
    }

    public int setPoints(PlayerFaction faction, int amount) {
        this.factionPointsMap.put(faction, amount);
        return amount;
    }
    public int takePoints(PlayerFaction faction, int amount) {
        return setPoints(faction, getPoints(faction) - amount);
    }
    public int addPoints(PlayerFaction faction, int amount) {
        return setPoints(faction, getPoints(faction) + amount);
    }

    @Override
    public EventType getEventType() {
        return EventType.CONQUEST;
    }

    @Override
    public void tick(EventTimer eventTimer, EventFaction eventFaction) {
        ConquestFaction conquestFaction = (ConquestFaction) eventFaction;
        List<CaptureZone> captureZones = conquestFaction.getCaptureZones();
        for (CaptureZone captureZone : captureZones) {
            captureZone.updateScoreboardRemaining();
            Player cappingPlayer = captureZone.getCappingPlayer();
            if (cappingPlayer == null) continue;

            if (!captureZone.getCuboid().contains(cappingPlayer)) {
                onControlLoss(cappingPlayer, captureZone, eventFaction);
                continue;
            }

            // The capture zone has been controlled.
            long remainingMillis = captureZone.getRemainingCaptureMillis();
            if (remainingMillis <= 0L) {
                UUID uuid = cappingPlayer.getUniqueId();

                PlayerFaction playerFaction = plugin.getFactionManager().getPlayerFaction(uuid);
                if (playerFaction != null) {
                    int newPoints = addPoints(playerFaction, 1);
                    if (newPoints < 250) {
                        // Reset back to the default for this tracker.
                        captureZone.setRemainingCaptureMillis(captureZone.getDefaultCaptureMillis());
                        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', prefix) + ChatColor.RED + playerFaction.getName() +
                                ChatColor.WHITE + " gained " + ChatColor.GRAY + 1 + ChatColor.WHITE + " point for capturing " + ChatColor.BLUE + captureZone.getDisplayName() + ChatColor.WHITE + ". " +
                                ChatColor.RED + '(' + newPoints + '/' + 250 + ')');
                    } else {
                        // Clear all the points for the next Conquest event.
                        this.factionPointsMap.clear();
                        plugin.getTimerManager().getEventTimer().handleWinner(cappingPlayer);
                        return;
                    }
                }
                return;
            }

            int remainingSeconds = (int) Math.round((double) remainingMillis / 1000L);
            if (remainingSeconds % 5 == 0) {
                cappingPlayer.sendMessage(ChatColor.WHITE + "You are now attempting to control " + ChatColor.RED + captureZone.getDisplayName() + ChatColor.WHITE + ". " +
                        ChatColor.RED + '(' + remainingSeconds + "s)");
            }
        }
    }

    @Override
    public void onContest(EventFaction eventFaction, EventTimer eventTimer) {
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', prefix) + ChatColor.LIGHT_PURPLE + eventFaction.getName() + ChatColor.WHITE + " can now be contested.");
    }

    @Override
    public boolean onControlTake(Player player, CaptureZone captureZone, EventFaction eventFaction) {
        if (plugin.getFactionManager().getPlayerFaction(player.getUniqueId()) == null) {
            player.sendMessage(ChatColor.RED + "You must be in a faction to capture for Conquest.");
            return false;
        }

        return true;
    }

    @Override
    public void onControlLoss(Player player, CaptureZone captureZone, EventFaction eventFaction) {
        long remainingMillis = captureZone.getRemainingCaptureMillis();
        if (remainingMillis > 0L && captureZone.getDefaultCaptureMillis() - remainingMillis > MINIMUM_CONTROL_TIME_ANNOUNCE) {
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', prefix) + ChatColor.RED + player.getName() +
                    ChatColor.WHITE + " was knocked off " + captureZone.getDisplayName() + ChatColor.WHITE + '.' + ChatColor.RED + '(' + DurationFormatter.getRemaining(captureZone.getRemainingCaptureMillis(), false) + ')');
        }
    }

    @Override
    public void stopTiming() {
        factionPointsMap.clear();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Faction currentEventFac = plugin.getTimerManager().getEventTimer().getEventFaction();
        if (currentEventFac instanceof ConquestFaction) {
            Player player = event.getEntity();
            PlayerFaction playerFaction = plugin.getFactionManager().getPlayerFaction(player);
            if (playerFaction != null && 3 > 0) {
                int oldPoints = getPoints(playerFaction);
                if (oldPoints == 0) return;

                int newPoints = takePoints(playerFaction, 5);
                event.setDeathMessage(null); // for some reason if it isn't handled manually, weird colour coding happens
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', prefix) + ChatColor.RED + playerFaction.getName() + ChatColor.WHITE + " lost " +
                        ChatColor.GRAY + 3 + ChatColor.WHITE + " points because " + ChatColor.RED + player.getName() + ChatColor.WHITE + " died." +
                        ChatColor.RED + " (" + newPoints + '/' + 250 + ')');
            }
        }
    }
}
