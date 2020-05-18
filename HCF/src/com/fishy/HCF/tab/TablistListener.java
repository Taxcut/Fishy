package com.fishy.hcf.tab;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.fishy.hcf.HCF;
import com.fishy.hcf.events.EventTimer;
import com.fishy.hcf.faction.type.PlayerFaction;

import net.md_5.bungee.api.ChatColor;


public class TablistListener  implements Listener {
	
    EventTimer eventTimer = HCF.getPlugin().getTimerManager().eventTimer;
    boolean nextCancelled = eventTimer.isNextCancelled();

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		TabList tab = TabList.createTabList(event.getPlayer());
		new BukkitRunnable() {
			
			@Override
			public void run() {
				if(TabList.hasTabList(event.getPlayer())) {
				 
					TablistListener.this.addTabEntry(event.getPlayer(), tab);
					return;
					
				}else {
					cancel();
				}
				
			}
		}.runTaskTimerAsynchronously(HCF.getPlugin(), 5, 5); 
	
		
	}
	
    @SuppressWarnings("deprecation")
	private void addTabEntry(Player player, TabList tab) {
		//Title thing
		tab.setSlot(5, "&5&l" + HCF.getPlugin().getConfig().getString("TITLE"));
		tab.setSlot(8, "&7desirepvp.net");

		
		PlayerFaction faction = HCF.getPlugin().getFactionManager().getPlayerFaction(player);
		if (faction != null) {
			tab.setSlot(16, "&5&lFaction Info");
			tab.setSlot(19, "&7Online:&f " + faction.getOnlineMembers().size() + "/" + faction.getMembers().size());
			tab.setSlot(22, "&7Balance:&f &2$&a" + faction.getBalance());
			if (faction.isRaidable()) {
				tab.setSlot(25, "&7DTR:&f " + ChatColor.RED + String.format("%.2f", faction.getDeathsUntilRaidable()));	
			} else {
				tab.setSlot(25, "&7DTR:&f " + String.format("%.2f", faction.getDeathsUntilRaidable()));	
			}	
		} else {
					tab.setSlot(16, "&5&lFaction Info");
			tab.setSlot(19, "&7None");
		}
		
		
		//Map Info
		
		//User Info
		tab.setSlot(45, "&5&lStats");
		tab.setSlot(48, "&7Kills:&f " + HCF.getPlugin().getUserManager().getUser(player.getUniqueId()).getKills());
		tab.setSlot(51, "&7Deaths: &f" + HCF.getPlugin().getUserManager().getUser(player.getUniqueId()).getDeaths());
		tab.setSlot(54, "&7Money: &2$&a" + HCF.getPlugin().getEconomyManager().getBalance(player.getUniqueId()));
		
		//Location
		String exactSpotName = HCF.getPlugin().getFactionManager().getFactionAt(player.getLocation()).getDisplayName(player);		
	    String value = "&7[" + this.getCardinalDirection(player) + "] " + "&f(" + player.getLocation().getBlockX() + ", " + player.getLocation().getBlockZ() + ")";
	    
	    tab.setSlot(43, "&5&lLocation");
		tab.setSlot(46, exactSpotName);
	    tab.setSlot(49, value);
	    
	    //Next KOTH
	    tab.setSlot(18, "&5&lEvent Schedule");
	    tab.setSlot(21, "&f" + (HCF.getPlugin().getTimerManager().getEventTimer().getNextEventFaction() != null ? HCF.getPlugin().getTimerManager().getEventTimer().getNextEventFaction().getName() : "None scheduled"));
	    


	}


	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		TabList.deleteTabList(event.getPlayer());
	}
	
	public String getCardinalDirection(Player player) {
		
	    String dir = "";
	    float y = player.getLocation().getYaw();
	    if (y < 0.0F) {
	      y += 360.0F;
	    }
	    y %= 360.0F;
	    int i = (int)((y + 8.0F) / 22.5D);
	    if ((i == 0) || (i == 1) || (i == 15)) {
	      dir = "S";
	    } else if ((i == 4) || (i == 5) || (i == 6) || (i == 2) || (i == 3)) {
	      dir = "W";
	    } else if ((i == 8) || (i == 7) || (i == 9)) {
	      dir = "N";
	    } else if ((i == 11) || (i == 10) || (i == 12) || (i == 13) || (i == 14)) {
	      dir = "E";
	    } else {
	      dir = "S";
	    }
	    return dir;
	}

}

		



	
	