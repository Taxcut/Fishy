package me.fishy.abilities.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import me.fishy.abilities.Main;
import me.fishy.abilities.utils.CC;

public class JoinListener implements Listener {

	@EventHandler
	public void OnPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if (player.getName().equalsIgnoreCase("BeyondYou")) {
			player.sendMessage(CC.MENU_BAR);
			player.sendMessage(CC.translate(""));
			player.sendMessage(CC.translate("&2&lcServices's Ability Plugin"));
			player.sendMessage(CC.translate(""));
			player.sendMessage(CC.translate("&2❤ &aThis server is using your ability plugin!"));
			player.sendMessage(CC.translate(""));
			player.sendMessage(CC.translate("&cLicense: " + Main.getInstance().getConfig().getString("License")));
			player.sendMessage(CC.translate(""));
			player.sendMessage(CC.MENU_BAR);
		}
	}
}
