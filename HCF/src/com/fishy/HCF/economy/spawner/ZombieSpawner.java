package com.fishy.hcf.economy.spawner;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Optional;

public class ZombieSpawner {

    public static final Material SPAWNER = Material.MOB_SPAWNER;
    public static final String SPAWNER_NAME = ChatColor.GREEN.toString() + "Spawner";
    private final ItemStack stack;

    public ZombieSpawner() {
        this.stack = new ItemStack(SPAWNER, 1);
    }

    public Optional<ItemStack> toItemStack() {

        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(SPAWNER_NAME);
        meta.setLore(Arrays.asList(String.format(ChatColor.WHITE + "Zombie")));

        stack.setItemMeta(meta);

        return Optional.of(stack);
    }

    public ItemStack getItemIfPresent() {
        Optional<ItemStack> optional = toItemStack();
        return optional.isPresent() ? optional.get() : new ItemStack(Material.AIR, 1);
    }
}
