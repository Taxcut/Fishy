package me.fishy.abilities.listeners;

import me.fishy.abilities.Main;
import me.fishy.abilities.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class InstantInvisListener implements Listener {
    private final List<String> configlist = Main.getInstance().getConfig().getStringList("InstantInvis.Message");
    @EventHandler
    public void on(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) return;
        if (!player.getInventory().getItemInHand().hasItemMeta())
            return;
        if (player.getInventory().getItemInHand().getType() != Material.INK_SACK)
            return;
        if (!player.getInventory().getItemInHand().getItemMeta().hasLore())
            return;
        if (!player.getInventory().getItemInHand().getItemMeta().getLore().contains(CC.translate(Main.getInstance().getConfig().getString("InstantInvis.Lore"))))
            return;
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (Main.getInstance().getInstantinvis().onCooldown(player)) {
                player.sendMessage(CC.translate(Main.getInstance().getConfig().getString("Main.Cooldown").replace("%time%", Main.getInstance().getInstantinvis().getRemaining(player))));

                player.updateInventory();
                event.setCancelled(true);
                return;
            }
        }
        Main.getInstance().getInstantinvis().CooldownApply(player, Main.getInstance().getConfig().getInt("InstantInvis.Cooldown") * 1000);
        player.playSound(player.getLocation(), Sound.BLAZE_HIT, 1f, 1f);
        player.removePotionEffect(PotionEffectType.INVISIBILITY);
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Main.getInstance().getConfig().getInt("InstantInvis.Invis.Time") * 20, 2));
        for (String s : configlist) {
            player.sendMessage(CC.translate(s).replace("%player%", player.getName()).replace("%heart%", "❤"));
        }
        player.updateInventory();
        event.setCancelled(true);

        if (player.getInventory().getItemInHand().getAmount() == 1) {
            player.getInventory().setItemInHand(null);
            return;
        }
        player.getInventory().getItemInHand().setAmount(player.getInventory().getItemInHand().getAmount() - 1);
    }

}
