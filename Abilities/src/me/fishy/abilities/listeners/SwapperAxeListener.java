package me.fishy.abilities.listeners;

import me.fishy.abilities.Main;
import me.fishy.abilities.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

public class SwapperAxeListener implements Listener {
    private Main plugin = Main.getPlugin(Main.class);
    private final FileConfiguration config = Main.getInstance().getConfig();
    private final List<String> usedm = Main.getInstance().getConfig().getStringList("SwapperAxe.Messages.Used");
    private final List<String> hitm = Main.getInstance().getConfig().getStringList("SwapperAxe.Messages.Hit");
    private final List<String> fullinvm = Main.getInstance().getConfig().getStringList("SwapperAxe.Messages.FullInventory");
    private final List<String> delaym = Main.getInstance().getConfig().getStringList("SwapperAxe.Messages.UsedDelay");
    private final List<String> hitdelaym = Main.getInstance().getConfig().getStringList("SwapperAxe.Messages.HitDelay");




    @EventHandler
    public void on(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getDamager();
        Player hit = (Player) event.getEntity();
        if (!(player.getItemInHand().getItemMeta().hasLore()))
            return;
        if (!player.getItemInHand().getItemMeta().getLore().contains(CC.translate(Main.getInstance().getConfig().getString("SwapperAxe.Lore"))))
            return;
        if (hit.getInventory().getHelmet() == null) {
            player.sendMessage(CC.translate("&c%hit% is not wearing a helmet!".replace("%hit%", hit.getName())));
            return;
        }
        if (hit.getInventory().getHelmet().getType() != Material.DIAMOND_HELMET) {
            player.sendMessage(CC.translate("&c%hit% is not wearing a diamond helmet!".replace("%hit%", hit.getName())));
            return;
        }
        if (Main.getInstance().getSwapperaxe().onCooldown(player)) {
            player.sendMessage(CC.translate(Main.getInstance().getConfig().getString("Main.Cooldown").replace("%time%", Main.getInstance().getSwapperaxe().getRemaining(player))));
            return;
        }
        Main.getInstance().getSwapperaxe().CooldownApply(player, config.getInt("SwapperAxe.Cooldown") * 1000);
        for (String s : hitdelaym) {
            hit.sendMessage(CC.translate(s).replace("%player%", player.getName()).replace("%heart%", "❤"));
        }
        for (String s : delaym) {
            player.sendMessage(CC.translate(s).replace("%player%", hit.getName()).replace("%heart%", "❤"));
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                if (!(hit.getInventory().firstEmpty() == -1)) {
                    hit.getInventory().addItem(hit.getInventory().getHelmet());
                    hit.getInventory().setHelmet(null);
                    for (String s : hitm) {
                        hit.sendMessage(CC.translate(s).replace("%player%", player.getName()).replace("%heart%", "❤"));
                    }
                    for (String s : usedm) {
                        player.sendMessage(CC.translate(s).replace("%player%", hit.getName()).replace("%heart%", "❤"));
                    }
                    Main.getInstance().saveConfig();
                } else {
                    World world = hit.getWorld();
                    world.dropItem(hit.getLocation(), hit.getInventory().getHelmet());
                    hit.getInventory().setHelmet(null);
                    for (String s : hitm) {
                        hit.sendMessage(CC.translate(s).replace("%player%", player.getName()).replace("%heart%", "❤"));
                    }
                    for (String s : fullinvm) {
                        hit.sendMessage(CC.translate(s).replace("%player%", player.getName()).replace("%heart%", "❤"));
                    }
                    for (String s : usedm) {
                        player.sendMessage(CC.translate(s).replace("%player%", hit.getName()).replace("%heart%", "❤"));
                    }
                    Main.getInstance().saveConfig();
                }
            }
        }, 20 * Main.getInstance().getConfig().getInt("SwapperAxe.Delay"));
    }
}
