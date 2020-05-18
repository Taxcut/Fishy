package com.fishy.hcf.timer.sotw;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.scheduler.BukkitRunnable;

import com.fishy.hcf.HCF;

import lombok.Getter;

public class SotwTimer {

    @Getter
    private SotwRunnable sotwRunnable;

    @Getter
    private static List<UUID> sotwTimer = new ArrayList<>();


    public boolean cancel() {
        if (this.sotwRunnable != null) {
            this.sotwRunnable.cancel();
            this.sotwRunnable = null;
            return true;
        }

        return false;
    }

    public void start(long millis) {
        if (this.sotwRunnable == null) {
            this.sotwRunnable = new SotwRunnable(this, millis);
            sotwTimer = new ArrayList<>();
            this.sotwRunnable.runTaskTimer(HCF.getPlugin(), 20L, 20L);
        }
    }

    public static class SotwRunnable extends BukkitRunnable {

        private SotwTimer sotwTimer;
        private long startMillis;
        private long endMillis;

        public SotwRunnable(SotwTimer sotwTimer, long duration) {
            this.sotwTimer = sotwTimer;
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
                this.sotwTimer.sotwRunnable = null;
                return;
            }
        }
    }
}
