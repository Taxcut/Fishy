package com.fishy.hcf.timer.type;

import com.fishy.hcf.HCF;
import com.fishy.hcf.timer.PlayerTimer;
import com.fishy.hcf.util.DurationFormatter;
import com.fishy.hcf.util.base.imagemessage.ImageMessage;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

/**
 * Timer used to prevent {@link Player}s from using Notch Apples too often.
 */
public class GappleTimer extends PlayerTimer implements Listener {

    private ImageMessage goppleArtMessage;

    public GappleTimer(HCF plugin) {
        super("Gapple", plugin.getConfig().getBoolean("KITMAP") ? TimeUnit.MINUTES.toMillis(10L) : TimeUnit.HOURS.toMillis(1L));
    }

    @Override
    public String getScoreboardPrefix() {
        return ChatColor.GREEN.toString() + ChatColor.BOLD;
    }

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerConsume(PlayerItemConsumeEvent event) {
		ItemStack stack = event.getItem();
		if (stack != null && stack.getType() == Material.GOLDEN_APPLE && stack.getDurability() == 1) {
			Player player = event.getPlayer();
			if (setCooldown(player, player.getUniqueId(), defaultCooldown, false, new Predicate<Long>() {
				@Override
				public boolean test(@Nullable Long value) {
					return false;
				}
			})) {
				{
					player.sendMessage(ChatColor.YELLOW + "Consumed " + ChatColor.GOLD + "Golden Apple"
							+ ChatColor.YELLOW + ", now on a cooldown for "
							+ DurationFormatUtils.formatDurationWords(defaultCooldown, true, true));
				}
			} else {
				event.setCancelled(true);
				player.sendMessage(ChatColor.LIGHT_PURPLE + "You still have a " + getDisplayName() + ChatColor.LIGHT_PURPLE
						+ " cooldown for another " + ChatColor.WHITE
						+ DurationFormatter.getRemaining(getRemaining(player), true, false) + ChatColor.LIGHT_PURPLE + ".");
			}
		}
	}
}
