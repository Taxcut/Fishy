package me.fishy.abilities.listeners;

import me.fishy.abilities.Main;
import me.fishy.abilities.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class CocaineListener implements Listener {

    private final FileConfiguration config = Main.getInstance().getConfig();
    private final List<String> configlist = Main.getInstance().getConfig().getStringList("Cocaine.Message");

    @EventHandler
    public void on(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)
            return;
        if (!(player.getInventory().getItemInHand().hasItemMeta()))
            return;
        if (player.getInventory().getItemInHand().getType() != Material.SUGAR)
            return;
        if (!player.getInventory().getItemInHand().getItemMeta().getLore().contains(CC.translate(config.getString("Cocaine.Lore"))))
            return;
        if (Main.getInstance().getCocaine().onCooldown(player)) {
            player.sendMessage(CC.translate(Main.getInstance().getConfig().getString("Main.Cooldown").replace("%time%", Main.getInstance().getCocaine().getRemaining(player))));
            return;
        }
        Main.getInstance().getCocaine().CooldownApply(player, config.getInt("Cocaine.Cooldown") * 1000);
        player.playSound(player.getLocation(), Sound.DIG_SNOW, 1f, 1f);
        player.removePotionEffect(PotionEffectType.SPEED);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, config.getInt("Cocaine.Speed.Time") * 20, 2));
        for (String s : configlist) {
            player.sendMessage(CC.translate(s).replace("%heart%", "❤"));

        }
        if (player.getInventory().getItemInHand().getAmount() == 1) {
            player.getInventory().setItemInHand(null);
            return;
        }
        player.getInventory().getItemInHand().setAmount(player.getInventory().getItemInHand().getAmount() - 1);




    }

}
