package com.fishy.hcf.command;


import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import com.fishy.hcf.HCF;
import com.fishy.hcf.classes.PvpClass;
import com.fishy.hcf.util.base.BukkitUtils;

public class HelpCommand implements CommandExecutor, TabCompleter {

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(ChatColor.DARK_GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
        if (HCF.getPlugin().getConfig().getBoolean("KITMAP")) {
            sender.sendMessage(ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + "Information");
            sender.sendMessage(ChatColor.LIGHT_PURPLE + " * " + ChatColor.WHITE + "World Border" + ChatColor.GRAY + ": " + ChatColor.GRAY + HCF.getPlugin().getConfig().getInt("BORDERS.NORMAL") + " Blocks");
            sender.sendMessage(ChatColor.LIGHT_PURPLE + " * " + ChatColor.WHITE + "Warzone Radius" + ChatColor.GRAY + ": " + ChatColor.GRAY + HCF.getPlugin().getConfig().getInt("WARZONERADIUS") + " Blocks");
            sender.sendMessage(ChatColor.LIGHT_PURPLE + " * " + ChatColor.WHITE + "Kits" + ChatColor.GRAY + ": " + ChatColor.GRAY + HCF.getPlugin().getPvpClassManager().getPvpClasses().stream().map(PvpClass::getName).collect(Collectors.joining(", ")));
            sender.sendMessage(ChatColor.LIGHT_PURPLE + " * " + ChatColor.WHITE + "End Exit" + ChatColor.GRAY + ": " + ChatColor.GRAY + "(" + 0 + "," + 250 + ") South Road");
        } else {
            sender.sendMessage(ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + "Map Information");
            sender.sendMessage(ChatColor.LIGHT_PURPLE + " * " + ChatColor.WHITE + "World Border" + ChatColor.GRAY + ": " + ChatColor.GRAY + HCF.getPlugin().getConfig().getInt("BORDERS.NORMAL") + " Blocks");
            sender.sendMessage(ChatColor.LIGHT_PURPLE + " * " + ChatColor.WHITE + "Warzone Radius" + ChatColor.GRAY + ": " + ChatColor.GRAY + HCF.getPlugin().getConfig().getInt("WARZONERADIUS") + " Blocks");
            sender.sendMessage(ChatColor.LIGHT_PURPLE + " * " + ChatColor.WHITE + "End Portals" + ChatColor.GRAY + ": " + ChatColor.GRAY + "(" + 1000 + "," + 1000 + ") Each Quadrant");
            sender.sendMessage(ChatColor.LIGHT_PURPLE + " * " + ChatColor.WHITE + "End Exit" + ChatColor.GRAY + ": " + ChatColor.GRAY + "(" + 0 + "," + 250 + ") South Road");
            sender.sendMessage(ChatColor.LIGHT_PURPLE + " * " + ChatColor.WHITE + "Classes" + ChatColor.GRAY + ": " + ChatColor.GRAY + HCF.getPlugin().getPvpClassManager().getPvpClasses().stream().map(PvpClass::getName).collect(Collectors.joining(", ")));
        }
    	sender.sendMessage(" ");
        sender.sendMessage(ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + "Links");
        sender.sendMessage(ChatColor.LIGHT_PURPLE + " * " + ChatColor.WHITE + "Discord" + ChatColor.GRAY + ": " + ChatColor.GRAY + "desirepvp.net/discord");
        sender.sendMessage(ChatColor.LIGHT_PURPLE + " * " + ChatColor.WHITE + "Teamspeak" + ChatColor.GRAY + ": " + ChatColor.GRAY + "ts.desirepvp.net");
        sender.sendMessage(ChatColor.LIGHT_PURPLE + " * " + ChatColor.WHITE + "Store" + ChatColor.GRAY + ": " + ChatColor.GRAY + "shop.desirepvp.net");
    	sender.sendMessage(ChatColor.DARK_GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
        return true;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return java.util.Collections.emptyList();
    }
}