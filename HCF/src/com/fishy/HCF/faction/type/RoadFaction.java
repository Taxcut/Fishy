package com.fishy.hcf.faction.type;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import com.fishy.hcf.HCF;
import com.fishy.hcf.faction.claim.Claim;
import com.fishy.hcf.util.base.BukkitUtils;

/**
 * Represents the {@link RoadFaction}.
 * <p>
 * TODO: Needs cleanup
 */
public class RoadFaction extends ClaimableFaction implements ConfigurationSerializable {

    // The difference the roads will end from the border.
    public static final int ROAD_EDGE_DIFF = 1000;

    // Represents how wide the roads are.
    public static final int ROAD_WIDTH_LEFT = 3;
    public static final int ROAD_WIDTH_RIGHT = 3;

    // The minimum and maximum heights for roads.
    public static final int ROAD_MIN_HEIGHT = 0; //50 'this allowed people to claim below the roads, temp disabled;
    public static final int ROAD_MAX_HEIGHT = 256; //80 'this allowed people to claim above the roads, temp disabled;

    public RoadFaction(String name) {
        super(name);
    }

    public static class NorthRoadFaction extends RoadFaction implements ConfigurationSerializable {

        public NorthRoadFaction() {
            super("NorthRoad");
            for (World world : Bukkit.getWorlds()) {
                World.Environment environment = world.getEnvironment();
                if (environment != World.Environment.THE_END) {
                    int roadLength = 4000;
                    int offset = (environment == Environment.NORMAL ? 50 : 25) + 1;
                    addClaim(new Claim(this,
                            new Location(world, -ROAD_WIDTH_LEFT, ROAD_MIN_HEIGHT, -offset),
                            new Location(world, ROAD_WIDTH_RIGHT, ROAD_MAX_HEIGHT, -roadLength + ROAD_EDGE_DIFF)), null);
                }
            }
        }

        public NorthRoadFaction(Map<String, Object> map) {
            super(map);
        }
    }

    public static class EastRoadFaction extends RoadFaction implements ConfigurationSerializable {

        public EastRoadFaction() {
            super("EastRoad");
            for (World world : Bukkit.getWorlds()) {
                World.Environment environment = world.getEnvironment();
                if (environment != World.Environment.THE_END) {
                    int roadLength = 4000;
                    int offset = (environment == Environment.NORMAL ? 50 : 25) + 1;
                    addClaim(new Claim(this,
                            new Location(world, offset, ROAD_MIN_HEIGHT, -ROAD_WIDTH_LEFT),
                            new Location(world, roadLength - ROAD_EDGE_DIFF, ROAD_MAX_HEIGHT, ROAD_WIDTH_RIGHT)), null);
                }
            }
        }

        public EastRoadFaction(Map<String, Object> map) {
            super(map);
        }
    }

    public static class SouthRoadFaction extends RoadFaction implements ConfigurationSerializable {

        public SouthRoadFaction() {
            super("SouthRoad");
            for (World world : Bukkit.getWorlds()) {
                World.Environment environment = world.getEnvironment();
                if (environment != World.Environment.THE_END) {
                    int roadLength = 4000;
                    int offset = (environment == Environment.NORMAL ? 50 : 25) + 1;
                    addClaim(new Claim(this,
                            new Location(world, ROAD_WIDTH_LEFT, ROAD_MIN_HEIGHT, offset),
                            new Location(world, -ROAD_WIDTH_RIGHT, ROAD_MAX_HEIGHT, roadLength - ROAD_EDGE_DIFF)), null);
                }
            }
        }

        public SouthRoadFaction(Map<String, Object> map) {
            super(map);
        }
    }

    public static class WestRoadFaction extends RoadFaction implements ConfigurationSerializable {

        public WestRoadFaction() {
            super("WestRoad");
            for (World world : Bukkit.getWorlds()) {
                World.Environment environment = world.getEnvironment();
                if (environment != World.Environment.THE_END) {
                    int roadLength = 4000;
                    int offset = (environment == Environment.NORMAL ? 50 : 25) + 1;
                    addClaim(new Claim(this,
                            new Location(world, -offset, ROAD_MIN_HEIGHT, ROAD_WIDTH_LEFT),
                            new Location(world, -roadLength + ROAD_EDGE_DIFF, ROAD_MAX_HEIGHT, -ROAD_WIDTH_RIGHT)), null);
                }
            }
        }

        public WestRoadFaction(Map<String, Object> map) {
            super(map);
        }
    }

    public RoadFaction(Map<String, Object> map) {
        super(map);
    }

    @Override
    public String getDisplayName(CommandSender sender) {
        String formattedName = getName().replace("st", "st ").replace("th", "th ");
        return ChatColor.valueOf(HCF.getPlugin().getConfig().getString("ROAD_COLOR")) + formattedName;
    }

    @Override
    public void printDetails(CommandSender sender) {
        sender.sendMessage(ChatColor.DARK_GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
        sender.sendMessage(' ' + getDisplayName(sender));
        sender.sendMessage(ChatColor.WHITE + "  Location: " + ChatColor.RED + "None");
        sender.sendMessage(ChatColor.DARK_GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
    }
}
