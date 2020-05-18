package me.fishy.abilities.listeners;

import me.fishy.abilities.Main;
import me.fishy.abilities.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

public class FastPearlListener implements Listener {
    private final FileConfiguration config = Main.getInstance().getConfig();
    private final List<String> configlist = Main.getInstance().getConfig().getStringList("FastPearl.Message");

    @EventHandler
    public void on(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)
            return;
        if (!(player.getItemInHand().hasItemMeta()))
            return;
        if (!(player.getItemInHand().getItemMeta().hasLore()))
            return;
        if (player.getItemInHand().getType() != Material.ENDER_PEARL)
            return;
        if (!(player.getItemInHand().getItemMeta().getLore().contains(CC.translate(Main.getInstance().getConfig().getString("FastPearl.Lore")))))
            return;

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), Main.getInstance().getConfig().getString("FastPearl.Command").replace("%player%", player.getName()));
        for (String s : configlist) {
            player.sendMessage(CC.translate(s).replace("%heart%", "❤"));

        }
    }

}

