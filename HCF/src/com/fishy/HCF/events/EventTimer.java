package com.fishy.hcf.events;

import java.sql.Date;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

import com.fishy.hcf.HCF;
import com.fishy.hcf.events.crate.EventKey;
import com.fishy.hcf.events.faction.ConquestFaction;
import com.fishy.hcf.events.faction.EventFaction;
import com.fishy.hcf.events.faction.KothFaction;
import com.fishy.hcf.faction.event.CaptureZoneEnterEvent;
import com.fishy.hcf.faction.event.CaptureZoneLeaveEvent;
import com.fishy.hcf.faction.type.Faction;
import com.fishy.hcf.faction.type.PlayerFaction;
import com.fishy.hcf.timer.GlobalTimer;
import com.google.common.collect.Iterables;

import javafx.event.Event;

public class EventTimer extends GlobalTimer implements Listener {

    private static final long RESCHEDULE_FREEZE_MILLIS = TimeUnit.SECONDS.toMillis(15L);
    private static final String RESCHEDULE_FREEZE_WORDS = DurationFormatUtils.formatDurationWords(RESCHEDULE_FREEZE_MILLIS, true, true);
    public static long EVENT_FREQUENCY = TimeUnit.HOURS.toMillis(4);

    private boolean nextCancelled = false;
    private Long lastEvent = null;
    private Long nextEvent = null;
    private EventFaction lastEventFaction;
    private EventFaction nextEventFaction;
    private boolean justAnnounced = false;
    private int sotwDay = -1;

    private long startStamp;                 // the milliseconds at when the current event started.
    private long lastContestedEventMillis;   // the milliseconds at when the last event was contested.
    private EventFaction eventFaction;
    
    private final int CONQUEST_DAY = 2;
    private final int CONQUEST_HOUR = 13;
    
    private final int FIRST_KOTH_DAY = 1;
    private final int FIRST_KOTH_HOUR = 17;

    private final HCF plugin;

    public EventFaction getEventFaction() {
        return eventFaction;
    }

    public EventTimer(final HCF plugin) {
        super("Event", 0L);
        this.plugin = plugin;
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (EventTimer.this.eventFaction != null) {
                EventTimer.this.eventFaction.getEventType().getEventTracker().tick(EventTimer.this, EventTimer.this.eventFaction);
            }
        }, 20, 20);
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            long now = System.currentTimeMillis();
                if (Bukkit.getServer().hasWhitelist()) {
                    nextCancelled = true;
                    plugin.getLogger().info("Cancelling next event due to whitelist");
                } else if (plugin.getSotwTimer().getSotwRunnable() != null) {
                    nextCancelled = true;
                    nextEventFaction = null;
                    Calendar calendar = new GregorianCalendar();
                    calendar.setTime(new Date(now));
                    calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + FIRST_KOTH_DAY);
                    calendar.set(Calendar.HOUR_OF_DAY, FIRST_KOTH_HOUR);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                    sotwDay = calendar.get(Calendar.DAY_OF_YEAR);
                    nextEvent = calendar.getTimeInMillis();
                    plugin.getLogger().info("Cancelling next event and moving till tomorow due to SOTW");
                } else if (getEventFaction() == null) {
                    if (lastEvent == null) {
                        lastEvent = now;
                        plugin.getLogger().info("Last event time assumed");
                    }
                    if (nextEvent == null) {
                        Calendar calendar = new GregorianCalendar();
                        int day = calendar.get(Calendar.DAY_OF_YEAR);
                        Faction faction;
                        if(day == sotwDay + CONQUEST_DAY && (faction = plugin.getFactionManager().getFaction("Conquest")) != null && faction instanceof ConquestFaction){
                            calendar.set(Calendar.HOUR_OF_DAY, CONQUEST_HOUR);
                            if(calendar.getTimeInMillis() > now) {
                                nextEventFaction = (EventFaction) faction;
                            } else{
                                calendar.setTime(new Date(lastEvent + EVENT_FREQUENCY));
                            }
                        }
                        else {
                            calendar.setTime(new Date(lastEvent + EVENT_FREQUENCY));
                        }
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);
                        nextEvent = calendar.getTimeInMillis();
                        if (!nextCancelled) {
                            plugin.getLogger().info("Next event will go live in " + DurationFormatUtils.formatDurationWords(nextEvent - now, true, true));
                        }
                    }
                    if (nextEventFaction == null) {
                        List<EventFaction> factions = plugin.getFactionManager().getFactions().stream().filter(faction -> faction instanceof KothFaction && faction != lastEventFaction).map(faction -> (EventFaction) faction).collect(Collectors.toList());
                        int size = factions.size();
                        if (size == 0) {
                            plugin.getLogger().warning("No available event factions found");
                        } else {
                            if (size == 1) {
                                nextEventFaction = factions.iterator().next();
                            } else {
                                nextEventFaction = factions.get(ThreadLocalRandom.current().nextInt(size));
                            }
                            plugin.getLogger().info("Next Event Faction: " + nextEventFaction.getName());
                        }
                    }
                } else if (lastEventFaction != getEventFaction()) {
                    nextCancelled = true;
                    plugin.getLogger().warning("Cancelling next event because a different event is still running");
                }
            if(nextEvent != null) {
                if (now >= nextEvent) {
                    if (!nextCancelled) {
                        if (!tryContesting(nextEventFaction, Bukkit.getConsoleSender())) {
                            plugin.getLogger().warning("Failed to start event " + nextEventFaction.getName());
                        }
                    }
                    lastEventFaction = nextEventFaction;
                    nextCancelled = false;
                    nextEventFaction = null;
                    nextEvent = null;
                    justAnnounced = true;
                }
                if (nextEvent != null && nextEventFaction != null) {
                    long timeTill = nextEvent - now;
                    if (timeTill > 0) {
                        if (!nextCancelled && !justAnnounced) {
                            if (timeTill > TimeUnit.MINUTES.toMillis(1) && (timeTill % TimeUnit.HOURS.toMillis(1) < TimeUnit.MINUTES.toMillis(1) || (timeTill < TimeUnit.HOURS.toMillis(1) && timeTill % TimeUnit.MINUTES.toMillis(15) < TimeUnit.MINUTES.toMillis(1)))) {
                                broadcastWarning(nextEventFaction, timeTill);
                                justAnnounced = true;
                            }
                        }
                    }
                    justAnnounced = false;
                }
            }
        }, 20 * 60, 20 * 60);
    }
    
    public static void broadcastWarning(EventFaction eventFaction, long timeTill) {
        if(HCF.getPlugin().getTimerManager().eventTimer.getRemaining() <= 0) {
            timeTill = (timeTill / 1000 / 60) * 1000 * 60;
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', ("&5&lEVENT &8� &f") + ChatColor.LIGHT_PURPLE + eventFaction.getName() + ChatColor.WHITE + " is starting in " + ChatColor.RED + DurationFormatUtils.formatDurationWords(timeTill, true, true) + ChatColor.WHITE + '.'));
        }
    }

    @Override
    public String getScoreboardPrefix() {
        return ChatColor.BLUE.toString();
    }

    @Override
    public String getName() {
        return eventFaction != null ? eventFaction.getName() : "Event";
    }

    @Override
    public boolean clearCooldown() {
        boolean result = super.clearCooldown();
        if (eventFaction != null) {
            for (CaptureZone captureZone : eventFaction.getCaptureZones()) {
                captureZone.setCappingPlayer(null);
            }

            // Make sure to set the land back as Deathban.
            eventFaction.setDeathban(true);
            eventFaction.getEventType().getEventTracker().stopTiming();
            eventFaction = null;
            startStamp = -1L;
            result = true;
        }

        return result;
    }
    
    public void setNextCancelled(boolean nextCancelled) {
        this.nextCancelled = nextCancelled;
    }

    public boolean isNextCancelled() {
        return nextCancelled;
    }

    public Long getLastEvent() {
        return lastEvent;
    }

    public Long getNextEvent() {
        return nextEvent;
    }

    public EventFaction getLastEventFaction() {
        return lastEventFaction;
    }

    public EventFaction getNextEventFaction() {
        return nextEventFaction;
    }

    @Override
    public long getRemaining() {
        if (eventFaction == null) {
            return 0L;
        } else if (eventFaction instanceof KothFaction) {
            return ((KothFaction) eventFaction).getCaptureZone().getRemainingCaptureMillis();
        } else {
            return super.getRemaining();
        }
    }

    public void handleWinner(Player winner) {
        if (eventFaction != null) {
            PlayerFaction playerFaction = plugin.getFactionManager().getPlayerFaction(winner);
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', ("&5&lKOTH &8� &f") + ChatColor.LIGHT_PURPLE + winner.getName() + ChatColor.LIGHT_PURPLE + " (" + (playerFaction == null ? Faction.FACTIONLESS_PREFIX : playerFaction.getName()) + ")" +
                    ChatColor.WHITE + " has captured " + ChatColor.LIGHT_PURPLE + eventFaction.getName() + ChatColor.WHITE + " after " +
                    ChatColor.LIGHT_PURPLE + DurationFormatUtils.formatDurationWords(getUptime(), true, true) + ChatColor.WHITE + " of up-time" + ChatColor.WHITE + '.'));

            EventType eventType = eventFaction.getEventType();
            EventKey eventKey = plugin.getKeyManager().getEventKey();
            Collection<Inventory> inventories = eventKey.getInventories(eventType);
            lastEvent = System.currentTimeMillis();
            if (playerFaction != null) {
            	switch (eventFaction.getEventType()) {
				case CONQUEST:
					playerFaction.setPoints(playerFaction.getPoints() + 20);
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cr givekey " + winner.getName() + " Conquest 15");
					break;
				case KOTH:
					playerFaction.setPoints(playerFaction.getPoints() + 5);
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cr givekey " + winner.getName() + " Koth 5");
					break;
				default:
					break;

            	}
            }

            clearCooldown(); // must always be cooled last as this nulls some variables.
        }
    }

    public boolean tryContesting(EventFaction eventFaction, CommandSender sender) {
        if (this.eventFaction != null) {
            sender.sendMessage(ChatColor.RED + "There is already an active event, use /event cancel to end it.");
            return false;
        }

        if (eventFaction instanceof KothFaction) {
            KothFaction kothFaction = (KothFaction) eventFaction;
            if (kothFaction.getCaptureZone() == null) {
                sender.sendMessage(ChatColor.RED + "Cannot schedule " + eventFaction.getName() + " as its' capture zone is not set.");
                return false;
            }
        } else if (eventFaction instanceof ConquestFaction) {
            ConquestFaction conquestFaction = (ConquestFaction) eventFaction;
            Collection<ConquestFaction.ConquestZone> zones = conquestFaction.getConquestZones();
            for (ConquestFaction.ConquestZone zone : ConquestFaction.ConquestZone.values()) {
                if (!zones.contains(zone)) {
                    sender.sendMessage(ChatColor.RED + "Cannot schedule " + eventFaction.getName() + " as capture zone '" + zone.getDisplayName() + ChatColor.RED + "' is not set.");
                    return false;
                }
            }
        }

        // Don't allow events to reschedule their-self before they are allowed to.
        long millis = System.currentTimeMillis();

        lastContestedEventMillis = millis;
        startStamp = millis;
        this.eventFaction = eventFaction;

        eventFaction.getEventType().getEventTracker().onContest(eventFaction, this);
        if (eventFaction instanceof ConquestFaction) {
            setRemaining(1000L, true); //TODO: Add a unpredicated timer impl instead of this xD.
            setPaused(true);
        }

        Collection<CaptureZone> captureZones = eventFaction.getCaptureZones();
        for (CaptureZone captureZone : captureZones) {
            if (captureZone.isActive()) {
                Player player = Iterables.getFirst(captureZone.getCuboid().getPlayers(), null);
                if (player != null && player.getGameMode() != GameMode.CREATIVE && eventFaction.getEventType().getEventTracker().onControlTake(player, captureZone, eventFaction)) {
                    captureZone.setCappingPlayer(player);
                }
            }
        }

        eventFaction.setDeathban(false); // the event should be lowered deathban whilst active.
        return true;
    }

    public long getUptime() {
        return System.currentTimeMillis() - startStamp;
    }

    public long getStartStamp() {
        return startStamp;
    }

    private void handleDisconnect(Player player) {
        if (eventFaction != null) {
            Objects.requireNonNull(player);
            Collection<CaptureZone> captureZones = eventFaction.getCaptureZones();
            for (CaptureZone captureZone : captureZones) {
                if (Objects.equals(captureZone.getCappingPlayer(), player)) {
                    captureZone.setCappingPlayer(null);
                    eventFaction.getEventType().getEventTracker().onControlLoss(player, captureZone, eventFaction);
                    break;
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        handleDisconnect(event.getEntity());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerLogout(PlayerQuitEvent event) {
        handleDisconnect(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerKick(PlayerKickEvent event) {
        handleDisconnect(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onCaptureZoneEnter(CaptureZoneEnterEvent event) {
        if (eventFaction != null) {
            CaptureZone captureZone = event.getCaptureZone();
            if (eventFaction.getCaptureZones().contains(captureZone)) {
                Player player = event.getPlayer();
                if (captureZone.getCappingPlayer() == null && eventFaction.getEventType().getEventTracker().onControlTake(player, captureZone, eventFaction) && (player.getGameMode() != GameMode.CREATIVE)) {
                    captureZone.setCappingPlayer(player);
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onCaptureZoneLeave(CaptureZoneLeaveEvent event) {
        if (Objects.equals(event.getFaction(), eventFaction)) {
            Player player = event.getPlayer();
            CaptureZone captureZone = event.getCaptureZone();
            if (Objects.equals(player, captureZone.getCappingPlayer())) {
                captureZone.setCappingPlayer(null);
                eventFaction.getEventType().getEventTracker().onControlLoss(player, captureZone, eventFaction);

                // Try and find a new capper.
                for (Player target : captureZone.getCuboid().getPlayers()) {
                    if (target != null && !target.equals(player) && eventFaction.getEventType().getEventTracker().onControlTake(target, captureZone, eventFaction)) {
                        captureZone.setCappingPlayer(target);
                        break;
                    }
                }
            }
        }
    }
}