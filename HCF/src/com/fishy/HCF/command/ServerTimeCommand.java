package com.fishy.hcf.command;

import com.fishy.hcf.HCF;
import org.apache.commons.lang3.time.FastDateFormat;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Command used to check the current time for the server.
 */
public class ServerTimeCommand implements CommandExecutor, TabCompleter {

    private final FastDateFormat format;

    public ServerTimeCommand(HCF plugin) {
        format = FastDateFormat.getInstance("E MMM dd h:mm:ssa z yyyy", TimeZone.getTimeZone("EST"), Locale.ENGLISH);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(ChatColor.WHITE + "The server time is " + ChatColor.GRAY + format.format(System.currentTimeMillis()) + ChatColor.WHITE + '.');
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }
}
