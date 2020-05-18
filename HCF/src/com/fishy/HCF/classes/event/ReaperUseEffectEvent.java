package com.fishy.hcf.classes.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import com.fishy.hcf.classes.type.ReaperClass;

/**
 * Event called when a player uses a reaper effect {@link ReaperClass}.
 */
public class ReaperUseEffectEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final ReaperClass ReaperClass;

    private boolean cancelled;

    public ReaperUseEffectEvent(Player player, ReaperClass reaperClass){
        super(player);
        this.ReaperClass = reaperClass;
    }

    public static HandlerList getHandlerList(){
        return handlers;
    }

    /**
     * Gets the {@link ReaperClass} being unequipped.
     *
     * @return the unequipped {@link ReaperClass}
     */
    public ReaperClass getReaperClass(){
        return ReaperClass;
    }

    @Override
    public HandlerList getHandlers(){
        return handlers;
    }

    /**
     * Gets a true/false boolean value whether the event is cancelled or not.
     * See {@link Cancellable}
     *
     * @return boolean value if the event is cancelled.
     */
    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    /**
     * Set the event as cancelled or not.
     * See {@link Cancellable}
     *
     * @param cancelled the boolean
     */
    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
