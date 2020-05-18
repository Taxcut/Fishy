package com.fishy.hcf.events.koth.argument;

import com.fishy.hcf.HCF;
import com.fishy.hcf.events.EventTimer;
import com.fishy.hcf.util.base.command.CommandArgument;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class KothScheduleArgument extends CommandArgument {

    //private final KothExecutor kothExecutor;
    private final HCF plugin;

    public KothScheduleArgument(HCF plugin) {
        super("help", "View help about how KOTH's work");
        this.plugin = plugin;
        this.permission = "hcf.command.koth.argument." + getName();
    }

    @Override
    public String getUsage(String label) {
        return '/' + label + ' ' + getName();
    }


    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        EventTimer eventTimer = HCF.getPlugin().getTimerManager().eventTimer;
        boolean nextCancelled = eventTimer.isNextCancelled();
        if(nextCancelled || eventTimer.getNextEventFaction() == null || eventTimer.getNextEvent() == null){
            sender.sendMessage(ChatColor.RED + "There are currently no events scheduled");
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', ("&5&lEVENT &8� &f") + ChatColor.LIGHT_PURPLE + eventTimer.getNextEventFaction().getName() + ChatColor.WHITE +
            		" is starting in " + ChatColor.RED + DurationFormatUtils.formatDurationWords(eventTimer.getNextEvent() - System.currentTimeMillis(), true, true) + ChatColor.WHITE + '.'));
            //sender.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.BLUE.toString() + ChatColor.BOLD + "Event" + ChatColor.DARK_GRAY + "] " + ChatColor.BLUE + eventTimer.getNextEventFaction().getName() + ChatColor.WHITE + " in " + ChatColor.GRAY + DurationFormatUtils.formatDurationWords(eventTimer.getNextEvent() - System.currentTimeMillis(), true, true));
        }
        return true;
    }
}
