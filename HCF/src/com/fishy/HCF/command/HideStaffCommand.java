package com.fishy.hcf.command;

import com.fishy.hcf.listener.HideStaffListener;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HideStaffCommand implements CommandExecutor {

    @Override
	public boolean onCommand(CommandSender player, Command cmd, String label, String[] args) {
        Player sender = (Player)player;
        HideStaffListener.getStaff();
        if (!(player instanceof Player)) {
            player.sendMessage(ChatColor.RED + "You must be a player to use this commad.");
        }
        else if (!player.hasPermission("hcf.command.hidestaff")) {
            player.sendMessage(ChatColor.RED + "No permission.");
        }
        else if (HideStaffListener.showStaffEnabled(sender)) {
        	HideStaffListener.showStaff.put(sender, false);
            for (int i = 0; i < HideStaffListener.staff.size(); ++i) {
                sender.hidePlayer(HideStaffListener.staff.get(i));
            }
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aAll staff users have been hidden."));
        }
        else {
        	HideStaffListener.showStaff.put(sender, true);
            for (int i = 0; i < HideStaffListener.staff.size(); ++i) {
                sender.showPlayer(HideStaffListener.staff.get(i));
            }
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cAll staff users have been shown."));
        }
        return true;
    }
}
