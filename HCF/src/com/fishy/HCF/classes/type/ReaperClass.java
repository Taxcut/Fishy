package com.fishy.hcf.classes.type;

import com.fishy.hcf.HCF;
import com.fishy.hcf.classes.PvpClass;
import com.fishy.hcf.classes.event.ReaperUseEffectEvent;

import gnu.trove.map.TObjectLongMap;
import gnu.trove.map.hash.TObjectLongHashMap;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ReaperClass extends PvpClass implements Listener {

    private static final PotionEffect ARCHER_SPEED_EFFECT = new PotionEffect(PotionEffectType.SPEED, 200, 3);
    private static final long REAPER_SPEED_COOLDOWN_DELAY = TimeUnit.SECONDS.toMillis(45L);
    private final TObjectLongMap<UUID> reaperSpeedCooldowns = new TObjectLongHashMap<>();

    private final HCF plugin;

    public ReaperClass(HCF plugin) {
        super("Reaper", TimeUnit.SECONDS.toMillis(5L));

        this.plugin = plugin;
        this.passiveEffects.add(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1));
        this.passiveEffects.add(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
        this.passiveEffects.add(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0));
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
                long timestamp = reaperSpeedCooldowns.get(uuid);
                long millis = System.currentTimeMillis();
                long remaining = timestamp == reaperSpeedCooldowns.getNoEntryValue() ? -1L : timestamp - millis;
                if (remaining > 0L) {
                    player.sendMessage(ChatColor.RED + "You cannot use this for another " + ChatColor.BOLD.toString() + DurationFormatUtils.formatDurationWords(remaining, true, true) + ChatColor.RED + ".");
                } else {

                    //call and listen for the archerUseEffectEvent
                    ReaperUseEffectEvent reaperUseEffectEvent = new ReaperUseEffectEvent(player, this);
                    Bukkit.getPluginManager().callEvent(reaperUseEffectEvent);
                    if (reaperUseEffectEvent.isCancelled()) {
                        return;
                    }

                    ItemStack stack = player.getItemInHand();
                    if (stack.getAmount() == 1) {
                        player.setItemInHand(new ItemStack(Material.AIR, 1));
                    } else stack.setAmount(stack.getAmount() - 1);
                    
                    //player.sendMessage(ChatColor.GREEN + "You have used your archer speed lasting 10 seconds.");

                    plugin.getEffectRestorer().setRestoreEffect(player, new PotionEffect(PotionEffectType.SPEED, 200, 4));
                    plugin.getEffectRestorer().setRestoreEffect(player, new PotionEffect(PotionEffectType.JUMP, 200, 4));
                    plugin.getEffectRestorer().setRestoreEffect(player, new PotionEffect(PotionEffectType.ABSORPTION, 200, 2));
                    plugin.getEffectRestorer().setRestoreEffect(player, new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 200, 1));
                    reaperSpeedCooldowns.put(event.getPlayer().getUniqueId(), System.currentTimeMillis() + REAPER_SPEED_COOLDOWN_DELAY);
                }
            }
        }
    }

    @Override
    public boolean isApplicableFor(Player player) {
        PlayerInventory playerInventory = player.getInventory();

        ItemStack helmet = playerInventory.getHelmet();
        if (helmet == null || helmet.getType() != Material.GOLD_HELMET)
            return false;

        ItemStack chestplate = playerInventory.getChestplate();
        if (chestplate == null || chestplate.getType() != Material.IRON_CHESTPLATE)
            return false;

        ItemStack leggings = playerInventory.getLeggings();
        if (leggings == null || leggings.getType() != Material.IRON_LEGGINGS)
            return false;

        ItemStack boots = playerInventory.getBoots();
        return !(boots == null || boots.getType() != Material.GOLD_BOOTS);
    }
}
