package com.fishy.hcf.potioncommands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.fishy.hcf.util.CC;


public class EffectSpeedCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!player.hasPermission("hcf.command.speed")) {
                player.sendMessage(ChatColor.RED + "No Permission.");
                return true;
            }
            if (player.hasPotionEffect(PotionEffectType.SPEED)) {
                player.removePotionEffect(PotionEffectType.SPEED);
                player.sendMessage(CC.translate("&5� &dYour &5&lSpeed&d has been &cdisabled."));
                return true;
            }
            if (!player.hasPotionEffect(PotionEffectType.SPEED)) {
                player.addPotionEffect((new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1)));
                player.sendMessage(CC.translate("&5� &dYour &5&lSpeed&d has been &aenabled."));
                return true;
            }
        }

        return false;
    }

}
