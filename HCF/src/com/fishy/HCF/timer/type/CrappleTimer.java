package com.fishy.hcf.timer.type;

import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

import com.fishy.hcf.HCF;
import com.fishy.hcf.timer.PlayerTimer;
import com.fishy.hcf.util.DurationFormatter;
public class CrappleTimer extends PlayerTimer implements Listener {

	public CrappleTimer(HCF plugin) {
		super("Apple", TimeUnit.SECONDS.toMillis(5L));
	}

	@Override
	public String getScoreboardPrefix() {
		return ChatColor.GREEN.toString() + ChatColor.BOLD;
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerConsume(PlayerItemConsumeEvent event) {
		ItemStack stack = event.getItem();
		if (stack != null && stack.getType() == Material.GOLDEN_APPLE && stack.getDurability() == 0) {
			Player player = event.getPlayer();
			if (setCooldown(player, player.getUniqueId(), defaultCooldown, false, new Predicate<Long>() {
				@Override
				public boolean test(Long arg0) {
					return false;
				}
				})) {
/*				player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c\u2588\u2588\u2588\u2588\u2588&c\u2588\u2588\u2588"));
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c\u2588\u2588\u2588&e\u2588\u2588&c\u2588\u2588\u2588"));
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', ("&c\u2588\u2588\u2588&e\u2588&c\u2588\u2588\u2588\u2588 &6&l " + this.name + ": ")));
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c\u2588\u2588&6\u2588\u2588\u2588\u2588&c\u2588\u2588 &7  Consumed"));
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c\u2588&6\u2588\u2588&f\u2588&6\u2588&6\u2588\u2588&c\u2588"));
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c\u2588&6\u2588&f\u2588&6\u2588&6\u2588&6\u2588\u2588&c\u2588 &6 Cooldown Remaining:"));
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', ("&c\u2588&6\u2588\u2588&6\u2588&6\u2588&6\u2588\u2588&c\u2588 &7  " + DurationFormatter.getRemaining(getRemaining(player), true, false))));
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c\u2588&6\u2588\u2588&6\u2588&6\u2588&6\u2588\u2588&c\u2588"));
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c\u2588\u2588&6\u2588\u2588\u2588\u2588&c\u2588\u2588"));
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c\u2588\u2588\u2588\u2588\u2588&c\u2588\u2588\u2588"));*/
			} else {
				event.setCancelled(true);
				player.sendMessage(ChatColor.LIGHT_PURPLE + "You still have a " + getDisplayName() + ChatColor.LIGHT_PURPLE + " cooldown for another " + ChatColor.WHITE + DurationFormatter.getRemaining(getRemaining(player), true, false) + ChatColor.LIGHT_PURPLE + ".");
			}
		}
	}
}