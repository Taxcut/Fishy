package com.fishy.hcf.faction.argument;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.fishy.hcf.HCF;
import com.fishy.hcf.faction.struct.Role;
import com.fishy.hcf.faction.type.PlayerFaction;
import com.fishy.hcf.util.base.command.CommandArgument;
import com.google.common.collect.ImmutableList;

public class FactionAnnouncementArgument extends CommandArgument {

    private final HCF plugin;

    public FactionAnnouncementArgument(HCF plugin) {
        super("announcement", "Set your faction announcement.", new String[]{"announce", "motd"});
        this.plugin = plugin;
    }

    @Override
    public String getUsage(String label) {
        return '/' + label + ' ' + getName() + " <newAnnouncement>";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
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
            sender.sendMessage(ChatColor.RED + "You must be a officer to edit the faction announcement.");
            return true;
        }

        String oldAnnouncement = playerFaction.getAnnouncement();
        String newAnnouncement;
        if (args[1].equalsIgnoreCase("clear") || args[1].equalsIgnoreCase("none") || args[1].equalsIgnoreCase("remove")) {
            newAnnouncement = null;
        } else {
            newAnnouncement = HCF.SPACE_JOINER.join(Arrays.copyOfRange(args, 1, args.length));
        }

        if (oldAnnouncement == null && newAnnouncement == null) {
            sender.sendMessage(ChatColor.RED + "Your factions' announcement is already unset.");
            return true;
        }

        if (oldAnnouncement != null && newAnnouncement != null && oldAnnouncement.equals(newAnnouncement)) {
            sender.sendMessage(ChatColor.RED + "Your factions' announcement is already " + newAnnouncement + '.');
            return true;
        }

        playerFaction.setAnnouncement(newAnnouncement);

        if (newAnnouncement == null) {
            playerFaction.broadcast(ChatColor.valueOf(plugin.getConfig().getString("TEAMMATE_COLOR")) + sender.getName() + ChatColor.WHITE + " has cleared the factions' announcement.");
            return true;
        }

        playerFaction.broadcast(ChatColor.valueOf(plugin.getConfig().getString("TEAMMATE_COLOR")) + player.getName() + ChatColor.WHITE + " has updated the factions' announcement from " +
                ChatColor.LIGHT_PURPLE + (oldAnnouncement != null ? oldAnnouncement : "none") +
                ChatColor.WHITE + " to " + ChatColor.LIGHT_PURPLE + newAnnouncement + ChatColor.WHITE + '.');

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return Collections.emptyList();
        } else if (args.length == 2) {
            return CLEAR_LIST;
        } else {
            return Collections.emptyList();
        }
    }

    private static final ImmutableList<String> CLEAR_LIST = ImmutableList.of("clear");
}
