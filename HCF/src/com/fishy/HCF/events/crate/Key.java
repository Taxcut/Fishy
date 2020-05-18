package com.fishy.hcf.events.crate;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import com.fishy.hcf.util.base.Config;

public abstract class Key {

    private String name;
    public Key(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public abstract ChatColor getColour();
    public String getDisplayName() {
        return getColour() + name;
    }
    public abstract ItemStack getItemStack();
    public void load(Config config) {
    }
    public void save(Config config) {
    }
}