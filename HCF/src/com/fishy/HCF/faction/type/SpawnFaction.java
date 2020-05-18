package com.fishy.hcf.faction.type;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import com.fishy.hcf.faction.claim.Claim;

import net.md_5.bungee.api.ChatColor;

/**
 * Represents the {@link SpawnFaction}.
 */
public class SpawnFaction extends ClaimableFaction implements ConfigurationSerializable {

    public SpawnFaction() {
        super("Spawn");

        this.safezone = true;
        addClaim(new Claim(this, new Location(Bukkit.getWorld("world"), 50, 0, 50), new Location(Bukkit.getWorld("world"), -50, Bukkit.getWorld("world").getMaxHeight(), -50)), null);
        addClaim(new Claim(this, new Location(Bukkit.getWorld("world_nether"), 25, 0, 25), new Location(Bukkit.getWorld("world_nether"), -25, Bukkit.getWorld("world_nether").getMaxHeight(), -25)), null);
        addClaim(new Claim(this, new Location(Bukkit.getWorld("world_the_end"), 15, 0, 15), new Location(Bukkit.getWorld("world_the_end"), -15, Bukkit.getWorld("world_the_end").getMaxHeight(), -15)), null);
    }

    public SpawnFaction(Map<String, Object> map) {
        super(map);
    }

    @Override
    public boolean isDeathban() {
        return false;
    }
    
    @Override
    public String getDisplayName(CommandSender sender) {
        return ChatColor.GREEN + getName();
    }

}
