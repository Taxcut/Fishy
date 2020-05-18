package com.fishy.hcf.command;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.minecraft.util.com.google.common.collect.ImmutableList;

public class RenameCommand implements CommandExecutor {
	
	public static ImmutableList<Material> BLOCKED_TYPES = ImmutableList.of(Material.NAME_TAG, Material.TRIPWIRE_HOOK);

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		if(!(sender instanceof Player)){
			sender.sendMessage(ChatColor.RED + "Only players can execute this command.");
			return true;
		}
		if(args.length < 1){
			sender.sendMessage(ChatColor.RED + "Usage: /rename <name>");
			return true;
		}
		Player player = (Player) sender;
		ItemStack stack = player.getItemInHand();
		if(stack == null || stack.getType() == Material.AIR){
			sender.sendMessage(ChatColor.RED + "You are not holding anything.");
			return true;
		}
		ItemMeta meta = stack.getItemMeta();
		String oldName = meta.getDisplayName();
		if(oldName != null){
			oldName = oldName.trim();
		}
		String newName = args[0].equalsIgnoreCase("none") || args[0].equalsIgnoreCase("null") ? null : ChatColor.translateAlternateColorCodes('&', StringUtils.join(args, ' ', 0, args.length));
		if(oldName == null && newName == null){
			sender.sendMessage(ChatColor.RED + "Your held item already has no name.");
			return true;
		}
		if(oldName != null && oldName.equals(newName)){
			sender.sendMessage(ChatColor.RED + "Your held item is already named this.");
			return true;
		}
		meta.setDisplayName(newName);
		stack.setItemMeta(meta);
		if(newName == null){
			sender.sendMessage(ChatColor.GREEN + "Removed name of held item from " + oldName + '.');
			return true;
		}
		sender.sendMessage(ChatColor.GREEN + "Renamed item in hand from " + (oldName == null ? "no name" : oldName) + ChatColor.GREEN + " to " + newName + ChatColor.GREEN + '.');
		return true;
	}
}

