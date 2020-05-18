package com.fishy.hcf.events.tracker;

import com.fishy.hcf.events.CaptureZone;
import com.fishy.hcf.events.EventTimer;
import com.fishy.hcf.events.EventType;
import com.fishy.hcf.events.faction.EventFaction;
import org.bukkit.entity.Player;

@Deprecated
public interface EventTracker {

    EventType getEventType();

    void tick(EventTimer eventTimer, EventFaction eventFaction);

    void onContest(EventFaction eventFaction, EventTimer eventTimer);

    boolean onControlTake(Player player, CaptureZone captureZone, EventFaction eventFaction);

    void onControlLoss(Player player, CaptureZone captureZone, EventFaction eventFaction);

    void stopTiming();
}
