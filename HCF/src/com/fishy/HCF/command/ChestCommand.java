package com.fishy.hcf.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.fishy.hcf.HCF;

public class ChestCommand implements CommandExecutor
{
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) { 
    	
        if (HCF.getPlugin().getTimerManager().getCombatTimer().getRemaining((Player) sender) > 0) {
        	sender.sendMessage(ChatColor.RED + "You cannot use this command in combat.");
        	return false;
        }
 
        Player player = (Player)sender;
        player.openInventory(player.getEnderChest());
        player.sendMessage(ChatColor.GREEN + "You are now opening your chest...");
        return false;
    }
}
