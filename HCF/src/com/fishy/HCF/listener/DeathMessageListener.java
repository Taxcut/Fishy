package com.fishy.hcf.listener;

import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.fishy.hcf.HCF;
import com.google.common.base.Preconditions;

import net.minecraft.server.v1_7_R4.EntityLiving;

public class DeathMessageListener implements Listener{
	private HCF plugin;

	public DeathMessageListener(HCF plugin) {
		this.plugin = plugin;
	}
	
    public static String replaceLast(String text, String regex, String replacement) {
        return text.replaceFirst("(?s)" + regex + "(?!.*?" + regex + ')', replacement);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        String originalMessage = event.getDeathMessage();
        Player killer = event.getEntity().getKiller();
        Entity entityKiller = killer;
        Player player = event.getEntity();
        event.setDeathMessage(getDeathMessage(originalMessage, player, entityKiller));
    }



    private Entity getKiller(PlayerDeathEvent event) {
        EntityLiving lastAttacker = ((CraftPlayer) event.getEntity()).getHandle().aX();
        return lastAttacker == null ? null : lastAttacker.getBukkitEntity();
    }

    private String getDeathMessage(String input, org.bukkit.entity.Entity entity, org.bukkit.entity.Entity killer) {
        input = input.replaceFirst("\\[", ChatColor.GRAY + "[" + ChatColor.GRAY);
        input = replaceLast(input, "]", ChatColor.GRAY + "]" + ChatColor.GRAY);
        if (entity != null) {
            input = input.replaceFirst("(?i)" + getEntityName(entity), ChatColor.RED + getDisplayName(entity) + ChatColor.WHITE);
        }
        if ((killer != null) && ((entity == null) || (!killer.equals(entity)))) {
            input = input.replaceFirst("(?i)" + getEntityName(killer), ChatColor.RED + getDisplayName(killer) +  ChatColor.WHITE);
        }
        return input;
    }

    private String getStattrackMessage(org.bukkit.entity.Entity entity, org.bukkit.entity.Entity killer) {
        return ChatColor.RED + getEntityName(entity) + ChatColor.WHITE + " was slain by " + ChatColor.RED + getEntityName(killer);
    }

    private String getEntityName(org.bukkit.entity.Entity entity) {
        Preconditions.checkNotNull(entity, "Entity cannot be null");
        return (entity instanceof Player) ? ((Player) entity).getName() : ((CraftEntity) entity).getHandle().getName();
    }

    private String getDisplayName(org.bukkit.entity.Entity entity) {
        Preconditions.checkNotNull(entity, "Entity cannot be null");
        if ((entity instanceof Player)) {
            Player player = (Player) entity;
            return player.getName() + ChatColor.DARK_RED + '[' + this.plugin.getUserManager().getUser(player.getUniqueId()).getKills() + ']';
        }
        return WordUtils.capitalizeFully(entity.getType().name().replace('_', ' '));
    }
}