package com.fishy.hcf.timer.type;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import com.fishy.hcf.HCF;

import lombok.Getter;

public class KeySaleTimer {

    @Getter
    private KeySaleRunnable keySaleRunnable;

    public boolean cancel() {
        if (this.keySaleRunnable != null) {
            this.keySaleRunnable.cancel();
            this.keySaleRunnable = null;
            return true;
        }

        return false;
    }

    public void start(long millis) {
        if (this.keySaleRunnable == null) {
            this.keySaleRunnable = new KeySaleRunnable(this, millis);
            this.keySaleRunnable.runTaskLater(HCF.getPlugin(), millis / 50L);
        }
    }

    public static class KeySaleRunnable extends BukkitRunnable {

        private KeySaleTimer keySaleTimer;
        private long startMillis;
        private long endMillis;

        public KeySaleRunnable(KeySaleTimer keySaleTimer, long duration) {
            this.keySaleTimer = keySaleTimer;
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
                this.keySaleTimer.keySaleRunnable = null;
                return;
            }
            this.cancel();
            this.keySaleTimer.keySaleRunnable = null;
        }
    }
}
