package com.fishy.hcf.kit.argument;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.fishy.hcf.HCF;
import com.fishy.hcf.kit.Kit;
import com.fishy.hcf.kit.event.KitCreateEvent;
import com.fishy.hcf.util.base.JavaUtils;
import com.fishy.hcf.util.base.command.CommandArgument;

public class KitCreateArgument extends CommandArgument
{
    private final HCF plugin;

    public KitCreateArgument(final HCF plugin) {
        super("create", "Creates a kit");
        this.plugin = plugin;
        this.permission = "crazyenchantments.gkitz." + this.getName();
    }

    @Override
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <kitName> [kitDescription]";
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players may create kits.");
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
            return true;
        }
        if (!JavaUtils.isAlphanumeric(args[1])) {
            sender.sendMessage(ChatColor.GRAY + "Kit names may only be alphanumeric.");
            return true;
        }
        Kit kit = this.plugin.getKitManager().getKit(args[1]);
        if (kit != null) {
            sender.sendMessage(ChatColor.RED + "There is already a kit named " + args[1] + '.');
            return true;
        }
        final Player player = (Player)sender;
        kit = new Kit(args[1], (args.length >= 3) ? args[2] : null, player.getInventory(), player.getActivePotionEffects());
        final KitCreateEvent event = new KitCreateEvent(kit);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return true;
        }
        this.plugin.getKitManager().createKit(kit);
        sender.sendMessage(ChatColor.YELLOW + "Created kit '" + kit.getDisplayName() + "'.");
        return true;
    }
}
