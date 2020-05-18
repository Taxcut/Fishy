package com.fishy.hcf.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.ChatColor;

public class ListCommand implements CommandExecutor {
    @SuppressWarnings("deprecation")
	@Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        sender.sendMessage(ChatColor.WHITE + "There are currently " + ChatColor.GRAY + Math.round(Bukkit.getOnlinePlayers().size()) + ChatColor.WHITE + " player" + (Bukkit.getOnlinePlayers().size() > 1 ? "s" : "") + " out of "
        		+ "a max of " + ChatColor.GRAY + Math.round(Bukkit.getMaxPlayers()) + ChatColor.WHITE + " connected to the server.");
        return false;
    }
}