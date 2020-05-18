package com.fishy.hcf.command;

import com.fishy.hcf.HCF;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

//import com.fishy.base.BasePlugin;

public class GiveCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
//        if (!(sender instanceof Player)) {
//            sender.sendMessage(ChatColor.RED + "This command is only executable for players.");
//            return true;
//        }
/*        Player p = (Player) sender;*/
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /give <player> <item>");
            return true;
        }
        if (Bukkit.getPlayer(args[0]) == null) {
            sender.sendMessage(ChatColor.RED + "Player named or with UUID '" + ChatColor.RED + args[0] + ChatColor.RED + "' not found.");
            return true;
        }
        Player t = Bukkit.getPlayer(args[0]);
        if (HCF.getPlugin().getItemDb().getItem(args[1]) == null) {
            sender.sendMessage(ChatColor.RED + "Item named or with ID '" + ChatColor.RED + args[1] + ChatColor.RED + "' not found.");
            return true;
        }
        if (args.length == 2) {
            if (!t.getInventory().addItem(new ItemStack[]{HCF.getPlugin().getItemDb().getItem(args[1], HCF.getPlugin().getItemDb().getItem(args[1]).getMaxStackSize())}).isEmpty()) {
            	sender.sendMessage(ChatColor.RED + "The inventory of the player is full.");
                return true;
            }
            sender.sendMessage(ChatColor.WHITE + "You gave '" + ChatColor.WHITE + t.getName() + ChatColor.WHITE + "' " + " 64, " + HCF.getPlugin().getItemDb().getName(HCF.getPlugin().getItemDb().getItem(args[1])));
                }
        if (args.length == 3) {
            if (!t.getInventory().addItem(new ItemStack[]{HCF.getPlugin().getItemDb().getItem(args[1], Integer.parseInt(args[2]))}).isEmpty()) {
            	sender.sendMessage(ChatColor.RED + "The inventory of the player is full.");
                return true;
            }
            sender.sendMessage(ChatColor.WHITE + "You gave '" + ChatColor.WHITE + t.getName() + ChatColor.WHITE + "' " + args[2] + ", " + HCF.getPlugin().getItemDb().getName(HCF.getPlugin().getItemDb().getItem(args[1])));
        }
        return true;
    }
    
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> results = new ArrayList<>();
        for (Player target : Bukkit.getOnlinePlayers()) {
            if (!results.contains(target.getName())) {
                results.add(target.getName());
            }
        }
        return results;
    }
}
