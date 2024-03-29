package com.fishy.hcf.faction.argument;

import com.fishy.hcf.visualise.VisualType;
import com.fishy.base.GuavaCompat;
import com.fishy.hcf.HCF;
import com.fishy.hcf.faction.LandMap;
import com.fishy.hcf.faction.claim.Claim;
import com.fishy.hcf.user.FactionUser;
import com.fishy.hcf.util.base.command.CommandArgument;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Faction argument used to view a interactive map of {@link Claim}s.
 */
public class FactionMapArgument extends CommandArgument {

    private final HCF plugin;

    public FactionMapArgument(HCF plugin) {
        super("map", "View all claims around your chunk.");
        this.plugin = plugin;
    }

    @Override
    public String getUsage(String label) {
        return '/' + label + ' ' + getName() + " [factionName]";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return true;
        }

        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();

        final FactionUser factionUser = plugin.getUserManager().getUser(uuid);
        final VisualType visualType;
        if (args.length < 2) {
            visualType = VisualType.CLAIM_MAP;
        } else if ((visualType = GuavaCompat.getIfPresent(VisualType.class, args[1]).orNull()) == null) {
            player.sendMessage(ChatColor.RED + "Visual type " + args[1] + " not found.");
            return true;
        }

        boolean newShowingMap = !factionUser.isShowClaimMap();
        if (newShowingMap) {
            if (!LandMap.updateMap(player, plugin, visualType, true)) {
                return true;
            }
        } else {
            plugin.getVisualiseHandler().clearVisualBlocks(player, visualType, null);
            sender.sendMessage(ChatColor.RED + "Claim pillars are no longer shown.");
        }

        factionUser.setShowClaimMap(newShowingMap);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2 || !(sender instanceof Player)) {
            return Collections.emptyList();
        }

        VisualType[] values = VisualType.values();
        List<String> results = new ArrayList<>(values.length);
        for (VisualType visualType : values) {
            results.add(visualType.name());
        }

        return results;
    }
}
