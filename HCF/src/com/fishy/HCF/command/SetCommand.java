package com.fishy.hcf.command;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.fishy.hcf.HCF;
import com.fishy.hcf.util.base.BukkitUtils;



public class SetCommand implements CommandExecutor
{
    private HCF plugin;
    
    public SetCommand(final HCF plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if (!sender.hasPermission("command.set")) {
            sender.sendMessage(ChatColor.RED + "No permission.");
            return true;
        }
        final Player p = (Player)sender;
        if (args.length != 1) {
            p.sendMessage(ChatColor.GOLD + BukkitUtils.STRAIGHT_LINE_DEFAULT);
            p.sendMessage(ChatColor.YELLOW + ChatColor.BOLD.toString() + "Set Location " + ChatColor.GRAY + "(Page 1/1)");
            p.sendMessage(ChatColor.YELLOW + " /set spawn" + ChatColor.GOLD + " � " + ChatColor.RESET + "Set the location for end-spawn.");
            p.sendMessage(ChatColor.YELLOW + " /set exit" + ChatColor.GOLD + " � " + ChatColor.RESET + "Set the location for end-exit.");
            p.sendMessage(ChatColor.GOLD + BukkitUtils.STRAIGHT_LINE_DEFAULT);
            return true;
        }
        if (args[0].equalsIgnoreCase("exit")) {
            final Location loc = p.getLocation();
            this.plugin.getConfig().set("Location.end_exit.x", (Object)loc.getX());
            this.plugin.getConfig().set("Location.end_exit.y", (Object)loc.getY());
            this.plugin.getConfig().set("Location.end_exit.z", (Object)loc.getZ());
            this.plugin.getConfig().set("Location.end_exit.pitch", (Object)loc.getPitch());
            this.plugin.getConfig().set("Location.end_exit.yaw", (Object)loc.getYaw());
            this.plugin.saveConfig();
            p.sendMessage(ChatColor.GREEN + "End Exit has been set!.");
        }
        else if (args[0].equalsIgnoreCase("spawn")) {
            final Location loc = p.getLocation();
            this.plugin.getConfig().set("Location.world_the_end.x", (Object)loc.getX());
            this.plugin.getConfig().set("Location.world_the_end.y", (Object)loc.getY());
            this.plugin.getConfig().set("Location.world_the_end.z", (Object)loc.getZ());
            this.plugin.getConfig().set("Location.world_the_end.pitch", (Object)loc.getPitch());
            this.plugin.getConfig().set("Location.world_the_end.yaw", (Object)loc.getYaw());
            this.plugin.saveConfig();
            p.sendMessage(ChatColor.GREEN + "End spawn has been set!.");
        }
        return true;
    }
}