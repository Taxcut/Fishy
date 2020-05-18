package com.fishy.hcf.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

import com.fishy.hcf.HCF;
import com.fishy.hcf.util.base.InventoryUtils;
import com.fishy.hcf.util.base.ItemBuilder;
import com.fishy.hcf.util.base.chat.Lang;

import net.minecraft.util.org.apache.commons.lang3.text.WordUtils;

public class MapKitCommand
  implements CommandExecutor, TabCompleter, Listener
{
  private final Set<Inventory> tracking = new HashSet();
  
  public MapKitCommand(HCF plugin) {
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }
  
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!(sender instanceof Player)) {
      sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
      return true;
    }
    List<ItemStack> items = new ArrayList();
    for (String entry : HCF.getPlugin().getConfig().getConfigurationSection("ENCHANTMENT_LIMIT").getKeys(false)) {
    	Enchantment enchantment = Enchantment.getByName(entry);
      items.add(new ItemBuilder(Material.ENCHANTED_BOOK).displayName(ChatColor.WHITE + Lang.fromEnchantment(enchantment) + ": " + ChatColor.GRAY + HCF.getPlugin().getConfig().getInt("ENCHANTMENT_LIMIT." + enchantment.getName().toUpperCase())).build());
    }
    Player player = (Player)sender;
    Inventory inventory = Bukkit.createInventory(player, InventoryUtils.getSafestInventorySize(items.size()), ChatColor.GREEN.toString() + ChatColor.BOLD + HCF.getPlugin().getConfig().getString("TITLE") + (HCF.getPlugin().getConfig().getBoolean("KITMAP") ? "" : ChatColor.WHITE + " Map " + HCF.getPlugin().getConfig().getInt("MAPNUM") + ChatColor.WHITE + " Map Kit"));
    this.tracking.add(inventory);
    for (ItemStack item : items) {
      inventory.addItem(new ItemStack[] { item });
    }
    player.openInventory(inventory);
    return true;
  }
  
  public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
    return Collections.emptyList();
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.HIGHEST)
  public void onInventoryClick(InventoryClickEvent event) {
    if (this.tracking.contains(event.getInventory())) {
      event.setCancelled(true);
    	}
  	}
  }   

