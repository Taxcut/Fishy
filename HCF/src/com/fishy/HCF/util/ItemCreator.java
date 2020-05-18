package com.fishy.hcf.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import lombok.Getter;

@Getter
public class ItemCreator {

	private String displayName;
	private Material type;
	private List<String> lore;
	private short data;
	private int amount;
	private final Map<Enchantment, Integer> enchantments;
	private boolean glow;
	private boolean loreBars;

	public ItemCreator(String displayName, Material type, List<String> lore, short data, int amount,
			Map<Enchantment, Integer> enchantments) {
		this.displayName = displayName;
		this.type = type;
		this.lore = lore;
		this.data = data;
		this.amount = amount;
		this.enchantments = enchantments;
	}

	public ItemCreator(String displayName, Material type, List<String> lore, short data, int amount) {
		this.displayName = displayName;
		this.type = type;
		this.lore = lore;
		this.data = data;
		this.amount = amount;
		this.enchantments = new HashMap<>();
	}

	public ItemCreator(String displayName, Material type, List<String> lore, short data, int amount, boolean loreBars) {
		this.displayName = displayName;
		this.type = type;
		this.lore = lore;
		this.data = data;
		this.loreBars = loreBars;
		this.amount = amount;
		this.enchantments = new HashMap<>();
	}

	public ItemCreator setGlow(boolean glow) {
		this.glow = glow;

		return this;
	}

	public ItemStack build() {
		ItemStack item = null;

		if (data != -1)
			item = new ItemStack(type, amount, data);
		else
			item = new ItemStack(type, amount);

		if (!enchantments.isEmpty())
			item.addUnsafeEnchantments(enchantments);

		ItemMeta meta = item.getItemMeta();

		meta.setLore(loreBars ? new ArrayList<String>() {
			{
				add(ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString()
				+ org.apache.commons.lang.StringUtils.repeat("-", 45));
				addAll(lore);
				add(ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString()
				+ org.apache.commons.lang.StringUtils.repeat("-", 45));
			}
		} : lore);
		meta.setDisplayName(displayName);
		item.setItemMeta(meta);

		return item;
	}

}
