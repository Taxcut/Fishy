package com.fishy.hcf.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemConstants {

	public static final ItemStack FENCE_GATE, WOOD_PLATE, WATER_BUCKET, DIRT, QUARTZ_BLOCK, CHISELED_QUARTZ_BLOCK,
			COBBLESTONE, STONE, GLOWSTONE, WOOD_STAIRS, COBBLESTONE_STAIRS, GLASS, HOPPER, REDSTONE, STRING,
			STAINED_CLAY, OAK_WOOD, LAPIS, GREEN_STAINED_CLAY, GREEN_STAINED_GLASS, BLUE_STAINED_GLASS, WHITE_STAINED_CLAY, GRASS, STONE_BRICK,
			YELLOW_STAINED_GLASS, WHITE_STAINED_GLASS, STICKY_PISTON, PISTON, COMPARATOR, REPEATER;

	static {
		GRASS = new ItemStack(Material.GRASS, 64);
		STONE_BRICK = new ItemStack(Material.SMOOTH_BRICK, 64);
		FENCE_GATE = new ItemStack(Material.FENCE_GATE, 64);
		WOOD_PLATE = new ItemStack(Material.WOOD_PLATE, 64);
		WATER_BUCKET = new ItemStack(Material.WATER_BUCKET, 1);
		DIRT = new ItemStack(Material.DIRT, 64);
		QUARTZ_BLOCK = new ItemStack(Material.QUARTZ_BLOCK, 64);
		COBBLESTONE = new ItemStack(Material.COBBLESTONE, 64);
		STONE = new ItemStack(Material.STONE, 64);
		GLOWSTONE = new ItemStack(Material.GLOWSTONE, 64);
		WOOD_STAIRS = new ItemStack(Material.WOOD_STAIRS, 64);
		COBBLESTONE_STAIRS = new ItemStack(Material.COBBLESTONE_STAIRS, 64);
		WHITE_STAINED_CLAY = new ItemStack(Material.STAINED_CLAY, 64, (short) 0);
		GLASS = new ItemStack(Material.GLASS, 64);
		HOPPER = new ItemStack(Material.HOPPER, 64);
		REDSTONE = new ItemStack(Material.REDSTONE, 64);
		STRING = new ItemStack(Material.STRING, 64);
		STAINED_CLAY = new ItemStack(Material.STAINED_CLAY, 64);
		OAK_WOOD = new ItemStack(17, 64);
		LAPIS = new ItemStack(Material.LAPIS_BLOCK, 64);
		CHISELED_QUARTZ_BLOCK = new ItemStack(Material.QUARTZ_BLOCK, 64, (short) 1);
		GREEN_STAINED_CLAY = new ItemStack(Material.STAINED_CLAY, 64, (short) 5);
		GREEN_STAINED_GLASS = new ItemStack(Material.STAINED_GLASS, 64, (short)13);
		BLUE_STAINED_GLASS = new ItemStack(Material.STAINED_GLASS, 64, (short)3);
		YELLOW_STAINED_GLASS = new ItemStack(Material.STAINED_GLASS, 64, (short)4);
		WHITE_STAINED_GLASS = new ItemStack(Material.STAINED_GLASS, 64, (short)0);
		STICKY_PISTON = new ItemStack(29, 64);
		PISTON = new ItemStack(33, 64);
		REPEATER = new ItemStack(356, 64);
		COMPARATOR = new ItemStack(404, 64);
	}

}
