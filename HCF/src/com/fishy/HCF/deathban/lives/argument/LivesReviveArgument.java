package com.fishy.hcf.deathban.lives.argument;

import com.fishy.hcf.HCF;
import com.fishy.hcf.deathban.Deathban;
import com.fishy.hcf.faction.struct.Relation;
import com.fishy.hcf.faction.type.PlayerFaction;
import com.fishy.hcf.user.FactionUser;
import com.fishy.hcf.util.base.command.CommandArgument;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageRecipient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class LivesReviveArgument extends CommandArgument {

    private static final String REVIVE_BYPASS_PERMISSION = "hcf.revive.bypass";
    private static final String PROXY_CHANNEL_NAME = "BungeeCord";

    private final HCF plugin;

    public LivesReviveArgument(HCF plugin) {
        super("revive", "Revive a death-banned player");
        this.plugin = plugin;
        this.permission = "hcf.command.lives.argument." + getName();
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, PROXY_CHANNEL_NAME);
    }

    @Override
    public String getUsage(String label) {
        return '/' + label + ' ' + getName() + " <playerName>";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]); //TODO: breaking

        if (!target.hasPlayedBefore() && !target.isOnline()) {
            sender.sendMessage(ChatColor.RED + "Player '" + ChatColor.RED + args[1] + ChatColor.RED + "' not found.");
            return true;
        }

        UUID targetUUID = target.getUniqueId();
        FactionUser factionTarget = plugin.getUserManager().getUser(targetUUID);
        Deathban deathban = factionTarget.getDeathban();

        if (deathban == null || !deathban.isActive()) {
            sender.sendMessage(ChatColor.RED + target.getName() + " is not death-banned.");
            return true;
        }

        Relation relation = Relation.ENEMY;
        if (sender instanceof Player) {
            if (!sender.hasPermission(REVIVE_BYPASS_PERMISSION)) {
                if (plugin.getEotwHandler().isEndOfTheWorld()) {
                    sender.sendMessage(ChatColor.RED + "You cannot revive players during EOTW.");
                    return true;
                }
            }

            Player player = (Player) sender;
            UUID playerUUID = player.getUniqueId();
            int selfLives = plugin.getDeathbanManager().getLives(playerUUID);

            if (selfLives <= 0) {
                sender.sendMessage(ChatColor.RED + "You do not have any lives.");
                return true;
            }

            plugin.getDeathbanManager().setLives(playerUUID, selfLives - 1);
            PlayerFaction playerFaction = plugin.getFactionManager().getPlayerFaction(player);
            relation = playerFaction == null ? Relation.ENEMY : playerFaction.getFactionRelation(plugin.getFactionManager().getPlayerFaction(targetUUID));
            sender.sendMessage(ChatColor.WHITE + "You have used a life to revive " + relation.toChatColour() + target.getName() + ChatColor.WHITE + '.');
        } else {
            sender.sendMessage(ChatColor.WHITE + "You have revived " + ChatColor.valueOf(plugin.getConfig().getString("TEAMMATE_COLOR")) + target.getName() + ChatColor.WHITE + '.');
        }

        if (sender instanceof PluginMessageRecipient) {
            //NOTE: This server needs at least 1 player online.
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("Message");
            out.writeUTF(args[1]);

            String serverDisplayName = ChatColor.GREEN + HCF.getPlugin().getConfig().getString("TITLE");
            out.writeUTF(relation.toChatColour() + sender.getName() + ChatColor.GRAY + " has just revived you from " + ChatColor.GREEN + serverDisplayName + ChatColor.GRAY + '.');
            ((PluginMessageRecipient) sender).sendPluginMessage(plugin, PROXY_CHANNEL_NAME, out.toByteArray());
        }

        factionTarget.removeDeathban();
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2) {
            return Collections.emptyList();
        }

        List<String> results = new ArrayList<>();
        Collection<FactionUser> factionUsers = plugin.getUserManager().getUsers().values();
        for (FactionUser factionUser : factionUsers) {
            Deathban deathban = factionUser.getDeathban();
            if (deathban == null || !deathban.isActive()) continue;

            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(factionUser.getUserUUID());
            String offlineName = offlinePlayer.getName();
            if (offlineName != null) {
                results.add(offlinePlayer.getName());
            }
        }

        return results;
    }
}
