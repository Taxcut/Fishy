package com.fishy.hcf.faction.argument.staff;

import com.fishy.hcf.HCF;
import com.fishy.hcf.faction.type.Faction;
import com.fishy.hcf.faction.type.PlayerFaction;
import com.fishy.hcf.util.base.BukkitUtils;
import com.fishy.hcf.util.base.command.CommandArgument;
import com.google.common.base.Joiner;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import java.util.Arrays;
import java.util.UUID;

public class FactionBanArgument extends CommandArgument {
    private final HCF plugin;
    public static final Joiner SPACE_JOIN;

    public FactionBanArgument(final HCF plugin) {
        super("ban", "Tempbans every faction member.");
        this.plugin = plugin;
        this.permission = String.valueOf("hcf.command.faction.argument.ban");
    }

    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <factionName> <reason>";
    }

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
            return true;
        }
        final Faction faction = this.plugin.getFactionManager().getContainingFaction(args[1]);
        if (!(faction instanceof PlayerFaction)) {
            sender.sendMessage(ChatColor.RED + "Player faction named or containing member with IGN or UUID " + args[1] + " not found.");
            return true;
        }
        final PlayerFaction playerFaction = (PlayerFaction)faction;
        final String extraArgs = FactionBanArgument.SPACE_JOIN.join((Object[]) Arrays.copyOfRange(args, 2, args.length));
        final ConsoleCommandSender console = Bukkit.getConsoleSender();
        for (final UUID uuid : playerFaction.getMembers().keySet()) {
            final String commandLine = "ban " + uuid.toString() + " " + extraArgs;
            sender.sendMessage(ChatColor.WHITE + "Executing commands " + commandLine);
            console.getServer().dispatchCommand(sender, commandLine);
        }
        Bukkit.broadcastMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
        Bukkit.broadcastMessage("�eBanning the entire faction for " + playerFaction.getName() + ".");
        Bukkit.broadcastMessage("�eReason: " + extraArgs);
        Bukkit.broadcastMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
        return true;
    }


    static {
        SPACE_JOIN = Joiner.on(' ');
    }
}
