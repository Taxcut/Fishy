package com.fishy.hcf.command;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpposCommand implements CommandExecutor {

    public boolean isDouble(String s) {
        try{
            Double.parseDouble(s);
            return true;
        } catch(NumberFormatException e) {
            return false;
        }
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

        if (cmd.getName().equalsIgnoreCase("tppos")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED +"You do not have permission to use this command.");
                return true;
            }

            Player p = (Player) sender;

            if (args.length < 3 || args.length > 3) {
                sender.sendMessage("�cUsage: /tppos [x] [y] [z]");
                return true;
            }
            if (!isDouble(args[0]) || !isDouble(args[1]) || !isDouble(args[2])) {
                sender.sendMessage("�cUsage: /tppos [x] [y] [z]");
                return true;
            }
            double x = Double.parseDouble(args[0])+0.5;
            double y = Double.parseDouble(args[1])+0.5;
            double z = Double.parseDouble(args[2])+0.5;
            Location loc = new Location(p.getWorld(), x, y, z, p.getLocation().getYaw(), p.getLocation().getPitch());
            p.teleport(loc);
            p.sendMessage("�fYou have been teleported to the coordinates " + x + ", " + y + ", " + z + ".");
            return true;
        }
        return false;
    }
}
