package com.fishy.hcf.deathban.lives.argument;

import com.fishy.hcf.HCF;
import com.fishy.hcf.util.base.JavaUtils;
import com.fishy.hcf.util.base.command.CommandArgument;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class LivesGiveArgument extends CommandArgument {

    private final HCF plugin;

    public LivesGiveArgument(HCF plugin) {
        super("give", "Give lives to a player");
        this.plugin = plugin;
        this.aliases = new String[]{"transfer", "send", "pay", "add"};
        this.permission = "hcf.command.lives.argument." + getName();
    }

    @Override
    public String getUsage(String label) {
        return '/' + label + ' ' + getName() + " <playerName> <amount>";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
            return true;
        }

        Integer amount = JavaUtils.tryParseInt(args[2]);

        if (amount == null) {
            sender.sendMessage(ChatColor.RED + "'" + args[2] + "' is not a number.");
            return true;
        }

        if (amount <= 0) {
            sender.sendMessage(ChatColor.RED + "The amount of lives must be positive.");
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]); //TODO: breaking

        if (!target.hasPlayedBefore() && !target.isOnline()) {
            sender.sendMessage(ChatColor.RED + "Player '" + ChatColor.RED + args[1] + ChatColor.RED + "' not found.");
            return true;
        }

        Player onlineTarget = target.getPlayer();

        if (sender instanceof Player) {
            Player player = (Player) sender;
            int ownedLives = plugin.getDeathbanManager().getLives(player.getUniqueId());

            if (amount > ownedLives) {
                sender.sendMessage(ChatColor.RED + "You tried to give " + target.getName() + ' ' + amount + " lives, but you only have " + ownedLives + '.');
                return true;
            }

            plugin.getDeathbanManager().takeLives(player.getUniqueId(), amount);
        }

        plugin.getDeathbanManager().addLives(target.getUniqueId(), amount);
        sender.sendMessage(ChatColor.WHITE + "You have sent " + ChatColor.GREEN + target.getName() + ChatColor.WHITE + ' ' + amount + ' ' + (amount > 1 ? "lives" : "life") + ChatColor.WHITE + '.');
        if (onlineTarget != null) {
            onlineTarget.sendMessage(ChatColor.GRAY + sender.getName() + ChatColor.WHITE + " has sent you " + ChatColor.GREEN + amount + ' ' + (amount > 1 ? "lives" : "life") + ChatColor.WHITE + '.');
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return args.length == 2 ? null : Collections.<String>emptyList();
    }
}
