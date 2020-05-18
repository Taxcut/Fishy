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
import com.google.common.collect.ImmutableList;

import net.minecraft.util.com.google.common.primitives.Ints;

public class KitSetMaxUsesArgument extends CommandArgument
{
    private static final List<String> COMPLETIONS_THIRD;
    private final HCF plugin;

    public KitSetMaxUsesArgument(final HCF plugin) {
        super("setmaxuses", "Sets the maximum uses for a kit");
        this.plugin = plugin;
        this.aliases = new String[] { "setmaximumuses" };
        this.permission = "hcf.command.kit." + this.getName();
    }

    @Override
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <kitName> <amount|unlimited>";
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
            return true;
        }
        final Kit kit = this.plugin.getKitManager().getKit(args[1]);
        if (kit == null) {
            sender.sendMessage(ChatColor.RED + "There is not a kit named " + args[1] + '.');
            return true;
        }
        Integer amount;
        if (KitSetMaxUsesArgument.COMPLETIONS_THIRD.contains(args[2])) {
            amount = Integer.MAX_VALUE;
        }
        else {
            amount = Ints.tryParse(args[2]);
            if (amount == null) {
                sender.sendMessage(ChatColor.RED + "'" + args[2] + "' is not a number.");
                return true;
            }
        }
        kit.setMaximumUses(amount);
        sender.sendMessage(ChatColor.YELLOW + "Set maximum uses of kit " + kit.getDisplayName() + " to " + ((amount == Integer.MAX_VALUE) ? "unlimited" : amount) + '.');
        return true;
    }

    @Override
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length == 2) {
            final Collection<Kit> kits = this.plugin.getKitManager().getKits();
            final List<String> results = new ArrayList<>(kits.size());
            for (final Kit kit : kits) {
                results.add(kit.getName());
            }
            return results;
        }
        if (args.length == 3) {
            return KitSetMaxUsesArgument.COMPLETIONS_THIRD;
        }
        return Collections.emptyList();
    }

    static {
        COMPLETIONS_THIRD = ImmutableList.of("UNLIMITED");
    }
}
