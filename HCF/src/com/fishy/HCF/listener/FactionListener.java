package com.fishy.hcf.listener;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import com.fishy.hcf.HCF;
import com.fishy.hcf.events.faction.KothFaction;
import com.fishy.hcf.faction.event.FactionCreateEvent;
import com.fishy.hcf.faction.event.FactionRemoveEvent;
import com.fishy.hcf.faction.event.FactionRenameEvent;
import com.fishy.hcf.faction.event.PlayerClaimEnterEvent;
import com.fishy.hcf.faction.event.PlayerJoinFactionEvent;
import com.fishy.hcf.faction.event.PlayerLeaveFactionEvent;
import com.fishy.hcf.faction.event.PlayerLeftFactionEvent;
import com.fishy.hcf.faction.struct.RegenStatus;
import com.fishy.hcf.faction.type.Faction;
import com.fishy.hcf.faction.type.PlayerFaction;
import com.fishy.hcf.util.ReflectionUtils;

public class FactionListener implements Listener {

	private static final long FACTION_JOIN_WAIT_MILLIS = TimeUnit.SECONDS.toMillis(30L);
	private static final String FACTION_JOIN_WAIT_WORDS = DurationFormatUtils
			.formatDurationWords(FACTION_JOIN_WAIT_MILLIS, true, true);

	private static final String LAND_CHANGED_META_KEY = "landChangedMessage";
	private static final long LAND_CHANGE_MSG_THRESHOLD = 225L;

	private final HCF plugin;

	public FactionListener(HCF plugin) {
		this.plugin = plugin;
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onFactionRenameMonitor(FactionRenameEvent event) {
		Faction faction = event.getFaction();
		if (faction instanceof KothFaction) {
			((KothFaction) faction).getCaptureZone().setName(event.getNewName());
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onFactionCreate(FactionCreateEvent event) {
		Faction faction = event.getFaction();
		if (faction instanceof PlayerFaction) {
			CommandSender sender = event.getSender();
			/*
			 * for (Player player : Bukkit.getOnlinePlayers()) {
			 * player.sendMessage(String.format("%s �fhas been �acreated �fby %s�f.",
			 * faction.getRelation(player).toChatColour() + faction.getName(),
			 * ChatColor.translateAlternateColorCodes('&',
			 * HCF.getPlugin().getChat().getPlayerPrefix((Player) sender)) +
			 * sender.getName())); }
			 */
			Bukkit.broadcastMessage(
					String.format("�eFaction �f%s �ehas been �acreated �eby �f%s�e.", faction.getName(), sender.getName()));
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onFactionRemove(FactionRemoveEvent event) {
		Faction faction = event.getFaction();
		if (faction instanceof PlayerFaction) {
			CommandSender sender = event.getSender();
			/*
			 * for (Player player : Bukkit.getOnlinePlayers()) {
			 * player.sendMessage(String.format("%s �fhas been �cdisbanded �fby %s�f.",
			 * faction.getRelation(player).toChatColour() + faction.getName(),
			 * ChatColor.translateAlternateColorCodes('&',
			 * HCF.getPlugin().getChat().getPlayerPrefix((Player) sender)) +
			 * sender.getName())); }
			 */
			Bukkit.broadcastMessage(
					String.format("�eFaction �f%s �ehas been �cdisbanded �eby �f%s�e.", faction.getName(), sender.getName()));
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onFactionRename(FactionRenameEvent event) {
		Faction faction = event.getFaction();
		if (faction instanceof PlayerFaction) {
			CommandSender sender = event.getSender();
			/*
			 * for (Player player : Bukkit.getOnlinePlayers()) { player.sendMessage(String.
			 * format("�f%s �fhas been �brenamed �fto �f%s �fby %s�f.",
			 * event.getFaction().getRelation(player).toChatColour() +
			 * event.getOriginalName(),
			 * event.getFaction().getRelation(player).toChatColour() + event.getNewName(),
			 * ChatColor.translateAlternateColorCodes('&',
			 * HCF.getPlugin().getChat().getPlayerPrefix((Player) sender)) +
			 * sender.getName())); }
			 */
			Bukkit.broadcastMessage(String.format("�eFaction �f%s �ehas been �brenamed �eto �f%s �eby �f%s�e.",
					event.getOriginalName(), event.getNewName(), sender.getName()));
		}
	}

	private long getLastLandChangedMeta(Player player) {
		MetadataValue value = ReflectionUtils.getPlayerMetadata(player, LAND_CHANGED_META_KEY, plugin);

		long millis = System.currentTimeMillis();
		long remaining = value == null ? 0L : value.asLong() - millis;
		if (remaining <= 0L) { // update the metadata.
			player.setMetadata(LAND_CHANGED_META_KEY,
					new FixedMetadataValue(plugin, millis + LAND_CHANGE_MSG_THRESHOLD));
		}

		return remaining;
	}

	/*
	 * @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	 * public void onCaptureZoneEnter(CaptureZoneEnterEvent event) { Player player =
	 * event.getPlayer(); if (getLastLandChangedMeta(player) > 0L) return; // delay
	 * before re-messaging.
	 * 
	 * if
	 * (plugin.getUserManager().getUser(player.getUniqueId()).isCapzoneEntryAlerts()
	 * ) { player.sendMessage(ChatColor.YELLOW + "Now entering capture zone: " +
	 * event.getCaptureZone().getDisplayName() + ChatColor.YELLOW + '(' +
	 * event.getFaction().getName() + ChatColor.YELLOW + ')'); } }
	 * 
	 * @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	 * public void onCaptureZoneLeave(CaptureZoneLeaveEvent event) { Player player =
	 * event.getPlayer(); if (getLastLandChangedMeta(player) > 0L) return; // delay
	 * before re-messaging.
	 * 
	 * if
	 * (plugin.getUserManager().getUser(player.getUniqueId()).isCapzoneEntryAlerts()
	 * ) { player.sendMessage(ChatColor.YELLOW + "Now leaving capture zone: " +
	 * event.getCaptureZone().getDisplayName() + ChatColor.YELLOW + '(' +
	 * event.getFaction().getName() + ChatColor.YELLOW + ')'); } }
	 */

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	private void onPlayerClaimEnter(PlayerClaimEnterEvent event) {
		Faction toFaction = event.getToFaction();
		Faction fromFaction = event.getFromFaction();
		if (toFaction.isSafezone()) {
			Player player = event.getPlayer();
			player.setHealth(player.getMaxHealth());
			player.setFoodLevel(20);
			player.setFireTicks(0);
			player.setSaturation(4.0F);
		}

		Player player = event.getPlayer();
		if (this.getLastLandChangedMeta(player) <= 0L) { // delay before re-messaging.
			player.sendMessage(ChatColor.YELLOW + "Now leaving " + fromFaction.getDisplayName(player) + ChatColor.YELLOW
					+ ", entering " + toFaction.getDisplayName(player));
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerLeftFaction(PlayerLeftFactionEvent event) {
		Optional<Player> optionalPlayer = event.getPlayer();
		if (optionalPlayer.isPresent()) {
			plugin.getUserManager().getUser(optionalPlayer.get().getUniqueId())
					.setLastFactionLeaveMillis(System.currentTimeMillis());
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onPlayerPreFactionJoin(PlayerJoinFactionEvent event) {
		PlayerFaction playerFaction = event.getFaction();
		Optional<Player> optionalPlayer = event.getPlayer();
		if (optionalPlayer.isPresent()) {
			Player player = optionalPlayer.get();

			if (!plugin.getEotwHandler().isEndOfTheWorld() && playerFaction.getRegenStatus() == RegenStatus.PAUSED) {
				event.setCancelled(true);
				player.sendMessage(ChatColor.RED + "You cannot join factions that are not regenerating DTR.");
				return;
			}

			long difference = (plugin.getUserManager().getUser(player.getUniqueId()).getLastFactionLeaveMillis()
					- System.currentTimeMillis()) + FACTION_JOIN_WAIT_MILLIS;
			if (difference > 0L && !player.hasPermission("hcf.faction.argument.staff.forcejoin")) {
				event.setCancelled(true);
				player.sendMessage(ChatColor.RED + "You cannot join factions after just leaving within "
						+ FACTION_JOIN_WAIT_WORDS + ". " + "You gotta wait another "
						+ DurationFormatUtils.formatDurationWords(difference, true, true) + '.');
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onFactionLeave(PlayerLeaveFactionEvent event) {
		if (event.isForce() || event.isKick()) {
			return;
		}

		PlayerFaction playerFaction = event.getFaction();
		Optional<Player> optional = event.getPlayer();
		if (optional.isPresent()) {
			Player player = optional.get();
			if (plugin.getFactionManager().getFactionAt(player.getLocation()) == playerFaction) {
				event.setCancelled(true);
				player.sendMessage(
						ChatColor.RED + "You cannot leave your faction whilst you remain in it's territory.");
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event) {
		event.setJoinMessage(null);
		Player player = event.getPlayer();
		PlayerFaction playerFaction = plugin.getFactionManager().getPlayerFaction(player);
		if (playerFaction != null) {
			playerFaction.printDetails(player);
			playerFaction.broadcast(
					ChatColor.GREEN + "Member Online: " + ChatColor.WHITE
							+ playerFaction.getMember(player).getRole().getAstrix() + player.getName(),
					player.getUniqueId());
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		PlayerFaction playerFaction = plugin.getFactionManager().getPlayerFaction(player);
		if (playerFaction != null) {
			playerFaction.broadcast(ChatColor.RED + "Member Offline: " + ChatColor.WHITE
					+ playerFaction.getMember(player).getRole().getAstrix() + player.getName());
		}
	}
}