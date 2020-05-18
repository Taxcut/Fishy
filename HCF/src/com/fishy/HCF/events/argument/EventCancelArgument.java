package com.fishy.hcf.events.argument;

import com.fishy.hcf.HCF;
import com.fishy.hcf.events.EventTimer;
import com.fishy.hcf.faction.type.Faction;
import com.fishy.hcf.util.base.command.CommandArgument;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * An {@link CommandArgument} used for cancelling the current running event.
 */
public class EventCancelArgument extends CommandArgument {

    private final HCF plugin;

    public EventCancelArgument(HCF plugin) {
        super("cancel", "Cancels a running event", new String[]{"stop", "end"});
        this.plugin = plugin;
        this.permission = "hcf.command.event.argument." + getName();
    }

    @Override
    public String getUsage(String label) {
        return '/' + label + ' ' + getName();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        EventTimer eventTimer = plugin.getTimerManager().getEventTimer();
        Faction eventFaction = eventTimer.getEventFaction();

        if (!eventTimer.clearCooldown()) {
            sender.sendMessage(ChatColor.RED + "There is not a running event.");
            return true;
        }

        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', ("&5&lEVENT &8� &f") + ChatColor.LIGHT_PURPLE + eventFaction.getName() + ChatColor.WHITE + " has been cancelled.")); 
        return true;
    }
}
