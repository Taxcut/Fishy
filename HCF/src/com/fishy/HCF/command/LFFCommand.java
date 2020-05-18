package com.fishy.hcf.command;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.fishy.hcf.HCF;
import com.fishy.hcf.faction.type.Faction;

import net.md_5.bungee.api.ChatColor;
import net.minecraft.util.com.google.common.cache.CacheBuilder;
import net.minecraft.util.org.apache.commons.lang3.time.DurationFormatUtils;

public class LFFCommand implements CommandExecutor {
	private HCF plugin;
	private static TimeUnit UNIT;
	private static int TIME = 1;
	private static long MILLIS;
	private static final String WORDS;
	private Map timestampMap;

	public LFFCommand(HCF plugin) {
		this.timestampMap = CacheBuilder.newBuilder().concurrencyLevel(1).expireAfterWrite(1L, LFFCommand.UNIT).build()
				.asMap();
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (cmd.getName().equals("lff")) {
				Faction faction = this.plugin.getFactionManager().getPlayerFaction(player);
				if (faction == null) {
					Long timeStamp = (Long) this.timestampMap.get(player.getUniqueId());
					long now = System.currentTimeMillis();
					long diff;
					if (timeStamp == null || (diff = now - timeStamp) > LFFCommand.MILLIS) {
				        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', " &5&lLFF &7� &a&l" + sender.getName() + "&f is looking for a &5&lfaction&f!"));
						this.timestampMap.put(player.getUniqueId(), System.currentTimeMillis());
				        player.sendMessage(ChatColor.WHITE + "You have announced that you are looking for a faction. You must wait 1 hour before using this again.");
					} else {
						player.sendMessage(ChatColor.RED + "You cannot use this for another " + ChatColor.BOLD
								+ DurationFormatUtils.formatDurationWords(LFFCommand.MILLIS - diff, true, true) + ChatColor.RED + ".");
					}
				} else {
					player.sendMessage(ChatColor.RED + "You're already in a faction.");
				}
			}
		}
		return false;
	}

	static {
		UNIT = TimeUnit.HOURS;
		MILLIS = LFFCommand.UNIT.toMillis(1L);
		WORDS = DurationFormatUtils.formatDurationWords(LFFCommand.MILLIS, true, true);
	}
}
