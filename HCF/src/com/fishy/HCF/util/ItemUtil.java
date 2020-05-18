package com.fishy.hcf.util;

import java.util.Collections;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import net.md_5.bungee.api.ChatColor;

public class ItemUtil {

	public static final ItemStack FILLER = new ItemCreator(ChatColor.WHITE.toString(), Material.STAINED_GLASS_PANE,
			Collections.emptyList(), (short) -1, 1).build();
	
}
