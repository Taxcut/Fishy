package com.fishy.hcf.classes.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import com.fishy.hcf.classes.archer.ArcherClass;

/**
 * Event called when a player uses a archer effect {@link ArcherClass}.
 */
public class ArcherUseEffectEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final ArcherClass archerClass;

    private boolean cancelled;

    public ArcherUseEffectEvent(Player player, ArcherClass archerClass){
        super(player);
        this.archerClass = archerClass;
    }

    public static HandlerList getHandlerList(){
        return handlers;
    }

    /**
     * Gets the {@link ArcherClass} being unequipped.
     *
     * @return the unequipped {@link ArcherClass}
     */
    public ArcherClass getArcherClass(){
        return archerClass;
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
