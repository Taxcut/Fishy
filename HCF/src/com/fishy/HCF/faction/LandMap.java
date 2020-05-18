package com.fishy.hcf.faction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

//import com.fishy.base.BasePlugin;
import com.fishy.hcf.HCF;
import com.fishy.hcf.faction.claim.Claim;
import com.fishy.hcf.faction.claim.ClaimHandler;
import com.fishy.hcf.faction.type.Faction;
import com.fishy.hcf.faction.type.PlayerFaction;
import com.fishy.hcf.visualise.VisualBlockData;
import com.fishy.hcf.visualise.VisualType;

public class LandMap {

    private static final int FACTION_MAP_RADIUS_BLOCKS = 22;
    private static final Material[] BLACKLISK = {Material.LEAVES, Material.LEAVES_2, Material.FENCE_GATE, Material.WATER, Material.LAVA, Material.STATIONARY_LAVA, Material.STATIONARY_WATER};
    
    /**
     * Updates the {@link Faction} {@link Claim} map for a {@link Player}.
     *
     * @param player     the {@link Player} to update for
     * @param plugin     the {@link org.bukkit.plugin.java.JavaPlugin} to update for
     * @param visualType the {@link VisualType} to update for
     * @param inform     if the {@link VisualType} should be informed
     * @return true if their are {@link Claim}s to update the map for
     */
    public static boolean updateMap(Player player, HCF plugin, VisualType visualType, boolean inform) {
        Location location = player.getLocation();
        World world = player.getWorld();
        int locationX = location.getBlockX();
        int locationZ = location.getBlockZ();

        int minimumX = locationX - FACTION_MAP_RADIUS_BLOCKS;
        int minimumZ = locationZ - FACTION_MAP_RADIUS_BLOCKS;
        int maximumX = locationX + FACTION_MAP_RADIUS_BLOCKS;
        int maximumZ = locationZ + FACTION_MAP_RADIUS_BLOCKS;

        final Set<Claim> board = new LinkedHashSet<>();
        final boolean subclaimBased;
        if (visualType == VisualType.SUBCLAIM_MAP) {
            subclaimBased = true;
        } else if (visualType == VisualType.CLAIM_MAP) {
            subclaimBased = false;
        } else {
            player.sendMessage(ChatColor.RED + "Not supported: " + visualType.name().toLowerCase() + '.');
            return false;
        }

        for (int x = minimumX; x <= maximumX; x++) {
            for (int z = minimumZ; z <= maximumZ; z++) {
                Claim claim = plugin.getFactionManager().getClaimAt(world, x, z);
                if (claim != null) {
                    if (subclaimBased) {
                        board.addAll(claim.getSubclaims());
                    } else {
                        board.add(claim);
                    }
                }
            }
        }

        if (board.isEmpty()) {
            player.sendMessage(ChatColor.RED + "No claims are in your visual range to display.");
            return false;
        }

        for (Claim claim : board) {
            int maxHeight = Math.min(world.getMaxHeight(), ClaimHandler.MAX_CLAIM_HEIGHT);
            Location[] corners = claim.getCornerLocations();
            List<Location> shown = new ArrayList<>(maxHeight * corners.length);
            for (Location corner : corners) {
                for (int y = 0; y < maxHeight; y++) {
                    shown.add(world.getBlockAt(corner.getBlockX(), y, corner.getBlockZ()).getLocation());
                }
            }

            Map<Location, VisualBlockData> dataMap = plugin.getVisualiseHandler().generate(player, shown, visualType, true);
            if (dataMap.isEmpty()) continue;
            String materialName = HCF.getPlugin().getItemDb().getName(new ItemStack(dataMap.entrySet().iterator().next().getValue().getItemType(), 1));

            if (inform) {
                player.sendMessage(ChatColor.YELLOW + claim.getFaction().getDisplayName(player) + ChatColor.WHITE + " owns land " + ChatColor.WHITE + claim.getName() +
                        ChatColor.GRAY + " (displayed with " + materialName + ')' + ChatColor.YELLOW + '.');
            }
        }

        return true;
    }

    /**
     * Finds the nearest safe {@link Location} from a given position.
     *
     * @param player       the {@link Player} to find for
     * @param origin       the {@link Location} to begin searching at
     * @param searchRadius the radius to search for
     * @return the nearest safe {@link Location} from origin
     */
    public static Location getNearestSafePosition(Player player, Location origin, int searchRadius) {
        return getNearestSafePosition(player, origin, searchRadius, false);
    }
    
    public static Location getNearestSafePosition(Player player, Location origin, int searchRadius, boolean stuck) {
        FactionManager factionManager = HCF.getPlugin().getFactionManager();
        Faction playerFaction = factionManager.getPlayerFaction(player.getUniqueId());
        int max = (HCF.getPlugin().getConfig().getBoolean("KITMAP") ? 1000 : 3000);
        int originalX = Math.max(Math.min(origin.getBlockX(), max), -max);
        int originalZ = Math.max(Math.min(origin.getBlockZ(), max), -max);
        int minX = Math.max(originalX - searchRadius, -max) - originalX;
        int maxX = Math.min(originalX + searchRadius, max) - originalX;
        int minZ = Math.max(originalZ - searchRadius, -max) - originalZ;
        int maxZ = Math.min(originalZ + searchRadius, max) - originalZ;
        for (int x = 0; x < searchRadius; x++) {
            if (x > maxX || -x < minX) {
                continue;
            }
            for (int z = 0; z < searchRadius; z++) {
                if (z > maxZ || -z < minZ) {
                    continue;
                }
                Location atPos = origin.clone().add(x, 0.0D, z);
                Faction factionAtPos = factionManager.getFactionAt(atPos);
                if (factionAtPos == null || (!stuck && playerFaction != null && playerFaction.equals(factionAtPos)) || !(factionAtPos instanceof PlayerFaction)) {
                    Location safe = getSafeLocation(origin.getWorld(), atPos.getBlockX(), atPos.getBlockZ());
                    if (safe != null) {
                        return safe.add(0.5, 0.5, 0.5);
                    }
                }
                Location atNeg = origin.clone().add(x, 0.0D, z);
                Faction factionAtNeg = factionManager.getFactionAt(atNeg);
                if (factionAtNeg == null || (!stuck && playerFaction != null && playerFaction.equals(factionAtNeg)) || !(factionAtNeg instanceof PlayerFaction)) {
                    Location safe = getSafeLocation(origin.getWorld(), atNeg.getBlockX(), atNeg.getBlockZ());
                    if (safe != null) {
                        return safe.add(0.5, 0.5, 0.5);
                    }
                }
            }
        }
        return null;
    }
    
    private static Location getSafeLocation(World world, int x, int z) {
        Block highest = world.getHighestBlockAt(x, z);
        Material type = highest.getType();
        if (Arrays.asList(BLACKLISK).contains(type)) {
            return null;
        }
        while (!type.isSolid()) {
            if (highest.getY() <= 1 || Arrays.asList(BLACKLISK).contains(type)) {
                return null;
            }
            highest = highest.getRelative(BlockFace.DOWN);
            type = highest.getType();
        }
        return highest.getRelative(BlockFace.UP).getLocation();
    }
}
