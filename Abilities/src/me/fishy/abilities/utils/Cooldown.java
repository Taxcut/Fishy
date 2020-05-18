package me.fishy.abilities.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.entity.Player;

public class Cooldown {

	private Map<UUID, Long> cooldownMap = new HashMap<>(); 
	
	public void CooldownApply(Player player, long cooldown) {
		cooldownMap.put(player.getUniqueId(), System.currentTimeMillis() + cooldown);
	}
	public boolean onCooldown(Player player) {
		return cooldownMap.containsKey(player.getUniqueId()) && (cooldownMap.get(player.getUniqueId()) >= System.currentTimeMillis());
	}
	public void cooldownRemove(Player player) {
		cooldownMap.remove(player.getUniqueId());
	}
	
	public String getRemaining(Player player) {
		long l = cooldownMap.get(player.getUniqueId()) - System.currentTimeMillis();
		return DurationFormatUtils.formatDuration(l, "s");

	}
			}

