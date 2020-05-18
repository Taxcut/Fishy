package me.fishy.abilities.listeners;


import me.fishy.abilities.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.entity.EntityDamageByEntityEvent;

import me.fishy.abilities.Main;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class TazerListener implements Listener {

    private final List<String> tazedm = Main.getInstance().getConfig().getStringList("Tazer.Messages.Tazed");
    private final List<String> usedm = Main.getInstance().getConfig().getStringList("Tazer.Messages.Used");

    @EventHandler
    public void Tazer(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        if (!(event.getEntity() instanceof Player)) return;
        Player damager = (Player) event.getDamager();
        Player damaged = (Player) event.getEntity();

        ItemStack tazer = new ItemStack(Material.BLAZE_ROD, damager.getInventory().getItemInHand().getAmount() - 0);
        ItemMeta tazerMeta = tazer.getItemMeta();
        ArrayList<String> lore = new ArrayList<>();
        lore.add(CC.translate(Main.getInstance().getConfig().getString("Tazer.Lore")));
        tazerMeta.setLore(lore);
        tazerMeta.setDisplayName(CC.translate(Main.getInstance().getConfig().getString("Tazer.Name")));
        tazer.setItemMeta(tazerMeta);
        if (damager.getInventory().getItemInHand().getType() != Material.BLAZE_ROD) return;
        if (!(damager.getInventory().getItemInHand().hasItemMeta())) return;
        if (!(damager.getInventory().getItemInHand().getItemMeta().getLore().contains(CC.translate(Main.getInstance().getConfig().getString("Tazer.Lore")))))
        return;
        if (Main.getInstance().getTazer().onCooldown(damager)) {
            damager.sendMessage(CC.translate(Main.getInstance().getConfig().getString("Main.Cooldown").replace("%time%", Main.getInstance().getTazer().getRemaining(damager))));

            damager.updateInventory();
            event.setCancelled(true);
            return;
        }

        Main.getInstance().getTazer().CooldownApply(damager, (Main.getInstance().getConfig().getInt("Tazer.Cooldown") * 1000));
        damager.playSound(damager.getLocation(), Sound.EXPLODE, 1f, 1f);
        damaged.playSound(damaged.getLocation(), Sound.EXPLODE, 1f, 1f);
        for (String s : tazedm) {
            damaged.sendMessage(CC.translate(s).replace("%player%", damager.getName()).replace("%heart%", "❤"));
        }
        for (String s2 : usedm) {
            damager.sendMessage(CC.translate(s2).replace("%player%", damaged.getName()).replace("%heart%", "❤"));
        }
        damaged.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Main.getInstance().getConfig().getInt("Tazer.Effect.Blindness.Time"), Main.getInstance().getConfig().getInt("Tazer.Effect.Blindness.Power")));
        damaged.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Main.getInstance().getConfig().getInt("Tazer.Effect.Slowness.Time"), Main.getInstance().getConfig().getInt("Tazer.Effect.Slowness.Power")));
        damager.updateInventory();
    }
    }

