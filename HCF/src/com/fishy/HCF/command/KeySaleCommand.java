package com.fishy.hcf.command;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.fishy.hcf.HCF;
import com.fishy.hcf.timer.type.KeySaleTimer;
import com.fishy.hcf.util.base.BukkitUtils;
import com.fishy.hcf.util.base.JavaUtils;

public class KeySaleCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("start")) {
                if (!sender.hasPermission("hcf.command.keysale.admin")) {
                    sender.sendMessage(ChatColor.RED + "No permission");
                } else {
                    if (args.length < 2) {
                        sender.sendMessage(ChatColor.RED + "Usage: /" + label + " " + args[0].toLowerCase() + " <duration>");
                        return true;
                    }



                    long duration = JavaUtils.parse(args[1]);

                    if (duration == -1L) {
                        sender.sendMessage(ChatColor.RED + "'" + args[0] + "' is an invalid duration.");
                        return true;
                    }

                    if (duration < 1000L) {
                        sender.sendMessage(ChatColor.RED + "The key sale time must last for at least 20 ticks.");
                        return true;
                    }

                    KeySaleTimer.KeySaleRunnable keySaleRunnable = HCF.getPlugin().getKeySaleTimer().getKeySaleRunnable();

                    if (keySaleRunnable != null) {
                        sender.sendMessage(ChatColor.RED + "The key sale is already active, use /" + label + " cancel to end it.");
                        return true;
                    }

                    HCF.getPlugin().getKeySaleTimer().start(duration);
                    sender.sendMessage(ChatColor.RED + "Started The key sale for " + DurationFormatUtils.formatDurationWords(duration, true, true) + ".");
                    return true;
                	}
                }


            if (args[0].equalsIgnoreCase("end") || args[0].equalsIgnoreCase("cancel") || args[0].equalsIgnoreCase("stop")) {
                if (!sender.hasPermission("hcf.command.keysale.admin")) {
                    sender.sendMessage(ChatColor.RED + "No permission");
                } else {
                    if (HCF.getPlugin().getKeySaleTimer().cancel()) {
                        sender.sendMessage(ChatColor.RED + "Cancelled the key sale.");

                        return true;
                    }

                    sender.sendMessage(ChatColor.RED + "The Key Sale timer is not active.");
                    return true;
                }
            }
        }
        sender.sendMessage(ChatColor.DARK_GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
        sender.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD + "Key Sale Help");
        sender.sendMessage("");
        sender.sendMessage(ChatColor.GREEN + "/keysale start <time> - Start the key sale");
        sender.sendMessage(ChatColor.GREEN + "/keysale end - End the key sale");
        sender.sendMessage(ChatColor.DARK_GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
        return true;
    }
}
