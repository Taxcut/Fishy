package me.fishy.abilities.listeners;

import me.fishy.abilities.Main;
import me.fishy.abilities.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.List;
import java.util.UUID;

public class AntiBuildEggListener implements Listener {
    private final FileConfiguration config = Main.getInstance().getConfig();
    private final List<String> buildeggm = Main.getInstance().getConfig().getStringList("Anti-BuildEgg.Messages.Used");
    private final List<String> buildegghitm = Main.getInstance().getConfig().getStringList("Anti-BuildEgg.Messages.Hit");
    private final List<String> cantbuild = Main.getInstance().getConfig().getStringList("Anti-BuildEgg.Messages.Anti-Build");


    private Main plugin = Main.getPlugin(Main.class);

    @EventHandler
    public void on(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)
            return;
        if (player.getInventory().getItemInHand().getType() != Material.EGG)
            return;
        if (!(player.getInventory().getItemInHand().hasItemMeta()))
            return;
        if (!(player.getInventory().getItemInHand().getItemMeta().getLore().contains(CC.translate(Main.getInstance().getConfig().getString("Anti-BuildEgg.Lore")))))
            return;
        if (Main.getInstance().getAntibuildegg().onCooldown(player)) {
            player.sendMessage(CC.translate(Main.getInstance().getConfig().getString("Main.Cooldown").replace("%time%", Main.getInstance().getAntibuildegg().getRemaining(player))));
            event.setCancelled(true);
           // player.getItemInHand().setAmount(player.getItemInHand().getAmount() + 1);
            return;
        }
        Main.getInstance().getAntibuildegg().CooldownApply(player, config.getInt("Cocaine.Cooldown") * 1000);
        Egg egg = event.getPlayer().launchProjectile(Egg.class);
        egg.setMetadata("buildegg", new FixedMetadataValue(plugin, player.getUniqueId()));
        player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 1f, 1f);
        event.setCancelled(true);


    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Egg)) return;
        Egg egg = (Egg) event.getDamager();
        if (!(egg.getShooter() instanceof Player) && !(event.getEntity() instanceof Player)) return;
        if (!egg.hasMetadata("buildegg")) return;
        Player damaged = (Player) event.getEntity();
        Main.getInstance().getAntibuildegghit().CooldownApply(damaged, config.getInt("Anti-BuildEgg.Time") * 1000);
        Player damager1 = (Player) egg.getShooter();
        Player damaged1 = (Player) event.getEntity();
        for (String s : buildegghitm) {
            damaged1.sendMessage(CC.translate(s).replace("%heart%", "").replace("%player%", damager1.getName()));

        }
        for (String s : buildeggm) {
            damager1.sendMessage(CC.translate(s).replace("%heart%", "").replace("%player%", damaged1.getName()));
        }

    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        if (Main.getInstance().getAntibuildegghit().onCooldown(player)) {
            event.setCancelled(true);
            for (String s : cantbuild) {
                player.sendMessage(CC.translate(s).replace("%time%", Main.getInstance().getAntibuildegghit().getRemaining(player)));
            }

        }

    }

}
