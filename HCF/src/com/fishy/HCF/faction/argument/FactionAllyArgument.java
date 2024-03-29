package com.fishy.hcf.faction.argument;

import com.fishy.hcf.HCF;
import com.fishy.hcf.faction.event.FactionRelationCreateEvent;
import com.fishy.hcf.faction.struct.Relation;
import com.fishy.hcf.faction.struct.Role;
import com.fishy.hcf.faction.type.Faction;
import com.fishy.hcf.faction.type.PlayerFaction;
import com.fishy.hcf.util.base.command.CommandArgument;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Faction argument used to request or accept ally {@link Relation} invitations from a {@link Faction}.
 */
public class FactionAllyArgument extends CommandArgument {

    private static final Relation RELATION = Relation.ALLY;

    private final HCF plugin;

    public FactionAllyArgument(HCF plugin) {
        super("ally", "Make an ally pact with other factions.", new String[]{"alliance"});
        this.plugin = plugin;
    }

    @Override
    public String getUsage(String label) {
        return '/' + label + ' ' + getName() + " <factionName>";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return true;
        }

        if (plugin.getConfig().getInt("FACTION.MAXALLIES") <= 0) {
            sender.sendMessage(ChatColor.RED + "Allies are disabled this map.");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
            return true;
        }

        Player player = (Player) sender;
        PlayerFaction playerFaction = plugin.getFactionManager().getPlayerFaction(player);

        if (playerFaction == null) {
            sender.sendMessage(ChatColor.RED + "You are not in a faction.");
            return true;
        }

        if (playerFaction.getMember(player.getUniqueId()).getRole() == Role.MEMBER) {
            sender.sendMessage(ChatColor.RED + "You must be an officer to make relation wishes.");
            return true;
        }

        Faction containingFaction = plugin.getFactionManager().getContainingFaction(args[1]);

        if (!(containingFaction instanceof PlayerFaction)) {
            sender.sendMessage(ChatColor.RED + "Player faction named or containing member with IGN or UUID " + args[1] + " not found.");
            return true;
        }

        PlayerFaction targetFaction = (PlayerFaction) containingFaction;

        if (playerFaction == targetFaction) {
            sender.sendMessage(ChatColor.RED + "You cannot send " + RELATION.getDisplayName() + ChatColor.RED + " requests to your own faction.");
            return true;
        }

        Collection<UUID> allied = playerFaction.getAllied();

        if (allied.size() >= plugin.getConfig().getInt("FACTION.MAXALLIES")) {
            sender.sendMessage(ChatColor.RED + "Your faction already has reached the alliance limit, which is " + plugin.getConfig().getInt("FACTION.MAXALLIES") + '.');
            return true;
        }

        if (targetFaction.getAllied().size() >= plugin.getConfig().getInt("FACTION.MAXALLIES")) {
            sender.sendMessage(targetFaction.getDisplayName(sender) + ChatColor.RED + " has reached their maximum alliance limit, which is " +
                    plugin.getConfig().getInt("FACTION.MAXALLIES") + '.');

            return true;
        }

        if (allied.contains(targetFaction.getUniqueID())) {
            sender.sendMessage(ChatColor.RED + "Your faction already is " + RELATION.getDisplayName() + 'd' + ChatColor.RED + " with " +
                    targetFaction.getDisplayName(playerFaction) + ChatColor.RED + '.');

            return true;
        }

        // Their faction has already requested us, lets' accept.
        if (targetFaction.getRequestedRelations().remove(playerFaction.getUniqueID()) != null) {
            FactionRelationCreateEvent event = new FactionRelationCreateEvent(playerFaction, targetFaction, RELATION);
            Bukkit.getPluginManager().callEvent(event);

            targetFaction.getRelations().put(playerFaction.getUniqueID(), RELATION);
            targetFaction.broadcast(ChatColor.WHITE + "Your faction is now " + RELATION.getDisplayName() + 'd' + ChatColor.WHITE +
                    " with " + playerFaction.getDisplayName(targetFaction) + ChatColor.WHITE + '.');

            playerFaction.getRelations().put(targetFaction.getUniqueID(), RELATION);
            playerFaction.broadcast(ChatColor.WHITE + "Your faction is now " + RELATION.getDisplayName() + 'd' + ChatColor.WHITE +
                    " with " + targetFaction.getDisplayName(playerFaction) + ChatColor.WHITE + '.');

            return true;
        }

        if (playerFaction.getRequestedRelations().putIfAbsent(targetFaction.getUniqueID(), RELATION) != null) {
            sender.sendMessage(ChatColor.WHITE + "Your faction has already requested to " + RELATION.getDisplayName() +
                    ChatColor.WHITE + " with " + targetFaction.getDisplayName(playerFaction) + ChatColor.WHITE + '.');

            return true;
        }

        // Handle the request.
        playerFaction.broadcast(targetFaction.getDisplayName(playerFaction) + ChatColor.WHITE + " were informed that you wish to be " + RELATION.getDisplayName() + ChatColor.WHITE + '.');
        targetFaction.broadcast(playerFaction.getDisplayName(targetFaction) + ChatColor.WHITE + " has sent a request to be " + RELATION.getDisplayName() + ChatColor.WHITE +
                ". Use " + ChatColor.valueOf(plugin.getConfig().getString("ALLY_COLOR")) + "/faction " + getName() + ' ' + playerFaction.getName() + ChatColor.WHITE + " to accept.");

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2 || !(sender instanceof Player)) {
            return Collections.emptyList();
        }

        Player player = (Player) sender;
        PlayerFaction playerFaction = plugin.getFactionManager().getPlayerFaction(player);
        if (playerFaction == null) {
            return Collections.emptyList();
        }

        List<String> results = new ArrayList<>();
        for (Player target : Bukkit.getOnlinePlayers()) {
            if (!target.equals(player) && !results.contains(target.getName())) {
                Faction targetFaction = plugin.getFactionManager().getPlayerFaction(target);
                if (targetFaction != null && playerFaction != targetFaction) {
                    if (playerFaction.getRequestedRelations().get(targetFaction.getUniqueID()) != RELATION && playerFaction.getRelations().get(targetFaction.getUniqueID()) != RELATION) {
                        results.add(targetFaction.getName());
                    }
                }
            }
        }

        return results;
    }
}
