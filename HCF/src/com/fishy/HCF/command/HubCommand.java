package com.fishy.hcf.command;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.fishy.hcf.HCF;

import net.minecraft.util.org.apache.commons.io.output.ByteArrayOutputStream;

public class HubCommand implements CommandExecutor{
	
	Random random = new Random();

    private final HCF plugin;
    public HubCommand(final HCF plugin) {
        this.plugin = plugin;
    }

    public static void teleport(Player pl, String input, String reason)
    {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF("server");
            out.writeUTF(input);
        }
        catch (IOException localIOException) {}
        pl.sendMessage(reason);
        pl.sendPluginMessage(HCF.getPlugin(), "BungeeCord", b.toByteArray());
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return true;
        }
        Player p = (Player)sender;
        String hub_name = "hub" + random.nextInt(3);
        teleport(p, hub_name, ChatColor.WHITE + "You have been sent to a hub.");
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(HCF.getPlugin(), new Runnable(){
            public void run(){
                if(p.isOnline()){
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&',"&cThere are no hubs currently online."));
                }
            }
        }, 20 * 5);
        return true;
    }

}
