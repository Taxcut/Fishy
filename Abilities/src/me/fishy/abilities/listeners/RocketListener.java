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
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;


import java.util.List;

public class RocketListener implements Listener {
    private final FileConfiguration config = Main.getInstance().getConfig();
    private final List<String> configlist = Main.getInstance().getConfig().getStringList("Rocket.Message");

    @EventHandler
    public void on(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)
            return;
        if (!(player.getInventory().getItemInHand().getType() == Material.FIREWORK))
            return;
        if (!(player.getInventory().getItemInHand().getItemMeta().getLore().contains(CC.translate(Main.getInstance().getConfig().getString("Rocket.Lore")))))
            return;
        if (!(player.getInventory().getItemInHand().hasItemMeta()))
            return;
        if (Main.getInstance().getRocket().onCooldown(player)) {
            player.sendMessage(CC.translate(Main.getInstance().getConfig().getString("Main.Cooldown").replace("%time%", Main.getInstance().getRocket().getRemaining(player))));
            event.setCancelled(true);
            return;

        }
        Main.getInstance().getRocket().CooldownApply(player, config.getInt("Rocket.Cooldown") * 1000);
        player.setVelocity(new Vector(player.getLocation().getDirection().getX() * Main.getInstance().getConfig().getInt("Rocket.Boost.ZandX"), Main.getInstance().getConfig().getInt("Rocket.Boost.Y"),
                player.getLocation().getDirection().getZ() * Main.getInstance().getConfig().getInt("Rocket.Boost.ZandX")));
        player.playSound(player.getLocation(), Sound.FIZZ, 1f, 1f);
        event.setCancelled(true);

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

    @EventHandler
    public void onFallDamage(EntityDamageEvent event) {

        if (!(event.getEntity() instanceof Player)) return;
        if (!(event.getCause().equals(EntityDamageEvent.DamageCause.FALL))) return;
        Player player = (Player) event.getEntity();
        if (Main.getInstance().getRocket().onCooldown(player)) {
            event.setCancelled(true);
        }
    }
}
