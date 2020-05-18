package com.fishy.hcf.util.event;

import org.bukkit.event.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.*;
import java.util.*;

public class PlayerMineItemsEvent extends Event implements Cancellable
{
    private static HandlerList handlerList;
    private final Player player;
    private final Collection<ItemStack> stackCollection;
    private boolean cancelled;
    
    public PlayerMineItemsEvent(final Player player, final Collection<ItemStack> stackCollection) {
        this.cancelled = false;
        this.player = player;
        this.stackCollection = new ArrayList<ItemStack>(stackCollection);
    }
    
    public Player getPlayer() {
        return this.player;
    }
    
    public Collection<ItemStack> getStackCollection() {
        return this.stackCollection;
    }
    
    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }
    
    @Override
    public void setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
    }
    
    @Override
    public HandlerList getHandlers() {
        return PlayerMineItemsEvent.handlerList;
    }
    
    public static HandlerList getHandlerList() {
        return PlayerMineItemsEvent.handlerList;
    }
    
    static {
        PlayerMineItemsEvent.handlerList = new HandlerList();
    }
}
