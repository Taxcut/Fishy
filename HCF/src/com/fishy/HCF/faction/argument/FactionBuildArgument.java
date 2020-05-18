package com.fishy.hcf.faction.argument;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.fishy.hcf.HCF;
import com.fishy.hcf.util.base.command.CommandArgument;

import net.md_5.bungee.api.ChatColor;

public class FactionBuildArgument extends CommandArgument {
	
	private final HCF plugin;
	
    public FactionBuildArgument(HCF plugin) {
        super("build", "Opens the build settings GUI.", new String[] { "" });
        this.plugin = plugin;
    }

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender instanceof Player) {
			if(sender.hasPermission("hcf.command.faction.build")) {
				if(args.length != 1) {
					sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
					return true;
				}
				
				Player player = (Player)sender;
				plugin.getClaimSettings().open(player);
			} else {
				sender.sendMessage(ChatColor.RED + "No permission.");
				return true;
			}
		}
		return false;
	}

	@Override
	public String getUsage(String label) {
        return '/' + label + ' ' + getName();
	}

}
