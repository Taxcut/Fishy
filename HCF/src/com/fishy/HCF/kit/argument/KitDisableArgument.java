package com.fishy.hcf.kit.argument;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.fishy.hcf.HCF;
import com.fishy.hcf.kit.Kit;
import com.fishy.hcf.util.base.command.CommandArgument;

public class KitDisableArgument extends CommandArgument
{
    private final HCF plugin;

    public KitDisableArgument(final HCF plugin) {
        super("disable", "Disable or enable a kit");
        this.plugin = plugin;
        this.aliases = new String[] { "enable", "toggle" };
        this.permission = "hcf.command.kit." + this.getName();
    }

    @Override
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <kitName>";
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
            return true;
        }
        final Kit kit = this.plugin.getKitManager().getKit(args[1]);
        if (kit == null) {
            sender.sendMessage(ChatColor.RED + "No kit named " + args[1] + " found.");
            return true;
        }
        final boolean newEnabled = !kit.isEnabled();
        kit.setEnabled(newEnabled);
        sender.sendMessage(ChatColor.YELLOW + "Kit " + kit.getDisplayName() + " has been " + (newEnabled ? (ChatColor.GREEN + "enabled") : (ChatColor.RED + "disabled")) + ChatColor.YELLOW + '.');
        return true;
    }

    @Override
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length != 2) {
            return Collections.emptyList();
        }
        final Collection<Kit> kits = this.plugin.getKitManager().getKits();
        final List<String> results = new ArrayList<>(kits.size());
        for (final Kit kit : kits) {
            results.add(kit.getName());
        }
        return results;
    }
}
