package com.fishy.hcf.timer.type;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import com.fishy.hcf.HCF;

import lombok.Getter;

public class DoubleKeyTimer {

    @Getter
    private doubleKeyRunnable doubleKeyRunnable;

    public boolean cancel() {
        if (this.doubleKeyRunnable != null) {
            this.doubleKeyRunnable.cancel();
            this.doubleKeyRunnable = null;
            return true;
        }

        return false;
    }

    public void start(long millis) {
        if (this.doubleKeyRunnable == null) {
            this.doubleKeyRunnable = new doubleKeyRunnable(this, millis);
            this.doubleKeyRunnable.runTaskLater(HCF.getPlugin(), millis / 50L);
        }
    }

    public static class doubleKeyRunnable extends BukkitRunnable {

        private DoubleKeyTimer doubleKeyTimer;
        private long startMillis;
        private long endMillis;

        public doubleKeyRunnable(DoubleKeyTimer doubleKeyTimer, long duration) {
            this.doubleKeyTimer = doubleKeyTimer;
            this.startMillis = System.currentTimeMillis();
            this.endMillis = this.startMillis + duration;
        }

        public long getRemaining() {
            return endMillis - System.currentTimeMillis();
        }

        @Override
        public void run() {
            if (this.getRemaining() <= 0L) {
                this.cancel();
                this.doubleKeyTimer.doubleKeyRunnable = null;
                return;
            }
            this.cancel();
            this.doubleKeyTimer.doubleKeyRunnable = null;
        }
    }
}
