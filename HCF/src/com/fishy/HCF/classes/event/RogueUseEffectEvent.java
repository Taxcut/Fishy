package com.fishy.hcf.classes.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import com.fishy.hcf.classes.type.RogueClass;

/**
 * Event called when a player uses a archer effect {@link RogueClass}.
 */
public class RogueUseEffectEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final RogueClass rogueClass;

    private boolean cancelled;

    public RogueUseEffectEvent(Player player, RogueClass rogueClass){
        super(player);
        this.rogueClass = rogueClass;
    }

    public static HandlerList getHandlerList(){
        return handlers;
    }

    /**
     * Gets the {@link RogueClass} being unequipped.
     *
     * @return the unequipped {@link RogueClass}
     */
    public RogueClass getRogueClass(){
        return rogueClass;
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
