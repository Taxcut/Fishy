package com.fishy.hcf.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.fishy.hcf.HCF;
import com.fishy.hcf.faction.type.Faction;
import com.fishy.hcf.faction.type.PlayerFaction;

public class FocusCommand implements CommandExecutor
{
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        Player player = (Player)sender;
        PlayerFaction playerFaction = HCF.getPlugin().getFactionManager().getPlayerFaction(player);
        if (playerFaction == null) {
            player.sendMessage(ChatColor.RED + "You are not in a faction.");
            return true;
        }
        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /focus <player>");
            return true;
        }
        String key = StringUtils.join((Object[])args).replace(" ", "");
        PlayerFaction playerFaction2 = HCF.getPlugin().getFactionManager().getPlayerFaction(player);
        if (playerFaction2 == null) {
            player.sendMessage(ChatColor.RED + "That player is not online.");
            return true;
        }
        if (playerFaction2.getFocused() == null) {
            Player playerMatch = Bukkit.getPlayer(key);
            PlayerFaction toFocus;
            if (playerMatch == null) {
                Faction faction = HCF.getPlugin().getFactionManager().getFaction(key);
                if (faction == null || !(faction instanceof PlayerFaction)) {
                    player.sendMessage(ChatColor.RED + "That player does not have a faction!");
                    return true;
                }
                toFocus = (PlayerFaction)faction;
            }
            else {
                toFocus = HCF.getPlugin().getFactionManager().getPlayerFaction(playerMatch);
                if (toFocus == null) {
                    Faction faction = HCF.getPlugin().getFactionManager().getFaction(key);
                    if (faction == null || !(faction instanceof PlayerFaction)) {
                        player.sendMessage(ChatColor.RED + "That player does not have a faction!");
                        return true;
                    }
                    toFocus = (PlayerFaction)faction;
                }
            }
            if (playerFaction2.equals(toFocus)) {
                player.sendMessage(ChatColor.RED + "You cannot focus yourself.");
                return true;
            }
            playerFaction2.broadcast(ChatColor.translateAlternateColorCodes('&', "&d" + key + " &fhas been focused by &d" + player.getName()));
            PlayerFaction ToFocus = toFocus;
            new BukkitRunnable() {
                public void run() {
                    FocusCommand.this.focus(playerFaction2, ToFocus, false);
                }
            }.runTaskAsynchronously((Plugin)HCF.getPlugin());
            new BukkitRunnable() {
                public void run() {
                    new BukkitRunnable() {
                        public void run() {
                            FocusCommand.this.focus(playerFaction, null, true);
                        }
                    }.runTaskAsynchronously((Plugin)HCF.getPlugin());
                    playerFaction.setFocused(null);
                }
            }.runTaskLater((Plugin)HCF.getPlugin(), 12000L);
        }
        else {
            if (playerFaction.getFocused() != null) {
                playerFaction.broadcast(ChatColor.translateAlternateColorCodes('&', "&d" + player.getName() + " &fhas unfocused the target."));
            }
            new BukkitRunnable() {
                public void run() {
                    FocusCommand.this.focus(playerFaction, null, true);
                }
            }.runTaskAsynchronously((Plugin)HCF.getPlugin());
            playerFaction.setFocused(null);
        }
        return false;
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerFaction playerFaction = HCF.getPlugin().getFactionManager().getPlayerFaction(player.getUniqueId());
        UUID focused = playerFaction.getFocused();
        if (focused != null) {
            this.focus(playerFaction, (PlayerFaction)HCF.getPlugin().getFactionManager().getFaction(playerFaction.getFocused()), false);
        }
    }
    
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        PlayerFaction playerFaction = HCF.getPlugin().getFactionManager().getPlayerFaction(player.getUniqueId());
        if (playerFaction == null) {
            return;
        }
        for (Faction faction : HCF.getPlugin().getFactionManager().getFactions()) {
            if (faction instanceof PlayerFaction) {
                if (((PlayerFaction)faction).getFocused() == null) {
                    continue;
                }
                if (!((PlayerFaction)faction).getFocused().equals(player.getUniqueId())) {
                    continue;
                }
                this.focus((PlayerFaction)faction, playerFaction, true);
            }
        }
    }
    
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> results = new ArrayList<>();
        for (Player target : Bukkit.getOnlinePlayers()) {
            if (!results.contains(target.getName())) {
                results.add(target.getName());
            }
        }
        return results;
    }
    
    private void focus(PlayerFaction playerFaction, PlayerFaction toFocus, boolean unfocus) {
        if (!unfocus) {
            playerFaction.setFocused(toFocus.getUniqueID());
        }
        for (Player player : playerFaction.getOnlinePlayers()) {
            Scoreboard scoreboard = player.getScoreboard();
            if (scoreboard != Bukkit.getScoreboardManager().getMainScoreboard()) {
                Team team = scoreboard.getTeam("focus");
                if (team != null) {
                    Set<OfflinePlayer> offlinePlayers = (Set<OfflinePlayer>)team.getPlayers();
                    for (OfflinePlayer offlinePlayer : offlinePlayers) {
                        Player other = offlinePlayer.getPlayer();
                        if (other != null) {
                            HCF.getPlugin().getScoreboardHandler().getPlayerBoard(player.getUniqueId()).addUpdate(other);
                        }
                    }
                    team.unregister();
                }
                else {
                    team = scoreboard.registerNewTeam("focus");
                    team.setPrefix(ChatColor.LIGHT_PURPLE.toString());
                }
                if (unfocus) {
                    break;
                }
                for (Player playerToFocus : toFocus.getOnlinePlayers()) {
                    team.addEntry(playerToFocus.getName());
                }
            }
        }
    }
}
