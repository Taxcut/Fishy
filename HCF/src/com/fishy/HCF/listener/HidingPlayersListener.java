package com.fishy.hcf.listener;

import com.fishy.hcf.HCF;
import com.fishy.hcf.faction.event.PlayerClaimEnterEvent;
import com.fishy.hcf.faction.type.SpawnFaction;

import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class HidingPlayersListener implements Listener {

    @EventHandler
    public void onClaimChange(PlayerClaimEnterEvent e) {
    	if (HCF.getPlugin().getSotwTimer().getSotwRunnable() == null) {
            if (e.getFromFaction() != null && e.getFromFaction() instanceof SpawnFaction && !(e.getToFaction() instanceof SpawnFaction)) {
                Player p = e.getPlayer();
                if (!HCF.getPlugin().getStaffModeListener().isVanished(p)) {
                	for (Player all : Bukkit.getOnlinePlayers()) {
                		all.showPlayer(p);
                	}
                }
            }
    	}
        if (HCF.getPlugin().getSotwTimer().getSotwRunnable() != null) {
            if (e.getFromFaction() != null && e.getFromFaction() instanceof SpawnFaction && !(e.getToFaction() instanceof SpawnFaction)) {
                Player p = e.getPlayer();
                if (!HCF.getPlugin().getStaffModeListener().isVanished(p)) {
                    for (Player all : Bukkit.getOnlinePlayers()) {
                        all.showPlayer(p);
                    }
                }
            } else if (e.getToFaction() != null && e.getToFaction() instanceof SpawnFaction && !(e.getFromFaction() instanceof SpawnFaction)) {
                Player p = e.getPlayer();
                for (Player all : Bukkit.getOnlinePlayers()) {
                    all.hidePlayer(p);
                }
            }
        }
    }
    

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        boolean hide = false;
        if (HCF.getPlugin().getSotwTimer().getSotwRunnable() != null) {
            if (HCF.getPlugin().getFactionManager().getClaimAt(p.getLocation()) != null
                    && HCF.getPlugin().getFactionManager().getClaimAt(p.getLocation()).getFaction() instanceof SpawnFaction) {
                hide = true;
            }
            for (Player all : Bukkit.getOnlinePlayers()) {
                if (hide) {
                    all.hidePlayer(p);
                }
                if (HCF.getPlugin().getFactionManager().getClaimAt(all.getLocation()) != null
                        && HCF.getPlugin().getFactionManager().getClaimAt(all.getLocation()).getFaction() instanceof SpawnFaction) {
                    p.hidePlayer(all);
                }
            }
        }
    }
}