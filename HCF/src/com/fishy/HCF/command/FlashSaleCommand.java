package com.fishy.hcf.command;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.fishy.hcf.HCF;
import com.fishy.hcf.timer.type.FlashSaleTimer;
import com.fishy.hcf.util.base.BukkitUtils;
import com.fishy.hcf.util.base.JavaUtils;

public class FlashSaleCommand implements CommandExecutor {

	private HCF plugin = HCF.getPlugin(HCF.class);
	
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("start")) {
                if (!sender.hasPermission("hcf.command.flashsale.admin")) {
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
                        sender.sendMessage(ChatColor.RED + "The flash sale time must last for at least 20 ticks.");
                        return true;
                    }

                    FlashSaleTimer.flashSaleRunnable flashSaleRunnable = plugin.getFlashSaleTimer().getFlashSaleRunnable();

                    if (flashSaleRunnable != null) {
                        sender.sendMessage(ChatColor.RED + "The flash sale is already active, use /" + label + " cancel to end it.");
                        return true;
                    }

                    HCF.getPlugin().getFlashSaleTimer().start(duration);
                    sender.sendMessage(ChatColor.RED + "Started The flash sale for " + DurationFormatUtils.formatDurationWords(duration, true, true) + ".");
                    return true;
                	}
                }


            if (args[0].equalsIgnoreCase("end") || args[0].equalsIgnoreCase("cancel") || args[0].equalsIgnoreCase("stop")) {
                if (!sender.hasPermission("hcf.command.flashsale.admin")) {
                    sender.sendMessage(ChatColor.RED + "No permission");
                } else {
                    if (HCF.getPlugin().getFlashSaleTimer().cancel()) {
                        sender.sendMessage(ChatColor.RED + "Cancelled the flash sale.");

                        return true;
                    }

                    sender.sendMessage(ChatColor.RED + "The flash sale is not active.");
                    return true;
                }
            }
        }
        sender.sendMessage(ChatColor.DARK_GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
        sender.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD + "Flash Sale Timer Help");
        sender.sendMessage("");
        sender.sendMessage(ChatColor.GREEN + "/flashsale start <time> - Start the flash sale");
        sender.sendMessage(ChatColor.GREEN + "/flashsale end - End the flash sale");
        sender.sendMessage(ChatColor.DARK_GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
        return true;
    }
}
