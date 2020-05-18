package com.fishy.hcf.command;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import net.md_5.bungee.api.ChatColor;

 public class SkullCommand implements CommandExecutor {
  
   private ItemStack playerSkullForName(String name) {
	   ItemStack is = new ItemStack(Material.SKULL_ITEM, 1);
	   is.setDurability((short)3);
	   SkullMeta meta = (SkullMeta)is.getItemMeta();
	   meta.setOwner(name);
	   is.setItemMeta(meta);
	   return is;
  }
  
   public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
	   if (!(sender instanceof Player)) { 
		   sender.sendMessage(ChatColor.RED + "You are not a player");
		   return true;
	   }
	   Player player = (Player) sender;
	   if (args.length > 1 || args.length == 0) {
		   sender.sendMessage(ChatColor.RED + "Usage: /skull <player>");
		   return true;
	   } 
	   player.getInventory().addItem(new ItemStack[] { playerSkullForName(args[0]) });
	   player.sendMessage(ChatColor.WHITE + "You have received " + args[0] + "'s head.");
	   return true;
   }
 }