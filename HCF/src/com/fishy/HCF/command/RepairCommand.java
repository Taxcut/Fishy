package com.fishy.hcf.command;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.fishy.base.BaseConstants;
import com.fishy.hcf.util.base.BukkitUtils;

public class RepairCommand implements CommandExecutor {
	
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player target;
        if (args.length > 0) {
            target = BukkitUtils.playerWithNameOrUUID(args[0]);
        } else {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Usage: /repair");
                return true;
            }
            target = (Player) sender;
        }
        if (target != null) {
            HashSet<ItemStack> toRepair = new HashSet();
            if (args.length >= 2 && args[1].equalsIgnoreCase("all")) {
                PlayerInventory targetInventory = target.getInventory();
                toRepair.addAll(Arrays.asList(targetInventory.getContents()));
                toRepair.addAll(Arrays.asList(targetInventory.getArmorContents()));
            } else {
                toRepair.add(target.getItemInHand());
            }
            for (ItemStack stack : toRepair) {
                if (stack != null && stack.getType() != Material.AIR) {
                    stack.setDurability((short) 0);
                }
            }
            sender.sendMessage(ChatColor.WHITE + "Repaired " + ((toRepair.size() > 1) ? "inventory" : "held item") + " of " + target.getName() + '.');
            return true;
        }
        sender.sendMessage(String.format(BaseConstants.PLAYER_WITH_NAME_OR_UUID_NOT_FOUND, args[0]));
        return true;
    }

    public List onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return (args.length == 1) ? null : Collections.emptyList();
    }

}
