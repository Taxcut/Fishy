package com.fishy.hcf.economy;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.fishy.hcf.HCF;
import com.fishy.base.BaseConstants;
import com.fishy.hcf.util.base.BukkitUtils;
import com.fishy.hcf.util.base.JavaUtils;

import net.minecraft.util.com.google.common.collect.ImmutableList;

public class EconomyCommand implements CommandExecutor, TabCompleter {

    private final HCF plugin;

    public EconomyCommand(HCF plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(final CommandSender sender, Command command, String label, String[] args) {
        OfflinePlayer target;
        if (args.length > 0 && sender.hasPermission(command.getPermission() + ".staff")) {
            target = BukkitUtils.offlinePlayerWithNameOrUUID(args[0]);
        } else if (sender instanceof Player) {
            target = (Player) sender;
        } else {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <playerName>");
            return true;
        }

        if (!target.hasPlayedBefore() && !target.isOnline()) {
            sender.sendMessage(String.format(BaseConstants.PLAYER_WITH_NAME_OR_UUID_NOT_FOUND, args[0]));
            return true;
        }

        UUID uuid = target.getUniqueId();
        int balance = plugin.getEconomyManager().getBalance(uuid);

        if (args.length < 2) {
            sender.sendMessage(ChatColor.WHITE + (sender.equals(target) ? "Your balance" : "Balance of "
                    + target.getName()) + " is " + ChatColor.GREEN + EconomyManager.ECONOMY_SYMBOL
                    + balance + ChatColor.WHITE + '.');

            return true;
        }

        if (args[1].equalsIgnoreCase("give") || args[1].equalsIgnoreCase("add")) {
            if (args.length < 3) {
                if (!sender.hasPermission("hcf.command.eco.staff")) {
                 sender.sendMessage(ChatColor.RED + "No permission.");
                 return true;
                }

                sender.sendMessage(ChatColor.RED + "Usage: /" + label + ' ' + target.getName() + ' '
                        + args[1] + " <amount>");
                return true;
            }

            if (!sender.hasPermission(command.getPermission() + ".staff")) {
                return true;
            }

            Integer amount = JavaUtils.tryParseInt(args[2]);

            if (amount == null) {
                sender.sendMessage(ChatColor.RED + "'" + args[2] + "' is not a valid number.");
                return true;
            }

            int newBalance = plugin.getEconomyManager().addBalance(uuid, amount);
            sender.sendMessage(new String[]{""
                    + ChatColor.WHITE
                    + "Added " + ChatColor.GREEN + EconomyManager.ECONOMY_SYMBOL + JavaUtils.format(amount) + ChatColor.WHITE + " to balance of "
                    + ChatColor.GREEN + target.getName() + ChatColor.WHITE + '.', ChatColor.WHITE + "Balance of " + ChatColor.GREEN + target.getName() + ChatColor.WHITE + " is now " + ChatColor.GREEN
                    + EconomyManager.ECONOMY_SYMBOL + newBalance + ChatColor.WHITE + '.'
            });

            return true;
        }

        if (args[1].equalsIgnoreCase("take") || args[1].equalsIgnoreCase("negate")
                || args[1].equalsIgnoreCase("minus") || args[1].equalsIgnoreCase("subtract")) {
            if (!sender.hasPermission("hcf.command.eco.staff")) {
                sender.sendMessage(ChatColor.RED + "No permission.");
                return true;
            }

            if (args.length < 3) {
                sender.sendMessage(ChatColor.RED + "Usage: /" + label + ' ' + target.getName() + ' '
                        + args[1] + " <amount>");
                return true;
            }

            Integer amount = JavaUtils.tryParseInt(args[2]);

            if (amount == null) {
                sender.sendMessage(ChatColor.RED + "'" + args[2] + "' is not a valid number.");
                return true;
            }

            int newBalance = plugin.getEconomyManager().subtractBalance(uuid, amount);

            sender.sendMessage(new String[]{""
                    + ChatColor.WHITE + "Taken " + ChatColor.GREEN + EconomyManager.ECONOMY_SYMBOL + JavaUtils.format(amount) + ChatColor.WHITE
                    + " from balance of " + ChatColor.GREEN + target.getName() + ChatColor.WHITE + '.', ChatColor.WHITE + "Balance of " + ChatColor.GREEN + target.getName()
                    + ChatColor.WHITE + " is now " + ChatColor.GREEN + EconomyManager.ECONOMY_SYMBOL + newBalance + ChatColor.WHITE + '.'
            });

            return true;
        }

        if (args[1].equalsIgnoreCase("set")) {
            if (!sender.hasPermission("hcf.command.eco.staff")) {
                sender.sendMessage(ChatColor.RED + "No permission.");
                return true;
            }
            if (args.length < 3) {
                sender.sendMessage(ChatColor.RED + "Usage: /" + label + ' ' + target.getName()
                        + ' ' + args[1] + " <amount>");
                return true;
            }

            Integer amount = JavaUtils.tryParseInt(args[2]);

            if (amount == null) {
                sender.sendMessage(ChatColor.RED + "'" + args[2] + "' is not a valid number.");
                return true;
            }

            int newBalance = plugin.getEconomyManager().setBalance(uuid, amount);
            sender.sendMessage(ChatColor.WHITE + "Set balance of " + target.getName() + " to "
                    + EconomyManager.ECONOMY_SYMBOL + JavaUtils.format(newBalance) + '.');
            return true;
        }

        sender.sendMessage(ChatColor.WHITE + (sender.equals(target) ? "Your balance" : "Balance of " +  ChatColor.GREEN + target.getName())
              + ChatColor.WHITE  + " is " + ChatColor.GREEN + EconomyManager.ECONOMY_SYMBOL + balance + ChatColor.WHITE + '.');

        return true;
    }

    private static final ImmutableList<String> COMPLETIONS_SECOND = ImmutableList.of("add", "set", "take");

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length) {
            case 1:
                List<String> results = Collections.singletonList("top");//you can use here Lists.newArrayList("top");
                if (sender.hasPermission(command.getPermission() + ".staff")) {
                    Player senderPlayer = sender instanceof Player ? (Player) sender : null;
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (senderPlayer == null) {
                            results.add(player.getName());
                        }
                    }
                }

                return BukkitUtils.getCompletions(args, results);
            case 2:
                if (!args[0].equals("top") && sender.hasPermission(command.getPermission() + ".staff")) {
                    return BukkitUtils.getCompletions(args, COMPLETIONS_SECOND);
                }
            default:
                return Collections.emptyList();
        }
    }
}
