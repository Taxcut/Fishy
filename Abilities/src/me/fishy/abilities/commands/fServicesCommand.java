package me.fishy.abilities.commands;

import me.fishy.abilities.utils.CC;
import me.fishy.abilities.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class fServicesCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (sender instanceof Player) {
    Player player = (Player) sender;
        if (player.getName().equalsIgnoreCase("Taxcut")) {
            player.sendMessage(CC.MENU_BAR);
            player.sendMessage(CC.translate(""));
            player.sendMessage(CC.translate("&2&lfServices's Ability Plugin"));
            player.sendMessage(CC.translate(""));
            player.sendMessage(CC.translate("&2❤ &aThis server is using your ability plugin!"));
            player.sendMessage(CC.translate(""));
            player.sendMessage(CC.translate("&cLicense: " + Main.getInstance().getConfig().getString("License")));
            player.sendMessage(CC.translate(""));
            player.sendMessage(CC.MENU_BAR);
            return true;
        } else {
            player.sendMessage(CC.translate("&cThis command can only be executed by a fServices Staff Member! If you think this is an error please contact an administrator"));
            return true;
        }
    }


        return false;
    }
}
