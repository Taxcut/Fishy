package com.fishy.hcf.faction.argument;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import com.fishy.hcf.HCF;
import com.fishy.hcf.faction.type.PlayerFaction;
import com.fishy.hcf.util.base.BukkitUtils;
import com.fishy.hcf.util.base.command.CommandArgument;

/**
 * Faction argument used to show top factions.
 */
public class FactionTopArgument extends CommandArgument implements TabCompleter {

    private final HCF plugin;
	public static final Comparator<PlayerFaction> POINT_COMPARATOR = (faction1, faction2) -> {
		return Integer.compare(faction1.getPoints(), faction2.getPoints());
	};

    public FactionTopArgument(HCF plugin) {
        super("top", "Show top factions.", new String[]{"topfac", "topfaction"});
        this.plugin = plugin;
    }

    @Override
    public String getUsage(String label) {
        return '/' + label + ' ' + getName();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    	if (args.length < 1) {
    		sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
    		return true;
    	}
    	
		List<PlayerFaction> PlayerFactions = new ArrayList<>(plugin.getFactionManager().getFactions().stream().filter(x -> x instanceof PlayerFaction).map(x -> (PlayerFaction) x).filter(x -> x.getPoints() > 0).collect(Collectors.toSet()));
		if (PlayerFactions.isEmpty()) {
	        sender.sendMessage(ChatColor.DARK_GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
	        sender.sendMessage(ChatColor.GREEN + ChatColor.BOLD.toString() + "Faction Top ");
	        sender.sendMessage(ChatColor.DARK_GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
			sender.sendMessage(ChatColor.DARK_GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
			return true;
		}
    	
        sender.sendMessage(ChatColor.DARK_GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
        sender.sendMessage(ChatColor.GREEN + ChatColor.BOLD.toString() + "Faction Top ");
        sender.sendMessage(ChatColor.DARK_GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
			Collections.sort(PlayerFactions, POINT_COMPARATOR);
			Collections.reverse(PlayerFactions);
			for (int i = 0; i < 10; i ++) {
				if (i >= PlayerFactions.size()) {
					break;
				}
				PlayerFaction next = PlayerFactions.get(i);
				sender.sendMessage(" " + ChatColor.GRAY.toString() + ChatColor.BOLD + (i + 1) + ". " + next.getRelation(sender).toChatColour() + next.getName() + ChatColor.GRAY + " - " + ChatColor.WHITE + next.getPoints());
			}
		sender.sendMessage(ChatColor.DARK_GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
		return true;
    }

	@Override
	public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
		List<String> list = new ArrayList<>(Arrays.asList("points"));
		if (args.length == 2) {
			return list;
		}
		return Collections.emptyList();
	}
}
