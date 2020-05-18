package com.fishy.hcf.listener.fixes;

import com.fishy.hcf.HCF;
import com.fishy.hcf.faction.type.SpawnFaction;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

@RequiredArgsConstructor
public class PVPTimerListener implements Listener{

    private final HCF plugin;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        doPvPTimerCheck(event.getPlayer(), true);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event){
        doPvPTimerCheck(event.getPlayer(), false);
    }

    private void doPvPTimerCheck(Player player, boolean join){
        if(plugin.getConfig().getBoolean("KITMAP")) return;

        if(plugin.getTimerManager().getEventTimer().getName().equals("EOTW")){
            if(plugin.getTimerManager().getInvincibilityTimer().getRemaining(player) > 0){
                plugin.getTimerManager().getInvincibilityTimer().clearCooldown(player);
            }
            return;
        }

        if(!join || !player.hasPlayedBefore()){
            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "timer set PVPTimer " + player.getName() + " 3600s");
            if(plugin.getFactionManager().getFactionAt(player.getLocation()) instanceof SpawnFaction){
                plugin.getTimerManager().getInvincibilityTimer().setPaused(player.getUniqueId(), true);
            }
        }
    }
}
