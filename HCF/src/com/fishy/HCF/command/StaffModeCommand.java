package com.fishy.hcf.command;

import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.fishy.hcf.HCF;
import com.fishy.hcf.listener.HideStaffListener;
import com.fishy.hcf.util.Color;


public class StaffModeCommand implements CommandExecutor, TabCompleter {

	private final HCF staffMode = HCF.getPlugin();

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] arguments) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (player.hasPermission("hcf.command.staffmode")) {
				if (arguments.length > 0) {
					player.sendMessage(Color.translate("&cUsage: /" + label));
				} else {
					if (staffMode.getStaffModeListener().isStaffModeActive(player)) {
						staffMode.getStaffModeListener().setStaffMode(player, false);
						player.sendMessage(Color.translate("&5� &dYou have &cdisabled &5&lStaff Mode&d!"));
					} else {
						staffMode.getStaffModeListener().setStaffMode(player, true);
			        	HideStaffListener.showStaff.put((Player) sender, true);
			            for (int i = 0; i < HideStaffListener.staff.size(); ++i) {
			                ((Player) sender).showPlayer(HideStaffListener.staff.get(i));
			            }
						player.sendMessage(Color.translate("&5� &dYou have &aenabled &5&lStaff Mode&d!"));
					}
				}
			}
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] arguments) {
		if (arguments.length > 1) {
			return Collections.emptyList();
		}
		return null;
	}
}