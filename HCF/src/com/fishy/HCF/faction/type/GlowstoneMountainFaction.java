package com.fishy.hcf.faction.type;

import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import com.fishy.hcf.faction.claim.Claim;
import com.fishy.hcf.util.base.BukkitUtils;

public class GlowstoneMountainFaction extends ClaimableFaction implements ConfigurationSerializable {

    public GlowstoneMountainFaction() {
        super("Glowstone");

        this.safezone = false;
    }

    public GlowstoneMountainFaction(Map<String, Object> map) {
        super(map);
    }

    @Override
    public String getDisplayName(CommandSender sender) {
        return ChatColor.GOLD + "Glowstone Mountain";
    }

    @Override
    public void printDetails(CommandSender sender) {
        sender.sendMessage(ChatColor.DARK_GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
        sender.sendMessage(getDisplayName(sender));

        for (Claim claim : claims) {
            Location location = claim.getCenter();
            sender.sendMessage(ChatColor.WHITE + "  Location: " + ChatColor.RED + '(' + ClaimableFaction.ENVIRONMENT_MAPPINGS.get(location.getWorld().getEnvironment()) + ", " + location.getBlockX() + " | "
                    + location.getBlockZ() + ')');
        }

        sender.sendMessage(ChatColor.DARK_GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
    }
}