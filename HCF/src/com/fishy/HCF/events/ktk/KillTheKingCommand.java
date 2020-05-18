package com.fishy.hcf.events.ktk;

import com.fishy.hcf.HCF;
import com.fishy.hcf.util.Color;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KillTheKingCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;

            if (p.hasPermission("hcf.command.killtheking")) {
                if (args.length == 0) {
                    p.sendMessage(ChatColor.RED + "Invalid usage.");
                    return true;
                }

                if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("stop")) {
                        HCF.getPlugin().setActiveKTK(null);
                        Bukkit.broadcastMessage(Color.translate("&8[&9&lKillTheKing&8] &fThe event has ended."));
                        return true;
                    } else {
                        Player target = Bukkit.getServer().getPlayer(args[0]);
                        if (target != null) {
                            HCF.getPlugin().setActiveKTK(new KillTheKing(target.getUniqueId(), System.currentTimeMillis()));
                        } else {
                            p.sendMessage(ChatColor.RED + "Invalid player.");
                        }
                    }
                }
            } else {
                p.sendMessage(ChatColor.RED + "No permission.");
            }
        }
        return true;
    }
}
