package com.fishy.hcf.faction.argument;

import java.awt.Color;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import gg.manny.lunar.packet.type.LCPacketUpdateWorld;
import gg.manny.lunar.packet.type.LCPacketWaypointAdd;
import gg.manny.lunar.packet.type.LCPacketWaypointRemove;

import com.fishy.hcf.HCF;
import com.fishy.hcf.faction.FactionMember;
import com.fishy.hcf.faction.type.PlayerFaction;
import com.fishy.hcf.util.CC;
import com.fishy.hcf.util.base.command.CommandArgument;

public class FactionRallyArgument extends CommandArgument{
	
    private final HCF plugin;

    public FactionRallyArgument(HCF plugin) {
        super("rally", "Set a waypoint at your current location.", new String[]{"r"});
        this.plugin = plugin;
    }
    
    @Override
    public String getUsage(String label) {
        return '/' + label + ' ' + getName();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return true;
        }
        Player player = (Player) sender;
        PlayerFaction playerFaction = plugin.getFactionManager().getPlayerFaction(player);

        if (playerFaction == null) {
            sender.sendMessage(ChatColor.RED + "You are not in a faction.");
            return true;
        }

        FactionMember factionMember = playerFaction.getMember(player.getUniqueId());


        
        UUID worldUID = player.getWorld().getUID();
        Location location = player.getLocation();
        
        playerFaction.getOnlinePlayers().forEach(it -> {
        	new LCPacketWaypointRemove("Rally", worldUID).sendTo(it);
        	new LCPacketWaypointAdd("Rally", worldUID, Color.BLUE, location.getBlockX(), location.getBlockY(), location.getBlockZ(), true, true).sendTo(it);
        	new LCPacketUpdateWorld(worldUID).sendTo(it);
        	
        	
        
        });
        
        player.sendMessage(CC.translate("&aYou have successfully set your team's rally point."));
        
        
		HCF.getPlugin().getServer().getScheduler().runTaskLater(HCF.getPlugin(), () -> playerFaction.getOnlinePlayers().forEach(it ->{
			new LCPacketWaypointRemove("Rally", worldUID).sendTo(it);
			new LCPacketUpdateWorld(worldUID).sendTo(it);
		}), TimeUnit.MINUTES.toSeconds(10)*20);
		return true;
	
    }
    
    }

