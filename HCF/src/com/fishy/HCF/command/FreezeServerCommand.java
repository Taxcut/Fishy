package com.fishy.hcf.command;

import com.fishy.hcf.HCF;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import static org.bukkit.Bukkit.getServer;

/**
 * Created by Sam on 28/09/2016.
 */
@RequiredArgsConstructor
public class FreezeServerCommand implements CommandExecutor{

    private final HCF plugin;

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
        if(!sender.hasPermission("freezeserver.freeze")){
            sender.sendMessage(ChatColor.RED + "No permission.");
            return true;
        }

        plugin.setServerFrozen(!plugin.isServerFrozen());
        getServer().broadcastMessage(ChatColor.RED + "The server is now " + (plugin.isServerFrozen() ? "frozen" : "unfrozen") + ".");
        return true;
    }


}
