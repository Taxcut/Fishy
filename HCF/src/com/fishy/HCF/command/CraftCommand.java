package com.fishy.hcf.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class CraftCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            // Make sure the sender is a player
            if (!(sender instanceof Player)) {
                sender.sendMessage("Only players.");
                return true;
            }

            Player player = (Player) sender;

            if (player.hasPermission("hcf.command.craft")) {
                player.openWorkbench(player.getLocation(), true);
            } else {
            	player.sendMessage(ChatColor.RED + "Sorry you don't have permission.");
            }
        return false;
    }
}
