package com.fishy.hcf.command.links;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.fishy.hcf.util.CC;

import net.md_5.bungee.api.ChatColor;

public class DiscordCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        sender.sendMessage(CC.DARK_PURPLE.toString() + ChatColor.BOLD + "Discord: " + ChatColor.WHITE + "desirepvp.net/discord");
        return false;
    }
}