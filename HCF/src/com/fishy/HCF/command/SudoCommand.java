package com.fishy.hcf.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.fishy.hcf.HCF;
import com.fishy.hcf.util.base.BukkitUtils;


public class SudoCommand implements CommandExecutor {

	private final HCF plugin;

	public SudoCommand(HCF plugin) {
		this.plugin = plugin;
	}
	
    private boolean executeCommand(final Player target, final String executingCommand, boolean force) {
        if (target.isOp()) {
            force = false;
        }
        boolean var5;
        try {
            if (force) {
                target.setOp(true);
            }
            target.performCommand(executingCommand);
            final boolean ex = true;
            return ex;
        } catch (Exception var6) {
            var5 = false;
        } finally {
            if (force) {
                target.setOp(false);
            }
        }
        return var5;
    }
    
    public List onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length == 1) {
            final ArrayList results = new ArrayList(2);
            results.add("true");
            results.add("false");
            return BukkitUtils.getCompletions(args, results);
        }
        if (args.length != 2) {
            return Collections.emptyList();
        }
        final ArrayList results = new ArrayList();
        results.add("ALL");
        final Player senderPlayer = (sender instanceof Player) ? (Player) sender : null;
        for (final Player target : Bukkit.getOnlinePlayers()) {
            if (senderPlayer == null) {
                results.add(target.getName());
            }
        }
        return BukkitUtils.getCompletions(args, results);
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Usage: " + "/sudo <force> <all|playerName> <command args...>");
            return true;
        }
        boolean force;
        try {
            force = Boolean.parseBoolean(args[0]);
        } catch (IllegalArgumentException var9) {
            sender.sendMessage(ChatColor.RED + "Usage: " + "/sudo <force> <all|playerName> <command args...>");
            return true;
        }
        final String executingCommand = StringUtils.join((Object[]) args, ' ', 2, args.length);
        if (args[1].equalsIgnoreCase("all")) {
            for (Player target3 : Bukkit.getOnlinePlayers()) {
                this.executeCommand(target3, executingCommand, force);
            }
            sender.sendMessage(ChatColor.RED + "Forcing all players to run " + executingCommand + (force ? " with permission bypasses" : "") + '.');
            return true;
        }
        final Player target4 = Bukkit.getPlayer(args[1]);
        if (target4 != null) {
            this.executeCommand(target4, executingCommand, force);
            Command.broadcastCommandMessage(sender, ChatColor.RED + sender.getName() + ChatColor.RED + " made " + target4.getName() + " run " + executingCommand + (force ? " with permission bypasses" : "") + '.');
            sender.sendMessage(ChatColor.RED + "Making " + target4.getName() + " to run " + executingCommand + (force ? " with permission bypasses" : "") + '.');
            return true;
        }
        sender.sendMessage(ChatColor.RED + "Player named or with UUID '" + ChatColor.RED + args[1] + ChatColor.RED + "' not found.");
        return true;
    }
}
