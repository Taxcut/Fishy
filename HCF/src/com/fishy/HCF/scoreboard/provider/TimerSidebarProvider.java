package com.fishy.hcf.scoreboard.provider;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

import com.fishy.hcf.DateTimeFormats;
import com.fishy.hcf.HCF;
import com.fishy.hcf.classes.PvpClass;
import com.fishy.hcf.classes.archer.ArcherClass;
import com.fishy.hcf.classes.archer.ArcherMark;
import com.fishy.hcf.classes.bard.BardClass;
import com.fishy.hcf.classes.type.MinerClass;
import com.fishy.hcf.classes.type.RogueClass;
import com.fishy.hcf.events.EventTimer;
import com.fishy.hcf.events.eotw.EotwHandler;
import com.fishy.hcf.events.faction.ConquestFaction;
import com.fishy.hcf.events.faction.EventFaction;
import com.fishy.hcf.events.faction.KothFaction;
import com.fishy.hcf.events.ktk.KillTheKing;
import com.fishy.hcf.events.tracker.ConquestTracker;
import com.fishy.hcf.faction.type.PlayerFaction;
import com.fishy.hcf.scoreboard.SidebarEntry;
import com.fishy.hcf.scoreboard.SidebarProvider;
import com.fishy.hcf.timer.PlayerTimer;
import com.fishy.hcf.timer.Timer;
import com.fishy.hcf.timer.custom.CustomTimer;
import com.fishy.hcf.timer.sotw.SotwTimer;
import com.fishy.hcf.timer.type.DoubleKeyTimer;
import com.fishy.hcf.timer.type.FlashSaleTimer;
import com.fishy.hcf.timer.type.KeySaleTimer;
import com.fishy.hcf.timer.type.RebootTimer;
import com.fishy.hcf.util.Color;
import com.fishy.hcf.util.DurationFormatter;
import com.fishy.hcf.util.base.BukkitUtils;

import net.minecraft.server.v1_7_R4.MinecraftServer;

public class TimerSidebarProvider implements SidebarProvider {
    public static final ThreadLocal<DecimalFormat> CONQUEST_FORMATTER;
    protected String STRAIGHT_LINE;
    private static final Comparator<Map.Entry<UUID, ArcherMark>> ARCHER_MARK_COMPARATOR;
    private final HCF plugin;

    static {
        CONQUEST_FORMATTER = new ThreadLocal<DecimalFormat>() {
            @Override
            protected DecimalFormat initialValue() {
                return new DecimalFormat("00.0");
            }
        };
        ARCHER_MARK_COMPARATOR = (Comparator.comparing(Map.Entry::getValue));
    }

    public TimerSidebarProvider(HCF plugin) {
        this.STRAIGHT_LINE = BukkitUtils.STRAIGHT_LINE_DEFAULT.substring(0, 13);
        this.plugin = plugin;
    }

    public static String format(double tps) {
        return (tps > 18.0 ? ChatColor.GREEN : tps > 16.0 ? ChatColor.YELLOW : ChatColor.WHITE).toString()
        		+ ((tps > 20.0) ? "*" : "") + Math.min(Math.round(tps * 100.0)/100.0, 20.0);
    }
    
    @Override
    public List<SidebarEntry> getLines(final Player player) {
        List<SidebarEntry> lines = new ArrayList<>();
        
        if (this.plugin.getConfig().getBoolean("KITMAP")) {
            lines.add(new SidebarEntry(ChatColor.DARK_PURPLE.toString(), "Kills", ChatColor.GRAY + ": " + ChatColor.WHITE + plugin.getUserManager().getUser(player.getUniqueId()).getKills()));
            lines.add(new SidebarEntry(ChatColor.DARK_PURPLE.toString(), "Deaths", ChatColor.GRAY + ": " + ChatColor.WHITE + plugin.getUserManager().getUser(player.getUniqueId()).getDeaths()));
            lines.add(new SidebarEntry(ChatColor.DARK_PURPLE.toString(), "Balance", ChatColor.GRAY + ": " + ChatColor.WHITE + '$' + plugin.getEconomyManager().getBalance(player.getUniqueId())));
        }
        if (this.plugin.getStaffModeListener().isStaffModeActive(player)) {
            lines.add(new SidebarEntry(ChatColor.LIGHT_PURPLE + "Vanish", ChatColor.WHITE + "" + ChatColor.GRAY + ": ", String.valueOf(ChatColor.WHITE.toString()) + (this.plugin.getStaffModeListener().isVanished(player) ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled")));
        }
        
        RebootTimer.AutoRestartRunnable restart = plugin.getTimerManager().getAutoRestartTimer().getAutoRestartRunnable();
        if (restart != null) {
            lines.add(new SidebarEntry(ChatColor.DARK_RED + ChatColor.BOLD.toString(), "Reboot" + ChatColor.GRAY + ": ", ChatColor.WHITE + DurationFormatter.getRemaining(restart.getRemaining(), true)));
        }
        
        EotwHandler.EotwRunnable eotwRunnable = plugin.getEotwHandler().getRunnable();
        if (eotwRunnable != null) {
            long remaining = eotwRunnable.getMillisUntilStarting();
            if (remaining > 0L) {
                lines.add(new SidebarEntry(String.valueOf(ChatColor.RED.toString()) + ChatColor.BOLD, "EOTW" + ChatColor.RED + " starts", " in " + ChatColor.BOLD + DurationFormatter.getRemaining(remaining, true)));
            }
            else if ((remaining = eotwRunnable.getMillisUntilCappable()) > 0L) {
                lines.add(new SidebarEntry(String.valueOf(ChatColor.RED.toString()) + ChatColor.BOLD, "EOTW" + ChatColor.RED + " cappable", " in " + ChatColor.BOLD + DurationFormatter.getRemaining(remaining, true)));
            }

        }
        
        KeySaleTimer.KeySaleRunnable keySale = plugin.getKeySaleTimer().getKeySaleRunnable();
        if (keySale != null) {
        	lines.add(new SidebarEntry(ChatColor.RED.toString() + ChatColor.BOLD, "KEY-SALE" + ChatColor.GRAY + ": ", ChatColor.WHITE + DurationFormatter.getRemaining(keySale.getRemaining(), true)));
        }
        
        FlashSaleTimer.flashSaleRunnable flashSale = plugin.getFlashSaleTimer().getFlashSaleRunnable();
        if (flashSale != null) {
        	lines.add(new SidebarEntry(ChatColor.DARK_AQUA.toString() + ChatColor.BOLD, "FLASH-SALE" + ChatColor.GRAY + ": ", ChatColor.WHITE + DurationFormatter.getRemaining(flashSale.getRemaining(), true)));
        }
        
        DoubleKeyTimer.doubleKeyRunnable doubleKey = plugin.getDoubleKeyTimer().getDoubleKeyRunnable();
        if (doubleKey != null) {
        	lines.add(new SidebarEntry(ChatColor.AQUA.toString() + ChatColor.BOLD, "DOUBLE-KEYS" + ChatColor.GRAY + ": ", ChatColor.WHITE + DurationFormatter.getRemaining(doubleKey.getRemaining(), true)));
        }
        
        SotwTimer.SotwRunnable sotwRunnable = this.plugin.getSotwTimer().getSotwRunnable();
        if (sotwRunnable != null) {
        	if (!HCF.getPlugin().getUserManager().getUser(player.getUniqueId()).isSOTW()) {
                lines.add(new SidebarEntry(ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD + ChatColor.STRIKETHROUGH + "SOTW ", ChatColor.STRIKETHROUGH.toString() + ChatColor.GRAY + ": ", ChatColor.RED.toString() + ChatColor.BOLD + ChatColor.STRIKETHROUGH + DurationFormatter.getRemaining(sotwRunnable.getRemaining(), true)));
        	} else {
        		lines.add(new SidebarEntry(ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD  + "SOTW",  ChatColor.GRAY + ": ", ChatColor.WHITE + DurationFormatter.getRemaining(sotwRunnable.getRemaining(), true)));
        	}
 
        	
        	
/*        	lines.add(new SidebarEntry((!HCF.getPlugin().getUserManager().getUser(player.getUniqueId()).isSOTW() ? ChatColor.GRAY + "(Off) " : "") + ChatColor.YELLOW.toString() 
        	+ ChatColor.BOLD + "SOTW", ChatColor.GRAY, ": " + ChatColor.RED + DurationFormatter.getRemaining(sotwRunnable.getRemaining(), false)));*/
        }

        final EventTimer eventTimer = this.plugin.getTimerManager().getEventTimer();
        List<SidebarEntry> conquestLines = null;
        final EventFaction eventFaction = eventTimer.getEventFaction();
        if (eventFaction instanceof KothFaction) {
            lines.add(new SidebarEntry(eventTimer.getScoreboardPrefix(), String.valueOf(eventFaction.getScoreboardName()) + ChatColor.GRAY, ": " + ChatColor.WHITE + DurationFormatter.getRemaining(eventTimer.getRemaining(), true)));
            //lines.add(new SidebarEntry(ChatColor.YELLOW, "/f show " + eventTimer.getScoreboardPrefix(), String.valueOf(eventFaction.getScoreboardName())));
        } else if (eventFaction instanceof ConquestFaction) {
        ConquestFaction conquestFaction = (ConquestFaction) eventFaction;
        DecimalFormat format = CONQUEST_FORMATTER.get();

        conquestLines = new ArrayList<>();
        conquestLines.add(new SidebarEntry(ChatColor.YELLOW.toString(), ChatColor.BOLD + conquestFaction.getName() + ChatColor.GRAY, ":"));

        conquestLines.add(new SidebarEntry("  " +
                ChatColor.RED.toString() + conquestFaction.getRed().getScoreboardRemaining(),
                ChatColor.RESET + " ",
                ChatColor.YELLOW.toString() + conquestFaction.getYellow().getScoreboardRemaining()));

        conquestLines.add(new SidebarEntry("  " +
                ChatColor.GREEN.toString() + conquestFaction.getGreen().getScoreboardRemaining(),
                ChatColor.RESET + " " + ChatColor.RESET,
                ChatColor.AQUA.toString() + conquestFaction.getBlue().getScoreboardRemaining()));

        // Show the top 3 factions next.
        ConquestTracker conquestTracker = (ConquestTracker) conquestFaction.getEventType().getEventTracker();
        int count = 0;
        for (Map.Entry<PlayerFaction, Integer> entry : conquestTracker.getFactionPointsMap().entrySet()) {
            String factionName = entry.getKey().getName();
            if (factionName.length() > 14) factionName = factionName.substring(0, 14);
            conquestLines.add(new SidebarEntry(ChatColor.RED, ChatColor.BOLD + factionName, ChatColor.GRAY + ": " + ChatColor.RED + entry.getValue()));
            if (++count == 3) break;
        }
    }

        final PvpClass pvpClass = this.plugin.getPvpClassManager().getEquippedClass(player);
        if (pvpClass != null) {
            if (pvpClass instanceof MinerClass) {
                lines.add(new SidebarEntry(ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD + pvpClass.getName() + ChatColor.GRAY + ": "));
                lines.add(new SidebarEntry(ChatColor.AQUA + " " + "�" + " ", ChatColor.AQUA + "Diamonds", ChatColor.GRAY + ": " + ChatColor.WHITE + player.getStatistic(Statistic.MINE_BLOCK, Material.DIAMOND_ORE)));
            }
    		UUID uuid = player.getUniqueId();
    		long timestamp = ArcherClass.archerSpeedCooldowns.get(uuid);
    		long millis = System.currentTimeMillis();
    		long remaining1 = timestamp == ArcherClass.archerSpeedCooldowns.getNoEntryValue() ? -1L : timestamp - millis;
    		long timestamp1 = RogueClass.rogueSpeedCooldowns.get(uuid);
    		long millis1 = System.currentTimeMillis();
    		long remaining2 = timestamp1 == RogueClass.rogueSpeedCooldowns.getNoEntryValue() ? -1L : timestamp1 - millis1;
    		
            if (pvpClass instanceof RogueClass && remaining2 >= 0L) {
                lines.add(new SidebarEntry(ChatColor.RED.toString() + ChatColor.BOLD, "Rogue", ChatColor.GRAY + ":"));
                lines.add(new SidebarEntry(ChatColor.RED + " � ", ChatColor.WHITE + "Buff Delay", ChatColor.GRAY + ": " + ChatColor.RED + DurationFormatter.getRemaining(remaining2, false)));
            }
            if (pvpClass instanceof ArcherClass && remaining1 >= 0L) {
                lines.add(new SidebarEntry(ChatColor.GREEN.toString() + ChatColor.BOLD, "Archer", ChatColor.GRAY + ":"));
                lines.add(new SidebarEntry(ChatColor.GREEN + " � ", ChatColor.WHITE + "Buff Delay", ChatColor.GRAY + ": " + ChatColor.RED + DurationFormatter.getRemaining(remaining1, false)));
            }
            if ((pvpClass instanceof BardClass)) {
                BardClass bardClass = (BardClass) pvpClass;
                lines.add(new SidebarEntry(ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD + bardClass.getName() + ChatColor.GRAY + ": "));
                lines.add(new SidebarEntry(ChatColor.GRAY + " " + "�" + " ", ChatColor.AQUA + "Energy", ChatColor.GRAY + ": " + ChatColor.WHITE + handleBardFormat(bardClass.getEnergyMillis(player), true)));
                long cooldown = bardClass.getRemainingBuffDelay(player);
                if (cooldown > 0L) {
                    lines.add(new SidebarEntry(ChatColor.GRAY + " " + "�" + " ", ChatColor.WHITE + "Buff Delay", ChatColor.GRAY + ": " + ChatColor.WHITE + DurationFormatter.getRemaining(cooldown, false)));
                }
            }
        }
        final Collection<Timer> timers = this.plugin.getTimerManager().getTimers();
        for (final Timer timer : timers) {
            if (timer instanceof PlayerTimer) {
                final PlayerTimer playerTimer = (PlayerTimer)timer;
                final long remaining3 = playerTimer.getRemaining(player);
                if (remaining3 <= 0L) {
                    continue;
                }
                String timerName = playerTimer.getName();
                lines.add(new SidebarEntry(playerTimer.getScoreboardPrefix(), String.valueOf(timerName) + ChatColor.GRAY, ": " + ChatColor.WHITE + DurationFormatter.getRemaining(remaining3, true)));//!(playerTimer instanceof SpawnTagTimer))));
            }
        }
        

        for (CustomTimer customTimer : CustomTimer.getCustomTimers()) {
            String display = Color.translate(customTimer.getDisplay());
            String time = DurationFormatter.getRemaining(customTimer.getCurrentSecond(), true);
            lines.add(new SidebarEntry(display, ChatColor.GRAY + ": " + ChatColor.WHITE, time));
        }

        if (plugin.getActiveKTK() != null) {
            KillTheKing killTheKing = plugin.getActiveKTK();
            Player king = Bukkit.getServer().getPlayer(killTheKing.getKing());
            if (king != null) {
                lines.add(new SidebarEntry(ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD, "Kill The King", ""));
                lines.add(new SidebarEntry("  ", ChatColor.LIGHT_PURPLE.toString() + "King: ", ChatColor.WHITE.toString() + king.getName()));
                lines.add(new SidebarEntry("  ", ChatColor.LIGHT_PURPLE.toString() + "Uptime: ", ChatColor.WHITE.toString() + DurationFormatter.getRemaining(System.currentTimeMillis() - killTheKing.getStarted(), true)));
                lines.add(new SidebarEntry("  ", ChatColor.LIGHT_PURPLE.toString() + "Location: ", ChatColor.WHITE.toString() + king.getLocation().getBlockX() + ", " + king.getLocation().getBlockZ()));
            }
        }
        if (conquestLines != null && !conquestLines.isEmpty()) {
            if (!lines.isEmpty()) {
                conquestLines.add(new SidebarEntry(" ", "  ", " "));
            }
            conquestLines.addAll(lines);
            lines = conquestLines;
        }
        if (!lines.isEmpty()) {
            lines.add(0, new SidebarEntry(ChatColor.GRAY, this.STRAIGHT_LINE, this.STRAIGHT_LINE));
/*            lines.add(new SidebarEntry(""));
            lines.add(new SidebarEntry(ChatColor.GRAY.toString() + ChatColor.ITALIC + ""));*/
            lines.add(lines.size(), new SidebarEntry(ChatColor.GRAY, ChatColor.STRIKETHROUGH + this.STRAIGHT_LINE, this.STRAIGHT_LINE));
        }
        return lines;
    }

    private static String handleBardFormat(final long millis, final boolean trailingZero) {
        return (trailingZero ? DateTimeFormats.REMAINING_SECONDS_TRAILING : DateTimeFormats.REMAINING_SECONDS).get().format(millis * 0.001);
    }
}