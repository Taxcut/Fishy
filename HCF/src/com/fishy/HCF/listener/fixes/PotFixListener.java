package com.fishy.hcf.listener.fixes;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.util.Vector;

import com.fishy.hcf.HCF;


public class PotFixListener implements Listener {
	
    private final HCF plugin;
    double speed = 2.0;

    public PotFixListener(HCF plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (event.getEntityType() == EntityType.SPLASH_POTION) {
            Projectile projectile = event.getEntity();

            if (projectile.getShooter() instanceof Player && ((Player) projectile.getShooter()).isSprinting()) {
                Vector velocity = projectile.getVelocity();

                velocity.setY(velocity.getY() - speed);
                projectile.setVelocity(velocity);
            }
        }
    }

    @EventHandler
    void onPotionSplash(PotionSplashEvent event) {
        if (event.getEntity().getShooter() instanceof Player) {
            Player shooter = (Player) event.getEntity().getShooter();

            if (shooter.isSprinting() && event.getIntensity(shooter) > 0.5D) {
                event.setIntensity(shooter, 1.0D);
            }
        }
    }
}
