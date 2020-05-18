package com.fishy.hcf.faction.argument.staff;

import com.fishy.hcf.HCF;
import com.fishy.hcf.faction.type.Faction;
import com.fishy.hcf.faction.type.PlayerFaction;
import com.fishy.hcf.util.base.command.CommandArgument;

import net.minecraft.util.com.google.common.primitives.Ints;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FactionAddPointsArgument extends CommandArgument {

    private final HCF plugin;

    public FactionAddPointsArgument(final HCF plugin) {
        super("addpoints", "Adds the points of a faction");
        this.plugin = plugin;
        this.permission = "hcf.command.faction.argument." + getName();
    }

    @Override
    public String getUsage(String label) {
        return '/' + label + ' ' + getName() + " <faction name> <points>";
    }

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        new BukkitRunnable() {
            public void run() {
                if (args.length < 3) {
                    sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
                    return;
                }
                Integer newPoints = Ints.tryParse(args[2]);
                if (newPoints == null) {
                    sender.sendMessage(ChatColor.RED + "'" + args[2] + "' is not a valid number.");
                    return;
                }
                final Faction faction2 = plugin.getFactionManager().getContainingFaction(args[1]);
                if (faction2 == null) {
                    sender.sendMessage(ChatColor.RED + "Faction named or containing member with IGN or UUID " + args[1] + " not found.");
                    return;
                }
                if (!(faction2 instanceof PlayerFaction)) {
                    sender.sendMessage(ChatColor.RED + "You can only set Points of player factions.");
                    return;
                }
                final PlayerFaction playerFaction = (PlayerFaction) faction2;
                int previousPoints = playerFaction.getPoints();
                newPoints = previousPoints + newPoints;
                playerFaction.setPoints(newPoints);
                Command.broadcastCommandMessage(sender, ChatColor.YELLOW + "Set Points of " + faction2.getName() + " from " + previousPoints + " to " + newPoints + '.');
            }
        }.runTaskAsynchronously(plugin);
        return true;
    }
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length != 2 || !(sender instanceof Player)) {
            return Collections.emptyList();
        }
        if (args[1].isEmpty()) {
            return null;
        }
        final Player player = (Player) sender;
        final List<String> results = new ArrayList<String>(this.plugin.getFactionManager().getFactionNameMap().keySet());
        for (final Player target : Bukkit.getOnlinePlayers()) {
            if (player.canSee(target) && !results.contains(target.getName())) {
                results.add(target.getName());
            }
        }
        return results;
    }
}
