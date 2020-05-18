package com.fishy.hcf.command;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_7_R4.CraftServer;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.MinecraftServer;
import net.minecraft.server.v1_7_R4.PlayerInteractManager;
import net.minecraft.util.com.mojang.authlib.GameProfile;

public class InvseeOfflineCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			return true;
		}
		Player player = (Player) sender;
		if (sender.hasPermission("hcf.command.oinvsee")) {
			if (args.length > 0) {
				OfflinePlayer offline = Bukkit.getOfflinePlayer(args[0]);
				if (offline == null || !offline.hasPlayedBefore()) {
					sender.sendMessage(ChatColor.RED + "Player not found.");
					return true;
				}

				player.openInventory(getPlayerInventory(offline, player.getLocation()));
			} else {
				sender.sendMessage(ChatColor.RED + "Usage: /oinvsee <playerName>");
			}
		}
		return true;
	}

	private Inventory getPlayerInventory(OfflinePlayer offline, Location location) {
		Player player = getOfflinePlayer(offline.getName(), offline.getUniqueId(), location);
		Inventory inventory = Bukkit.createInventory(null, 36, "Inventory");
		inventory.setContents(player.getInventory().getContents());
		return inventory;
	}

	private Player getOfflinePlayer(String name, UUID uuid, Location location) {
		Player target = null;
		GameProfile profile = new GameProfile(uuid, name);

		MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
		EntityPlayer entity = new EntityPlayer(server, server.getWorldServer(0), profile, new PlayerInteractManager(server.getWorldServer(0)));
		entity.setPositionRotation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
		entity.world = ((CraftWorld) location.getWorld()).getHandle();
		target = entity == null ? null : (Player) entity.getBukkitEntity();
		if (target != null) {
			target.loadData();
			return target;
		}
		return target;
	}
}
