package com.fishy.hcf.kit.argument;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.fishy.hcf.HCF;
import com.fishy.hcf.kit.Kit;
import com.fishy.hcf.util.base.command.CommandArgument;

public class KitGuiArgument extends CommandArgument
{
    private final HCF plugin;

    public KitGuiArgument(final HCF plugin) {
        super("gui", "Opens the kit gui");
        this.plugin = plugin;
        this.aliases = new String[] { "menu" };
        this.permission = "hcf.command.kit." + this.getName();
    }

    @Override
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName();
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players may open kit GUI's.");
            return true;
        }
        final Collection<Kit> kits = this.plugin.getKitManager().getKits();
        if (kits.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "No kits have been defined.");
            return true;
        }
        final Player player = (Player)sender;
        player.openInventory(this.plugin.getKitManager().getGui(player));
        return true;
    }

    @Override
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        return Collections.emptyList();
    }
}
