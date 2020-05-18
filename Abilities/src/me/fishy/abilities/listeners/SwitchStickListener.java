package me.fishy.abilities.listeners;

import me.fishy.abilities.Main;
import me.fishy.abilities.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class SwitchStickListener implements Listener {
    private final List<String> damagem = Main.getInstance().getConfig().getStringList("SwitchStick.Messages.Switched");
    private final List<String> damagerm = Main.getInstance().getConfig().getStringList("SwitchStick.Messages.Switcher");

    @EventHandler
    public void on(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        if (!(event.getEntity() instanceof Player)) return;
        Player damager = (Player) event.getDamager();
        Player damaged = (Player) event.getEntity();
        if (!damager.getInventory().getItemInHand().hasItemMeta())
            return;
        if (damager.getInventory().getItemInHand().getType() != Material.STICK)
            return;
        if (!damager.getInventory().getItemInHand().getItemMeta().getLore().contains(CC.translate(Main.getInstance().getConfig().getString("SwitchStick.Lore"))))
            return;
        Location location = damaged.getLocation();
        location.setYaw(location.getYaw() + 180);
        if (Main.getInstance().getSwitchstick().onCooldown(damager)) {
            damager.sendMessage(CC.translate(Main.getInstance().getConfig().getString("Main.Cooldown").replace("%time%", Main.getInstance().getSwitchstick().getRemaining(damager))));
            damager.updateInventory();
            event.setCancelled(true);
            return;
        }

        Main.getInstance().getSwitchstick().CooldownApply(damager, (Main.getInstance().getConfig().getInt("SwitchStick.Cooldown") * 1000));
        damaged.teleport(location);
        for (String s : damagem) {
            damaged.sendMessage(CC.translate(s).replace("%player%", damager.getName()).replace("%heart%", "❤"));

        }
        for (String s2 : damagerm) {
            damager.sendMessage(CC.translate(s2).replace("%player%", damaged.getName()).replace("%heart%", "❤"));
        }

        damager.updateInventory();
    }
}
