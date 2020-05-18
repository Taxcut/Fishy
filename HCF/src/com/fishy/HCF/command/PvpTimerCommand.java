package com.fishy.hcf.command;

import com.fishy.hcf.HCF;
import com.fishy.hcf.timer.type.InvincibilityTimer;
import com.fishy.hcf.util.DurationFormatter;
import com.fishy.hcf.util.base.BukkitUtils;
import com.google.common.collect.ImmutableList;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class PvpTimerCommand implements CommandExecutor, TabCompleter {

    private final HCF plugin;

    public PvpTimerCommand(HCF plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return true;
        }

        Player player = (Player) sender;
        InvincibilityTimer pvpTimer = plugin.getTimerManager().getInvincibilityTimer();

        if (args.length < 1) {
            printUsage(sender, label, pvpTimer);
            return true;
        }

        if (args[0].equalsIgnoreCase("enable") || args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("off")) {
            if (pvpTimer.getRemaining(player) <= 0L) {
                sender.sendMessage(ChatColor.RED + "Your " + pvpTimer.getDisplayName() + ChatColor.RED + " timer is currently not active.");
                return true;
            }

            sender.sendMessage(ChatColor.RED + "Your " + pvpTimer.getDisplayName() + ChatColor.RED + " timer is now off.");
            pvpTimer.clearCooldown(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("remaining") || args[0].equalsIgnoreCase("time") || args[0].equalsIgnoreCase("left") || args[0].equalsIgnoreCase("check")) {
            long remaining = pvpTimer.getRemaining(player);
            if (remaining <= 0L) {
                sender.sendMessage(ChatColor.RED + "Your " + pvpTimer.getDisplayName() + ChatColor.RED + " timer is currently not active.");
                return true;
            }

            sender.sendMessage(ChatColor.RED + "Your " + pvpTimer.getDisplayName() + ChatColor.RED + " timer is active for another " +
                    ChatColor.BOLD + DurationFormatter.getRemaining(remaining, true, false) + ChatColor.RED + (pvpTimer.isPaused(player) ? " and is currently paused." : "."));

            return true;
        }

        printUsage(sender, label, pvpTimer);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return args.length == 1 ? BukkitUtils.getCompletions(args, COMPLETIONS) : Collections.emptyList();
    }

    private static final ImmutableList<String> COMPLETIONS = ImmutableList.of("enable", "time");

    private void printUsage(CommandSender sender, String label, InvincibilityTimer pvpTimer) {
    	sender.sendMessage(ChatColor.DARK_GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
    	sender.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD + "PVP Timer Help " + ChatColor.GRAY + "(Page 1/1)");
        sender.sendMessage(ChatColor.GREEN + "/pvp enable " + ChatColor.GRAY + "- " + ChatColor.WHITE + "Removes your PVP Timer.");
        sender.sendMessage(ChatColor.GREEN + "/pvp time " + ChatColor.GRAY + "- " + ChatColor.WHITE + "Check remaining PVP Timer time.");
        sender.sendMessage(ChatColor.GREEN + "/lives " + ChatColor.GRAY + "- " + ChatColor.WHITE + "Life and Deathban related commands.");
        sender.sendMessage(ChatColor.DARK_GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
    }

}
