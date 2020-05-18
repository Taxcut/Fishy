package com.fishy.hcf.deathban;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import net.minecraft.util.org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.fishy.hcf.HCF;
import com.fishy.hcf.user.FactionUser;
import com.fishy.hcf.util.DelayedMessageRunnable;
import com.fishy.hcf.util.DurationFormatter;

import net.minecraft.util.gnu.trove.map.TObjectIntMap;
import net.minecraft.util.gnu.trove.map.TObjectLongMap;
import net.minecraft.util.gnu.trove.map.hash.TObjectIntHashMap;
import net.minecraft.util.gnu.trove.map.hash.TObjectLongHashMap;

public class DeathbanListener implements Listener {

    private static final long LIFE_USE_DELAY_MILLIS = TimeUnit.SECONDS.toMillis(30L);
    private static final String LIFE_USE_DELAY_WORDS = DurationFormatUtils.formatDurationWords(DeathbanListener.LIFE_USE_DELAY_MILLIS, true, true);
    private static final String DEATH_BAN_BYPASS_PERMISSION = "hcf.deathban.bypass";

    private final TObjectIntMap<UUID> respawnTickTasks = new TObjectIntHashMap<>();
    private final TObjectLongMap<UUID> lastAttemptedJoinMap = new TObjectLongHashMap<>();
    private final HCF plugin;

    private final Random random = new Random();
    
    public DeathbanListener(HCF plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().spigot().respawn(); // Method already checks if player is dead first
        event.setJoinMessage(null);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        FactionUser user = plugin.getUserManager().getUser(player.getUniqueId());
        Deathban deathban = user.getDeathban();
        if (deathban == null || !deathban.isActive()) {
            return;
        }

        if (player.hasPermission(DeathbanListener.DEATH_BAN_BYPASS_PERMISSION)) {
            plugin.getUserManager().getUser(player.getUniqueId()).removeDeathban();
            informAboutDeathbanBypass(player, deathban, plugin, true);
            return;
        }

        if (deathban.isEotwDeathban()) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.RED + "Deathbanned for the entirety of the map due to EOTW.");
            return;
        }

        UUID uuid = player.getUniqueId();
        int lives = plugin.getDeathbanManager().getLives(uuid);

        String formattedRemaining = DurationFormatter.getRemaining(deathban.getRemaining(), true, false);
        Location deathbanLocation = deathban.getDeathPoint();

        if (lives <= 0) {  // If the user has no lives, inform that they need some.
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.WHITE + "You are currrently deathbanned for " + ChatColor.GREEN + formattedRemaining + ChatColor.WHITE + " for "
            		+ ChatColor.GREEN + deathban.getReason() + ChatColor.WHITE + ". Purchase lives at " + ChatColor.GRAY + "shop.desirepvp.net" + ChatColor.WHITE + '.'
            );

            return;
        }

        long millis = System.currentTimeMillis();
        long lastAttemptedJoinMillis = lastAttemptedJoinMap.get(uuid);

        // If the user has tried joining in the last 30 seconds and kicked for deathban but has lives, let them join this time to prevent accidental life use.
        if (lastAttemptedJoinMillis != lastAttemptedJoinMap.getNoEntryValue() && lastAttemptedJoinMillis - millis < DeathbanListener.LIFE_USE_DELAY_MILLIS) {
            lastAttemptedJoinMap.remove(uuid);
            user.removeDeathban();
            lives = plugin.getDeathbanManager().takeLives(uuid, 1);

            event.setResult(PlayerLoginEvent.Result.ALLOWED);
            new DelayedMessageRunnable(plugin, player, ChatColor.WHITE + "You have used a life for entry. You now have " + ChatColor.GREEN + lives + ChatColor.WHITE + " lives.");

            return;
        }

        // The user has lives, but just in case they didn't want them to use, tell them to join again in the next 30 seconds.
        lastAttemptedJoinMap.put(uuid, millis + LIFE_USE_DELAY_MILLIS);

        event.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.WHITE + "You are currrently deathbanned for " + ChatColor.GREEN + formattedRemaining + ChatColor.WHITE + " for "
        		+ ChatColor.GREEN + deathban.getReason() + ChatColor.WHITE + ". You currently have " + ChatColor.GREEN + lives + ChatColor.WHITE + " lives. You may use one by reconnecting within " +
                ChatColor.GRAY + DeathbanListener.LIFE_USE_DELAY_WORDS + ChatColor.WHITE + "."
        );
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Deathban deathban = plugin.getDeathbanManager().applyDeathBan(player, event.getDeathMessage());
        long remaining = deathban.getRemaining();
        if (remaining <= 0L || player.hasPermission(DeathbanListener.DEATH_BAN_BYPASS_PERMISSION)) {
            return;
        }

        long ticks = TimeUnit.SECONDS.toMillis(0L);

        if (ticks <= 0L || remaining < ticks) {
            handleKick(player, deathban);
            return;
        }

        // Let the player see the death screen for x seconds
        respawnTickTasks.put(player.getUniqueId(), new BukkitRunnable() {
            @Override
            public void run() {
                respawnTickTasks.remove(player.getUniqueId());
                handleKick(player, deathban);
            }
        }.runTaskLater(plugin, ticks).getTaskId());
    }
    
	@EventHandler
	public void onRespawn(PlayerRespawnEvent e) {
		e.setRespawnLocation(Bukkit.getWorld("world").getSpawnLocation());
	}

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerRequestRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        FactionUser user = plugin.getUserManager().getUser(player.getUniqueId());
        Deathban deathban = user.getDeathban();
        player.teleport(Bukkit.getWorld("world").getSpawnLocation());
        player.getInventory().clear();
		player.getInventory().setArmorContents(null);
		player.updateInventory();
        if (deathban != null && deathban.getRemaining() > 0L) {
            if (player.hasPermission(DeathbanListener.DEATH_BAN_BYPASS_PERMISSION)) {
                cancelRespawnKickTask(player);
                user.removeDeathban();
                informAboutDeathbanBypass(player, deathban, plugin, false);
                return;
            }

            //event.setCancelled(true);
            handleKick(player, deathban);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        cancelRespawnKickTask(event.getPlayer());
    }

    private static void informAboutDeathbanBypass(Player player, Deathban deathban, JavaPlugin plugin, boolean later) {
        String message = ChatColor.RED + "You would be death-banned for " + ChatColor.RED + ChatColor.stripColor(deathban.getReason()) + ChatColor.RED + ", but you have access to bypass.";
        if (later) {
            new DelayedMessageRunnable(plugin, player, message);
        } else {
            player.sendMessage(message);
        }
    }

    private void cancelRespawnKickTask(Player player) {
        int taskId = respawnTickTasks.remove(player.getUniqueId());
        if (taskId != respawnTickTasks.getNoEntryValue()) {
            Bukkit.getScheduler().cancelTask(taskId);
        }
    }

    private void handleKick(Player player, Deathban deathban) {
        if (plugin.getEotwHandler().isEndOfTheWorld()) {
        	player.kickPlayer(ChatColor.RED + "Deathbanned for the entirety of the map due to EOTW.\nCome back tomorrow for SOTW!");
        }
        else {
            player.kickPlayer(ChatColor.RED + "Deathbanned for " + DurationFormatter.getRemaining(deathban.getRemaining(), true, false) + ": " + ChatColor.WHITE + deathban.getReason());
        }
    }
}
