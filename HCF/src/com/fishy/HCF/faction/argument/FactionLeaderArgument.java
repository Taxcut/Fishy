package com.fishy.hcf.faction.argument;

import com.fishy.hcf.HCF;
import com.fishy.hcf.faction.FactionMember;
import com.fishy.hcf.faction.struct.Role;
import com.fishy.hcf.faction.type.PlayerFaction;
import com.fishy.hcf.util.base.command.CommandArgument;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FactionLeaderArgument extends CommandArgument {

    private final HCF plugin;

    public FactionLeaderArgument(HCF plugin) {
        super("leader", "Sets the new leader for your faction.");
        this.plugin = plugin;
        this.aliases = new String[]{"setleader", "newleader"};
    }

    @Override
    public String getUsage(String label) {
        return '/' + label + ' ' + getName() + " <playerName>";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can set faction leaders.");
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

        UUID uuid = player.getUniqueId();
        FactionMember selfMember = playerFaction.getMember(uuid);
        Role selfRole = selfMember.getRole();

        if (selfRole != Role.LEADER) {
            sender.sendMessage(ChatColor.RED + "You must be the current faction leader to transfer the faction.");
            return true;
        }

        FactionMember targetMember = playerFaction.getMember(args[1]);

        if (targetMember == null) {
            sender.sendMessage(ChatColor.RED + "Player '" + args[1] + "' is not in your faction.");
            return true;
        }

        if (targetMember.getUniqueId().equals(uuid)) {
            sender.sendMessage(ChatColor.RED + "You are already the faction leader.");
            return true;
        }

        targetMember.setRole(Role.LEADER);
        selfMember.setRole(Role.CAPTAIN);

        ChatColor colour = ChatColor.valueOf(plugin.getConfig().getString("TEAMMATE_COLOR"));
        playerFaction.broadcast(colour + selfMember.getRole().getAstrix() + selfMember.getName() + ChatColor.WHITE +
                " has transferred the faction to " + colour + targetMember.getRole().getAstrix() + targetMember.getName() + ChatColor.WHITE + '.');

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2 || !(sender instanceof Player)) {
            return Collections.emptyList();
        }

        Player player = (Player) sender;
        PlayerFaction playerFaction = plugin.getFactionManager().getPlayerFaction(player);
        if (playerFaction == null || (playerFaction.getMember(player.getUniqueId()).getRole() != Role.LEADER)) {
            return Collections.emptyList();
        }

        List<String> results = new ArrayList<>();
        Map<UUID, FactionMember> members = playerFaction.getMembers();
        for (Map.Entry<UUID, FactionMember> entry : members.entrySet()) {
            if (entry.getValue().getRole() != Role.LEADER) {
                OfflinePlayer target = Bukkit.getOfflinePlayer(entry.getKey());
                String targetName = target.getName();
                if (targetName != null && !results.contains(targetName)) {
                    results.add(targetName);
                }
            }
        }

        return results;
    }
}
