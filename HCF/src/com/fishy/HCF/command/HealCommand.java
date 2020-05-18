package com.fishy.hcf.command;

import java.util.Set;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import com.fishy.hcf.util.base.BukkitUtils;
import com.google.common.collect.ImmutableSet;

import net.md_5.bungee.api.ChatColor;

public class HealCommand implements CommandExecutor {
	
    private static final Set<PotionEffectType> HEALING_REMOVEABLE_POTION_EFFECTS;

    static {
        HEALING_REMOVEABLE_POTION_EFFECTS = (Set) ImmutableSet.of((Object) PotionEffectType.SLOW, (Object) PotionEffectType.SLOW_DIGGING, (Object) PotionEffectType.POISON, (Object) PotionEffectType.WEAKNESS);
    }

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "You are not a player");
			return true;
		}
		if (args.length == 0) {
			sender.sendMessage(ChatColor.RED + "Usage: /heal <player>");
			return true;
		}
		if (args.length > 1) {
			sender.sendMessage(ChatColor.RED + "Usage: /heal <player>");
			return true;
		}
		Player target = BukkitUtils.playerWithNameOrUUID(args[0]);
		if (target == null) {
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cPlayer with name or UUID with '" + args[0] + "' not found."));
			return true;
		}
		target.setHealth(20);
        target.setFoodLevel(20);
        for (PotionEffectType type : HealCommand.HEALING_REMOVEABLE_POTION_EFFECTS) {
            target.removePotionEffect(type);
        }
        sender.sendMessage(ChatColor.WHITE + "Healed " + target.getName() + ".");
		return true;
	}

}
