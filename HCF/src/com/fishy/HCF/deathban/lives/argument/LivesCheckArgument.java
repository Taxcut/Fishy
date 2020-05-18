package com.fishy.hcf.deathban.lives.argument;

import com.fishy.hcf.HCF;
import com.fishy.hcf.util.base.command.CommandArgument;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
public class LivesCheckArgument extends CommandArgument {

    private final HCF plugin;

    public LivesCheckArgument(HCF plugin) {
        super("check", "Check how much lives a player has");
        this.plugin = plugin;
        this.permission = "hcf.command.lives.argument." + getName();
    }

    @Override
    public String getUsage(String label) {
        return '/' + label + ' ' + getName() + " [playerName]";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        OfflinePlayer target;
        if (args.length > 1) {
            target = Bukkit.getOfflinePlayer(args[1]); //TODO: breaking
        } else if (sender instanceof Player) {
            target = (Player) sender;
        } else {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
            return true;
        }

        if (!target.hasPlayedBefore() && !target.isOnline()) {
            sender.sendMessage(ChatColor.RED + "Player '" + ChatColor.RED + args[1] + ChatColor.RED + "' not found.");
            return true;
        }

        int targetLives = plugin.getDeathbanManager().getLives(target.getUniqueId());

        sender.sendMessage(target.getName() + ChatColor.WHITE + " has " + ChatColor.GREEN + targetLives + ChatColor.WHITE + ' ' + (targetLives == 1 ? "life" : "lives") + '.');
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return args.length == 2 ? null : Collections.<String>emptyList();
    }
}
