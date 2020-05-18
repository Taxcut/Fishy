package com.fishy.hcf.events.eotw;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.fishy.hcf.HCF;
import com.fishy.hcf.events.faction.KothFaction;
import com.fishy.hcf.faction.claim.Claim;
import com.fishy.hcf.faction.type.Faction;
import com.fishy.hcf.faction.type.PlayerFaction;
import com.fishy.hcf.faction.type.SpawnFaction;
import com.fishy.hcf.user.FactionUser;
import com.fishy.hcf.util.DurationFormatter;

public class EotwHandler {

    public static final int BORDER_DECREASE_MINIMUM = 1000;
    public static final int BORDER_DECREASE_AMOUNT = 200;
    
    public static final long BORDER_DECREASE_TIME_MILLIS = TimeUnit.SECONDS.toMillis(20L);
    public static final int BORDER_DECREASE_TIME_SECONDS = (int) TimeUnit.MILLISECONDS.toSeconds(BORDER_DECREASE_TIME_MILLIS);
    public static final int BORDER_DECREASE_TIME_SECONDS_HALVED = BORDER_DECREASE_TIME_SECONDS / 2;
    public static final String BORDER_DECREASE_TIME_WORDS = DurationFormatUtils.formatDurationWords(BORDER_DECREASE_TIME_MILLIS, true, true);
    public static final String BORDER_DECREASE_TIME_ALERT_WORDS = DurationFormatUtils.formatDurationWords(BORDER_DECREASE_TIME_MILLIS / 2, true, true);

    public static final long EOTW_WARMUP_WAIT_MILLIS = TimeUnit.MINUTES.toMillis(5L);
    public static final int EOTW_WARMUP_WAIT_SECONDS = (int) (TimeUnit.MILLISECONDS.toSeconds(EOTW_WARMUP_WAIT_MILLIS));

    private static final long EOTW_CAPPABLE_WAIT_MILLIS = TimeUnit.MINUTES.toMillis(10L);

    private EotwRunnable runnable;
    private final HCF plugin;

    public EotwHandler(HCF plugin) {
        this.plugin = plugin;
    }

    public EotwRunnable getRunnable() {
        return runnable;
    }
    public boolean isEndOfTheWorld() {
        return isEndOfTheWorld(true);
    }
    public boolean isEndOfTheWorld(boolean ignoreWarmup) {
        return runnable != null && (!ignoreWarmup || runnable.getElapsedMilliseconds() > 0);
    }
    public void setEndOfTheWorld(boolean yes) {
        // Don't unnecessary edit task.
        if (yes == isEndOfTheWorld(false)) {
            return;
        }

        if (yes) {
            runnable = new EotwRunnable();
            runnable.runTaskTimer(plugin, 20L, 20L);
        } else {
            if (runnable != null) {
                runnable.cancel();
                runnable = null;
            }
        }
    }

    public static final class EotwRunnable extends BukkitRunnable {


        private long startStamp;
        private int elapsedSeconds;
        private KothFaction EOTWFACTION;
        private boolean hasInformedStarted;
        private boolean hasInformedCapable;

        public EotwRunnable() {
            this.startStamp = System.currentTimeMillis() + EOTW_WARMUP_WAIT_MILLIS;
            this.elapsedSeconds = -EOTW_WARMUP_WAIT_SECONDS;
        }

        public void handleDisconnect(Player player) {
        }

        //TODO: Cleanup these millisecond managements
        public long getMillisUntilStarting() {
            long difference = System.currentTimeMillis() - startStamp;
            return difference > 0L ? -1L : Math.abs(difference);
        }

        public long getMillisUntilCappable() {
            return EOTW_CAPPABLE_WAIT_MILLIS - getElapsedMilliseconds();
        }

        public long getElapsedMilliseconds() {
            return System.currentTimeMillis() - startStamp;
        }

        public void run() {
            long elapsedMillis = this.getElapsedMilliseconds();
            int elapsedSeconds = (int) Math.round(elapsedMillis / 1000.0);
            if (!this.hasInformedStarted && elapsedSeconds >= 0) {
                Faction eotwFaction = HCF.getPlugin().getFactionManager().getFaction("EOTW");
                if(eotwFaction == null){
                    eotwFaction = new KothFaction("EOTW");
                }
                else if(!(eotwFaction instanceof KothFaction)){
                    HCF.getPlugin().getFactionManager().removeFaction(eotwFaction, Bukkit.getConsoleSender());
                    eotwFaction = new KothFaction("EOTW");
                }
                Command.broadcastCommandMessage(Bukkit.getConsoleSender(), ChatColor.RED + "Created EOTW faction");
                EOTWFACTION = (KothFaction) eotwFaction;
                for (Faction faction : HCF.getPlugin().getFactionManager().getFactions()) {
                    if (faction instanceof PlayerFaction) {
                        PlayerFaction playerFaction = (PlayerFaction) faction;
                        playerFaction.setDeathsUntilRaidable(-9999999);
                    }
                    else if(faction instanceof SpawnFaction){
                        for(Claim claim: new ArrayList<>(((SpawnFaction) faction).getClaims())){
                            ((SpawnFaction) faction).removeClaim(claim, Bukkit.getConsoleSender());
                            if(claim.getWorld().getEnvironment() == World.Environment.NORMAL){
                                try {
                                    EOTWFACTION.addClaim(new Claim(EOTWFACTION, claim), Bukkit.getConsoleSender());
                                    Command.broadcastCommandMessage(Bukkit.getConsoleSender(), ChatColor.RED + "EOTW Faction claim has been setup");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }

                Command.broadcastCommandMessage(Bukkit.getConsoleSender(), ChatColor.RED + "All factions have been set raidable");


                for(FactionUser factionUser: HCF.getPlugin().getUserManager().getUsers().values()) {
                    factionUser.removeDeathban();
                }
                Command.broadcastCommandMessage(Bukkit.getConsoleSender(), ChatColor.RED + "All death-bans have been cleared.");

                this.hasInformedStarted = true;
                Bukkit.broadcastMessage(ChatColor.DARK_GREEN.toString() + ChatColor.BOLD + "EOTW" + ChatColor.RED + " has begun.");
            }
            else if(!this.hasInformedCapable && elapsedMillis >= EOTW_CAPPABLE_WAIT_MILLIS){
                if(EOTWFACTION != null){
                    HCF.getPlugin().getTimerManager().eventTimer.tryContesting(EOTWFACTION, Bukkit.getConsoleSender());
                }
                hasInformedCapable = true;
            }
            if (elapsedMillis < 0L && elapsedMillis >= -EotwHandler.EOTW_WARMUP_WAIT_MILLIS) {
                Bukkit.broadcastMessage(ChatColor.DARK_GREEN.toString() + ChatColor.BOLD + "EOTW" + ChatColor.RED + " is starting in " + DurationFormatter.getRemaining(Math.abs(elapsedMillis), true, false) + '.');
            }
        }
    }
}
