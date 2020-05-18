package com.fishy.hcf.listener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.material.MaterialData;

import com.fishy.hcf.HCF;
import com.fishy.hcf.faction.struct.Role;
import com.fishy.hcf.faction.type.Faction;
import com.fishy.hcf.faction.type.PlayerFaction;
import com.fishy.hcf.util.NameUtils;

import net.minecraft.util.com.google.common.collect.ImmutableList;
import net.minecraft.util.com.google.common.collect.Lists;

public class SubclaimListener implements Listener {

    private enum SubclaimType {

        LEADER(ImmutableList.of("[Leader]", Role.LEADER.getAstrix()), ChatColor.DARK_RED + "Leader", "Leader"),

        CAPTAIN(ImmutableList.of("[Captain]", "[Officer]", Role.CAPTAIN.getAstrix()),
                ChatColor.DARK_GREEN + "Captain", "Captain"),

        MEMBER(ImmutableList.of("[Private]", "[Personal]", "[Subclaim]", "[Member]"),
                ChatColor.GREEN + "Subclaim", "Member");

        private final List<String> aliases;
        private final String outputText;
        private final String displayName;

        SubclaimType(List<String> aliases, String outputText, String displayName) {
            this.aliases = aliases;
            this.outputText = outputText;
            this.displayName = displayName;
        }
    }

    private static final int MAX_SIGN_LINE_CHARS = 16;
    private static final BlockFace[] SIGN_FACES = new BlockFace[]{
            BlockFace.NORTH,
            BlockFace.EAST,
            BlockFace.SOUTH,
            BlockFace.WEST,
            BlockFace.UP
    };

    private final HCF plugin;

    public SubclaimListener(HCF plugin) {
        this.plugin = plugin;
    }

    private SubclaimType getSubclaimType(String value, boolean creating) {
        if (creating) {
            value = value.toUpperCase();
        }

        for (SubclaimType type : SubclaimType.values()) {
            if (creating) {
                if (type.aliases.contains(value)) {
                    return type;
                }
            } else {
                if (type.outputText.equals(value)) {
                    return type;
                }
            }
        }

        return null;
    }

    private boolean isSubclaimable(Block block) {
        Material type = block.getType();
        return type == Material.FENCE_GATE || type == Material.TRAP_DOOR || block.getState() instanceof InventoryHolder;
    }

    private SubclaimType getSubclaimType(Sign sign, boolean creating) {
        SubclaimType subclaimType = getSubclaimType(sign.getLine(0), creating);
        return subclaimType;
    }

    private SubclaimType getSubclaimType(Block block, boolean creating) {
        if (isSubclaimable(block)) {
            Collection<Sign> attachedSigns = getAttachedSigns(block);
            for (Sign attachedSign : attachedSigns) {
                SubclaimType subclaimType = getSubclaimType(attachedSign, creating);
                if (subclaimType != null) {
                    return subclaimType;
                }
            }
        }

        return null;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onSignChange(SignChangeEvent event) {
        Block block = event.getBlock();
        MaterialData materialData = block.getState().getData();
        if (materialData instanceof org.bukkit.material.Sign) {
            org.bukkit.material.Sign sign = (org.bukkit.material.Sign) materialData;
            Block attachedBlock = block.getRelative(sign.getAttachedFace());
            if (isSubclaimable(attachedBlock)) {
                Player player = event.getPlayer();
                PlayerFaction playerFaction = plugin.getFactionManager().getPlayerFaction(player);
                if (playerFaction == null) {
                    return; // only allow officers to create Subclaims
                }

                Faction factionAt = plugin.getFactionManager().getFactionAt(block.getLocation());
                if (playerFaction == factionAt) {
                    SubclaimType subclaimType = getSubclaimType(attachedBlock, false);
                    if (subclaimType != null) {
                        player.sendMessage(ChatColor.RED + "There is already a " + subclaimType.displayName + " subclaim sign on this " + NameUtils.getPrettyName(attachedBlock.getType().name()) + '.');
                        return;
                    }

                    String[] lines = event.getLines();
                    subclaimType = getSubclaimType(lines[0], true);
                    if (subclaimType == null) {
                        return;
                    }

                    List<String> memberList = null;
                    if (subclaimType == SubclaimType.MEMBER) {
                        memberList = new ArrayList<>(3);
                        for (int i = 1; i < lines.length; i++) {
                            String line = lines[i];
                            if (StringUtils.isNotBlank(line)) {
                                memberList.add(line);
                            }
                        }

                        if (memberList.isEmpty()) {
                            player.sendMessage(ChatColor.RED + "Subclaim signs need to have at least 1 player name inserted.");
                            return;
                        }
                    } else if (subclaimType == SubclaimType.CAPTAIN) {
                        if (playerFaction.getMember(player).getRole() == Role.MEMBER) {
                            player.sendMessage(ChatColor.RED + "Only faction officers can create captain subclaimed objects.");
                            return;
                        }

                        // Clear the other lines.
                        event.setLine(1, null);
                        event.setLine(2, null);
                        event.setLine(3, null);
                    } else if (subclaimType == SubclaimType.LEADER) {
                        if (playerFaction.getMember(player).getRole() != Role.LEADER) {
                            player.sendMessage(ChatColor.RED + "Only faction leaders can create leader subclaimed objects.");
                            return;
                        }

                        // Clear the other lines.
                        event.setLine(1, null);
                        event.setLine(2, null);
                        event.setLine(3, null);
                    }

                    // Finalise the subclaim.
                    event.setLine(0, subclaimType.outputText);
                    StringBuilder builder = new StringBuilder(ChatColor.GREEN + player.getName() +
                            ChatColor.YELLOW + " has created a subclaim on block type " + ChatColor.AQUA + NameUtils.getPrettyName(attachedBlock.getType().name()) +
                            ChatColor.YELLOW + " at " + ChatColor.WHITE + '(' + attachedBlock.getX() + ", " + attachedBlock.getZ() + ')' + ChatColor.YELLOW + " for ");

                    if (subclaimType == SubclaimType.LEADER) {
                        builder.append("leaders");
                    } else if (subclaimType == SubclaimType.CAPTAIN) {
                        builder.append("captains");
                    } else if (memberList != null) { // Should never be null, but best safe; SubclaimType.PRIVATE
                        builder.append("members ").append(ChatColor.RED).append('[');
                        builder.append(memberList.stream().filter(string -> playerFaction.getMember(string) != null).collect(Collectors.joining(", "))).append("]");
                    }

                    playerFaction.broadcast(builder.toString());
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {
        if (plugin.getEotwHandler().isEndOfTheWorld()) {
            return;
        }

        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE && player.hasPermission(ProtectionListener.PROTECTION_BYPASS_PERMISSION)) {
            return;
        }

        Block block = event.getBlock();
        BlockState state = block.getState();

        Block subclaimObjectBlock = null;
        if (state instanceof Sign) {
            Sign sign = (Sign) state;
            MaterialData signData = sign.getData();
            if (signData instanceof org.bukkit.material.Sign) {
                org.bukkit.material.Sign materialSign = (org.bukkit.material.Sign) signData;
                subclaimObjectBlock = block.getRelative(materialSign.getAttachedFace());
            }
        } else {
            subclaimObjectBlock = block;
        }

        if (subclaimObjectBlock != null && !checkSubclaimIntegrity(player, subclaimObjectBlock)) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You cannot break this subclaimed " + NameUtils.getPrettyName(subclaimObjectBlock.getType().name()) + '.');
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onInventoryMoveItem(InventoryMoveItemEvent event) {
        if (plugin.getEotwHandler().isEndOfTheWorld()) {
            return;
        }

        // Have to do this hackery since Bukkit doesn't
        // provide an API for us to do this
        InventoryHolder holder = event.getSource().getHolder();
        Collection<Block> sourceBlocks;
        if (holder instanceof Chest) {
            sourceBlocks = Collections.singletonList(((Chest) holder).getBlock());
        } else if (holder instanceof DoubleChest) {
            DoubleChest doubleChest = (DoubleChest) holder;
            sourceBlocks = Lists.newArrayList(((Chest) doubleChest.getLeftSide()).getBlock(), ((Chest) doubleChest.getRightSide()).getBlock());
        } else {
            return;
        }

        for (Block block : sourceBlocks) {
            if (getSubclaimType(block, false) != null) {
                event.setCancelled(true);
                break;
            }
        }
    }

    private String getShortenedName(String originalName) {
        if (originalName.length() >= MAX_SIGN_LINE_CHARS) {
            originalName = originalName.substring(0, MAX_SIGN_LINE_CHARS);
        }

        return originalName;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Player player = event.getPlayer();
            if (player.getGameMode() == GameMode.CREATIVE && player.hasPermission(ProtectionListener.PROTECTION_BYPASS_PERMISSION)) {
                return;
            }

            if (plugin.getEotwHandler().isEndOfTheWorld()) {
                return;
            }

            Block block = event.getClickedBlock();
            if (!checkSubclaimIntegrity(player, block)) {
                event.setUseInteractedBlock(Event.Result.DENY);
                player.sendMessage(ChatColor.RED + "You do not have access to this subclaimed " + NameUtils.getPrettyName(block.getType().name()) + '.');
            }
        }
    }

    private boolean checkSubclaimIntegrity(Player player, Block subclaimObject) {
        if (!isSubclaimable(subclaimObject)) {
            return true; // Not even subclaimed.
        }

        PlayerFaction playerFaction = plugin.getFactionManager().getPlayerFaction(player);
        if (playerFaction == null || playerFaction.isRaidable()) {
            return true; // If the faction is raidable, just allow it.
        }

        Role role = playerFaction.getMember(player).getRole();
        if (role == Role.LEADER) {
            return true; // Let leaders open regardless.
        }

        if (playerFaction != plugin.getFactionManager().getFactionAt(subclaimObject)) {
            return true; // Let enemies be able to open
        }

        Collection<Sign> attachedSigns = getAttachedSigns(subclaimObject);
        if (attachedSigns.isEmpty()) {
            return true;
        }

        boolean flag = true;
        final String playerName = this.getShortenedName(player.getName());
        for (final Sign attachedSign : attachedSigns) {
            final SubclaimType subclaimType = this.getSubclaimType(attachedSign, false);
            if (subclaimType == null) {
                continue;
            }
            if (subclaimType == SubclaimType.LEADER) {
            	if (role != Role.LEADER) {//&& role != Role.COLEADER) {
            		flag = false;
            		continue;
            	}
            }
            if (subclaimType == SubclaimType.CAPTAIN) {
                if (role != Role.MEMBER) {
                    return true;
                }
                flag = false;
            }
            else {
                if (subclaimType != SubclaimType.MEMBER) {
                    continue;
                }
                if (role == Role.CAPTAIN) {
                    return true;
                }
                for (final String line : attachedSign.getLines()) {
                    if (line.equalsIgnoreCase(playerName)) {
                        return true;
                    }
                }
                flag = false;
            }
        }

        return flag;
    }

    public Collection<Sign> getAttachedSigns(Block block) {
        LinkedHashSet<Sign> results = new LinkedHashSet<>();
        getSignsAround(block, results);

        BlockState state = block.getState();
        if (state instanceof Chest) {
            Inventory chestInventory = ((Chest) state).getInventory();
            if (chestInventory instanceof DoubleChestInventory) {
                DoubleChest doubleChest = ((DoubleChestInventory) chestInventory).getHolder();
                Block left = ((Chest) doubleChest.getLeftSide()).getBlock();
                Block right = ((Chest) doubleChest.getRightSide()).getBlock();
                getSignsAround(left.equals(block) ? right : left, results);
            }
        }

        return results;
    }
    private Set<Sign> getSignsAround(Block block, LinkedHashSet<Sign> results) {
        for (BlockFace face : SIGN_FACES) {
            Block relative = block.getRelative(face);
            BlockState relativeState = relative.getState();
            if (relativeState instanceof Sign) {
                org.bukkit.material.Sign materialSign = (org.bukkit.material.Sign) relativeState.getData();
                if (relative.getRelative(materialSign.getAttachedFace()).equals(block)) {
                    results.add((Sign) relative.getState());
                }
            }
        }

        return results;
    }
}