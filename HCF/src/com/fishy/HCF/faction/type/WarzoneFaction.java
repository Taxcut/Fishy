package com.fishy.hcf.faction.type;

import java.util.Map;

import org.bukkit.command.CommandSender;

import com.fishy.hcf.HCF;

import net.md_5.bungee.api.ChatColor;

/**
 * Represents the {@link WarzoneFaction}.
 */
public class WarzoneFaction extends Faction {

    public WarzoneFaction() {
        super("Warzone");
    }

    public WarzoneFaction(Map<String, Object> map) {
        super(map);
    }

    @Override
    public String getDisplayName(CommandSender sender) {
        return ChatColor.valueOf(HCF.getPlugin().getConfig().getString("WARZONE_COLOR")) + getName();
    }
}
