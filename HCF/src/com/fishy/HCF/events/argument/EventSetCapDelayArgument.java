package com.fishy.hcf.events.argument;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.fishy.hcf.HCF;
import com.fishy.hcf.events.CaptureZone;
import com.fishy.hcf.events.faction.EventFaction;
import com.fishy.hcf.events.faction.KothFaction;
import com.fishy.hcf.faction.type.Faction;
import com.fishy.hcf.util.base.JavaUtils;
import com.fishy.hcf.util.base.command.CommandArgument;

/**
 * An {@link CommandArgument} used for deleting an {@link EventFaction}.
 */
public class EventSetCapDelayArgument extends CommandArgument {

    private final HCF plugin;

    public EventSetCapDelayArgument(HCF plugin) {
        super("setcapdelay", "Sets cap delay");
        this.plugin = plugin;
        this.aliases = new String[]{"setcaptime"};
        this.permission = "hcf.command.event.argument." + getName();
    }

    @Override
    public String getUsage(String label) {
        return '/' + label + ' ' + getName() + " <eventName>";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
            return true;
        }

        Faction faction = plugin.getFactionManager().getFaction(args[1]);

        if (!(faction instanceof KothFaction)) {
            sender.sendMessage(ChatColor.RED + "There is not a KOTH arena named '" + args[1] + "'.");
            return true;
        }

        long duration = JavaUtils.parse(HCF.SPACE_JOINER.join(Arrays.copyOfRange(args, 2, args.length)));

        if (duration == -1L) {
            sender.sendMessage(ChatColor.RED + "Invalid duration, use the correct format: 10m 1s");
            return true;
        }

        KothFaction kothFaction = (KothFaction) faction;
        CaptureZone captureZone = kothFaction.getCaptureZone();

        if (captureZone == null) {
            sender.sendMessage(ChatColor.RED + kothFaction.getDisplayName(sender) + ChatColor.RED + " does not have a capture zone.");
            return true;
        }

        // Update the remaining time if it is less.
        if (captureZone.isActive() && duration < captureZone.getRemainingCaptureMillis()) {
            captureZone.setRemainingCaptureMillis(duration);
        }

        captureZone.setDefaultCaptureMillis(duration);
        sender.sendMessage(ChatColor.WHITE + "Set the capture delay of KOTH arena " +
                ChatColor.WHITE + kothFaction.getDisplayName(sender) + ChatColor.WHITE + " to " +
                ChatColor.WHITE + DurationFormatUtils.formatDurationWords(duration, true, true) + ChatColor.WHITE + '.');

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2) {
            return Collections.emptyList();
        }

        return plugin.getFactionManager().getFactions().stream().filter(faction -> faction instanceof KothFaction).map(Faction::getName).collect(Collectors.toList());
    }
}
