package com.fishy.hcf.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.fishy.hcf.listener.DeathListener;

public class DeathCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        String usage = ChatColor.RED + "/refund <player>";
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You must be a player");
        }
        else if (args.length < 1) {
            sender.sendMessage(usage);
        }
        else {
            Player target;
            if ((target = Bukkit.getPlayer(args[0])) == null) {
                sender.sendMessage(ChatColor.RED + "Player must be online");
            } else {
                if (DeathListener.inventoryContents.containsKey(target.getUniqueId())) {
                    target.getInventory().setContents(DeathListener.inventoryContents.remove(target.getUniqueId()));
                    target.getInventory().setArmorContents(DeathListener.armorContents.remove(target.getUniqueId()));
                    Command.broadcastCommandMessage(sender, ChatColor.YELLOW + "Refunded " + target.getName() + "'s items");
                } else {
                    sender.sendMessage(ChatColor.RED + "That player has already been refunded their items.");
                }
            }
        }
        return true;
    }
}
