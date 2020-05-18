package com.fishy.hcf.scoreboard;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.fishy.hcf.HCF;
import com.fishy.hcf.classes.archer.ArcherClass;
import com.fishy.hcf.faction.type.PlayerFaction;

import lombok.Getter;
import net.md_5.bungee.api.ChatColor;

public class PlayerBoard {

    @Getter
    private boolean sidebarVisible = false;

    public static boolean NAMES_ENABLED = true;
    public static boolean INVISIBILITYFIX = true;
    private SidebarProvider defaultProvider;
    private SidebarProvider temporaryProvider;
    private BukkitRunnable runnable;

    private final AtomicBoolean removed = new AtomicBoolean(false);
    private final Team members;
    private final Team neutrals;
    private final Team allies;
    private final Team focused;
    private final Team archerTagged;
    private final BufferedObjective bufferedObjective;

    @Getter
    private final Scoreboard scoreboard;

    @Getter
    private final Player player;

    private final HCF plugin;

    public PlayerBoard(HCF plugin, Player player) {
        this.plugin = plugin;
        this.player = player;

        this.scoreboard = plugin.getServer().getScoreboardManager().getNewScoreboard();
        this.bufferedObjective = new BufferedObjective(scoreboard, ChatColor.translateAlternateColorCodes('&', HCF.getPlugin().getConfig().getString("BOARDTITLE")));

        this.members = scoreboard.registerNewTeam("members");
        this.members.setPrefix(ChatColor.valueOf(plugin.getConfig().getString("TEAMMATE_COLOR")).toString());
        this.members.setCanSeeFriendlyInvisibles(true);

        this.neutrals = scoreboard.registerNewTeam("neutrals");
        this.neutrals.setPrefix(ChatColor.valueOf(plugin.getConfig().getString("ENEMY_COLOR")).toString());

        this.allies = scoreboard.registerNewTeam("allies");
        this.allies.setPrefix(ChatColor.valueOf(plugin.getConfig().getString("ALLY_COLOR")).toString());

        this.focused = scoreboard.registerNewTeam("focused");
        this.focused.setPrefix(ChatColor.LIGHT_PURPLE.toString());

        this.archerTagged = scoreboard.registerNewTeam("archerTagged");
        this.archerTagged.setPrefix(ChatColor.valueOf(plugin.getConfig().getString("ARCHER_COLOR")).toString());

        player.setScoreboard(this.scoreboard);
    }

    /**
     * Removes this {@link PlayerBoard}.
     */
    public void remove() {
        if (!this.removed.getAndSet(true) && this.scoreboard != null) {
            for (Team team : this.scoreboard.getTeams()) {
                team.unregister();
            }

            for (Objective objective : this.scoreboard.getObjectives()) {
                objective.unregister();
            }
        }
    }


    public boolean checkInvis(Player player)
    {
        return (INVISIBILITYFIX) && (player.hasPotionEffect(PotionEffectType.INVISIBILITY));
    }


    public void setSidebarVisible(boolean visible) {
        this.sidebarVisible = visible;
        this.bufferedObjective.setDisplaySlot(visible ? DisplaySlot.SIDEBAR : null);
    }

    public void setDefaultSidebar(final SidebarProvider provider, long updateInterval) {
        if (provider != this.defaultProvider) {
            this.defaultProvider = provider;
            if (this.runnable != null) {
                this.runnable.cancel();
            }

            if (provider == null) {
                this.scoreboard.clearSlot(DisplaySlot.SIDEBAR);
                return;
            }

            (this.runnable = new BukkitRunnable() {
                @Override
                public void run() {
                    if (removed.get()) {
                        cancel();
                        return;
                    }

                    if (provider == defaultProvider) {
                        updateObjective();
                    }
                }
            }).runTaskTimerAsynchronously(plugin, updateInterval, updateInterval);
        }
    }

    public void setTemporarySidebar(final SidebarProvider provider, final long expiration) {
        if (this.removed.get()) {
            throw new IllegalStateException("Cannot update whilst board is removed");
        }

        this.temporaryProvider = provider;
        this.updateObjective();
        new BukkitRunnable() {
            @Override
            public void run() {
                if (removed.get()) {
                    cancel();
                    return;
                }

                if (temporaryProvider == provider) {
                    temporaryProvider = null;
                    updateObjective();
                }
            }
        }.runTaskLaterAsynchronously(plugin, expiration);
    }

    private void updateObjective() {
        if (this.removed.get()) {
            throw new IllegalStateException("Cannot update whilst board is removed");
        }

        SidebarProvider provider = this.temporaryProvider != null ? this.temporaryProvider : this.defaultProvider;
        if (provider == null) {
            this.bufferedObjective.setVisible(false);
        } else {
            this.bufferedObjective.setAllLines(provider.getLines(player));
            this.bufferedObjective.flip();
        }
    }

    public void addUpdate(Player target) {
        this.addUpdates(Collections.singleton(target));
    }

    public void addUpdates(Iterable<? extends Player> updates){
        new BukkitRunnable(){
            @Override
            public void run(){
                // Lazy load - don't lookup this in every iteration
                PlayerFaction playerFaction = null;
                boolean firstExecute = false;
                //

                for(Player update : updates){
                    if(player.equals(update)){
                        if(!members.hasPlayer(update)){
                            members.addPlayer(update);
                        }
                        continue;
                    }

                    if(!firstExecute){
                        if(plugin.getFactionManager().hasFaction(player)){
                            playerFaction = plugin.getFactionManager().getPlayerFaction(player);
                        }
                        firstExecute = true;
                    }

                    PlayerFaction targetFaction = plugin.getFactionManager().hasFaction(update) ? plugin.getFactionManager().getPlayerFaction(update) : null;

                    if (playerFaction != null && playerFaction.getFocused() != null) {
                        if (playerFaction.getFocused().equals(update.getUniqueId())) {
                            focused.addPlayer(update);
                            continue;
                        }
                    }

                    if(playerFaction != null && targetFaction != null){
                        if(playerFaction == targetFaction){
                            members.addPlayer(update);
                            continue;
                        }else if(playerFaction.getAllied().contains(targetFaction.getUniqueID())){
                            allies.addPlayer(update);
                            continue;
                        }else if(playerFaction.getFocused() != null && playerFaction.getFocused().equals(targetFaction.getUniqueID())) {
                            focused.addPlayer(update);
                            continue;
                        }
                    }

                    if (ArcherClass.TAGGED != null && ArcherClass.TAGGED.containsKey(update.getUniqueId())) {
                        archerTagged.addPlayer(update);
                        continue;
                    }                    

                    neutrals.addPlayer(update);

                }
            }
        }.runTaskAsynchronously(plugin);
    }

}