package com.fishy.hcf.faction.argument;

import com.fishy.hcf.HCF;
import com.fishy.hcf.faction.argument.subclaim.FactionSubclaimAddMemberArgument;
import com.fishy.hcf.faction.argument.subclaim.FactionSubclaimCreateArgument;
import com.fishy.hcf.faction.argument.subclaim.FactionSubclaimDelMemberArgument;
import com.fishy.hcf.faction.argument.subclaim.FactionSubclaimDeleteArgument;
import com.fishy.hcf.faction.argument.subclaim.FactionSubclaimListArgument;
import com.fishy.hcf.faction.argument.subclaim.FactionSubclaimMembersArgument;
import com.fishy.hcf.faction.argument.subclaim.FactionSubclaimRenameArgument;
import com.fishy.hcf.faction.argument.subclaim.FactionSubclaimStartArgument;
import com.fishy.hcf.util.base.BukkitUtils;
import com.fishy.hcf.util.base.command.CommandArgument;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class FactionSubclaimArgumentExecutor extends CommandArgument {

    private final List<CommandArgument> arguments = new ArrayList<>(8);

    public FactionSubclaimArgumentExecutor(HCF plugin) {
        super("subclaim", "Shows the subclaim help page.", new String[]{"sub", "subland", "subclaimland"});
        this.arguments.add(new FactionSubclaimAddMemberArgument(plugin));
        this.arguments.add(new FactionSubclaimCreateArgument(plugin));
        this.arguments.add(new FactionSubclaimDeleteArgument(plugin));
        this.arguments.add(new FactionSubclaimDelMemberArgument(plugin));
        this.arguments.add(new FactionSubclaimListArgument(plugin));
        this.arguments.add(new FactionSubclaimMembersArgument(plugin));
        this.arguments.add(new FactionSubclaimRenameArgument(plugin));
        this.arguments.add(new FactionSubclaimStartArgument());
        this.permission = "hcf.command.faction.argument." + getName();
    }

    @Override
    public String getUsage(String label) {
        return '/' + label + ' ' + getName();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.DARK_GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
            sender.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD + "Faction Subclaim Help");
            for (CommandArgument argument : arguments) {
                String permission = argument.getPermission();
                if (permission == null || sender.hasPermission(permission)) {
                    sender.sendMessage(ChatColor.GRAY + argument.getUsage(label) + " - " + argument.getDescription() + '.');
                }
            }

            sender.sendMessage(ChatColor.GRAY + "/" + label + " map subclaim - Shows the faction subclaim map.");
            sender.sendMessage(ChatColor.DARK_GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
            return true;
        }

        CommandArgument argument = getArgument(arguments, args[1]);
        String permission = (argument == null) ? null : argument.getPermission();

        if (argument == null || (permission != null && !sender.hasPermission(permission))) {
            sender.sendMessage(ChatColor.RED + "Faction subclaim sub-command " + args[1] + " not found.");
            return true;
        }

        argument.onCommand(sender, command, label, args);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 2) {
            List<String> results = new ArrayList<>();
            for (CommandArgument argument : arguments) {
                String permission = argument.getPermission();
                if (permission == null || sender.hasPermission(permission)) {
                    results.add(argument.getName());
                }
            }

            return results;
        }

        CommandArgument argument = getArgument(arguments, args[1]);
        if (argument != null) {
            String permission = argument.getPermission();
            if (permission == null || sender.hasPermission(permission)) {
                return argument.onTabComplete(sender, command, label, args);
            }
        }

        return Collections.emptyList();
    }

    //TODO: Needs moving into better library
    private static CommandArgument getArgument(Collection<CommandArgument> arguments, String id) {
        for (CommandArgument commandArgument : arguments) {
            if (commandArgument.getName().equalsIgnoreCase(id) || Arrays.asList(commandArgument.getAliases()).contains(id)) {
                return commandArgument;
            }
        }

        return null;
    }
}
