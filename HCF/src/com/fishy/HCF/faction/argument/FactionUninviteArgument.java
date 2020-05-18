package com.fishy.hcf.faction.argument;

import com.fishy.hcf.HCF;
import com.fishy.hcf.faction.FactionMember;
import com.fishy.hcf.faction.struct.Role;
import com.fishy.hcf.faction.type.PlayerFaction;
import com.fishy.hcf.util.base.command.CommandArgument;
import com.google.common.collect.ImmutableList;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class FactionUninviteArgument extends CommandArgument {

    private final HCF plugin;

    public FactionUninviteArgument(HCF plugin) {
        super("uninvite", "Revoke an invitation to a player.", new String[]{"deinvite", "deinv", "uninv", "revoke"});
        this.plugin = plugin;
    }

    @Override
    public String getUsage(String label) {
        return '/' + label + ' ' + getName() + " <all|playerName>";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can un-invite from a faction.");
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

        FactionMember factionMember = playerFaction.getMember(player);

        if (factionMember.getRole() == Role.MEMBER) {
            sender.sendMessage(ChatColor.RED + "You must be a faction officer to un-invite players.");
            return true;
        }

        Set<String> invitedPlayerNames = playerFaction.getInvitedPlayerNames();

        if (args[1].equalsIgnoreCase("all")) {
            invitedPlayerNames.clear();
            sender.sendMessage(ChatColor.WHITE + "You have cleared all pending invitations.");
            return true;
        }

        if (!invitedPlayerNames.remove(args[1])) {
            sender.sendMessage(ChatColor.RED + "There is not a pending invitation for " + args[1] + '.');
            return true;
        }

        playerFaction.broadcast(ChatColor.WHITE + factionMember.getRole().getAstrix() + sender.getName() + " has uninvited " +
                ChatColor.valueOf(plugin.getConfig().getString("ENEMY_COLOR")) + args[1] + ChatColor.WHITE + " from the faction.");

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2 || !(sender instanceof Player)) {
            return Collections.emptyList();
        }

        Player player = (Player) sender;
        PlayerFaction playerFaction = plugin.getFactionManager().getPlayerFaction(player);
        if (playerFaction == null || playerFaction.getMember(player.getUniqueId()).getRole() == Role.MEMBER) {
            return Collections.emptyList();
        }

        List<String> results = new ArrayList<>(COMPLETIONS);
        results.addAll(playerFaction.getInvitedPlayerNames());
        return results;
    }

    private static final ImmutableList<String> COMPLETIONS = ImmutableList.of("all");
}
