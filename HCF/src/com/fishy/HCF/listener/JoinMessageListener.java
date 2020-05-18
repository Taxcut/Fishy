package com.fishy.hcf.listener;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.fishy.hcf.HCF;
import com.fishy.hcf.util.base.BukkitUtils;

import net.md_5.bungee.api.ChatColor;

public class JoinMessageListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        new BukkitRunnable() {
            public void run() {
                player.sendMessage(ChatColor.DARK_GRAY + ChatColor.STRIKETHROUGH.toString() + BukkitUtils.STRAIGHT_LINE_DEFAULT.substring(0, 38));
                player.sendMessage(ChatColor.WHITE + "Welcome to " + ChatColor.DARK_PURPLE + ChatColor.BOLD.toString() + HCF.getPlugin().getConfig().getString("TITLE") + (HCF.getPlugin().getConfig().getBoolean("KITMAP") ? "" : ChatColor.WHITE + " Map " + HCF.getPlugin().getConfig().getInt("MAPNUM")));
                player.sendMessage(ChatColor.LIGHT_PURPLE + " * " + ChatColor.WHITE + "Faction Size: " + ChatColor.GRAY + HCF.getPlugin().getConfig().getInt("FACTION.MAXPLAYERS") + " Man / " + (HCF.getPlugin().getConfig().getInt("FACTION.MAXALLIES") == 0 ? "No Allies" : HCF.getPlugin().getConfig().getInt("FACTION.MAXALLIES") == 1 ? "1 Ally" : HCF.getPlugin().getConfig().getInt("FACTION.MAXALLIES") + " Allies"));
                player.sendMessage(ChatColor.LIGHT_PURPLE + " * " + ChatColor.WHITE + "Map Kit: " + ChatColor.GRAY + "Protection " + HCF.getPlugin().getConfig().getInt("ENCHANTMENT_LIMIT.PROTECTION_ENVIRONMENTAL") + " /" + ChatColor.GRAY + " Sharpness " + HCF.getPlugin().getConfig().getInt("ENCHANTMENT_LIMIT.DAMAGE_ALL"));
                player.sendMessage(ChatColor.LIGHT_PURPLE + " * " + ChatColor.WHITE + "Blocking Up: " + (HCF.getPlugin().getConfig().getBoolean("ANTICOMBATPLACE") ? ChatColor.RED + "Disabled" : ChatColor.GREEN  + "Enabled"));
                player.sendMessage(ChatColor.LIGHT_PURPLE + " * " + ChatColor.WHITE + "Teamspeak: " + ChatColor.GRAY + "ts.desirepvp.net");
                player.sendMessage(ChatColor.LIGHT_PURPLE + " * " + ChatColor.WHITE + "Store: " + ChatColor.GRAY + "shop.desirepvp.net");
                player.sendMessage(ChatColor.LIGHT_PURPLE + " * " + ChatColor.WHITE + "Discord: " + ChatColor.GRAY + "desirepvp.net/discord");
                player.sendMessage(ChatColor.DARK_GRAY + ChatColor.STRIKETHROUGH.toString() + BukkitUtils.STRAIGHT_LINE_DEFAULT.substring(0, 38));
            }
        }.runTaskLaterAsynchronously(HCF.getPlugin(), 10);
    }
}
