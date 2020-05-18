package com.fishy.hcf.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.fishy.hcf.HCF;

public class StackCommand implements CommandExecutor{
    private final HCF hcf;

    public StackCommand(HCF hcf) {
        this.hcf = hcf;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = ((Player) sender);
            PlayerInventory inventory = player.getInventory();
            ItemStack[] contents = inventory.getContents();
            int done = 0;
            for(int i = 0; i < contents.length; i++) {
                ItemStack current = contents[i];
                if (current != null) {
                    for (int i2 = i + 1; i2 < contents.length; i2++) {
                        ItemStack current2 = contents[i2];
                        if (current.isSimilar(current2)) {
                            int allowed = current.getMaxStackSize() - current.getAmount();
                            if(allowed > 0) {
                                int left = current2.getAmount() - allowed;
                                if (left > 0) {
                                    current2.setAmount(left);
                                    current.setAmount(current.getMaxStackSize());
                                } else {
                                    done++;
                                    current.setAmount(current.getAmount() + current2.getAmount());
                                    contents[i2] = null;
                                }
                            }
                        }
                    }
                }
            }
            inventory.setContents(contents);
            player.updateInventory();
            sender.sendMessage(done == 0 ? ChatColor.RED + "You have no items to stack" : ChatColor.WHITE + "You've stacked " + done + " item" + (done != 1 ? "s" : ""));
        } else {
            sender.sendMessage(ChatColor.RED + "You may not do this command");
        }
        return true;
    }
}
	