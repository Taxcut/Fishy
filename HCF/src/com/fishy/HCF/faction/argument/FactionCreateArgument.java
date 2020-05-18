package com.fishy.hcf.faction.argument;

import com.fishy.hcf.HCF;
import com.fishy.hcf.faction.type.Faction;
import com.fishy.hcf.faction.type.PlayerFaction;
import com.fishy.hcf.util.base.JavaUtils;
import com.fishy.hcf.util.base.command.CommandArgument;

import net.minecraft.util.com.google.common.collect.ImmutableList;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Faction argument used to create a new {@link Faction}.
 */
public class FactionCreateArgument extends CommandArgument {

    private final HCF plugin;
    
    private static final ImmutableList<String> BLOCKED_NAMES = ImmutableList.of("nigger", "jew", "eotw", "kys", "faggot", "cunt", "beaner", "commitsuicide", "nazi");

    public FactionCreateArgument(HCF plugin) {
        super("create", "Create a faction.", new String[]{"make", "define"});
        this.plugin = plugin;
    }

    @Override
    public String getUsage(String label) {
        return '/' + label + ' ' + getName() + " <factionName>";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command may only be executed by players.");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
            return true;
        }

        String name = args[1];

        if (BLOCKED_NAMES.contains(name.toLowerCase())) {
            sender.sendMessage(ChatColor.RED + "'" + name + "' is a blocked faction name.");
            return true;
        }

        int value = plugin.getConfig().getInt("FACTION.MINIMUMNAMECHARACTERS");

        if (name.length() < value) {
            sender.sendMessage(ChatColor.RED + "Faction names must have at least " + value + " characters.");
            return true;
        }

        value = plugin.getConfig().getInt("FACTION.MAXNAMECHARACTERS");

        if (name.length() > value) {
            sender.sendMessage(ChatColor.RED + "Faction names cannot be longer than " + value + " characters.");
            return true;
        }

        if (!JavaUtils.isAlphanumeric(name)) {
            sender.sendMessage(ChatColor.RED + "Faction names may only be alphanumeric.");
            return true;
        }

        if (plugin.getFactionManager().getFaction(name) != null) {
            sender.sendMessage(ChatColor.RED + "Faction '" + name + "' already exists.");
            return true;
        }

        if (plugin.getFactionManager().getPlayerFaction((Player) sender) != null) {
            sender.sendMessage(ChatColor.RED + "You are already in a faction.");
            return true;
        }

        plugin.getFactionManager().createFaction(new PlayerFaction(name), sender);
        return true;
    }
}
