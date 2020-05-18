package com.fishy.hcf.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpallCommand implements CommandExecutor {

    public boolean isDouble(String s) {
        try{
            Double.parseDouble(s);
            return true;
        } catch(NumberFormatException e) {
            return false;
        }
    }
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

            if (args.length > 0) {
                sender.sendMessage("�cUsage: /tpall");
                return true;
            }
            
            Player player = (Player) sender;
            for (Player target : Bukkit.getOnlinePlayers()) {
                target.teleport(player);
            }
            player.sendMessage("�fYou have teleported all players to you.");
            return true;
    }
}
