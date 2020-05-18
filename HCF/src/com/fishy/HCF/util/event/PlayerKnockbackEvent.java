package com.fishy.hcf.util.event;

import org.bukkit.event.*;
import org.bukkit.entity.*;

public class PlayerKnockbackEvent extends Event implements Cancellable
{
    private static HandlerList handlerList;
    private final Player player;
    private double dx;
    private double dy;
    private double dz;
    private boolean cancelled;
    
    public PlayerKnockbackEvent(final Player player, final double dx, final double dy, final double dz) {
        this.cancelled = false;
        this.player = player;
        this.dx = dx;
        this.dy = dy;
        this.dz = dz;
    }
    
    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }
    
    @Override
    public void setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
    }
    
    public Player getPlayer() {
        return this.player;
    }
    
    public double getDx() {
        return this.dx;
    }
    
    public double getDy() {
        return this.dy;
    }
    
    public double getDz() {
        return this.dz;
    }
    
    public void setDx(final double dx) {
        this.dx = dx;
    }
    
    public void setDy(final double dy) {
        this.dy = dy;
    }
    
    public void setDz(final double dz) {
        this.dz = dz;
    }
    
    @Override
    public HandlerList getHandlers() {
        return PlayerKnockbackEvent.handlerList;
    }
    
    public static HandlerList getHandlerList() {
        return PlayerKnockbackEvent.handlerList;
    }
    
    static {
        PlayerKnockbackEvent.handlerList = new HandlerList();
    }
}
