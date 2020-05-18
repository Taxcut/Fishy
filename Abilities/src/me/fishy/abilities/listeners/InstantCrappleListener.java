package me.fishy.abilities.listeners;

import me.fishy.abilities.Main;
import me.fishy.abilities.utils.CC;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class InstantCrappleListener implements Listener {

    private final FileConfiguration config = Main.getInstance().getConfig();
    private final List<String> configlist = Main.getInstance().getConfig().getStringList("InstantCrapple.Message");

    @EventHandler
    public void on(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)
            return;
        if (!player.getInventory().getItemInHand().hasItemMeta())
            return;
        if (player.getInventory().getItemInHand().getType() != Material.GOLDEN_APPLE)
            return;
        if (!player.getInventory().getItemInHand().getItemMeta().getLore().contains(CC.translate(config.getString("InstantCrapple.Lore"))))
            return;
        if (Main.getInstance().getInstantcrapple().onCooldown(player)) {
            player.sendMessage(CC.translate(Main.getInstance().getConfig().getString("Main.Cooldown").replace("%time%", Main.getInstance().getInstantcrapple().getRemaining(player))));
            event.setCancelled(true);
            return;
        }
        Main.getInstance().getInstantcrapple().CooldownApply(player,
                (Main.getInstance().getConfig().getInt("InstantCrapple.Cooldown") * 1000));
        player.playSound(player.getLocation(), Sound.EAT, 1f, 1f);
        player.removePotionEffect(PotionEffectType.ABSORPTION);
        player.removePotionEffect(PotionEffectType.REGENERATION);
        player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 120 * 20, 0));
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 5 * 20, 2));
        event.setCancelled(true);
        player.updateInventory();
        for (String s : configlist) {
            player.sendMessage(CC.translate(s).replace("%heart%", "❤"));
        }
        if (player.getInventory().getItemInHand().getAmount() == 1) {
            player.getInventory().setItemInHand(null);
            player.updateInventory();
            return;
        }
        player.getInventory().getItemInHand().setAmount(player.getInventory().getItemInHand().getAmount() - 1);
        player.updateInventory();
    }


}
