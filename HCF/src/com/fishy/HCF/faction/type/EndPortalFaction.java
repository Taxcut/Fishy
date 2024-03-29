package com.fishy.hcf.faction.type;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import com.fishy.hcf.faction.claim.Claim;

import net.md_5.bungee.api.ChatColor;

/**
 * Represents the {@link EndPortalFaction}.
 */
public class EndPortalFaction extends ClaimableFaction implements ConfigurationSerializable {

    public EndPortalFaction() {
        super("EndPortal");

        World overworld = Bukkit.getWorld("world");
        int maxHeight = overworld.getMaxHeight();
        int min = 730;
        int max = 770;

        // North East (++)
        addClaim(new Claim(this, new Location(overworld, min, 0, min), new Location(overworld, max, maxHeight, max)), null);

        // South West (--)
        addClaim(new Claim(this, new Location(overworld, -max, maxHeight, -max), new Location(overworld, -min, 0, -min)), null);

        // North West (-+)
        addClaim(new Claim(this, new Location(overworld, -max, 0, min), new Location(overworld, -min, maxHeight, max)), null);

        // South East (+-)
        addClaim(new Claim(this, new Location(overworld, min, 0, -max), new Location(overworld, max, maxHeight, -min)), null);

        this.safezone = true;
    }

    public EndPortalFaction(Map<String, Object> map) {
        super(map);
    }

    @Override
    public boolean isDeathban() {
        return false;
    }
    
    @Override
    public String getDisplayName(CommandSender sender) {
        String formattedName = getName().replace("EndPortal", "End Portal");
        return ChatColor.GREEN + formattedName;
    }
}
