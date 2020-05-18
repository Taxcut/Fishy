package com.fishy.hcf.command;

import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.fishy.hcf.HCF;
import com.fishy.hcf.util.base.ItemBuilder;

import net.minecraft.util.org.apache.commons.lang3.text.WordUtils;

public class SpawnerCommand implements CommandExecutor, TabCompleter
{
    private final HCF plugin;
    
    public SpawnerCommand(final HCF plugin) {
        this.plugin = plugin;
    }
    
    public String C(final String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }
    
    public boolean onCommand(final CommandSender sender, final Command cmd, final String commandLabel, final String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "/spawner <entity>");
            return false;
        }
        final String spawner = args[0];
        final Player p = (Player)sender;
        final Inventory inv = (Inventory)p.getInventory();
        inv.addItem(new ItemStack[] { new ItemBuilder(Material.MOB_SPAWNER).displayName(ChatColor.GREEN + "Spawner").loreLine(ChatColor.WHITE + WordUtils.capitalizeFully(spawner)).build() });
        p.sendMessage(this.C("&cYou just got a &4" + spawner + "&c."));
        return false;
    }
    
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        return Collections.emptyList();
    }
}
