package com.fishy.hcf.events.tracker;

import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.fishy.hcf.DateTimeFormats;
import com.fishy.hcf.HCF;
import com.fishy.hcf.events.CaptureZone;
import com.fishy.hcf.events.EventTimer;
import com.fishy.hcf.events.EventType;
import com.fishy.hcf.events.faction.EventFaction;
import com.fishy.hcf.events.faction.KothFaction;

@Deprecated
public class KothTracker implements EventTracker {

    /**
     * Minimum time the KOTH has to be controlled before this tracker will announce when control has been lost.
     */
    private static final long MINIMUM_CONTROL_TIME_ANNOUNCE = TimeUnit.SECONDS.toMillis(30L);

    public static final long DEFAULT_CAP_MILLIS = TimeUnit.MINUTES.toMillis(5L);

    private final HCF plugin;
    
    public static String prefix = ChatColor.translateAlternateColorCodes('&', "&5&lKOTH &8� &f");

    public KothTracker(HCF plugin) {
        this.plugin = plugin;
    }

    @Override
    public EventType getEventType() {
        return EventType.KOTH;
    }

    @Override
    public void tick(EventTimer eventTimer, EventFaction eventFaction) {
        CaptureZone captureZone = ((KothFaction) eventFaction).getCaptureZone();
        captureZone.updateScoreboardRemaining();
        long remainingMillis = captureZone.getRemainingCaptureMillis();

        if (captureZone.getCappingPlayer() != null && !captureZone.getCuboid().contains(captureZone.getCappingPlayer())) {
            captureZone.setCappingPlayer(null);
            onControlLoss(captureZone.getCappingPlayer(), captureZone, eventFaction);
            return;
        }

        if (remainingMillis <= 0L) { // has been captured.
            plugin.getTimerManager().getEventTimer().handleWinner(captureZone.getCappingPlayer());
            eventTimer.clearCooldown();
            return;
        }

        if (remainingMillis == captureZone.getDefaultCaptureMillis()) return;

        int remainingSeconds = (int) (remainingMillis / 1000L);
        if (remainingSeconds > 0 && remainingSeconds % MINIMUM_CONTROL_TIME_ANNOUNCE == 0) {
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', prefix) + ChatColor.WHITE + "Someone is controlling " + ChatColor.LIGHT_PURPLE + captureZone.getDisplayName() + ChatColor.WHITE + ". " +
                    ChatColor.RED + '(' + DateTimeFormats.KOTH_FORMAT.format(remainingMillis) + ')');
        }
    }

    @Override
    public void onContest(EventFaction eventFaction, EventTimer eventTimer) {
        Bukkit.broadcastMessage(prefix + ChatColor.LIGHT_PURPLE + eventFaction.getName() + ChatColor.WHITE + " can now be contested. " +
                ChatColor.RED + '(' + DateTimeFormats.KOTH_FORMAT.format(eventTimer.getRemaining()) + ')');
    }

    @Override
    public boolean onControlTake(Player player, CaptureZone captureZone, EventFaction eventFaction) {
        player.sendMessage(prefix + ChatColor.WHITE + "You are now in control of " + ChatColor.LIGHT_PURPLE + captureZone.getDisplayName() + ChatColor.WHITE + '.');
        return true;
    }

    @Override
    public void onControlLoss(Player player, CaptureZone captureZone, EventFaction eventFaction) {
        player.sendMessage(prefix + ChatColor.WHITE + "You are no longer in control of " + ChatColor.LIGHT_PURPLE + captureZone.getDisplayName() + ChatColor.WHITE + '.');

        // Only broadcast if the KOTH has been controlled for at least 25 seconds to prevent spam.
        long remainingMillis = captureZone.getRemainingCaptureMillis();
        if (remainingMillis > 0L && captureZone.getDefaultCaptureMillis() - remainingMillis > MINIMUM_CONTROL_TIME_ANNOUNCE) {
        Bukkit.broadcastMessage(prefix + ChatColor.RED + player.getName() + ChatColor.WHITE + " has lost control of " +
                ChatColor.LIGHT_PURPLE + captureZone.getDisplayName() + ChatColor.WHITE + '.' +
                ChatColor.RED + " (" + captureZone.getScoreboardRemaining() + ')');
    }
//        }
    }

    @Override
    public void stopTiming() {

    }
}
