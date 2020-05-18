package com.fishy.hcf.listener;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BrewingStand;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

import com.fishy.hcf.HCF;

import lombok.Getter;
	

/**
 * Listener that prevents the brewing of illegal {@link org.bukkit.potion.PotionEffectType}s.
 */
public class PotionLimitListener implements Listener {

    private static final int EMPTY_BREW_TIME = 400;

    private final HCF plugin;
    @Getter private static Map<PotionType, Integer> maxPotion = new HashMap<>();
    
	static {
		maxPotion.put(PotionType.INSTANT_DAMAGE, HCF.getPlugin().getConfig().getInt("POTION_LIMIT.INSTANT_DAMAGE"));
		maxPotion.put(PotionType.REGEN, HCF.getPlugin().getConfig().getInt("POTION_LIMIT.REGEN"));
		maxPotion.put(PotionType.STRENGTH, HCF.getPlugin().getConfig().getInt("POTION_LIMIT.STRENGTH"));
		maxPotion.put(PotionType.WEAKNESS, HCF.getPlugin().getConfig().getInt("POTION_LIMIT.WEAKNESS"));
		maxPotion.put(PotionType.SLOWNESS, HCF.getPlugin().getConfig().getInt("POTION_LIMIT.SLOWNESS"));
		maxPotion.put(PotionType.INVISIBILITY, HCF.getPlugin().getConfig().getInt("POTION_LIMIT.INVISIBILITY"));
		maxPotion.put(PotionType.POISON, HCF.getPlugin().getConfig().getInt("POTION_LIMIT.POISON"));
		maxPotion.put(PotionType.FIRE_RESISTANCE, HCF.getPlugin().getConfig().getInt("POTION_LIMIT.FIRE_RESISTANCE"));
		maxPotion.put(PotionType.SPEED, HCF.getPlugin().getConfig().getInt("POTION_LIMIT.SPEED"));
		maxPotion.put(PotionType.INSTANT_HEAL, HCF.getPlugin().getConfig().getInt("POTION_LIMIT.INSTANT_HEAL"));
		maxPotion.put(PotionType.NIGHT_VISION, HCF.getPlugin().getConfig().getInt("POTION_LIMIT.NIGHT_VISION"));
		maxPotion.put(PotionType.WATER_BREATHING, HCF.getPlugin().getConfig().getInt("POTION_LIMIT.WATER_BREATHING"));
		maxPotion.put(PotionType.WATER, HCF.getPlugin().getConfig().getInt("POTION_LIMIT.WATER"));
	}

    public PotionLimitListener(HCF plugin) {
        this.plugin = plugin;
        for (String key : HCF.getPlugin().getConfig().getConfigurationSection("POTION_LIMIT").getKeys(false)) {
            try {
                PotionType potion = PotionType.valueOf(key);
                getMaxPotion().put(potion, HCF.getPlugin().getConfig().getInt("POTION_LIMIT." + key));
            } catch (IllegalStateException e) {
                System.out.println("Invalid potion " + key);
                e.printStackTrace();
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBrew(BrewEvent event) {
        BrewerInventory inventory = event.getContents();
        ItemStack[] contents = inventory.getContents();
        int length = contents.length;
        ItemStack[] cloned = new ItemStack[length];
        for (int i = 0; i < length; i++) {
            ItemStack previous = contents[i];
            cloned[i] = (previous == null ? null : previous.clone());
        }

        BrewingStand stand = inventory.getHolder();
        Bukkit.getScheduler().runTask(HCF.getPlugin(), () -> {
            if (!testValidity(inventory.getContents())) {
                stand.setBrewingTime(EMPTY_BREW_TIME);
                inventory.setContents(cloned);
            }
        });
    }

    private boolean testValidity(ItemStack[] results) {
        for (ItemStack stack : results) {
            if (stack != null && stack.getType() == Material.POTION && stack.getDurability() != 0) {
                Potion potion = Potion.fromItemStack(stack);

                // Just to be safe, null check this.
                if (potion == null) {
                    continue;
                }

                // Mundane potions etc, can return a null type
                PotionType type = potion.getType();
                if (type == null) {
                    continue;
                }

                if (type == PotionType.POISON || type == PotionType.SLOWNESS || type == PotionType.WEAKNESS) {
                    if (potion.getLevel() == 2) {
                        return false;
                    }
                }

                if (type == PotionType.POISON || type == PotionType.SLOWNESS) {
                    if (potion.hasExtendedDuration()) {
                        return false;
                    }
                }

                // TODO: More configurable if 33 second splash poison is allowed
                if (type == PotionType.POISON) {
                    if (!potion.hasExtendedDuration() && potion.getLevel() == 1) {
                        continue;
                    }
                }

                if (potion.getLevel() > maxPotion.get(type)) {
                    return false;
                }

                if (maxPotion.get(type) < 0) {
                	return false;
                }
            }
        }

        return true;
    }
}
