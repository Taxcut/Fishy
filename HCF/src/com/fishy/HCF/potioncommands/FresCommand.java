package com.fishy.hcf.potioncommands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.fishy.hcf.util.CC;

public class FresCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (!player.hasPermission("hcf.command.fres")) {
                player.sendMessage(ChatColor.RED + "No Permission.");
                return true;
            }
            if (player.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE)) {
                player.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
                player.sendMessage(CC.translate("&5� &dYour &5&lFire Resistance&d has been &cdisabled."));
                return true;
            }
            if (!player.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE)) {
                player.addPotionEffect((new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0)));
                player.sendMessage(CC.translate("&5� &dYour &5&lFire Resistance&d has been &aenabled."));
                return true;
            }
        }

        return false;
    }

}
