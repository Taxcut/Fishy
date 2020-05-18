package com.fishy.hcf.listener;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.fishy.hcf.HCF;

public class BookDisenchantListener implements Listener {

    private final HCF plugin;

    public BookDisenchantListener(HCF plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void disenchantBook(PlayerInteractEvent event) {
        // Do nothing if disenchanting is disabled in configuration or player
        // is not left clicking an enchantment table.
        if (event.getAction() != Action.LEFT_CLICK_BLOCK ||
                event.getClickedBlock().getType() != Material.ENCHANTMENT_TABLE) {
            return;
        }

        // Do nothing if player is not holding a valid enchanted book.
        Player player = event.getPlayer();
        ItemStack item = player.getItemInHand();
        if (item == null || item.getType() != Material.ENCHANTED_BOOK) {
            return;
        }

        // Cancel the event, set the book to a regular book and inform.
        event.setCancelled(true);
        player.setItemInHand(new ItemStack(Material.BOOK, 1));
        player.sendMessage(ChatColor.WHITE + "You reverted this item to its original form.");
    }

}
