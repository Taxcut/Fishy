package com.fishy.hcf.listener.fixes;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.fishy.hcf.HCF;
import com.fishy.hcf.events.faction.EventFaction;
import com.fishy.hcf.faction.type.Faction;

public class EventPearlFix implements Listener {
    private final HCF plugin;

    public EventPearlFix(HCF plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerEnderpearl(PlayerInteractEvent event) {
        Action action = event.getAction();
        if(action == Action.RIGHT_CLICK_AIR) {
            ItemStack itemStack = event.getItem();
            if(itemStack != null && itemStack.getType() == Material.ENDER_PEARL) {
                Player player = event.getPlayer();
                Faction factionAt = plugin.getFactionManager().getFactionAt(player.getLocation());
                if (factionAt instanceof EventFaction) {
                    event.setUseItemInHand(Event.Result.DENY);
                    player.sendMessage(ChatColor.RED + "You cannot " + plugin.getTimerManager().enderPearlTimer.getDisplayName() + ChatColor.RED + " in an active event zone.");
                }
            }
        }
    }
}
