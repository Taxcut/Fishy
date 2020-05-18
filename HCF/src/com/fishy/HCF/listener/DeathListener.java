package com.fishy.hcf.listener;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import com.fishy.hcf.HCF;
import com.fishy.hcf.faction.struct.Role;
import com.fishy.hcf.faction.type.Faction;
import com.fishy.hcf.faction.type.PlayerFaction;
import com.fishy.hcf.user.FactionUser;
import com.fishy.hcf.util.base.JavaUtils;

import net.minecraft.server.v1_7_R4.EntityLightning;
import net.minecraft.server.v1_7_R4.PacketPlayOutSpawnEntityWeather;
import net.minecraft.server.v1_7_R4.WorldServer;

public class DeathListener implements Listener {

    private HCF plugin;
    public static HashMap<UUID, ItemStack[]> inventoryContents = new HashMap<>();
    public static HashMap<UUID, ItemStack[]> armorContents = new HashMap<>();

    public DeathListener(HCF plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerDeathKillIncrement(PlayerDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer != null) {
            FactionUser user = plugin.getUserManager().getUser(killer.getUniqueId());
            user.setKills(user.getKills() + 1);

            if (plugin.getFactionManager().getPlayerFaction(killer) != null) {
        		plugin.getFactionManager().getPlayerFaction(killer).setPoints(plugin.getFactionManager().getPlayerFaction(killer).getPoints() + 1);
        	}
        }
    }
    
    public void Respawn(Player player, int Time) {
        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                player.spigot().respawn();
            }
        }, Time);
    }
    
    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        this.Respawn(player, 6);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Player killer = event.getEntity().getKiller();
        PlayerFaction playerFaction = plugin.getFactionManager().getPlayerFaction(player);
        
        if (killer != null) {
            //HCF.getPlugin().getUserManager().getUser(killer.getUniqueId()).setKills(HCF.getPlugin().getUserManager().getUser(killer.getUniqueId()).getKills() + 1);	
            PlayerFaction killerFaction = plugin.getFactionManager().getPlayerFaction(killer);
            if (killerFaction != null) {
                killerFaction.setPoints(killerFaction.getPoints() + 1);
                killerFaction.broadcast(ChatColor.WHITE + "Your faction has been awarded " + ChatColor.GRAY + "1" + ChatColor.WHITE + " point.");		
            }
        }
        
        HCF.getPlugin().getTimerManager().getCombatTimer().clearCooldown(player);
        
/*        if (HCF.getPlugin().getActiveKTK() != null && HCF.getPlugin().getActiveKTK().getKing().equals(player.getUniqueId())) {
        	HCF.getPlugin().getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&8[&9&lKillTheKing&8] &7" + Bukkit.getPlayer(HCF.getPlugin().getActiveKTK().getKing()).getName() + " &fhas been killed. &c(" +
        	DurationFormatter.getRemaining(HCF.getPlugin().getActiveKTK().getStarted(), false) + "&c)"));
        	HCF.getPlugin().setActiveKTK(null);
        }*/

        if (HCF.getPlugin().getConfig().getBoolean("KITMAP")) {
            if (killer != null) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cr givekey " + killer.getName() + " kill 1");
            }
        }
        plugin.getUserManager().getUser(player.getUniqueId()).setDeaths(plugin.getUserManager().getUser(player.getUniqueId()).getDeaths() + 1);

        if (playerFaction == null) return;
        
        Player p = event.getEntity();
        HCF.getPlugin().hm.put(p.getName(), p.getInventory().getContents());
        HCF.getPlugin().armorContents.put(p.getName(), p.getInventory().getArmorContents());
        
        inventoryContents.put(player.getUniqueId(), player.getInventory().getContents());
        armorContents.put(player.getUniqueId(), player.getInventory().getArmorContents());
        
        Location location = player.getLocation();
        
        if (Bukkit.spigot().getTPS()[0] > 12.0D) { // Prevent unnecessary lag during prime times.
            Location location1 = player.getLocation();
            WorldServer worldServer = ((CraftWorld) location.getWorld()).getHandle();

            EntityLightning entityLightning = new EntityLightning(worldServer, location1.getX(), location1.getY(), location1.getZ(), false);
            PacketPlayOutSpawnEntityWeather packet = new PacketPlayOutSpawnEntityWeather(entityLightning);
            for (Player target : Bukkit.getOnlinePlayers()) {
                if (plugin.getUserManager().getUser(target.getUniqueId()).isShowLightning()) {
                    ((CraftPlayer) target).getHandle().playerConnection.sendPacket(packet);
                    target.playSound(target.getLocation(), Sound.AMBIENCE_THUNDER, 1.0F, 1.0F);
                }
            }
        }

        Faction factionAt = plugin.getFactionManager().getFactionAt(player.getLocation());
        double dtrLoss = ((HCF.getPlugin().getConfig().getBoolean("KITMAP") ? 0.1D : 1.0D) * factionAt.getDtrLossMultiplier());
        double newDtr = playerFaction.setDeathsUntilRaidable(playerFaction.getDeathsUntilRaidable() - dtrLoss);

        Role role = playerFaction.getMember(player.getUniqueId()).getRole();
        long baseDelay = ((HCF.getPlugin().getConfig().getBoolean("KITMAP") ? TimeUnit.MINUTES.toMillis(1L) : TimeUnit.MINUTES.toMillis(30L)));
        playerFaction.setRemainingRegenerationTime(baseDelay);
        playerFaction.broadcast(ChatColor.RED + "Member Death: " + ChatColor.WHITE +
                role.getAstrix() + player.getName());
        playerFaction.broadcast(ChatColor.RED + "DTR: " + ChatColor.WHITE + JavaUtils.format(newDtr, 2));
        playerFaction.setPoints(playerFaction.getPoints() - 1);
        if (playerFaction != null) {
            playerFaction.broadcast(ChatColor.WHITE + "Your faction has lost " + ChatColor.GRAY + "1" + ChatColor.WHITE + " point.");	
        } 
        if (HCF.getPlugin().getActiveKTK() != null) {
            if (player.getUniqueId().equals(HCF.getPlugin().getActiveKTK().getKing())) {
                HCF.getPlugin().getActiveKTK().initKiller(killer);
                HCF.getPlugin().setActiveKTK(null);
            }
        }
    }

}
