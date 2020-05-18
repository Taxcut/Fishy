package com.fishy.hcf.command;


import com.fishy.hcf.HCF;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GamemodeCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (cmd.getName().equalsIgnoreCase("gamemode")) {
			if (!sender.hasPermission("hcf.command.gamemode") && !sender.hasPermission("hcf.command.*") && !sender.hasPermission("*")) {
				sender.sendMessage(ChatColor.RED +"You do not have permission to use this command.");
				return true;
			}

			if (args.length != 0) {
			if (args[0].equalsIgnoreCase("survival") || args[0].equalsIgnoreCase("s") || args[0].equalsIgnoreCase("0")) {
				if (args.length == 1) {
				if (!(sender instanceof Player)) {
					sender.sendMessage(ChatColor.RED +"You do not have permission to use this command.");
					return true;
				}
				Player p = (Player) sender;
				p.setGameMode(GameMode.SURVIVAL);
				sender.sendMessage("�f%player%'s gamemode has been set to �cSurvival.".replace("%player%", p.getName()));
				return true;
				}
				if (args.length == 2) {
				Player t = Bukkit.getPlayer(args[1]);
				if (t == null) {
					sender.sendMessage(ChatColor.RED + "That player is currently offline");
					return true;
				}
				t.setGameMode(GameMode.SURVIVAL);
				sender.sendMessage("�cGamemode of " + t.getName() + " set to �fSURVIVAL�c.");
				return true;
				}
			}

			if (args[0].equalsIgnoreCase("creative") || args[0].equalsIgnoreCase("c") || args[0].equalsIgnoreCase("1")) {
				if (args.length == 1) {
				if (!(sender instanceof Player)) {
					sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
					return true;
				}
				Player p = (Player) sender;
				p.setGameMode(GameMode.CREATIVE);
				sender.sendMessage("�fYour gamemode has been set to �cCreative.".replace("%player", p.getName()));
				return true;
				}
				if (args.length == 2) {
				Player t = Bukkit.getPlayer(args[1]);
				if (t == null) {
					sender.sendMessage(ChatColor.RED + "That player is currently offline");
					return true;
				}
				t.setGameMode(GameMode.CREATIVE);
				sender.sendMessage("�f" + t.getName() + "'s gamemode has been set to �cCreative.".replace("%player", t.getName()));
				return true;
				}
			}

			if (args[0].equalsIgnoreCase("adventure") || args[0].equalsIgnoreCase("a") || args[0].equalsIgnoreCase("2")) {
				if (args.length == 1) {
				if (!(sender instanceof Player)) {
					sender.sendMessage(ChatColor.RED +"You do not have permission to use this command.");
					return true;
				}
				Player p = (Player) sender;
				p.setGameMode(GameMode.ADVENTURE);
				sender.sendMessage("�fYour gamemode set to �cAdventure.");
				return true;
			}
				if (args.length == 2) {
				Player t = Bukkit.getPlayer(args[1]);
				if (t == null) {
					sender.sendMessage(ChatColor.RED + "That player is currently offline");
					return true;
				}
				t.setGameMode(GameMode.ADVENTURE);
				sender.sendMessage("�f" + t.getName() + "'s gamemode has been set to �cAdventure.".replace("%player", t.getName()));
				return true;
				}
			}
			}

			sender.sendMessage("�cUsage: /gamemode <mode> <player>");
		}

		if (cmd.getName().equalsIgnoreCase("gamemodes")) {
			if (!sender.hasPermission("hcf.command.gamemode") && !sender.hasPermission("hcf.command.*") && !sender.hasPermission("*")) {
				sender.sendMessage(ChatColor.RED +"You do not have permission to use this command.");
				return true;
			}

				if (args.length == 0) {
					if (!(sender instanceof Player)) {
						sender.sendMessage(ChatColor.RED +"You do not have permission to use this command.");
						return true;
					}
					Player p = (Player) sender;
					p.setGameMode(GameMode.SURVIVAL);
					sender.sendMessage(HCF.getPlugin().getConfig().getString("COMMAND.GAMEMODE.SURVIVAL"));
					return true;
				}

				if (args.length == 1) {
				Player t = Bukkit.getPlayer(args[0]);
				if (t == null) {
					sender.sendMessage(ChatColor.RED + "That player is currently offline");
					return true;
				}
				t.setGameMode(GameMode.SURVIVAL);
				sender.sendMessage("�f" + t.getName() + "'s gamemode has been set to �cSurvival.");
				return true;
				}

			sender.sendMessage("�cUsage: /gms <player>");
		}

		if (cmd.getName().equalsIgnoreCase("gamemodec")) {
			if (!sender.hasPermission("hcf.command.gamemode") && !sender.hasPermission("hcf.command.*") && !sender.hasPermission("*")) {
				sender.sendMessage(ChatColor.RED +"You do not have permission to use this command.");
				return true;
			}

			if (args.length == 0) {
				if (!(sender instanceof Player)) {
					sender.sendMessage(ChatColor.RED +"You do not have permission to use this command.");
					return true;
				}
				Player p = (Player) sender;
				p.setGameMode(GameMode.CREATIVE);
				sender.sendMessage(HCF.getPlugin().getConfig().getString("COMMAND.GAMEMODE.CREATIVE"));
				return true;
			}
				if (args.length == 1) {
				Player t = Bukkit.getPlayer(args[0]);
				if (t == null) {
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cPlayer with name or UUID with '" + args[0] + "' not found."));
					return true;
				}
				t.setGameMode(GameMode.CREATIVE);
				sender.sendMessage("�cGamemode of " + t.getName() + " set to �fCREATIVE�c.".replace("%player", t.getName()));
				return true;
				}

				sender.sendMessage("�cUsage: /gmc <player>");
		}

		if (cmd.getName().equalsIgnoreCase("gamemodea")) {
			if (!sender.hasPermission("hcf.command.gamemode") && !sender.hasPermission("hcf.command.*") && !sender.hasPermission("*")) {
				sender.sendMessage(ChatColor.RED +"You do not have permission to use this command.");
				return true;
			}

			if (args.length == 0) {
				if (!(sender instanceof Player)) {
					sender.sendMessage(ChatColor.RED +"You do not have permission to use this command.");
					return true;
				}
				Player p = (Player) sender;
				p.setGameMode(GameMode.ADVENTURE);
				sender.sendMessage(HCF.getPlugin().getConfig().getString("COMMAND.GAMEMODE.ADVENTURE"));
				return true;
			}
				if (args.length == 1) {
				Player t = Bukkit.getPlayer(args[0]);
				if (t == null) {
					sender.sendMessage(ChatColor.RED + "That player is currently offline");
					return true;
				}
				t.setGameMode(GameMode.ADVENTURE);
				sender.sendMessage("�cGamemode of " + t.getName() + " set to �fADVENTURE�c.");
				return true;
				}

				sender.sendMessage("�cUsage: /gma <player>");
		}
		return true;
	}
}
