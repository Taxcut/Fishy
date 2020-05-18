package com.fishy.hcf.classes.type;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.fishy.hcf.HCF;
import com.fishy.hcf.classes.PvpClass;
import com.fishy.hcf.classes.event.RogueUseEffectEvent;
import com.fishy.hcf.util.DurationFormatter;

import net.minecraft.util.gnu.trove.map.TObjectLongMap;
import net.minecraft.util.gnu.trove.map.hash.TObjectLongHashMap;

public class RogueClass extends PvpClass implements Listener {

    private final HCF plugin;
    private static final PotionEffect ARCHER_SPEED_EFFECT = new PotionEffect(PotionEffectType.SPEED, 200, 4);
    private static final long ARCHER_SPEED_COOLDOWN_DELAY = TimeUnit.SECONDS.toMillis(45L);
    public static TObjectLongMap<UUID> rogueSpeedCooldowns = new TObjectLongHashMap<>();
    private static final PotionEffect ARCHER_JUMP_EFFECT = new PotionEffect(PotionEffectType.JUMP, 200, 4);
    private static final long ARCHER_JUMP_COOLDOWN_DELAY = TimeUnit.MINUTES.toMillis(1L);
    private final TObjectLongMap<UUID> rogueJumpCooldowns = new TObjectLongHashMap<>();
    private final TObjectLongMap<UUID> rogueBackstabCooldowns = new TObjectLongHashMap<>();

    public RogueClass(HCF plugin) {
        super("Rogue", TimeUnit.SECONDS.toMillis(5L));

        this.plugin = plugin;
        this.passiveEffects.add(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0));
        this.passiveEffects.add(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 0));
        this.passiveEffects.add(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        Entity damager = event.getDamager();
        if (entity instanceof Player && damager instanceof Player) {
            Player attacker = (Player) damager;
            if (plugin.getPvpClassManager().getEquippedClass(attacker) == this) {
                ItemStack stack = attacker.getItemInHand();
                if (stack != null && stack.getType() == Material.GOLD_SWORD && stack.getEnchantments().isEmpty()) {
                    ChatColor relationColourEnemy = ChatColor.valueOf(plugin.getConfig().getString("ENEMY_COLOR"));

                    Player player = (Player) entity;
                    Player player1 = (Player) damager;
                    UUID uuid = player1.getUniqueId();
                    long timestamp = rogueBackstabCooldowns.get(uuid);
                    long millis = System.currentTimeMillis();
                    long remaining = timestamp == rogueBackstabCooldowns.getNoEntryValue() ? -1L : timestamp - millis;
                    if (remaining > 0L) {
                    	player1.sendMessage(ChatColor.RED + "You cannot use this for another " + ChatColor.BOLD.toString() + DurationFormatter.getRemaining(remaining, true, true) + ChatColor.RED + ".");
                    } else {
                        player.sendMessage(relationColourEnemy + attacker.getName() + ChatColor.WHITE  + " has backstabbed you.");
                        player.playSound(player.getLocation(), Sound.ITEM_BREAK, 1.0F, 1.0F);

                        attacker.sendMessage(ChatColor.WHITE + "You have backstabbed " + relationColourEnemy + player.getName() + ChatColor.WHITE + '.');
                        attacker.setItemInHand(new ItemStack(Material.AIR, 1));
                        attacker.playSound(player.getLocation(), Sound.ITEM_BREAK, 1.0F, 1.0F);
                        event.setDamage(8.0f);
                        rogueBackstabCooldowns.put(event.getDamager().getUniqueId(), System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(3L));
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
                long timestamp = rogueSpeedCooldowns.get(uuid);
                long millis = System.currentTimeMillis();
                long remaining = timestamp == rogueSpeedCooldowns.getNoEntryValue() ? -1L : timestamp - millis;
                if (remaining > 0L) {
                    player.sendMessage(ChatColor.RED + "You cannot use this for another " + ChatColor.BOLD.toString() + DurationFormatUtils.formatDurationWords(remaining, true, true) + ChatColor.RED + ".");
                } else {

                    //call and listen for the rogueUseEffectEvent
                    RogueUseEffectEvent rogueUseEffectEvent = new RogueUseEffectEvent(player, this);
                    Bukkit.getPluginManager().callEvent(rogueUseEffectEvent);
                    if (rogueUseEffectEvent.isCancelled()) {
                        return;
                    }

                    ItemStack stack = player.getItemInHand();
                    if (stack.getAmount() == 1) {
                        player.setItemInHand(new ItemStack(Material.AIR, 1));
                    } else stack.setAmount(stack.getAmount() - 1);
                    
                    //player.sendMessage(ChatColor.GREEN + "You have used your rogue speed lasting 10 seconds.");

                    plugin.getEffectRestorer().setRestoreEffect(player, ARCHER_SPEED_EFFECT);
                    rogueSpeedCooldowns.put(event.getPlayer().getUniqueId(), System.currentTimeMillis() + ARCHER_SPEED_COOLDOWN_DELAY);
                }
            }
            if (event.hasItem() && event.getItem().getType() == Material.FEATHER) {
                if (plugin.getPvpClassManager().getEquippedClass(event.getPlayer()) != this) {
                    return;
                }

                Player player = event.getPlayer();
                UUID uuid = player.getUniqueId();
                long timestamp = rogueJumpCooldowns.get(uuid);
                long millis = System.currentTimeMillis();
                long remaining = timestamp == rogueJumpCooldowns.getNoEntryValue() ? -1L : timestamp - millis;
                if (remaining > 0L) {
                    player.sendMessage(ChatColor.RED + "You cannot use this for another " + ChatColor.BOLD.toString() + DurationFormatUtils.formatDurationWords(remaining, true, true) + ChatColor.RED + ".");
                } else {

                    //call and listen for the rogueUseEffectEvent
                    RogueUseEffectEvent rogueUseEffectEvent = new RogueUseEffectEvent(player, this);
                    Bukkit.getPluginManager().callEvent(rogueUseEffectEvent);
                    if (rogueUseEffectEvent.isCancelled()) {
                        return;
                    }

                    ItemStack stack = player.getItemInHand();
                    if (stack.getAmount() == 1) {
                        player.setItemInHand(new ItemStack(Material.AIR, 1));
                    } else stack.setAmount(stack.getAmount() - 1);
                    
                    //player.sendMessage(ChatColor.GREEN + "You have used your rogue speed lasting 10 seconds.");

                    plugin.getEffectRestorer().setRestoreEffect(player, ARCHER_JUMP_EFFECT);
                    rogueJumpCooldowns.put(event.getPlayer().getUniqueId(), System.currentTimeMillis() + ARCHER_JUMP_COOLDOWN_DELAY);
                }
            }
        }
    }

    @Override
    public boolean isApplicableFor(Player player) {
        PlayerInventory playerInventory = player.getInventory();

        ItemStack helmet = playerInventory.getHelmet();
        if (helmet == null || helmet.getType() != Material.CHAINMAIL_HELMET) {
            return false;
        }

        ItemStack chestplate = playerInventory.getChestplate();
        if (chestplate == null || chestplate.getType() != Material.CHAINMAIL_CHESTPLATE) {
            return false;
        }

        ItemStack leggings = playerInventory.getLeggings();
        if (leggings == null || leggings.getType() != Material.CHAINMAIL_LEGGINGS) {
            return false;
        }

        ItemStack boots = playerInventory.getBoots();
        return !(boots == null || boots.getType() != Material.CHAINMAIL_BOOTS);
    }
}
