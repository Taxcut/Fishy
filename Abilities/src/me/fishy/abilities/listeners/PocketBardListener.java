package me.fishy.abilities.listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.fishy.abilities.Main;
import me.fishy.abilities.utils.CC;

public class PocketBardListener implements Listener {
	private final List<String> configlist = Main.getInstance().getConfig().getStringList("PocketBard.Message");

	@EventHandler
	public void PocketBardUsed(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		ItemStack pocketbard = new ItemStack(Material.GOLD_NUGGET,
				player.getInventory().getItemInHand().getAmount() - 1);
		ItemMeta pocketbardMeta = pocketbard.getItemMeta();
		ArrayList<String> lore = new ArrayList<>();
		lore.add(CC.translate(Main.getInstance().getConfig().getString("PocketBard.Lore")));
		pocketbardMeta.setLore(lore);
		pocketbardMeta.setDisplayName(CC.translate(Main.getInstance().getConfig().getString("PocketBard.Name")));
		pocketbard.setItemMeta(pocketbardMeta);

		if (!(player.getInventory().getItemInHand().getType() == Material.GOLD_NUGGET))
			return;
		if (!(event.getItem().hasItemMeta()))
			return;
		if (!event.getItem().getItemMeta().getLore()
				.contains(CC.translate(Main.getInstance().getConfig().getString("PocketBard.Lore"))))
			return;
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
			if (Main.getInstance().getPocketbard().onCooldown(player)) {
				player.sendMessage(CC.translate(Main.getInstance().getConfig().getString("Main.Cooldown").replace("%time%", Main.getInstance().getPocketbard().getRemaining(player))));

				player.updateInventory();
				event.setCancelled(true);
				return;
			}
			Main.getInstance().getPocketbard().CooldownApply(player,
					(Main.getInstance().getConfig().getInt("PocketBard.Cooldown") * 1000));
			player.getInventory().setItemInHand(pocketbard);
			player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 1f, 1f);
			player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Main.getInstance().getConfig().getInt("PocketBard.Effects.Strength.Time"), Main.getInstance().getConfig().getInt("PocketBard.Effects.Strength.Power")));
			player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Main.getInstance().getConfig().getInt("PocketBard.Effects.Regeneration.Time"), Main.getInstance().getConfig().getInt("PocketBard.Effects.Regeneration.Power")));
			player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Main.getInstance().getConfig().getInt("PocketBard.Effects.Resistance.Time"), Main.getInstance().getConfig().getInt("PocketBard.Effects.Resistance.Power")));
			for (String s : configlist) {
				player.sendMessage(CC.translate(s).replace("%heart%", "❤"));
			}
			player.updateInventory();
			event.setCancelled(true);

		}
	}
}
