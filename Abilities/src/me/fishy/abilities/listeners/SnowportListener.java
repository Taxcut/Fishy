package me.fishy.abilities.listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import me.fishy.abilities.Main;
import me.fishy.abilities.utils.CC;

public class SnowportListener implements Listener {
	private final List<String> configlist = Main.getInstance().getConfig().getStringList("Snowport.Message");
	@EventHandler
	public void SnowportThrown(PlayerInteractEvent event) {

		Player player = event.getPlayer();
		ItemStack snowport = new ItemStack(Material.SNOW_BALL, player.getInventory().getItemInHand().getAmount() - 1);
		ItemMeta snowportMeta = snowport.getItemMeta();
		ArrayList<String> lore = new ArrayList<>();
		lore.add(CC.translate(Main.getInstance().getConfig().getString("Snowport.Lore")));
		snowportMeta.setLore(lore);
		snowportMeta.setDisplayName(CC.translate(Main.getInstance().getConfig().getString("Snowport.Name")));
		snowport.setItemMeta(snowportMeta);

		if (!(player.getInventory().getItemInHand().getType() == Material.SNOW_BALL))
			return;
		if (!(event.getItem().hasItemMeta()))
			return;
		if (!event.getItem().getItemMeta().getLore()
				.contains(CC.translate(Main.getInstance().getConfig().getString("Snowport.Lore"))))
			return;


		if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
			if (Main.getInstance().getSnowport().onCooldown(player)) {
				player.sendMessage(CC.translate(Main.getInstance().getConfig().getString("Main.Cooldown").replace("%time%", Main.getInstance().getSnowport().getRemaining(player))));

				player.updateInventory();
				event.setCancelled(true);
				return;
			}
			Snowball snowball = player.launchProjectile(Snowball.class);
			snowball.setMetadata("Snowport", new FixedMetadataValue(Main.getPlugin(Main.class), player.getUniqueId()));
			Main.getInstance().getSnowport().CooldownApply(player, (Main.getInstance().getConfig().getInt("Snowport.Cooldown") * 1000));
			player.getInventory().setItemInHand(snowport);
			player.playSound(player.getLocation(), Sound.SHOOT_ARROW, 1f, 1f);
			player.updateInventory();
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void SnowportHit(EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;
		if (!(event.getDamager() instanceof Snowball))
			return;
		Snowball snowball = (Snowball) event.getDamager();
		if (!(snowball.getShooter() instanceof Player) && !(event.getEntity() instanceof Player))
			return;

		if (!snowball.hasMetadata("Snowport"))
			return;

		Player damager = (Player) snowball.getShooter();
		Player damaged = (Player) event.getEntity();
		Location loc = damager.getLocation();
		if (Main.getInstance().getConfig().getBoolean("Snowport.Range.Enabled")) {
			if (damaged.getLocation().distance(damager.getLocation()) > Main.getInstance().getConfig()
					.getInt("Snowport.Range.Range")) {
				damager.sendMessage(CC.translate(Main.getInstance().getConfig().getString("Snowport.Range.Message")).replace("%heart%", "❤"));
				event.setCancelled(true);
				return;
			}
			damager.teleport(damaged);
			damaged.teleport(loc);
			for (String s : configlist) {
				damager.sendMessage(CC.translate(s).replace("%player%", damaged.getName()).replace("%heart%", "❤"));
				damaged.sendMessage(CC.translate(s).replace("%player%", damager.getName()).replace("%heart%", "❤"));
			}
		} else {
			damager.teleport(damaged);
			damaged.teleport(loc);
			for (String s : configlist) {
				damager.sendMessage(CC.translate(s).replace("%player%", damaged.getName()).replace("%heart%", "❤"));
				damaged.sendMessage(CC.translate(s).replace("%player%", damager.getName()).replace("%heart%", "❤"));
			}
		}

	}
}
