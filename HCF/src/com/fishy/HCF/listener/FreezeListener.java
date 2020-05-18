package com.fishy.hcf.listener;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import com.fishy.hcf.HCF;
import com.fishy.hcf.util.Color;
import com.fishy.hcf.util.FancyMessage;
import com.fishy.hcf.util.base.chat.ClickAction;
import com.fishy.hcf.util.base.chat.Text;

public class FreezeListener implements Listener {
	private final HCF utilities = HCF.getPlugin();

	public final Set<UUID> frozen = new HashSet<>();

	private AlertTask alertTask;

	public FreezeListener () {
		Bukkit.getPluginManager().registerEvents(this, utilities);
	}

	public boolean isFrozen(Player player) {
		return frozen.contains(player.getUniqueId());
	}

	public void setFreeze(CommandSender sender, Player target, boolean status) {
		if (status) {
			frozen.add(target.getUniqueId());
			alertTask = new AlertTask(target);
			alertTask.runTaskTimerAsynchronously(utilities, 0L, 5 * 20);

			if (sender instanceof Player) {
				Bukkit.getServer().getConsoleSender().sendMessage(Color.translate("&c" + target.getName()
						+ " is now frozen."));
				for (Player staff : Bukkit.getServer().getOnlinePlayers()) {
					if (staff.hasPermission("hcf.command.sm")) {
						if (staff.equals(sender)) {
							sender.sendMessage(Color.translate("&c" + target.getName() + " is now frozen."));
						} else {
							staff.sendMessage(Color.translate("&c" + target.getName()
									+ " &cis now frozen."));
						}
					}
				}
			} else {
				sender.sendMessage(Color.translate("&c" + target.getName()
				+ " &cis now frozen."));
				for (Player staff : Bukkit.getServer().getOnlinePlayers()) {
					if (staff.hasPermission("hcf.command.sm")) {
						staff.sendMessage(Color.translate("&c" + target.getName()
						+ " &cis now frozen."));
					}
				}
			}
		} else {
			alertTask.cancel();
			frozen.remove(target.getUniqueId());
			target.sendMessage(Color.translate("&aYou have been unfrozen by a staff member."));

			if (sender instanceof Player) {
				Bukkit.getServer().getConsoleSender().sendMessage(Color.translate("&c" + target.getName()
				+ " &cis no longer frozen."));
				for (Player staff : Bukkit.getServer().getOnlinePlayers()) {
					if (staff.hasPermission("hcf.command.sm")) {
						if (staff.equals(sender)) {
							sender.sendMessage(Color.translate("&c" + target.getName() + " &cis no longer frozen."));
						} else {
							staff.sendMessage(Color.translate("&c" + target.getName()
							+ " &cis no longer frozen."));
						}
					}
				}
			} else {
				sender.sendMessage(Color.translate("&c" + target.getName()
				+ " &cis no longer frozen."));
				for (Player staff : Bukkit.getServer().getOnlinePlayers()) {
					if (staff.hasPermission("hcf.command.sm")) {
						staff.sendMessage(Color.translate("&c" + target.getName()
						+ " &cis no longer frozen."));
					}
				}
			}
		}
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();

		Location from = event.getFrom();
		Location to = event.getTo();
		if (isFrozen(player)) {
			// from.getBlockY() != to.getBlockY()
			if (from.getBlockX() != to.getBlockX() || from.getBlockZ() != to.getBlockZ()) {
				event.setTo(from);
			}
		}
	}

	@EventHandler
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		if (isFrozen(player)) {
			if (!event.getMessage().startsWith("/helpop") && !event.getMessage().startsWith("/faction chat")
					&& !event.getMessage().startsWith("/fac chat") && !event.getMessage().startsWith("/f chat")
					&& !event.getMessage().startsWith("/faction c") && !event.getMessage().startsWith("/fac c")
					&& !event.getMessage().startsWith("/f c") && !event.getMessage().startsWith("/helpop")
					&& !event.getMessage().startsWith("/request") && !event.getMessage().startsWith("/msg")
					&& !event.getMessage().startsWith("/r") && !event.getMessage().startsWith("/reply")
					&& !event.getMessage().startsWith("/ss") && !event.getMessage().startsWith("/freeze")
					&& !event.getMessage().startsWith("/panic") && !event.getMessage().startsWith("/tpm")
					&& !event.getMessage().startsWith("/message") && !event.getMessage().startsWith("/reply")) {
				event.setCancelled(true);
				player.sendMessage(Color.translate("&cYou can not use commands while you are frozen."));
			}
		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		if (event.getBlock() != null) {
			if (isFrozen(player)) {
				event.setCancelled(true);
				player.sendMessage(Color.translate("&cYou can not place blocks while you are frozen."));
			}
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		if (event.getBlock() != null) {
			if (isFrozen(player)) {
				event.setCancelled(true);
				player.sendMessage(Color.translate("&cYou can not break blocks while you are frozen."));
			}
		}
	}

	@EventHandler
	public void onPlayerKick(PlayerKickEvent event) {
		Player player = event.getPlayer();
		if (isFrozen(player)) {
			alertTask.cancel();
			frozen.remove(player.getUniqueId());
		}
	}

	public Player getDamager(Entity entity) {
		if (entity instanceof Player) {
			return (Player) entity;
		}
		if (entity instanceof Projectile) {
			Projectile projectile = (Projectile) entity;
			if (projectile.getShooter() != null && projectile.getShooter() instanceof Player) {
				return (Player) projectile.getShooter();
			}
		}
		return null;
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		Player damager = getDamager(event.getDamager());
		Player damaged = getDamager(event.getEntity());
		if (damager != null && damaged != null && damaged != damager) {
			if (utilities.getFreezeListener().isFrozen(damager)) {
				damager.sendMessage(Color.translate("&cYou can not attack players while frozen."));
				event.setCancelled(true);
			}

			if (utilities.getFreezeListener().isFrozen(damaged)) {
				damager.sendMessage(Color
						.translate("&c" + damaged.getName() + "&c is currently frozen, you may not attack."));
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if (isFrozen(player)) {
			for (Player staff : Bukkit.getServer().getOnlinePlayers()) {
				if (staff.hasPermission("hcf.command.sm") || staff.isOp()) {
					FancyMessage fancyMessage = new FancyMessage("");
					fancyMessage.then(Color
							.translate("&4&l" + player.getName() + " &chas disconnected while frozen. "));
					fancyMessage.then(Color.translate("&7(Click here to ban)"));
					fancyMessage.tooltip(Color.translate("&fClick to permanently ban " + player.getName()));
					fancyMessage.command("/ban " + player.getName() + " Refusal to SS -s");
					fancyMessage.send(staff);
				}
			}
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if (isFrozen(player)) {
			alertTask = new AlertTask(player);
			alertTask.runTaskTimerAsynchronously(utilities, 0L, 5 * 20);

			for (Player staff : Bukkit.getServer().getOnlinePlayers()) {
				if (staff.hasPermission("hcf.command.sm")) {
					FancyMessage fancyMessage = new FancyMessage("");
					fancyMessage.then(Color.translate("&4&l" + player.getName() + " &chas joined but he is frozen. "));
					fancyMessage.then(Color.translate("&7(Click here to ban)"));
					fancyMessage.tooltip(Color.translate("&fClick to permanently ban " + player.getName()));
					fancyMessage.command("/ban " + player.getName() + " Refusal to SS -s");
					fancyMessage.send(staff);
				}
			}
		}
	}

	private class AlertTask extends BukkitRunnable {
		private final Player player;

		public AlertTask(Player player) {
			this.player = player;
		}

		@Override
		public void run() {
			if (isFrozen(player)) {
				player.setSaturation(3.0F);
/*                player.sendMessage(" ");
                player.sendMessage("�f\u2588\u2588\u2588\u2588�c\u2588�f\u2588\u2588\u2588\u2588");
                player.sendMessage("�f\u2588\u2588\u2588�c\u2588�6\u2588�c\u2588�f\u2588\u2588\u2588");
                player.sendMessage("�f\u2588\u2588�c\u2588�6\u2588�0\u2588�6\u2588�c\u2588�f\u2588\u2588 �4�lDo NOT log out!");
                player.sendMessage("�f\u2588\u2588�c\u2588�6\u2588�0\u2588�6\u2588�c\u2588�f\u2588\u2588 �cIf you do, you will be banned!");
                player.sendMessage("�f\u2588�c\u2588�6\u2588\u2588�0\u2588�6\u2588\u2588�c\u2588�f\u2588 �ePlease download �lTeamspeak �eand connect to:");
                player.sendMessage("�f\u2588�c\u2588�6\u2588\u2588\u2588\u2588\u2588�c\u2588�f\u2588 " + "�fts.hcrealms.us");
                player.sendMessage("�c\u2588�6\u2588\u2588\u2588�0\u2588�6\u2588\u2588\u2588�c\u2588");
                player.sendMessage("�c\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588");
                player.sendMessage(" ");*/
				player.sendMessage("");
				player.sendMessage(ChatColor.GRAY + "�8�m---------�8�m-----------�8�m------");
				player.sendMessage(ChatColor.GRAY + "You have been frozen by a staff member.");
				player.sendMessage(ChatColor.GRAY + "If you disconnect you will be " + ChatColor.DARK_RED
						+ ChatColor.BOLD + "BANNED" + ChatColor.GRAY + '.');
				player.sendMessage(
						ChatColor.GRAY + "Please connect to our Teamspeak" + ChatColor.GRAY + '.');
				new Text(ChatColor.GRAY + "(" + "ts.desirepvp.net" + ") "
						+ ChatColor.ITALIC + "Click me to download" + ChatColor.GRAY + '.')
								.setClick(ClickAction.OPEN_URL, "http://www.teamspeak.com/downloads")
								.send(player);
				player.sendMessage(ChatColor.GRAY + "�8�m---------�8�m-----------�8�m------");
				player.sendMessage("");
			}
		}
	}



}