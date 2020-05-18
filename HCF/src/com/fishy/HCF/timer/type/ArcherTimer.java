package com.fishy.hcf.timer.type;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.projectiles.ProjectileSource;

import com.fishy.hcf.HCF;
import com.fishy.hcf.classes.archer.ArcherClass;
import com.fishy.hcf.scoreboard.PlayerBoard;
import com.fishy.hcf.timer.PlayerTimer;
import com.fishy.hcf.timer.event.TimerExpireEvent;

public class ArcherTimer extends PlayerTimer implements Listener {

    public ArcherTimer() {
        super("Archer Tag", TimeUnit.SECONDS.toMillis(15L), false);
    }

    public String getScoreboardPrefix() {
        return ChatColor.GOLD.toString() + ChatColor.BOLD;
    }

    public void run() {}

    @EventHandler
    public void onExpire(TimerExpireEvent event) {
        if (event.getUserUUID().isPresent() && event.getTimer().equals(this)) {
            UUID userUUID = event.getUserUUID().get();
            Player player = Bukkit.getPlayer(userUUID);

            if (player == null) {
                return;
            }

            Player p = Bukkit.getPlayer(ArcherClass.TAGGED.get(userUUID));

            if (p != null) {
                p.sendMessage(ChatColor.LIGHT_PURPLE + "Your archer tag on " + ChatColor.WHITE + player.getDisplayName() + ChatColor.LIGHT_PURPLE + " has expired.");
            }

            player.sendMessage(ChatColor.LIGHT_PURPLE + "You are no longer archer tagged.");

            ArcherClass.TAGGED.remove(player.getUniqueId());

            for (PlayerBoard playerBoard : HCF.getPlugin().getScoreboardHandler().getPlayerBoards().values()) {
                playerBoard.addUpdates(Collections.singletonList(player));
            }

        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        Player entity;
        double damage;

        if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
            entity = (Player) e.getEntity();

            if (this.getRemaining(entity) > 0L) {
                damage = e.getDamage() * 0.3D;
                e.setDamage(e.getDamage() + damage);
            }
        }

        if (e.getEntity() instanceof Player && e.getDamager() instanceof Arrow) {
            entity = (Player) e.getEntity();

            ProjectileSource damager = ((Arrow) e.getDamager()).getShooter();

            if (damager instanceof Player && this.getRemaining(entity) > 0L) {
                if ((ArcherClass.TAGGED.get(entity.getUniqueId())).equals(((Player)damager).getUniqueId())) {
                    this.setCooldown(entity, entity.getUniqueId());
                }

                damage = e.getDamage() * 0.3D;
                e.setDamage(e.getDamage() + damage);
            }
        }
    }

}