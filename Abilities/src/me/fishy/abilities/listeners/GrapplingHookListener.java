package me.fishy.abilities.listeners;

import me.fishy.abilities.Main;
import me.fishy.abilities.utils.CC;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.util.Vector;

import java.util.List;

public class GrapplingHookListener implements Listener {

    private final FileConfiguration config = Main.getInstance().getConfig();
    private final List<String> configlist = Main.getInstance().getConfig().getStringList("GrapplingHook.Message");
    private Main plugin = Main.getPlugin(Main.class);

    @EventHandler(priority = EventPriority.HIGH)
    public void onFish(PlayerFishEvent event) {
        Player player = event.getPlayer();
        if (player.getInventory().getItemInHand().getType() != Material.FISHING_ROD)
            return;
        if (!(player.getInventory().getItemInHand().hasItemMeta()))
            return;
        if (!(player.getInventory().getItemInHand().getItemMeta().getLore().contains(CC.translate(Main.getInstance().getConfig().getString("GrapplingHook.Lore")))))
            return;
        if (Main.getInstance().getGrapplinghook().onCooldown(player)) {
            player.sendMessage(CC.translate(Main.getInstance().getConfig().getString("Main.Cooldown").replace("%time%", Main.getInstance().getGrapplinghook().getRemaining(player))));
            return;
        }
        if (event.getState() == PlayerFishEvent.State.IN_GROUND) {
            Location loc = event.getHook().getLocation();
            Location ploc = player.getLocation();
            Location nloc = loc.subtract(ploc);
            Vector vector = new Vector(nloc.toVector().normalize().multiply(1.5).getX(), 0.5, nloc.toVector().normalize().multiply(1.5).getZ());
            player.setVelocity(vector);
        }
        Main.getInstance().getGrapplinghook().CooldownApply(player, config.getInt("GrapplingHook.Cooldown") * 1000);

    }
}