package com.fishy.hcf.timer.type;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import com.fishy.hcf.HCF;
import com.fishy.hcf.faction.event.PlayerClaimEnterEvent;
import com.fishy.hcf.faction.event.PlayerJoinFactionEvent;
import com.fishy.hcf.faction.event.PlayerLeaveFactionEvent;
import com.fishy.hcf.kit.event.KitApplyEvent;
import com.fishy.hcf.timer.PlayerTimer;
import com.fishy.hcf.timer.TimerCooldown;
import com.fishy.hcf.timer.event.TimerStartEvent;
import com.fishy.hcf.util.DurationFormatter;
import com.fishy.hcf.util.base.BukkitUtils;
import com.fishy.hcf.visualise.VisualType;

import net.minecraft.util.org.apache.commons.lang3.time.DurationFormatUtils;

/**
 * Timer used to tag {@link Player}s in combat to prevent entering safe-zones.
 */
public class SpawnTagTimer extends PlayerTimer implements Listener {

    private final HCF plugin;

    public SpawnTagTimer(HCF plugin) {
        super("Combat", TimeUnit.SECONDS.toMillis(30L));
        this.plugin = plugin;
    }

    @Override
    public String getScoreboardPrefix() {
        return ChatColor.RED.toString() + ChatColor.BOLD;
    }

    @Override
    public TimerCooldown clearCooldown(@Nullable Player player, UUID playerUUID) {
        TimerCooldown cooldown = super.clearCooldown(player, playerUUID);
        if (cooldown != null && player != null) {
            plugin.getVisualiseHandler().clearVisualBlocks(player, VisualType.SPAWN_BORDER, null);
        }

        return cooldown;
    }

    @Override
    public void handleExpiry(@Nullable Player player, UUID playerUUID) {
        super.handleExpiry(player, playerUUID);
        if (player != null) {
            plugin.getVisualiseHandler().clearVisualBlocks(player, VisualType.SPAWN_BORDER, null);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onFactionJoin(PlayerJoinFactionEvent event) {
        Optional<Player> optional = event.getPlayer();
        if (optional.isPresent()) {
            Player player = optional.get();
            long remaining = getRemaining(player);
            if (remaining > 0L) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "You cannot join factions whilst your " + getDisplayName() + ChatColor.RED + " timer is active [" + ChatColor.BOLD + DurationFormatter.getRemaining(getRemaining(player), true) + 
                		ChatColor.RED + " remaining]");
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onFactionLeave(PlayerLeaveFactionEvent event) {
        if (event.isForce()) {
            return;
        }

        Optional<Player> optional = event.getPlayer();
        if (optional.isPresent()) {
            Player player = optional.get();
            long remaining = getRemaining(player);
            if (remaining > 0L) {
                event.setCancelled(true);

                CommandSender sender = event.getSender();
                if (sender == player) {
                    player.sendMessage(ChatColor.RED + "You cannot leave your faction whilst your " + getDisplayName() + ChatColor.RED + " timer is active [" + ChatColor.BOLD + DurationFormatter.getRemaining(getRemaining(player), true) + 
                    		ChatColor.RED + " remaining]");
                } else {
                    player.sendMessage(ChatColor.RED + "You cannot kick members whilst their " + getDisplayName() + ChatColor.RED + " timer is active [" + ChatColor.BOLD + DurationFormatter.getRemaining(getRemaining(player), true) + 
                    		ChatColor.RED + " remaining]");
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPreventClaimEnter(PlayerClaimEnterEvent event) {
        if (event.getEnterCause() == PlayerClaimEnterEvent.EnterCause.TELEPORT) {
            return;
        }

        // Prevent entering spawn if the player is spawn tagged.
        Player player = event.getPlayer();
        if (!event.getFromFaction().isSafezone() && event.getToFaction().isSafezone() && getRemaining(player) > 0L) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You cannot enter Spawn whilst your " + getDisplayName() + ChatColor.RED + " timer is active [" + ChatColor.BOLD + DurationFormatter.getRemaining(getRemaining(player), true) + 
            		ChatColor.RED + " remaining]");
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {

        Player attacker = BukkitUtils.getFinalAttacker(event, true);

        Entity entity;

        if ((attacker != null) && (((entity = event.getEntity()) instanceof Player))) {

            Player attacked = (Player) entity;

            boolean weapon = event.getDamager() instanceof Arrow;

            if (!weapon) {

                ItemStack stack = attacker.getItemInHand();

                weapon = (stack != null) && ((EnchantmentTarget.WEAPON.includes(stack)) || stack.getType() == Material.FISHING_ROD);

            }

            long duration = weapon ? this.defaultCooldown : TimeUnit.SECONDS.toMillis(5L);

            setCooldown(attacked, attacked.getUniqueId(), Math.max(getRemaining(attacked), duration), true);

            setCooldown(attacker, attacker.getUniqueId(), Math.max(getRemaining(attacker), duration), true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onTimerStart(TimerStartEvent event) {
        if (event.getTimer() == this) {
            Optional<Player> optional = event.getPlayer();
            if (optional.isPresent()) {
                Player player = optional.get();
                player.sendMessage(ChatColor.WHITE + "You are now spawn tagged for " + ChatColor.RED.toString() + DurationFormatUtils.formatDurationWords(event.getDuration(), true, true));
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        clearCooldown(event.getPlayer(), event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (HCF.getPlugin().getConfig().getBoolean("ANTICOMBATPLACE")) {
            if (getRemaining(event.getPlayer()) > 0L) {
                if (plugin.getFactionManager().getPlayerFaction(event.getPlayer()) != null && plugin.getFactionManager().getPlayerFaction(event.getPlayer()).equals(plugin.getFactionManager().getFactionAt(event.getBlock()))) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(ChatColor.RED + "You cannot place blocks whilst your " + getDisplayName() + ChatColor.RED + " timer is active [" + ChatColor.BOLD + DurationFormatter.getRemaining(getRemaining(event.getPlayer()), true) + 
                    		ChatColor.RED + " remaining]");
                }
            }

        } else {
            return;
        }

    }
	
	@EventHandler
	public void onKitApply(KitApplyEvent event) {
		Player player = event.getPlayer();
		if (getRemaining(player) > 0L) {
				event.setCancelled(true);
	            player.sendMessage(ChatColor.RED + "You cannot apply kits whilst your " + getDisplayName() + ChatColor.RED + " timer is active [" + ChatColor.BOLD + DurationFormatter.getRemaining(getRemaining(player), true) + 
	            		ChatColor.RED + " remaining]");
		}
	}

    @Override
    public boolean setCooldown(@Nullable Player player, UUID playerUUID, long duration, boolean overwrite, @Nullable Predicate<Long> currentCooldownPredicate) {
        if (player != null && plugin.getFactionManager().getFactionAt(player.getLocation()).isSafezone()) {
            return false;
        }
        return super.setCooldown(player, playerUUID, duration, overwrite, currentCooldownPredicate);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPreventClaimEnterMonitor(PlayerClaimEnterEvent event) {
        if (event.getEnterCause() == PlayerClaimEnterEvent.EnterCause.TELEPORT && (!event.getFromFaction().isSafezone() && event.getToFaction().isSafezone())) {
            clearCooldown(event.getPlayer(), event.getPlayer().getUniqueId());
        }
    }
}
