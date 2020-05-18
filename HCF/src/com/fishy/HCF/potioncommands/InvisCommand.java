package com.fishy.hcf.potioncommands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.fishy.hcf.util.CC;

public class InvisCommand implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!player.hasPermission("hcf.command.invis")) {
                player.sendMessage(ChatColor.RED + "No Permission.");
                return true;
            }
            if (player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                player.removePotionEffect(PotionEffectType.INVISIBILITY);
                player.sendMessage(CC.translate("&5� &dYour &5&lInvisibility&d has been &cdisabled."));
                return true;
            }
            if (!player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                player.addPotionEffect((new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0)));
                player.sendMessage(CC.translate("&5� &dYour &5&lInvisibility&d has been &aenabled"));
                return true;
            }
        }

        return false;
    }
}
