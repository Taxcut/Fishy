package com.fishy.hcf.timer.sotw;

import com.fishy.hcf.user.FactionUser;
import com.fishy.hcf.util.base.BukkitUtils;
import com.fishy.hcf.util.base.JavaUtils;
import com.fishy.hcf.HCF;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SotwCommand implements CommandExecutor, TabCompleter {

    private static final List<String> COMPLETIONS = ImmutableList.of("start", "end");

    private final HCF plugin;

    public SotwCommand(HCF plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("start")) {
                if (!sender.hasPermission("hcf.command.sotw.admin")) {
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
                        sender.sendMessage(ChatColor.RED + "SOTW protection time must last for at least 20 ticks.");
                        return true;
                    }

                    SotwTimer.SotwRunnable sotwRunnable = plugin.getSotwTimer().getSotwRunnable();

                    if (sotwRunnable != null) {
                        sender.sendMessage(ChatColor.RED + "SOTW protection is already enabled, use /" + label + " cancel to end it.");
                        return true;
                    }

                    plugin.getSotwTimer().start(duration);
                    sender.sendMessage(ChatColor.RED + "Started SOTW protection for " + DurationFormatUtils.formatDurationWords(duration, true, true) + ".");
                    return true;
                }
                }

            if (args[0].equalsIgnoreCase("enable")) {
                if (!(sender instanceof Player)) {
                    return false;
                }

                if (HCF.getPlugin().getSotwTimer() == null || HCF.getPlugin().getSotwTimer().getSotwRunnable() == null) {
                    sender.sendMessage(ChatColor.RED + "You can only run this command if " + ChatColor.GREEN.toString() + ChatColor.BOLD + "SOTW Timer" + ChatColor.RED + " is enabled.");
                    return false;
                }
/*                
                if (HCF.getPlugin().getSotwTimer().getSotwRunnable().getRemaining() > TimeUnit.MINUTES.toMillis(30L)) {
                	sender.sendMessage(ChatColor.RED + "You cannot enable your " + ChatColor.GREEN.toString() + ChatColor.BOLD + "SOTW Timer" + ChatColor.RED + " if there is more than 30 minutes remaining.");
                	return false;
                }*/

                FactionUser user = HCF.getPlugin().getUserManager().getUser(((Player) sender).getUniqueId());

                if (user.isSOTW()) {
                    user.setSOTW(false);
                    sender.sendMessage(ChatColor.GREEN + "You have enabled your " + ChatColor.GREEN.toString() + ChatColor.BOLD + "SOTW Timer" + ChatColor.GREEN + "!" + " You can now attack other players.");
                } else {
                    sender.sendMessage(ChatColor.RED + "You have already disabled your " + ChatColor.GREEN.toString() + ChatColor.BOLD + "SOTW Timer" + ChatColor.RED + "!");
                }
                return true;
            }

            if (args[0].equalsIgnoreCase("end") || args[0].equalsIgnoreCase("cancel") || args[0].equalsIgnoreCase("stop")) {
                if (!sender.hasPermission("hcf.command.sotw.admin")) {
                    sender.sendMessage(ChatColor.RED + "No permission");
                } else {
                    if (plugin.getSotwTimer().cancel()) {
                        sender.sendMessage(ChatColor.RED + "Cancelled SOTW protection.");

                        return true;
                    }

                    sender.sendMessage(ChatColor.RED + "SOTW protection is not active.");
                    return true;
                }
            }
                }
        sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
        sender.sendMessage(ChatColor.GREEN + ChatColor.BOLD.toString() + "SOTW Timer" + ChatColor.GREEN + " Help");
        sender.sendMessage(ChatColor.GREEN + "/sotw enable - Enable your SOTW Timer");
        if ((sender.hasPermission("hcf.command.sotw.admin"))) {
                    sender.sendMessage(ChatColor.GREEN + "/sotw start <time> - Start SOTW");
                    sender.sendMessage(ChatColor.GREEN + "/sotw end - End SOTW");
                }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return args.length == 1 ? BukkitUtils.getCompletions(args, COMPLETIONS) : Collections.emptyList();
    }
}
