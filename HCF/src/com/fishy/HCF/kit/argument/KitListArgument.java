package com.fishy.hcf.kit.argument;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.fishy.hcf.HCF;
import com.fishy.hcf.kit.Kit;
import com.fishy.hcf.util.base.BukkitUtils;
import com.fishy.hcf.util.base.command.CommandArgument;

import net.minecraft.util.org.apache.commons.lang3.StringUtils;

public class KitListArgument extends CommandArgument
{
    private final HCF plugin;

    public KitListArgument(final HCF plugin) {
        super("list", "Lists all current kits");
        this.plugin = plugin;
        this.permission = "hcf.command.kit." + this.getName();
    }

    @Override
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName();
    }

	@Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        final List<Kit> kits = this.plugin.getKitManager().getKits();
        if (kits.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "No kits have been defined.");
            return true;
        }
        final List<String> kitNames = new ArrayList<>();
        for (final Kit kit : kits) {
            final String permission = kit.getPermissionNode();
            if (permission == null || sender.hasPermission(permission)) {
                final ChatColor color = ChatColor.GREEN;
                kitNames.add(color + kit.getDisplayName());
            }
        }
        final String kitList = StringUtils.join(kitNames, ChatColor.GRAY + ", ");
        sender.sendMessage(ChatColor.DARK_GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
        sender.sendMessage(ChatColor.DARK_AQUA + ChatColor.BOLD.toString() + "Kit List" + ChatColor.GREEN + "[" + kitNames.size() + '/' + kits.size() + "]");
        sender.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
        sender.sendMessage(ChatColor.GRAY + "[" + ChatColor.RED + kitList + ChatColor.GRAY + ']');
        sender.sendMessage(ChatColor.DARK_GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
        return true;
    }

    @Override
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        return Collections.emptyList();
    }
}
