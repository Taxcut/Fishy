package com.fishy.hcf.kit;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.fishy.hcf.HCF;
import com.fishy.hcf.kit.argument.KitApplyArgument;
import com.fishy.hcf.kit.argument.KitCreateArgument;
import com.fishy.hcf.kit.argument.KitDeleteArgument;
import com.fishy.hcf.kit.argument.KitDisableArgument;
import com.fishy.hcf.kit.argument.KitGuiArgument;
import com.fishy.hcf.kit.argument.KitListArgument;
import com.fishy.hcf.kit.argument.KitPreviewArgument;
import com.fishy.hcf.kit.argument.KitRenameArgument;
import com.fishy.hcf.kit.argument.KitSetDelayArgument;
import com.fishy.hcf.kit.argument.KitSetDescriptionArgument;
import com.fishy.hcf.kit.argument.KitSetImageArgument;
import com.fishy.hcf.kit.argument.KitSetIndexArgument;
import com.fishy.hcf.kit.argument.KitSetItemsArgument;
import com.fishy.hcf.kit.argument.KitSetMaxUsesArgument;
import com.fishy.hcf.kit.argument.KitSetMinPlaytimeArgument;
import com.fishy.hcf.util.base.BukkitUtils;
import com.fishy.hcf.util.base.command.ArgumentExecutor;
import com.fishy.hcf.util.base.command.CommandArgument;
import com.fishy.hcf.util.base.command.CommandWrapper;

public class KitExecutor extends ArgumentExecutor
{
    private final HCF plugin;

    public KitExecutor(final HCF plugin) {
        super("kit");
        this.plugin = plugin;
        this.addArgument(new KitApplyArgument(plugin));
        this.addArgument(new KitCreateArgument(plugin));
        this.addArgument(new KitDeleteArgument(plugin));
        this.addArgument(new KitSetDescriptionArgument(plugin));
        this.addArgument(new KitDisableArgument(plugin));
        this.addArgument(new KitGuiArgument(plugin));
        this.addArgument(new KitListArgument(plugin));
        this.addArgument(new KitPreviewArgument(plugin));
        this.addArgument(new KitRenameArgument(plugin));
        this.addArgument(new KitSetDelayArgument(plugin));
        this.addArgument(new KitSetImageArgument(plugin));
        this.addArgument(new KitSetIndexArgument(plugin));
        this.addArgument(new KitSetItemsArgument(plugin));
        this.addArgument(new KitSetMaxUsesArgument(plugin));
        this.addArgument(new KitSetMinPlaytimeArgument(plugin));
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length < 1) {
            CommandWrapper.printUsage(sender, label, this.arguments);
            sender.sendMessage(ChatColor.GRAY + "/" + label + " <kitName> " + "- " + "Applies a kit.");
            return true;
        }
        final CommandArgument argument = this.getArgument(args[0]);
        final String permission = (argument == null) ? null : argument.getPermission();
        if (argument == null || (permission != null && !sender.hasPermission(permission))) {
            final Kit kit = this.plugin.getKitManager().getKit(args[0]);
            if (sender instanceof Player && kit != null) {
                final String kitPermission = kit.getPermissionNode();
                if (kitPermission == null || sender.hasPermission(kitPermission)) {
                    final Player player = (Player)sender;
                    kit.applyTo(player, false, true);
                    return true;
                }
            }
            sender.sendMessage(ChatColor.RED + "Kit or command " + args[0] + " not found.");
            return true;
        }
        argument.onCommand(sender, command, label, args);
        return true;
    }

    @Override
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length != 1) {
            return super.onTabComplete(sender, command, label, args);
        }
        List<String> previous = super.onTabComplete(sender, command, label, args);
        final List<String> kitNames = new ArrayList<>();
        for (final Kit kit : this.plugin.getKitManager().getKits()) {
            final String permission = kit.getPermissionNode();
            if (permission == null || sender.hasPermission(permission)) {
                kitNames.add(kit.getName());
            }
        }
        if (previous == null || previous.isEmpty()) {
            previous = kitNames;
        }
        else {
            previous = new ArrayList<>(previous);
            previous.addAll(0, kitNames);
        }
        return BukkitUtils.getCompletions(args, previous);
    }
}
