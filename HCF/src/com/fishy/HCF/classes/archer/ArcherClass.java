package com.fishy.hcf.classes.archer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;

import com.fishy.hcf.HCF;
import com.fishy.hcf.classes.PvpClass;
import com.fishy.hcf.classes.event.ArcherUseEffectEvent;
import com.fishy.hcf.classes.event.PvpClassUnequipEvent;
import com.fishy.hcf.scoreboard.PlayerBoard;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import gnu.trove.map.TObjectLongMap;
import gnu.trove.map.hash.TObjectLongHashMap;

/**
 * Represents a {@link PvpClass} used to buff Archer game-play such as Bows.
 */
public class ArcherClass extends PvpClass implements Listener {

    public static final HashMap<UUID, UUID> TAGGED = new HashMap<>();// for tagged key
    private static final int INVISIBILITY_SECONDS = 15;
    private static final int INVISIBILITY_TICKS = INVISIBILITY_SECONDS * 20;
    private static final PotionEffect ARCHER_SPEED_EFFECT = new PotionEffect(PotionEffectType.SPEED, 200, 3);
    private static final long ARCHER_SPEED_COOLDOWN_DELAY = TimeUnit.SECONDS.toMillis(30L);
    public static TObjectLongMap<UUID> archerSpeedCooldowns = new TObjectLongHashMap<>();
    private static final PotionEffect ARCHER_JUMP_EFFECT = new PotionEffect(PotionEffectType.JUMP, 200, 4);
    private static final long ARCHER_JUMP_COOLDOWN_DELAY = TimeUnit.MINUTES.toMillis(1L);
    public static TObjectLongMap<UUID> archerJumpCooldowns = new TObjectLongHashMap<>();

    private final Table<UUID, UUID, ArcherMark> marks = HashBasedTable.create(); // Key of the Players UUID who applied the mark, value as the Mark applied.
    private final HCF plugin;

    public ArcherClass(HCF plugin) {
        super("Archer", TimeUnit.SECONDS.toMillis(5L));
        this.plugin = plugin;

        this.passiveEffects.add(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0));
        this.passiveEffects.add(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));
    }

    public Map<UUID, ArcherMark> getSentMarks(Player player) {
        synchronized (marks) {
            return marks.column(player.getUniqueId());
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerClassUnequip(PvpClassUnequipEvent event) {
        getSentMarks(event.getPlayer()).clear();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        getSentMarks(event.getEntity()).clear();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        getSentMarks(event.getPlayer()).clear();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerKick(PlayerKickEvent event) {
        getSentMarks(event.getPlayer()).clear();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEntityDamage(final EntityDamageByEntityEvent event) {
        final Entity entity = event.getEntity();
        final Entity damager = event.getDamager();
        if (entity instanceof Player && damager instanceof Arrow) {
            final Arrow arrow = (Arrow) damager;
            final ProjectileSource source = arrow.getShooter();
            if (source instanceof Player) {
                Player damaged = (Player) event.getEntity();
                final Player shooter = (Player) source;
                final PvpClass equipped = this.plugin.getPvpClassManager().getEquippedClass(shooter);
                if (equipped == null || !equipped.equals(this)) {
                    return;
                }
                
                if (plugin.getSotwTimer().getSotwRunnable() != null && plugin.getUserManager().getUser(damaged.getUniqueId()).isSOTW()) {
                	return;
                }
                
                for(PotionEffect potionEffect: new ArrayList<>(damaged.getActivePotionEffects())){
                    if(potionEffect.getType().getId() == PotionEffectType.INVISIBILITY.getId()){
                        damaged.sendMessage(ChatColor.WHITE + "Since you had invisibility it has been removed.");
                        int ticks = potionEffect.getDuration();
                        damaged.removePotionEffect(potionEffect.getType());
                        if(ticks > INVISIBILITY_TICKS){
                            PotionEffect copyEffect = new PotionEffect(potionEffect.getType(), potionEffect.getDuration() - INVISIBILITY_TICKS, potionEffect.getAmplifier(), potionEffect.isAmbient());
                            new BukkitRunnable(){
                                public void run() {
                                    if(damaged.isOnline() && !damaged.isDead() && plugin.getTimerManager().archerTimer.getRemaining(damaged) <= 0) {
                                        damaged.addPotionEffect(copyEffect);
                                    }
                                }
                            }.runTaskLater(plugin, INVISIBILITY_TICKS);
                        }
                        break;
                    }
                }

                if (plugin.getTimerManager().archerTimer.getRemaining((Player) entity) == 0 || plugin.getTimerManager().archerTimer.getRemaining((Player) entity) < TimeUnit.SECONDS.toMillis(5)) {
                    if (plugin.getPvpClassManager().getEquippedClass(damaged) != null && plugin.getPvpClassManager().getEquippedClass(damaged).equals(this))
                        return;
                    plugin.getTimerManager().archerTimer.setCooldown((Player) entity, entity.getUniqueId());
                    TAGGED.put(damaged.getUniqueId(), shooter.getUniqueId());
                    final double distance = shooter.getLocation().distance(damaged.getLocation());
                    shooter.sendMessage(ChatColor.WHITE + "You successfully tagged " + ChatColor.RED + damaged.getName() + ChatColor.WHITE + " from " + ChatColor.GRAY + String.format("%.1f", distance) + ChatColor.WHITE + " blocks away.");
                    damaged.sendMessage(ChatColor.WHITE + "You were archer tagged by " + ChatColor.RED + shooter.getName() + ChatColor.WHITE + " from " + ChatColor.GRAY + String.format("%.1f", distance) + ChatColor.WHITE + " blocks away.");
                    Iterable<Player> updates;

                    updates = Arrays.asList(
                            ((Player) entity)
                    );

                    for (PlayerBoard playerBoard : HCF.getPlugin().getScoreboardHandler().getPlayerBoards().values()) {
                        playerBoard.addUpdates(updates);
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            if (event.hasItem() && event.getItem().getType() == Material.SUGAR) {
                if (plugin.getPvpClassManager().getEquippedClass(event.getPlayer()) != this) {
                    return;
                }

                Player player = event.getPlayer();
                UUID uuid = player.getUniqueId();
                long timestamp = archerSpeedCooldowns.get(uuid);
                long millis = System.currentTimeMillis();
                long remaining = timestamp == archerSpeedCooldowns.getNoEntryValue() ? -1L : timestamp - millis;
                if (remaining > 0L) {
                    player.sendMessage(ChatColor.RED + "You cannot use this for another " + ChatColor.BOLD.toString() + DurationFormatUtils.formatDurationWords(remaining, true, true) + ChatColor.RED + ".");
                } else {

                    //call and listen for the archerUseEffectEvent
                    ArcherUseEffectEvent archerUseEffectEvent = new ArcherUseEffectEvent(player, this);
                    Bukkit.getPluginManager().callEvent(archerUseEffectEvent);
                    if (archerUseEffectEvent.isCancelled()) {
                        return;
                    }

                    ItemStack stack = player.getItemInHand();
                    if (stack.getAmount() == 1) {
                        player.setItemInHand(new ItemStack(Material.AIR, 1));
                    } else stack.setAmount(stack.getAmount() - 1);
                    
                    //player.sendMessage(ChatColor.GREEN + "You have used your archer speed lasting 10 seconds.");

                    plugin.getEffectRestorer().setRestoreEffect(player, ARCHER_SPEED_EFFECT);
                    archerSpeedCooldowns.put(event.getPlayer().getUniqueId(), System.currentTimeMillis() + ARCHER_SPEED_COOLDOWN_DELAY);
                }
            }
            if (event.hasItem() && event.getItem().getType() == Material.FEATHER) {
                if (plugin.getPvpClassManager().getEquippedClass(event.getPlayer()) != this) {
                    return;
                }

                Player player = event.getPlayer();
                UUID uuid = player.getUniqueId();
                long timestamp = archerJumpCooldowns.get(uuid);
                long millis = System.currentTimeMillis();
                long remaining = timestamp == archerJumpCooldowns.getNoEntryValue() ? -1L : timestamp - millis;
                if (remaining > 0L) {
                    player.sendMessage(ChatColor.RED + "You cannot use this for another " + ChatColor.BOLD.toString() + DurationFormatUtils.formatDurationWords(remaining, true, true) + ChatColor.RED + ".");
                } else {

                    //call and listen for the archerUseEffectEvent
                    ArcherUseEffectEvent archerUseEffectEvent = new ArcherUseEffectEvent(player, this);
                    Bukkit.getPluginManager().callEvent(archerUseEffectEvent);
                    if (archerUseEffectEvent.isCancelled()) {
                        return;
                    }

                    ItemStack stack = player.getItemInHand();
                    if (stack.getAmount() == 1) {
                        player.setItemInHand(new ItemStack(Material.AIR, 1));
                    } else stack.setAmount(stack.getAmount() - 1);
                    
                    //player.sendMessage(ChatColor.GREEN + "You have used your archer speed lasting 10 seconds.");

                    plugin.getEffectRestorer().setRestoreEffect(player, ARCHER_JUMP_EFFECT);
                    archerJumpCooldowns.put(event.getPlayer().getUniqueId(), System.currentTimeMillis() + ARCHER_JUMP_COOLDOWN_DELAY);
                }
            }
        }
    }

    @Override
    public boolean isApplicableFor(Player player) {
        PlayerInventory playerInventory = player.getInventory();

        ItemStack helmet = playerInventory.getHelmet();
        if (helmet == null || helmet.getType() != Material.LEATHER_HELMET) return false;

        ItemStack chestplate = playerInventory.getChestplate();
        if (chestplate == null || chestplate.getType() != Material.LEATHER_CHESTPLATE) return false;

        ItemStack leggings = playerInventory.getLeggings();
        if (leggings == null || leggings.getType() != Material.LEATHER_LEGGINGS) return false;

        ItemStack boots = playerInventory.getBoots();
        return !(boots == null || boots.getType() != Material.LEATHER_BOOTS);
    }
}
