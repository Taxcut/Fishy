package com.fishy.hcf.timer.custom;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import com.fishy.hcf.HCF;
import com.fishy.hcf.util.base.JavaUtils;

import java.util.concurrent.TimeUnit;

public class CustomTimerCommand implements CommandExecutor {

    // /customtimer start <name> <time> <display>
    // /customtimer stop <name>

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        CommandSender p = sender;
        if (p.hasPermission("hcf.command.customtimer")) {

            if (args.length == 0) {
                p.sendMessage(ChatColor.RED + "Usage: /customtimer <start/stop/list> <name> <time> <display> <command>");
                return true;
            }

            switch (args[0]) {
                case "start":
                    String name = args[1];
                    long time = JavaUtils.parse(args[2]);
                    String display = args[3].toString().replace('_', ' ');
                    String commands = args[4].toString().replace('_', ' ');

                    CustomTimer customTimer = new CustomTimer(name, display, time, commands);

                    CustomTimer.getCustomTimers().add(customTimer);

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (customTimer.getCurrentSecond() == 0) {
                            	Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), customTimer.getCommand());
                                cancel();
                                CustomTimer.getCustomTimers().remove(customTimer);
                            } else {
                                customTimer.setCurrentSecond(customTimer.getCurrentSecond() - 100);
                            }
                        }
                    }.runTaskTimerAsynchronously(HCF.getPlugin(), 0, 2);
                    return true;
                case "stop":
                    String timerName = args[1];

                    for (CustomTimer customTimer1 : CustomTimer.getCustomTimers()) {
                        if (customTimer1.getName().equalsIgnoreCase(timerName)) {
                            CustomTimer.getCustomTimers().remove(customTimer1);
                            return true;
                        }
                    }

                    p.sendMessage(ChatColor.RED + timerName + " is not found!");
                    return true;
                case "list":
                    p.sendMessage(ChatColor.DARK_PURPLE + "Active Timers:");
                    for (CustomTimer timer : CustomTimer.getCustomTimers()) {
                        p.sendMessage(ChatColor.YELLOW + timer.getName() + ChatColor.GRAY + " - " + ChatColor.WHITE + "/" + timer.getCommand());
                    }
                    return true;
                default:
                    p.sendMessage(ChatColor.RED + "Usage: /customtimer <start/stop/list> <name> <time> <display> <command>");
                    return true;
            }

        } else {
            p.sendMessage(ChatColor.RED + "No permission.");
        }
        return true;
    }

}
