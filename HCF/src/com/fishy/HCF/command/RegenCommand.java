package com.fishy.hcf.command;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.fishy.hcf.HCF;
import com.fishy.hcf.faction.struct.RegenStatus;
import com.fishy.hcf.faction.type.PlayerFaction;

public class RegenCommand implements CommandExecutor, TabCompleter {

    private final HCF plugin;

    public RegenCommand(HCF plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return true;
        }

        Player player = (Player) sender;
        PlayerFaction playerFaction = plugin.getFactionManager().getPlayerFaction(player);
        if (playerFaction == null) {
            sender.sendMessage(ChatColor.RED + "You are not in a faction.");
            return true;
        }

        RegenStatus regenStatus = playerFaction.getRegenStatus();
        switch (regenStatus) {
            case FULL:
                sender.sendMessage(ChatColor.RED + "Your faction currently has full DTR.");
                return true;
            case PAUSED:
                sender.sendMessage(ChatColor.BLUE + "Your faction is currently on DTR freeze for another " + ChatColor.WHITE +
                        DurationFormatUtils.formatDurationWords(playerFaction.getRemainingRegenerationTime(), true, true) + ChatColor.BLUE + '.');

                return true;
            case REGENERATING:
                sender.sendMessage(ChatColor.BLUE + "Your faction currently has " + ChatColor.YELLOW + regenStatus.getSymbol() + ' ' +
                        playerFaction.getDeathsUntilRaidable() + ChatColor.BLUE + " DTR and is regenerating at a rate of " + ChatColor.GOLD +
                        1.0f + ChatColor.BLUE + " every " + ChatColor.GOLD +
                        DurationFormatUtils.formatDurationWords((long) 45000, true, true) + ChatColor.BLUE + ". Your ETA for maximum DTR is " + ChatColor.LIGHT_PURPLE +
                        DurationFormatUtils.formatDurationWords(getRemainingRegenMillis(playerFaction), true, true) + ChatColor.BLUE + '.');

                return true;
        }

        sender.sendMessage(ChatColor.RED + "Unrecognised regen status, please inform an Administrator.");
        return true;
    }

    public long getRemainingRegenMillis(PlayerFaction faction) {
        long millisPassedSinceLastUpdate = System.currentTimeMillis() - faction.getLastDtrUpdateTimestamp();
        double dtrRequired = faction.getMaximumDeathsUntilRaidable() - faction.getDeathsUntilRaidable();
        return (long) ((45000 / 1.0f) * dtrRequired) - millisPassedSinceLastUpdate;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return Collections.emptyList();
    }
}
