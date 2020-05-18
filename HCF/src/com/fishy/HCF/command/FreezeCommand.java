package com.fishy.hcf.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.fishy.hcf.HCF;

public class FreezeCommand implements CommandExecutor, TabCompleter {

	private final HCF staffMode = HCF.getPlugin();

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 0 || args.length > 1) {
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cUsage: /" + label + " <playerName>"));
		} else {
			Player target = Bukkit.getServer().getPlayerExact(args[0]);
			if (target == null) {
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cPlayer with name or UUID with '" + args[0] + "' not found."));
			} else {
				if (target.equals(sender)) {
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou can not freeze yourself."));
				} else {
					if (sender instanceof Player) {
						if (target.hasPermission("hcf.command.freeze") || target.isOp()) {
							if (staffMode.getFreezeListener().isFrozen(target)) {
								staffMode.getFreezeListener().setFreeze(sender, target, false);
							} else {
								staffMode.getFreezeListener().setFreeze(sender, target, true);
							}
						} else {
							if (staffMode.getFreezeListener().isFrozen(target)) {
								staffMode.getFreezeListener().setFreeze(sender, target, false);
							} else {
								staffMode.getFreezeListener().setFreeze(sender, target, true);
							}
						}
					} else {
						if (staffMode.getFreezeListener().isFrozen(target)) {
							staffMode.getFreezeListener().setFreeze(sender, target, false);
						} else {
							staffMode.getFreezeListener().setFreeze(sender, target, true);
						}
					}
				}
			}
		}
		return true;
	}


    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> results = new ArrayList<>();
        for (Player target : Bukkit.getOnlinePlayers()) {
            if (!results.contains(target.getName())) {
                results.add(target.getName());
            }
        }
        return results;
    }
}