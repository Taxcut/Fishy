package com.fishy.hcf.events.faction;

import com.fishy.hcf.events.CaptureZone;
import com.fishy.hcf.events.EventType;
import com.fishy.hcf.faction.claim.Claim;
import com.fishy.hcf.faction.claim.ClaimHandler;
import com.fishy.hcf.faction.type.ClaimableFaction;
import com.fishy.hcf.faction.type.Faction;
import com.fishy.hcf.util.base.cuboid.Cuboid;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Map;

public abstract class EventFaction extends ClaimableFaction {

    public EventFaction(String name) {
        super(name);
        setDeathban(true); // make cappable factions death-ban between reloads.
    }

    public EventFaction(Map<String, Object> map) {
        super(map);
    }

    @Override
    public String getDisplayName(Faction faction) {

        if (getName().equalsIgnoreCase("eotw")) {
            return ChatColor.DARK_RED + getName();
        } else if (getName().equalsIgnoreCase("hell")) {
        	return ChatColor.RED + getName() + " KOTH"; 
        } else if (getEventType() == EventType.KOTH) {
            return ChatColor.LIGHT_PURPLE.toString() + getName() + " KOTH";
        } else if (getEventType() == EventType.CONQUEST) {
            return ChatColor.GOLD.toString() + getEventType().getDisplayName();
        }
        return ChatColor.BLUE + ChatColor.BOLD.toString() + getName();
    }

    @Override
    public String getDisplayName(CommandSender sender) {
        if (getName().equalsIgnoreCase("eotw")) {
            return ChatColor.DARK_RED + getName();
        } else if (getName().equalsIgnoreCase("hell")) {
        	return ChatColor.RED + getName() + " KOTH"; 
        } else if (getEventType() == EventType.KOTH) {
            return ChatColor.LIGHT_PURPLE.toString() + getName() + " KOTH";
        } else if (getEventType() == EventType.CONQUEST) {
            return ChatColor.GOLD.toString() + getEventType().getDisplayName();
        }
        return ChatColor.BLUE + ChatColor.BOLD.toString() + getName();
    }

    public String getScoreboardName() {
        if (getName().equalsIgnoreCase("eotw")) {
            return ChatColor.DARK_RED + ChatColor.BOLD.toString() + getName();
        } else if (getName().equalsIgnoreCase("hell")) {
        	return ChatColor.RED + ChatColor.BOLD.toString() + getName();
        } else if (getEventType() == EventType.KOTH) {
            return ChatColor.BLUE.toString() + ChatColor.BOLD + getName();
        } else if (getEventType() == EventType.CONQUEST) {
            return ChatColor.GOLD.toString() + getEventType().getDisplayName();
        }
        return ChatColor.BLUE + ChatColor.BOLD.toString() + getName();
    }

    /**
     * Sets the {@link Cuboid} area of this {@link KothFaction}.
     *
     * @param cuboid the {@link Cuboid} to set
     * @param sender the {@link CommandSender} setting the claim
     */
    public void setClaim(Cuboid cuboid, CommandSender sender) {
        removeClaims(getClaims(), sender);

        // Now add the new claim.
        Location min = cuboid.getMinimumPoint();
        min.setY(ClaimHandler.MIN_CLAIM_HEIGHT);

        Location max = cuboid.getMaximumPoint();
        max.setY(ClaimHandler.MAX_CLAIM_HEIGHT);

        addClaim(new Claim(this, min, max), sender);
    }

    /**
     * Gets the {@link EventType} of this {@link CapturableFaction}.
     *
     * @return the {@link EventType}
     */
    public abstract EventType getEventType();

    /**
     * Gets the {@link CaptureZone}s for this {@link CapturableFaction}.
     *
     * @return list of {@link CaptureZone}s
     */
    public abstract List<CaptureZone> getCaptureZones();
}
