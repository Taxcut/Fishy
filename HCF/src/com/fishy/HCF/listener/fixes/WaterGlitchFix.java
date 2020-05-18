package com.fishy.hcf.listener.fixes;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class WaterGlitchFix implements Listener {

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();

		if (player.getLocation().getBlock().getType() != Material.STATIONARY_WATER
				|| (player.getLocation().getBlock().getType() != Material.WATER))
			return;

		Location location = player.getLocation();
		location.setY(location.getY() + 0.5);
		
		player.teleport(location);
	}

}
