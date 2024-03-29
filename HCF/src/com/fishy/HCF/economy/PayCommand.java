package com.fishy.hcf.economy;

import com.fishy.hcf.HCF;
import com.fishy.hcf.util.base.JavaUtils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class PayCommand implements CommandExecutor, TabCompleter {

    private final HCF plugin;

    public PayCommand(HCF plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <playerName> <amount>");
            return true;
        }

        Integer amount = JavaUtils.tryParseInt(args[1]);

        if (amount == null) {
            sender.sendMessage(ChatColor.RED + "'" + args[1] + "' is not a valid number.");
            return true;
        }

        if (amount <= 0) {
            sender.sendMessage(ChatColor.RED + "You must send money in positive quantities.");
            return true;
        }

        // Calculate the senders balance here.
        Player senderPlayer = sender instanceof Player ? (Player) sender : null;
        int senderBalance = senderPlayer != null ? plugin.getEconomyManager().getBalance(senderPlayer.getUniqueId()) : 1024;

        if (senderBalance < amount) {
            sender.sendMessage(ChatColor.RED + "You tried to pay " + EconomyManager.ECONOMY_SYMBOL + amount + ", but you only have " +
                    EconomyManager.ECONOMY_SYMBOL + senderBalance + " in your bank account.");

            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]); //TODO: breaking

        if (sender.equals(target)) {
            sender.sendMessage(ChatColor.RED + "You cannot send money to yourself.");
            return true;
        }

        Player targetPlayer = target.getPlayer();

        if (!target.hasPlayedBefore() && targetPlayer == null) {
            sender.sendMessage(ChatColor.RED + "Player '" + ChatColor.RED + args[0] + ChatColor.RED + "' not found.");
            return true;
        }

        if (targetPlayer == null) return false; // won't happen, IntelliJ compiler won't ignore

        // Make the money transactions.
        if (senderPlayer != null) plugin.getEconomyManager().subtractBalance(senderPlayer.getUniqueId(), amount);
        plugin.getEconomyManager().addBalance(targetPlayer.getUniqueId(), amount);

        targetPlayer.sendMessage(ChatColor.WHITE + sender.getName() + " has sent you " + ChatColor.GREEN + EconomyManager.ECONOMY_SYMBOL + amount + ChatColor.WHITE + '.');
        sender.sendMessage(ChatColor.WHITE + "You have sent " + ChatColor.GREEN + EconomyManager.ECONOMY_SYMBOL + amount + ChatColor.WHITE + " to " + target.getName() + '.');
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return args.length == 1 ? null : Collections.<String>emptyList();
    }
}
