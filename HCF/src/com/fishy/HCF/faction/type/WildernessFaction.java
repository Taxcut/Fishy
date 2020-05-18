package com.fishy.hcf.faction.type;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.command.CommandSender;

import java.util.Map;

/**
 * Represents the {@link WildernessFaction}.
 */
public class WildernessFaction extends Faction {

    public WildernessFaction() {
        super("Wilderness");
    }

    public WildernessFaction(Map<String, Object> map) {
        super(map);
    }

    @Override
    public String getDisplayName(CommandSender sender) {
        return ChatColor.DARK_GREEN + getName();
    }
}
