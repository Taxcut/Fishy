package com.fishy.hcf.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.fishy.hcf.HCF;
import com.fishy.hcf.util.base.BukkitUtils;


public class WorldCommand implements CommandExecutor {

	private final HCF plugin;

	public WorldCommand(HCF plugin) {
		this.plugin = plugin;
	}

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable for players.");
            return true;
        }
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /world <world>");
            return true;
        }
        final World world = Bukkit.getWorld(args[0]);
        if (world == null) {
            sender.sendMessage(ChatColor.RED + "World '" + args[0] + "' not found.");
            return true;
        }
        final Player player = (Player) sender;
        if (player.getWorld().equals(world)) {
            sender.sendMessage(ChatColor.RED + "You are already in that world.");
            return true;
        }
        final Location origin = player.getLocation();
        final Location location = new Location(world, origin.getX(), origin.getY(), origin.getZ(), origin.getYaw(), origin.getPitch());
        player.teleport(location);
        sender.sendMessage(ChatColor.WHITE + "Switched world to '" + world.getName() + ChatColor.WHITE + " [" + WordUtils.capitalizeFully(world.getEnvironment().name().replace('_', ' ')) + ']' + ChatColor.WHITE + "'.");
        return true;
    }

    public List onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) {
            return Collections.emptyList();
        }
        List worlds = Bukkit.getWorlds();
        ArrayList results = new ArrayList(worlds.size());
        for (World world : Bukkit.getWorlds()) {
            results.add(world.getName());
        }
        return BukkitUtils.getCompletions(args, results);
    }
}
