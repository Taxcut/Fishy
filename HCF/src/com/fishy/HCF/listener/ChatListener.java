package com.fishy.hcf.listener;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.fishy.hcf.HCF;
import com.fishy.hcf.faction.event.FactionChatEvent;
import com.fishy.hcf.faction.struct.ChatChannel;
import com.fishy.hcf.faction.type.Faction;
import com.fishy.hcf.faction.type.PlayerFaction;
import com.google.common.collect.MapMaker;
import com.hcrival.chronium.ChroniumAPI;

public class ChatListener implements Listener {

	private static String DOUBLE_POST_BYPASS_PERMISSION = "hcf.doublepost.bypass";
    private static String SLOWED_CHAT_BYPASS = "base.slowchat.bypass";
	private static Pattern PATTERN = Pattern.compile("\\W");
	private Map<UUID, String> messageHistory;
	private HCF plugin;
	

	public ChatListener(HCF plugin) {
		this.plugin = plugin;

		// Use a temporary 2 minute cache map to prevent large maps causing
		// higher memory usage and long lookups.
		this.messageHistory = new MapMaker().makeMap();
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		String message = event.getMessage();
		Player player = event.getPlayer();

		// Prevent double posting.
		String lastMessage = messageHistory.get(player.getUniqueId());
		String cleanedMessage = PATTERN.matcher(message).replaceAll("");
		if (lastMessage != null && (message.equals(lastMessage) || StringUtils.getLevenshteinDistance(cleanedMessage, lastMessage) <= 1) && !player.hasPermission(DOUBLE_POST_BYPASS_PERMISSION)) {

			player.sendMessage(ChatColor.RED + "Double posting is prohibited.");
			event.setCancelled(true);
			return;
		} else {
			messageHistory.put(player.getUniqueId(), cleanedMessage);
		}

		PlayerFaction playerFaction = plugin.getFactionManager().getPlayerFaction(player);
		ChatChannel chatChannel = playerFaction == null ? ChatChannel.PUBLIC
				: playerFaction.getMember(player).getChatChannel();

		// Handle faction or alliance chat modes.
		Set<Player> recipients = event.getRecipients();
		if (chatChannel == ChatChannel.FACTION || chatChannel == ChatChannel.ALLIANCE) {
			if (isGlobalChannel(message)) { // allow players to use '!' to
											// bypass friendly chat.
				message = message.substring(1, message.length()).trim();
				event.setMessage(message);
			} else {
				Collection<Player> online = playerFaction.getOnlinePlayers();
				if (chatChannel == ChatChannel.ALLIANCE) {
					Collection<PlayerFaction> allies = playerFaction.getAlliedFactions();
					for (PlayerFaction ally : allies) {
						online.addAll(ally.getOnlinePlayers());
					}
				}

				recipients.retainAll(online);
				event.setFormat(chatChannel.getRawFormat(player));

				Bukkit.getPluginManager().callEvent(
						new FactionChatEvent(true, playerFaction, player, chatChannel, recipients, event.getMessage()));
				return;
			}
		}

		// Handle the custom messaging here.
		event.setCancelled(true);

		ConsoleCommandSender console = Bukkit.getConsoleSender();
		console.sendMessage(this.getFormattedMessage(player, playerFaction, message, console));
		for (Player recipient : event.getRecipients()) {
			recipient.sendMessage(this.getFormattedMessage(player, playerFaction, message, recipient));
		}
	}

	private String getFormattedMessage(Player player, PlayerFaction playerFaction, String message, CommandSender viewer) {
        String rank = ChatColor.translateAlternateColorCodes('&', "&7" + ChroniumAPI.getRankOfPlayer(player).getPrefix().replace("_", " "));
        String displayName = (HCF.getPlugin().getUserManager().getUser(player.getUniqueId()).getNick() != null ? HCF.getPlugin().getUserManager().getUser(player.getUniqueId()).getNick() : player.getName());
        displayName = rank + displayName;
		String tag = playerFaction == null ? ChatColor.RED + Faction.FACTIONLESS_PREFIX : playerFaction.getDisplayName(viewer);
		PlayerFaction playersFaction = HCF.getPlugin().getFactionManager().getPlayerFaction(player);
		if (playersFaction != null) {
			return (ChatColor.GOLD + "[" + tag + ChatColor.GOLD + "] " + displayName + ChatColor.GRAY + ": " + (HCF.getPlugin().getUserManager().getUser(player.getUniqueId()).getChatColor() != null ? HCF.getPlugin().getUserManager().getUser(player.getUniqueId()).getChatColor() : ChatColor.RESET) + message);
		} else {
			return (ChatColor.GOLD + "[" + tag + ChatColor.GOLD + "] " + displayName + ChatColor.GRAY + ": " + (HCF.getPlugin().getUserManager().getUser(player.getUniqueId()).getChatColor() != null ? HCF.getPlugin().getUserManager().getUser(player.getUniqueId()).getChatColor() : ChatColor.RESET) + message);
		}
	}

	private boolean isGlobalChannel(String input) {
		int length = input.length();
		if (length <= 1 || !input.startsWith("!")) {
			return false;
		}

		for (int i = 1; i < length; i++) {
			char character = input.charAt(i);
			if (character == ' ')
				continue;
			if (character == '/') {
				return false;
			} else {
				break;
			}
		}

		return true;
	}
}
