package com.fishy.hcf.util;

import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.fishy.hcf.HCF;
import com.fishy.hcf.user.FactionUser;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
public class MessageEvent
extends Event
implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final Player sender;
    private final Player recipient;
    private final String message;
    private final boolean isReply;
    private boolean cancelled = false;

    @SuppressWarnings({ "unchecked", "rawtypes" })
	public MessageEvent(Player sender, Set<Player> recipients, String message, boolean isReply) {
        this.sender = sender;
        this.recipient = (Player)Iterables.getFirst((Iterable)recipients, (Object)null);
        this.message = message;
        this.isReply = isReply;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Player getSender() {
        return this.sender;
    }

    public Player getRecipient() {
        return this.recipient;
    }

    public String getMessage() {
        return this.message;
    }

    public boolean isReply() {
        return this.isReply;
    }

    public void send() {
        Preconditions.checkNotNull((Object)this.sender, (Object)"The sender cannot be null");
        Preconditions.checkNotNull((Object)this.recipient, (Object)"The recipient cannot be null");
        if (this.message.equalsIgnoreCase("IP")) {
            
        }
        HCF plugin = HCF.getPlugin();
        FactionUser sendingUser = plugin.getUserManager().getUser(this.sender.getUniqueId());
        FactionUser recipientUser = plugin.getUserManager().getUser(this.recipient.getUniqueId());
        sendingUser.setLastRepliedTo(recipientUser.getUserUUID());
        recipientUser.setLastRepliedTo(sendingUser.getUserUUID());
        long millis = System.currentTimeMillis();
        recipientUser.setLastReceivedMessageMillis(millis);
        String rank = ChatColor.translateAlternateColorCodes('&', "&f" + plugin.getChat().getPlayerPrefix(sender)).replace("_", " ");
        String displayName = rank + this.sender.getName();
        String rank1 = ChatColor.translateAlternateColorCodes('&', "&f" + plugin.getChat().getPlayerPrefix(recipient)).replace("_", " ");
        String displayName1 = rank1 + this.recipient.getName();
        this.sender.sendMessage(ChatColor.GRAY + "(To " + ChatColor.GRAY + displayName1 + ChatColor.GRAY + ") " + ChatColor.GRAY + this.message);
        this.recipient.sendMessage(ChatColor.GRAY + "(From " + ChatColor.GRAY + displayName + ChatColor.GRAY + ") " + ChatColor.GRAY + this.message);
    }

    @Override
	public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
	public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
	public HandlerList getHandlers() {
        return handlers;
    }
}

