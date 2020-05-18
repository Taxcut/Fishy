package com.fishy.hcf.listener;


import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.fishy.hcf.HCF;

import net.minecraft.util.gnu.trove.map.TObjectIntMap;
import net.minecraft.util.gnu.trove.map.hash.TObjectIntHashMap;

public class KillstreakListener implements Listener
{
    TObjectIntMap<UUID> killStreakMap;
    private HCF plugin;
    
    public KillstreakListener(HCF plugin) {
        this.killStreakMap = (TObjectIntMap<UUID>)new TObjectIntHashMap();
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity().getKiller();
        if (player != null) {
            this.killStreakMap.adjustOrPutValue(event.getEntity().getKiller().getUniqueId(), 1, 1);
            if (this.killStreakMap.containsKey(event.getEntity().getUniqueId())) {
                this.killStreakMap.remove(event.getEntity().getUniqueId());
            }
            for (KillStreaks killStreaks : KillStreaks.values()) {
                if (this.killStreakMap.get(player.getUniqueId()) == killStreaks.kills) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), killStreaks.command.replaceAll("name", player.getName()));
                    Bukkit.broadcastMessage(ChatColor.RED.toString() + ChatColor.BOLD + player.getName() + ChatColor.WHITE + " has now reached a killstreak of " + ChatColor.GRAY.toString() + ChatColor.BOLD + killStreaks.kills + ChatColor.WHITE + " and received " + ChatColor.AQUA + killStreaks.name);
                    return;
                }
            }
        }
    }
}
