package com.fishy.hcf.listener.fixes;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import com.fishy.hcf.HCF;

import net.minecraft.util.com.google.common.collect.ImmutableList;

public class PluginListFix implements Listener {
	
	
	public static ImmutableList<String> BLOCKED_COMMMANDS = ImmutableList.of("/?", "/minecraft:", "/bukkit:", "/spigot:", "/ver", "/about", "/say", "/me", "/version", "/icanhasbukkit", "/plugins", "/pl", "/plugin");
	public static ImmutableList<String> SPIGOT_BLOCKED_COMMMANDS = ImmutableList.of("/exospigot", "/exo", "/kspigot", "/meihjihasafatforehead", "/reload", "/ipwl", "/ipwhitelist", "/ipwhitelist:");//, "/pex");
	public static ImmutableList<String> TPS = ImmutableList.of("/tps");
	public static ImmutableList<String> PUNISHMENTS = ImmutableList.of("/ipmute", "/muteip", "/tempban", "/lban", "/unban", "/unmute", "/mute", "/banip", "/warn", "/ban", "/ipban", "/banip", "/tempmute", "/kick");
	public static ImmutableList<String> BLOCKED_CHAT = ImmutableList.of("kys", "nigger", "cunt", "faggot", "fag", "beaner", "chink", "kill yourself", "killyours", "Centile");
	public static ImmutableList<String> FILTERED = ImmutableList.of("cavepvp", "veltpvp", "faithful", "pornhub", "suicide", "sage", "sagepvp", "inside", "allying", "staff", "grief", "insiding", "boost", "dupe", "boosting", "duping",
			"glitching", "glitch", "hacking", "cheating", "vape", "hacks", "cheats", "autoclicker", "autoclicking", "hax");
	
    public static String format(double tps) {
        return (tps > 18.0 ? ChatColor.GREEN : tps > 16.0 ? ChatColor.YELLOW : ChatColor.RED).toString()
        		+ Math.min(Math.round(tps * 10.0)/10.0, 20.0);
    }
    
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		String message = event.getMessage();
		
		if (BLOCKED_CHAT.contains(message.toLowerCase().split(" ")[0])) {
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mute " + player.getName() + " 1h Blocked Words in Chat -s");
			for (Player staff : Bukkit.getOnlinePlayers()) {
				if (staff.hasPermission("hcf.command.staffmode")) { 
					staff.sendMessage(ChatColor.RED.toString() + "[Blocked] " + HCF.c(HCF.getPlugin().getChat().getPlayerSuffix(player)) + player.getName() + ChatColor.GRAY + ": " + ChatColor.WHITE + event.getMessage());	
				}
			}
			event.setCancelled(true);
		}
		
		if (message.contains("spoof") || message.contains("sp00f") || message.contains("spoofing") || message.contains("sp00fing") || message.contains("bots") || message.contains("botting") || message.contains("b0ts") || message.contains("b0tting")) {
			//player.sendMessage(HCF.c(HCF.getPlugin().getChat().getPlayerSuffix(player)) + player.getName() + ChatColor.GRAY + ": " + ChatColor.WHITE + event.getMessage());
			event.setCancelled(true);
		}
		
		if (FILTERED.contains(message.toLowerCase().split(" ")[0])) {
			//Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mute " + player.getName() + " 1h Blocked Words in Chat -s");
			for (Player staff : Bukkit.getOnlinePlayers()) {
				if (staff.hasPermission("hcf.command.staffmode")) { 
					staff.sendMessage(ChatColor.RED.toString() + "[Filtered] " + HCF.c(HCF.getPlugin().getChat().getPlayerSuffix(player)) + player.getName() + ChatColor.GRAY + ": " + ChatColor.WHITE + event.getMessage());	
				}
			}
			//event.setCancelled(true);
		}
		
	}
	
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onCommand(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		String message = event.getMessage();
		
		if (SPIGOT_BLOCKED_COMMMANDS.contains(message.toLowerCase().split(" ")[0])) {
			player.sendMessage("Unknown command. Type \"/help\" for help.");
			event.setCancelled(true); 
			return;
		}
		
		if (PUNISHMENTS.contains(message.toLowerCase().split(" ")[0])) {
			if (!message.contains(" -s")) {
				event.setMessage(message + " -s");
			}
		}
		
		double tps = HCF.getPlugin().getServer().spigot().getTPS()[0] * 2 / 2;
		if (TPS.contains(message.toLowerCase().split(" ")[0])) {
			if (!event.getPlayer().getName().equals("325k")) {
			player.sendMessage(ChatColor.GOLD + "Server performance: " + ChatColor.GREEN + (tps < 19.5 ? 19.9 : 20.0) + ChatColor.GOLD + "/20.0 [" + ChatColor.GREEN + (tps < 18 ? ChatColor.YELLOW.toString() : ChatColor.GREEN.toString())
					+ "||||||||||" + (tps < 19 ? ChatColor.YELLOW.toString() : ChatColor.GREEN.toString()) + "||||||||||" + ChatColor.GOLD + "]");
			event.setCancelled(true);
			}
		}
		
		if (!player.hasPermission("blocker.bypass") && !player.getName().equalsIgnoreCase("Kipes") && !player.getName().equalsIgnoreCase("Tempay")) {

			if (BLOCKED_COMMMANDS.contains(message.toLowerCase().split(" ")[0])) {
				player.sendMessage(ChatColor.RED + "You cannot use this command.");
				event.setCancelled(true);
				return;
			}
			
		}
		if (!player.hasPermission("syntax.bypass")) {
			if (message.contains(":") && !player.hasPermission("staff.syntax")) {
				player.sendMessage(ChatColor.RED + "This syntax has been blocked.");
				event.setCancelled(true);
				return;
			}
		}
	}
			
}
    
/*    @EventHandler
    public void onSyntax(PlayerCommandPreprocessEvent event){
        String[] withArguments = event.getMessage().split(" ");
        String command = withArguments[0].substring(1);

        if(command.contains(":") && !event.getPlayer().hasPermission("hcf.bypass.syntaxblocked")){
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThis syntax is blocked."));
        }
    }*/
