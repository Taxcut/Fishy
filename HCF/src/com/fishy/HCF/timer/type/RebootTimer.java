package com.fishy.hcf.timer.type;

import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.fishy.hcf.HCF;

import lombok.Getter;
import net.md_5.bungee.api.ChatColor;

public class RebootTimer {

    @Getter
    private AutoRestartRunnable autoRestartRunnable;

    public boolean cancel() {
        if (this.autoRestartRunnable != null) {
            this.autoRestartRunnable.cancel();
            this.autoRestartRunnable = null;
            return true;
        }

        return false;
    }

    public void start(long millis) {
        if (this.autoRestartRunnable == null) {
            this.autoRestartRunnable = new AutoRestartRunnable(this, millis);
            this.autoRestartRunnable.runTaskLater(HCF.getPlugin(), millis / 50L);
        }
    }

    public static class AutoRestartRunnable extends BukkitRunnable {

        private RebootTimer autoRestartTimer;
        private long startMillis;
        private long endMillis;

        public AutoRestartRunnable(RebootTimer autoRestartTimer, long duration) {
            this.autoRestartTimer = autoRestartTimer;
            this.startMillis = System.currentTimeMillis();
            this.endMillis = this.startMillis + duration;
        }

        public long getRemaining() {
            return endMillis - System.currentTimeMillis();
        }

        @Override
        public void run() {
            if (this.getRemaining() <= 0L) {
            	HCF.getPlugin().saveData();
            	Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop");
            	Bukkit.getServer().shutdown();
                this.cancel();
                this.autoRestartTimer.autoRestartRunnable = null;
                return;
            }
         	HCF.getPlugin().saveData();
        	Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop");
        	Bukkit.getServer().shutdown();
            this.cancel();
            this.autoRestartTimer.autoRestartRunnable = null;
        }
    }
}
