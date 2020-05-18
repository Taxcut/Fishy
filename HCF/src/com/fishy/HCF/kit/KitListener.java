package com.fishy.hcf.kit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import com.fishy.hcf.faction.type.Faction;
import com.fishy.hcf.faction.type.PlayerFaction;
import com.fishy.hcf.kit.event.KitApplyEvent;
import com.fishy.hcf.user.FactionUser;
import com.fishy.hcf.util.base.ParticleEffect;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.fishy.hcf.HCF;

import net.minecraft.util.org.apache.commons.lang3.time.DurationFormatUtils;

public class KitListener implements Listener
{
    private final HCF plugin;
    public static List<Inventory> previewInventory;

    public KitListener(final HCF plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClose(final InventoryCloseEvent e) {
        if (KitListener.previewInventory.contains(e.getInventory())) {
            KitListener.previewInventory.remove(e.getInventory());
            e.getInventory().clear();
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onInventoryClick(final InventoryClickEvent event) {
        if (KitListener.previewInventory.contains(event.getInventory())) {
            event.setCancelled(true);
            return;
        }
        final Inventory inventory = event.getInventory();
        if (inventory == null) {
            return;
        }
        final String title = inventory.getTitle();
        final HumanEntity humanEntity = event.getWhoClicked();
        if (title.contains("Kit Selector") && humanEntity instanceof Player) {
            event.setCancelled(true);
            if (!Objects.equals(event.getView().getTopInventory(), event.getClickedInventory())) {
                return;
            }
            final ItemStack stack = event.getCurrentItem();
            if (stack == null || !stack.hasItemMeta()) {
                return;
            }
            final ItemMeta meta = stack.getItemMeta();
            if (!meta.hasDisplayName()) {
                return;
            }
            final Player player = (Player)humanEntity;
            final String name = ChatColor.stripColor(stack.getItemMeta().getDisplayName());
            final Kit kit = this.plugin.getKitManager().getKit(name);
            if (kit == null) {
                return;
            }
            kit.applyTo(player, false, true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onKitSign(final PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            final Block block = event.getClickedBlock();
            final BlockState state = block.getState();
            if (!(state instanceof Sign)) {
                return;
            }
            final Sign sign = (Sign) state;
            final String[] lines = sign.getLines();
            if (lines.length >= 2 && lines[1].contains("Kit") && lines[1].contains(String.valueOf(ChatColor.COLOR_CHAR))) {
                final Kit kit = this.plugin.getKitManager().getKit((lines.length >= 3) ? lines[2] : null);
                if (kit == null) {
                    return;
                }
                event.setCancelled(true);
                final Player player = event.getPlayer();
                final String[] fakeLines = Arrays.copyOf(sign.getLines(), 4);
                final boolean applied = kit.applyTo(player, false, false);
                if (applied) {
                    fakeLines[0] = ChatColor.GREEN + "Successfully";
                    fakeLines[1] = ChatColor.GREEN + "equipped kit";
                    fakeLines[2] = kit.getDisplayName();
                    fakeLines[3] = "";
                } else {
                    fakeLines[0] = ChatColor.RED + "Failed to";
                    fakeLines[1] = ChatColor.RED + "equip kit";
                    fakeLines[2] = kit.getDisplayName();
                    fakeLines[3] = ChatColor.RED + "Check chat";
                }
                if (this.plugin.getSignHandler().showLines(player, sign, fakeLines, 15L, false) && applied) {
                    ParticleEffect.FIREWORK_SPARK.display(player, sign.getLocation().clone().add(0.5, 0.5, 0.5), 0.01f, 10);
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onKitApply(final KitApplyEvent event) {
        if (!event.isForce()) {
            final Player player = event.getPlayer();
            final Kit kit = event.getKit();
            if (!player.isOp() && !kit.isEnabled()) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "The " + kit.getDisplayName() + " kit is currently disabled.");
            } else {
                final String kitPermission = kit.getPermissionNode();
                if (kitPermission != null && !player.hasPermission(kitPermission)) {
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.RED + "You do not have permission to use this kit.");
                } else {
                    FactionUser factionUser = plugin.getUserManager().getUser(player.getUniqueId());
                    final long remaining = factionUser.getRemainingKitCooldown(kit);
                    if (remaining > 0L) {
                    	player.sendMessage(ChatColor.RED + "You cannot use the " + kit.getDisplayName() + " kit for " + DurationFormatUtils.formatDurationWords(remaining, true, true) + '.');
                        event.setCancelled(true);
                    	} else {
                    		final int curUses = factionUser.getKitUses(kit);
                        	final int maxUses = kit.getMaximumUses();
                            if (curUses >= maxUses && maxUses != Integer.MAX_VALUE) {
                                player.sendMessage(ChatColor.RED + "You have already used this kit " + curUses + '/' + maxUses + " times.");
                                event.setCancelled(true);
                            }
                    	}
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onKitApplyMonitor(final KitApplyEvent event) {
        if (!event.isForce()) {
            final Kit kit = event.getKit();
            final FactionUser baseUser = this.plugin.getUserManager().getUser(event.getPlayer().getUniqueId());
            baseUser.incrementKitUses(kit);
            baseUser.updateKitCooldown(kit);
        }
    }
    @EventHandler(ignoreCancelled=true, priority=EventPriority.HIGHEST)
    public void onKitApplyHigh(KitApplyEvent event) {
      Player player = event.getPlayer();
      Location location = player.getLocation();
      Faction factionAt = plugin.getFactionManager().getFactionAt(location);
      PlayerFaction playerFaction;
    }

    static {
        KitListener.previewInventory = new ArrayList<>();
    }
}
