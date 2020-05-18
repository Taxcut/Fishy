package com.fishy.hcf.timer.type;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import com.fishy.hcf.HCF;

import lombok.Getter;

public class FlashSaleTimer {

    @Getter
    private flashSaleRunnable flashSaleRunnable;

    public boolean cancel() {
        if (this.flashSaleRunnable != null) {
            this.flashSaleRunnable.cancel();
            this.flashSaleRunnable = null;
            return true;
        }

        return false;
    }

    public void start(long millis) {
        if (this.flashSaleRunnable == null) {
            this.flashSaleRunnable = new flashSaleRunnable(this, millis);
            this.flashSaleRunnable.runTaskLater(HCF.getPlugin(), millis / 50L);
        }
    }

    public static class flashSaleRunnable extends BukkitRunnable {

        private FlashSaleTimer flashSaleTimer;
        private long startMillis;
        private long endMillis;

        public flashSaleRunnable(FlashSaleTimer flashSaleTimer, long duration) {
            this.flashSaleTimer = flashSaleTimer;
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
                this.flashSaleTimer.flashSaleRunnable = null;
                return;
            }
            this.cancel();
            this.flashSaleTimer.flashSaleRunnable = null;
        }
    }
}
