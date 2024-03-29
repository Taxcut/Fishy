package com.fishy.hcf.faction.argument;

import com.fishy.hcf.HCF;
import com.fishy.hcf.timer.type.StuckTimer;
import com.fishy.hcf.util.DurationFormatter;
import com.fishy.hcf.util.base.command.CommandArgument;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Faction argument used to teleport to a nearby {@link org.bukkit.Location} safely if stuck.
 */
public class FactionStuckArgument extends CommandArgument {

    private final HCF plugin;

    public FactionStuckArgument(HCF plugin) {
        super("stuck", "Teleport to a safe position.", new String[]{"trap", "trapped"});
        this.plugin = plugin;
    }

    @Override
    public String getUsage(String label) {
        return '/' + label + ' ' + getName();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return true;
        }

        Player player = (Player) sender;

        if (player.getWorld().getEnvironment() != World.Environment.NORMAL) {
            sender.sendMessage(ChatColor.RED + "You can only use this command from the overworld.");
            return true;
        }

        StuckTimer stuckTimer = plugin.getTimerManager().getStuckTimer();

        if (!stuckTimer.setCooldown(player, player.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "Your " + stuckTimer.getName() + ChatColor.RED + " timer is already active.");
            return true;
        }

        sender.sendMessage(ChatColor.WHITE + stuckTimer.getDisplayName() + ChatColor.WHITE + " timer has started. " +
                "Teleport will occur in " + ChatColor.LIGHT_PURPLE + DurationFormatter.getRemaining(stuckTimer.getRemaining(player), true, false) + ChatColor.WHITE + ". " +
                "This will cancel if you move more than " + StuckTimer.MAX_MOVE_DISTANCE + " blocks.");

        return true;
    }
}
