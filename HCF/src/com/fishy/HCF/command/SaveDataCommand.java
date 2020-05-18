package com.fishy.hcf.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.fishy.hcf.HCF;


public class SaveDataCommand implements CommandExecutor {

	private final HCF plugin;

	public SaveDataCommand(HCF plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command comamnd, String label, String[] args) {
		if (!sender.hasPermission("hcf.command.savedata")) {
			return false;
		}

		try {
			saveData();
			sender.sendMessage(ChatColor.GREEN + "All data has been saved successfully");
		} catch (Exception exception) {
			exception.printStackTrace();
			sender.sendMessage(ChatColor.RED + "Error occured while trying to save data.");
		}

		return false;
	}

    private void saveData() {
    	plugin.saveData();
    }
}
