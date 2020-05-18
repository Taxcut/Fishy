package com.fishy.hcf.kit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fishy.hcf.kit.event.KitRenameEvent;
import com.fishy.hcf.util.base.Config;
import com.fishy.hcf.util.base.GenericUtils;

import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.ChatPaginator;

import com.fishy.hcf.HCF;
import com.google.common.collect.Lists;

public class FlatFileKitManager implements KitManager, Listener {
	private final Map<String, Kit> kitNameMap;
	private final Map<UUID, Kit> kitUUIDMap;
	private final HCF plugin;
	private Config config;
	private List<Kit> kits;

	public FlatFileKitManager(final HCF plugin) {
		this.kits = new ArrayList<>();
		this.kitNameMap = new CaseInsensitiveMap<>();
		this.kitUUIDMap = new HashMap<>();
		this.plugin = plugin;
		this.reloadKitData();
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onKitRename(final KitRenameEvent event) {
		this.kitNameMap.remove(event.getOldName());
		this.kitNameMap.put(event.getNewName(), event.getKit());
	}

	@Override
	public List<Kit> getKits() {
		return this.kits;
	}

	@Override
	public Kit getKit(final UUID uuid) {
		return this.kitUUIDMap.get(uuid);
	}

	@Override
	public Kit getKit(final String id) {
		return this.kitNameMap.get(id);
	}

	@Override
	public boolean containsKit(final Kit kit) {
		return this.kits.contains(kit);
	}

	@Override
	public void createKit(final Kit kit) {
		if (this.kits.add(kit)) {
			this.kitNameMap.put(kit.getName(), kit);
			this.kitUUIDMap.put(kit.getUniqueID(), kit);
		}
	}

	@Override
	public void removeKit(final Kit kit) {
		if (this.kits.remove(kit)) {
			this.kitNameMap.remove(kit.getName());
			this.kitUUIDMap.remove(kit.getUniqueID());
		}
	}

	@Override
	public Inventory getGui(final Player player) {
		final UUID uuid = player.getUniqueId();
		final Inventory inventory = Bukkit.createInventory(player, (this.kits.size() + 9 - 1) / 9 * 9,
				ChatColor.GREEN + "Kit Selector");
		for (final Kit kit : this.kits) {
			final ItemStack stack = kit.getImage();
			final String description = kit.getDescription();
			final String kitPermission = kit.getPermissionNode();
			List<String> lore;
			if (kitPermission == null || player.hasPermission(kitPermission)) {
				lore = new ArrayList<>();
				if (kit.isEnabled()) {
					if (kit.getDelayMillis() > 0L) {
						lore.add(ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + "--------------------");
						lore.add(ChatColor.YELLOW + "Cooldown will end in " + ChatColor.GOLD + " � " + ChatColor.WHITE
								+ kit.getDelayWords());
					}
				} else {
					lore.add(ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + "--------------------");
					lore.add(ChatColor.RED + "This kit is disabled");
				}
				final int maxUses = kit.getMaximumUses();
				if (maxUses != Integer.MAX_VALUE) {
					lore.add(ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + "--------------------");
					lore.add(ChatColor.YELLOW + ChatColor.BOLD.toString() + "Useable " + ChatColor.WHITE
							+ this.plugin.getUserManager().getUser(uuid).getKitUses(kit) + " out of " + maxUses);
				}
				if (description != null) {
					lore.add(ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + "--------------------");
					for (final String part : ChatPaginator.wordWrap(description, 24)) {
						lore.add(ChatColor.YELLOW + ChatColor.BOLD.toString() + "Description " + ChatColor.GOLD + " � " + ChatColor.WHITE
								+ part);
						lore.add(ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + "--------------------");
					}
				}
			} else {
				lore = Lists.newArrayList(
						ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + "--------------------");
				lore = Lists.newArrayList(ChatColor.RED + "You do not own this kit");
			}
			final ItemStack cloned = stack.clone();
			final ItemMeta meta = cloned.getItemMeta();
			meta.setDisplayName(ChatColor.GREEN + kit.getName());
			meta.setLore(lore);
			cloned.setItemMeta(meta);
			inventory.addItem(new ItemStack[] { cloned });
		}
		return inventory;
	}

	@Override
	public void reloadKitData() {
		this.config = new Config(this.plugin, "kits");
		final Object object = this.config.get("kits");
		if (object instanceof List) {
			this.kits = GenericUtils.createList(object, Kit.class);
			for (final Kit kit : this.kits) {
				this.kitNameMap.put(kit.getName(), kit);
				this.kitUUIDMap.put(kit.getUniqueID(), kit);
			}
		}
	}

	@Override
	public void saveKitData() {
		this.config.set("kits", this.kits);
		this.config.save();
	}
}
