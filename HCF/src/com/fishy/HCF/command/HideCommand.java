package com.fishy.hcf.command;

import com.fishy.hcf.HCF;
import com.fishy.hcf.listener.HideStaffListener;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HideCommand implements CommandExecutor {

    @Override
	public boolean onCommand(CommandSender player, Command cmd, String label, String[] args) {
        Player sender = (Player)player;
        HideStaffListener.getStaff();
        if (!(player instanceof Player)) {
            player.sendMessage(ChatColor.RED + "You must be a player to use this commad.");
        }
        if (!HCF.hiddenPlayers) {
        	HCF.setHidden(true);
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aAll players inside of spawn have been hidden."));
        } else {
        	HCF.setHidden(false);
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cAll players inside of spawn have been shown."));
        }
        return true;
    }
}
