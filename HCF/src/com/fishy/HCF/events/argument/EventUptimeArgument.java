package com.fishy.hcf.events.argument;

import com.fishy.hcf.DateTimeFormats;
import com.fishy.hcf.HCF;
import com.fishy.hcf.events.EventTimer;
import com.fishy.hcf.events.faction.EventFaction;
import com.fishy.hcf.util.base.command.CommandArgument;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * A {@link CommandArgument} argument used for checking the uptime of current event.
 */
public class EventUptimeArgument extends CommandArgument {

    private final HCF plugin;

    public EventUptimeArgument(HCF plugin) {
        super("uptime", "Check the uptime of an event");
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

        if (eventTimer.getRemaining() <= 0L) {
            sender.sendMessage(ChatColor.RED + "There is not a running event.");
            return true;
        }

        EventFaction eventFaction = eventTimer.getEventFaction();
        sender.sendMessage(ChatColor.WHITE + "Up-time of " + eventTimer.getName() + " timer" +
                (eventFaction == null ? "" : ": " + ChatColor.RED + '(' + eventFaction.getDisplayName(sender) + ChatColor.RED + ')') +
                ChatColor.WHITE + " is " + ChatColor.GRAY + DurationFormatUtils.formatDurationWords(eventTimer.getUptime(), true, true) + ChatColor.WHITE + ", started at " +
                ChatColor.GRAY + DateTimeFormats.HR_MIN_AMPM_TIMEZONE.format(eventTimer.getStartStamp()) + ChatColor.WHITE + '.');

        return true;
    }
}
