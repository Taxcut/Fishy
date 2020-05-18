package com.fishy.hcf.command;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.fishy.hcf.HCF;
import com.fishy.hcf.timer.type.RebootTimer;
import com.fishy.hcf.util.base.BukkitUtils;
import com.fishy.hcf.util.base.JavaUtils;

public class RebootCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("start")) {
                if (!sender.hasPermission("hcf.command.reboot.admin")) {
                    sender.sendMessage(ChatColor.RED + "No permission");
                } else {
                    if (args.length < 2) {
                        sender.sendMessage(ChatColor.RED + "Usage: /" + label + " " + args[0].toLowerCase() + " <duration>");
                        return true;
                    }



                    long duration = JavaUtils.parse(args[1]);

                    if (duration == -1L) {
                        sender.sendMessage(ChatColor.RED + "'" + args[1] + "' is an invalid duration.");
                        return true;
                    }

                    if (duration < 1000L) {
                        sender.sendMessage(ChatColor.RED + "The reboot time must last for at least 20 ticks.");
                        return true;
                    }

                    RebootTimer.AutoRestartRunnable autoRestartRunnable = HCF.getPlugin().getTimerManager().getAutoRestartTimer().getAutoRestartRunnable();

                    if (autoRestartRunnable != null) {
                        sender.sendMessage(ChatColor.RED + "The reboot is already active, use /" + label + " cancel to end it.");
                        return true;
                    }

                    HCF.getPlugin().getTimerManager().getAutoRestartTimer().start(duration);
                    sender.sendMessage(ChatColor.RED + "Started reboot for " + DurationFormatUtils.formatDurationWords(duration, true, true) + ".");
                    return true;
                	}
                }


            if (args[0].equalsIgnoreCase("end") || args[0].equalsIgnoreCase("cancel") || args[0].equalsIgnoreCase("stop")) {
                if (!sender.hasPermission("hcf.command.keysale.admin")) {
                    sender.sendMessage(ChatColor.RED + "No permission");
                } else {
                    if (HCF.getPlugin().getTimerManager().getAutoRestartTimer().cancel()) {
                        sender.sendMessage(ChatColor.RED + "Cancelled the reboot.");

                        return true;
                    }

                    sender.sendMessage(ChatColor.RED + "The reboot is not active.");
                    return true;
                }
            }
        }
        sender.sendMessage(ChatColor.DARK_GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
        sender.sendMessage(ChatColor.DARK_RED.toString() + ChatColor.BOLD + "Reboot Help");
        sender.sendMessage("");
        sender.sendMessage(ChatColor.RED + "/reboot start <time> - Start the reboot");
        sender.sendMessage(ChatColor.RED + "/reboot end - End the reboot");
        sender.sendMessage(ChatColor.DARK_GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
        return true;
    }
}
