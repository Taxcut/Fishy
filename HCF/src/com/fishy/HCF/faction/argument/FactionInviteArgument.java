package com.fishy.hcf.faction.argument;

import com.fishy.hcf.HCF;
import com.fishy.hcf.faction.struct.Relation;
import com.fishy.hcf.faction.struct.Role;
import com.fishy.hcf.faction.type.Faction;
import com.fishy.hcf.faction.type.PlayerFaction;
import com.fishy.hcf.util.base.command.CommandArgument;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.fishy.hcf.util.SpigotUtils.toBungee;

/**
 * Faction argument used to invite players into {@link Faction}s.
 */
public class FactionInviteArgument extends CommandArgument {

    private final HCF plugin;

    public FactionInviteArgument(HCF plugin) {
        super("invite", "Invite a player to the faction.");
        this.plugin = plugin;
        this.aliases = new String[]{ "inv", "invitemember", "inviteplayer" };
    }

    @Override
    public String getUsage(String label) {
        return '/' + label + ' ' + getName() + " <playerName>";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can invite to a faction.");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cPlayer with name or UUID with '" + args[0] + "' not found."));
            return true;
        }

        Player player = (Player) sender;
        PlayerFaction playerFaction = plugin.getFactionManager().getPlayerFaction(player);

        if (playerFaction == null) {
            sender.sendMessage(ChatColor.RED + "You are not in a faction.");
            return true;
        }

        if (playerFaction.getMember(player.getUniqueId()).getRole() == Role.MEMBER) {
            sender.sendMessage(ChatColor.RED + "You must a faction officer to invite members.");
            return true;
        }

        Set<String> invitedPlayerNames = playerFaction.getInvitedPlayerNames();

        if (playerFaction.getMember(target) != null) {
            sender.sendMessage(ChatColor.RED + "'" + target.getName() + "' is already in your faction.");
            return true;
        }

        if (!plugin.getEotwHandler().isEndOfTheWorld() && playerFaction.isRaidable()) {
            sender.sendMessage(ChatColor.RED + "You may not invite players whilst your faction is raidable.");
            return true;
        }

        if (!invitedPlayerNames.add(target.getName())) {
            sender.sendMessage(ChatColor.RED + target.getName() + " has already been invited.");
            return true;
        }

        net.md_5.bungee.api.ChatColor enemyRelationColor = toBungee(Relation.ENEMY.toChatColour());
        ComponentBuilder builder = new ComponentBuilder(sender.getName()).color(enemyRelationColor);
        builder.append(" has invited you to join ", ComponentBuilder.FormatRetention.NONE).color(net.md_5.bungee.api.ChatColor.WHITE);
        builder.append(playerFaction.getName()).color(enemyRelationColor).append(". ", ComponentBuilder.FormatRetention.NONE).color(net.md_5.bungee.api.ChatColor.WHITE);
        builder.append("Click here").color(net.md_5.bungee.api.ChatColor.GREEN).
                event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + label + " accept " + playerFaction.getName())).
                event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to join ").color(net.md_5.bungee.api.ChatColor.WHITE).
                        append(playerFaction.getName(), ComponentBuilder.FormatRetention.NONE).color(enemyRelationColor).
                        append(".", ComponentBuilder.FormatRetention.NONE).color(net.md_5.bungee.api.ChatColor.WHITE).create()));
        builder.append(" to accept this invitation.", ComponentBuilder.FormatRetention.NONE).color(net.md_5.bungee.api.ChatColor.WHITE);
        target.spigot().sendMessage(builder.create());
        playerFaction.broadcast(Relation.MEMBER.toChatColour() + sender.getName() + ChatColor.WHITE + " has invited " + Relation.ENEMY.toChatColour() + target.getName() + ChatColor.WHITE + " into the faction.");

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2 || !(sender instanceof Player)) {
            return Collections.emptyList();
        }

        Player player = (Player) sender;
        PlayerFaction playerFaction = plugin.getFactionManager().getPlayerFaction(player);
        if (playerFaction == null || (playerFaction.getMember(player.getUniqueId()).getRole() == Role.MEMBER)) {
            return Collections.emptyList();
        }

        List<String> results = new ArrayList<>();
        for (Player target : Bukkit.getOnlinePlayers()) {
            if (!results.contains(target.getName())) {
                if (playerFaction != plugin.getFactionManager().getPlayerFaction(target.getUniqueId())) {
                    results.add(target.getName());
                }
            }
        }

        return results;
    }
}
