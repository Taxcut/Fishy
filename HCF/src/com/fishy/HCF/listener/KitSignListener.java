package com.fishy.hcf.listener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.fishy.hcf.HCF;
import com.fishy.hcf.util.ItemConstants;
import com.google.common.collect.ImmutableMap;

import net.minecraft.util.org.apache.commons.lang3.time.DurationFormatUtils;

public class KitSignListener implements Listener {

	private static final ImmutableMap<Enchantment, Integer> ARMOR_ENCH = new ImmutableMap.Builder<Enchantment, Integer>()
			.put(Enchantment.PROTECTION_ENVIRONMENTAL, 1).put(Enchantment.DURABILITY, 4).build();
	private static final ItemStack HEALTH_POT, SPEED_POT, SWORD, PEARL, STEAK, FEATHER, IRON_INGOT, GHAST_TEAR, SUGAR,
			BLAZE_POWDER, BOW, PICKAXE, AXE, SPADE, GOLD_SWORD, ARROW;
	private static final ItemStack[] ARMOR;

	static {
		PICKAXE = new ItemStack(Material.DIAMOND_PICKAXE, 1);
		AXE = new ItemStack(Material.DIAMOND_AXE, 1);
		SPADE = new ItemStack(Material.DIAMOND_SPADE, 1);
		ARROW = new ItemStack(Material.ARROW, 1);
		BOW = new ItemStack(Material.BOW, 1);
		FEATHER = new ItemStack(Material.FEATHER, 64);
		IRON_INGOT = new ItemStack(Material.IRON_INGOT, 64);
		GHAST_TEAR = new ItemStack(Material.GHAST_TEAR, 16);
		SUGAR = new ItemStack(Material.SUGAR, 64);
		BLAZE_POWDER = new ItemStack(Material.BLAZE_POWDER, 32);
		STEAK = new ItemStack(Material.COOKED_BEEF, 64);
		PEARL = new ItemStack(Material.ENDER_PEARL, 16);
		HEALTH_POT = new ItemStack(Material.POTION, 1, (short) 16421);
		SPEED_POT = new ItemStack(Material.POTION, 1, (short) 8226);
		SWORD = new ItemStack(Material.DIAMOND_SWORD, 1);
		ARMOR = new ItemStack[4];
		GOLD_SWORD = new ItemStack(Material.GOLD_SWORD, 1);
		
		ARMOR[0] = new ItemStack(Material.DIAMOND_HELMET, 1);
		ARMOR[1] = new ItemStack(Material.DIAMOND_CHESTPLATE, 1);
		ARMOR[2] = new ItemStack(Material.DIAMOND_LEGGINGS, 1);
		ARMOR[3] = new ItemStack(Material.DIAMOND_BOOTS, 1);
		ARMOR[0].addUnsafeEnchantment(Enchantment.PROTECTION_FALL, 4);

		ARMOR[0].addUnsafeEnchantments(ARMOR_ENCH);
		ARMOR[1].addUnsafeEnchantments(ARMOR_ENCH);
		ARMOR[2].addUnsafeEnchantments(ARMOR_ENCH);
		ARMOR[3].addUnsafeEnchantments(ARMOR_ENCH);

		SWORD.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);
		SWORD.addUnsafeEnchantment(Enchantment.DURABILITY, 4);

		BOW.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 3);
		BOW.addUnsafeEnchantment(Enchantment.DURABILITY, 4);
		BOW.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);
		BOW.addUnsafeEnchantment(Enchantment.ARROW_FIRE, 1);

		AXE.addUnsafeEnchantment(Enchantment.DIG_SPEED, 5);
		AXE.addUnsafeEnchantment(Enchantment.DURABILITY, 4);

		PICKAXE.addUnsafeEnchantment(Enchantment.DIG_SPEED, 5);
		PICKAXE.addUnsafeEnchantment(Enchantment.DURABILITY, 4);

		SPADE.addUnsafeEnchantment(Enchantment.DIG_SPEED, 5);
		SPADE.addUnsafeEnchantment(Enchantment.DURABILITY, 4);
	}

	private final Map<UUID, Long> lastClicked;

	public KitSignListener(HCF plugin) {
		this.lastClicked = new HashMap<>();
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Block block = event.getClickedBlock();
		Action action = event.getAction();

		if (action == Action.RIGHT_CLICK_BLOCK
				&& (block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST)) {
			BlockState state = event.getClickedBlock().getState();
			if (state instanceof Sign) {
				Sign sign = (Sign) state;
				if (sign.getLine(1).equalsIgnoreCase(ChatColor.DARK_RED + "- Kit -")) {
					if (lastClicked.containsKey(player.getUniqueId())) {
						long lastClicked = this.lastClicked.get(player.getUniqueId());
						long remaining = (lastClicked + TimeUnit.SECONDS.toMillis(5L)) - System.currentTimeMillis();
						
						if (remaining > 0) {
							player.sendMessage(ChatColor.RED + "You must wait another " + ChatColor.BOLD + DurationFormatUtils.formatDuration(remaining, "s") + "s" + ChatColor.RED + " before using this again.");
							return;
						}
					}

					String kit = sign.getLine(2).toLowerCase();

					this.giveKit(player, kit);
					lastClicked.put(player.getUniqueId(), System.currentTimeMillis());
				}
				event.setCancelled(true);
			}
		}

	}

	@EventHandler
	public void onSign(SignChangeEvent e) {
		Player p = e.getPlayer();
		Sign sign = (Sign) e.getBlock().getState();
		String[] lines = sign.getLines();
		if (p.hasPermission("staff.createkitmapsign") && lines[1].equalsIgnoreCase("[Kit]")) {
			p.sendMessage(ChatColor.translateAlternateColorCodes('&',
					"&aSuccesfully created a &a" + ChatColor.stripColor(e.getLine(2)) + "&a kit sign!"));
			e.setLine(1, ChatColor.DARK_RED + "- Kit -");
			e.setLine(2, ChatColor.BLACK + e.getLine(1));
		}
	}

	public void giveKit(Player player, String name) {
		for (int i = 0; i < player.getInventory().getSize(); i++)
			player.getInventory().setItem(i, HEALTH_POT);

		switch (name) {
		case "diamond":
			ARMOR[3].setType(Material.DIAMOND_HELMET);
			ARMOR[2].setType(Material.DIAMOND_CHESTPLATE);
			ARMOR[1].setType(Material.DIAMOND_LEGGINGS);
			ARMOR[0].setType(Material.DIAMOND_BOOTS);

			player.getInventory().setItem(1, PEARL);
			player.getInventory().setItem(2, SPEED_POT);
			player.getInventory().setItem(16, SPEED_POT);
			player.getInventory().setItem(17, SPEED_POT);
			player.getInventory().setItem(26, SPEED_POT);
			player.getInventory().setItem(35, SPEED_POT);
			break;
		case "rogue":
			ARMOR[3].setType(Material.CHAINMAIL_HELMET);
			ARMOR[2].setType(Material.CHAINMAIL_CHESTPLATE);
			ARMOR[1].setType(Material.CHAINMAIL_LEGGINGS);
			ARMOR[0].setType(Material.CHAINMAIL_BOOTS);
			
			player.getInventory().setItem(1, PEARL);
			player.getInventory().setItem(2, GOLD_SWORD);
			player.getInventory().setItem(3, GOLD_SWORD);
			player.getInventory().setItem(7, SUGAR);
			player.getInventory().setItem(9, GOLD_SWORD);
			player.getInventory().setItem(10, GOLD_SWORD);
			player.getInventory().setItem(18, GOLD_SWORD);
			player.getInventory().setItem(19, GOLD_SWORD);
			break;
		case "bard":
			ARMOR[3].setType(Material.GOLD_HELMET);
			ARMOR[2].setType(Material.GOLD_CHESTPLATE);
			ARMOR[1].setType(Material.GOLD_LEGGINGS);
			ARMOR[0].setType(Material.GOLD_BOOTS);

			player.getInventory().setItem(1, PEARL);
			player.getInventory().setItem(3, FEATHER);
			player.getInventory().setItem(4, IRON_INGOT);
			player.getInventory().setItem(5, GHAST_TEAR);
			player.getInventory().setItem(6, SUGAR);
			player.getInventory().setItem(7, BLAZE_POWDER);
			break;
		case "reaper":
			ARMOR[3].setType(Material.GOLD_HELMET);
			ARMOR[2].setType(Material.IRON_CHESTPLATE);
			ARMOR[1].setType(Material.IRON_LEGGINGS);
			ARMOR[0].setType(Material.GOLD_BOOTS);
			player.getInventory().setItem(1, PEARL);
			player.getInventory().setItem(2, SUGAR);
			break;
		case "builder":
			ARMOR[3].setType(Material.IRON_HELMET);
			ARMOR[2].setType(Material.IRON_CHESTPLATE);
			ARMOR[1].setType(Material.IRON_LEGGINGS);
			ARMOR[0].setType(Material.IRON_BOOTS);

			player.getInventory().setItem(1, PEARL);
			player.getInventory().setItem(0, SWORD);
			player.getInventory().setItem(8, STEAK);
			player.getInventory().setItem(1, PICKAXE);
			player.getInventory().setItem(2, AXE);
			player.getInventory().setItem(3, SPADE);
			player.getInventory().setItem(4, ItemConstants.FENCE_GATE);
			player.getInventory().setItem(5, ItemConstants.WOOD_PLATE);
			player.getInventory().setItem(6, ItemConstants.STAINED_CLAY);
			player.getInventory().setItem(7, ItemConstants.WHITE_STAINED_CLAY); 
			player.getInventory().setItem(8, STEAK);
			player.getInventory().setItem(9, ItemConstants.OAK_WOOD);
			player.getInventory().setItem(10, ItemConstants.OAK_WOOD);
			player.getInventory().setItem(11, ItemConstants.GRASS);
			player.getInventory().setItem(12, ItemConstants.LAPIS);
			player.getInventory().setItem(13, ItemConstants.CHISELED_QUARTZ_BLOCK);
			player.getInventory().setItem(14, ItemConstants.QUARTZ_BLOCK);
			player.getInventory().setItem(15, ItemConstants.STONE_BRICK);
			player.getInventory().setItem(16, ItemConstants.COBBLESTONE);
			player.getInventory().setItem(17, ItemConstants.STONE);
			player.getInventory().setItem(18, ItemConstants.GLOWSTONE);
			player.getInventory().setItem(19, ItemConstants.WOOD_STAIRS);
			player.getInventory().setItem(20, ItemConstants.COBBLESTONE_STAIRS);
			player.getInventory().setItem(21, ItemConstants.COBBLESTONE_STAIRS);
			player.getInventory().setItem(22, ItemConstants.WATER_BUCKET);
			player.getInventory().setItem(23, ItemConstants.GLASS);
			player.getInventory().setItem(24, ItemConstants.YELLOW_STAINED_GLASS);
			player.getInventory().setItem(25, ItemConstants.BLUE_STAINED_GLASS);
			player.getInventory().setItem(26, ItemConstants.WHITE_STAINED_GLASS);
			player.getInventory().setItem(27, ItemConstants.STICKY_PISTON);
			player.getInventory().setItem(28, ItemConstants.PISTON);
			player.getInventory().setItem(29, ItemConstants.REPEATER);
			player.getInventory().setItem(30, ItemConstants.COMPARATOR);
			player.getInventory().setItem(31, ItemConstants.HOPPER);
			player.getInventory().setItem(32, ItemConstants.REDSTONE);
			player.getInventory().setItem(33, ItemConstants.REDSTONE);
			player.getInventory().setItem(34, ItemConstants.STRING);
			player.getInventory().setItem(35, ItemConstants.STRING);
			break;
		case "archer":
			ARMOR[3].setType(Material.LEATHER_HELMET);
			ARMOR[2].setType(Material.LEATHER_CHESTPLATE);
			ARMOR[1].setType(Material.LEATHER_LEGGINGS);
			ARMOR[0].setType(Material.LEATHER_BOOTS);

			player.getInventory().setItem(1, PEARL);
			player.getInventory().setItem(2, BOW);
			player.getInventory().setItem(7, SUGAR);
			player.getInventory().setItem(16, ARROW);
			player.getInventory().setItem(17, FEATHER);
			break;
		default:
			player.sendMessage(ChatColor.RED + "Kit not found!");
			player.getInventory().clear();
			return;
		}
		player.getInventory().setArmorContents(ARMOR);
		player.getInventory().setItem(0, SWORD);
		player.getInventory().setItem(8, STEAK);

		player.updateInventory();
	}

}
