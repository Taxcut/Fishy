package com.fishy.hcf.faction.argument;

import com.fishy.hcf.HCF;
import com.fishy.hcf.faction.claim.ClaimHandler;
import com.fishy.hcf.faction.type.PlayerFaction;
import com.fishy.hcf.util.base.command.CommandArgument;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

import java.util.UUID;

public class FactionClaimArgument extends CommandArgument {

    private final HCF plugin;

    public FactionClaimArgument(HCF plugin) {
        super("claim", "Claim land in the Wilderness.", new String[]{"claimland"});
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
        UUID uuid = player.getUniqueId();

        PlayerFaction playerFaction = plugin.getFactionManager().getPlayerFaction(uuid);

        if (playerFaction == null) {
            sender.sendMessage(ChatColor.RED + "You are not in a faction.");
            return true;
        }

        if (playerFaction.isRaidable()) {
            sender.sendMessage(ChatColor.RED + "You cannot claim land for your faction while raidable.");
            return true;
        }

        PlayerInventory inventory = player.getInventory();

        if (inventory.contains(ClaimHandler.CLAIM_WAND)) {
            sender.sendMessage(ChatColor.RED + "You already have a claiming wand in your inventory.");
            return true;
        }

        if (inventory.contains(ClaimHandler.SUBCLAIM_WAND)) {
            sender.sendMessage(ChatColor.RED + "You cannot have a claiming wand whilst you have a subclaim wand in your inventory.");
            return true;
        }

        if (!inventory.addItem(ClaimHandler.CLAIM_WAND).isEmpty()) {
            sender.sendMessage(ChatColor.RED + "Your inventory is full.");
            return true;
        }

        sender.sendMessage(ChatColor.WHITE + "Claiming wand added to inventory, read the item to understand how to claim. " +
                 ChatColor.GREEN + ChatColor.BOLD + "Alternatively" + ChatColor.WHITE + " you can use " + ChatColor.GREEN + ChatColor.BOLD + '/' + label + " claimchunk" + ChatColor.WHITE + '.');

        return true;
    }
}
