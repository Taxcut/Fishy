package com.fishy.hcf.listener.fixes;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import com.fishy.hcf.HCF;

import java.util.regex.Pattern;

public class NameVerifyListener implements Listener {
    public static final Pattern NAME_PATTERN;

    static {
        NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{1,16}$");
    }

    private final HCF plugin;

    public NameVerifyListener(final HCF plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerLogin(final PlayerLoginEvent event) {
        final PlayerLoginEvent.Result result = event.getResult();
        if (result == PlayerLoginEvent.Result.ALLOWED) {
            final Player player = event.getPlayer();
            final String playerName = player.getName();
            if (!NameVerifyListener.NAME_PATTERN.matcher(playerName).matches()) {
                this.plugin.getLogger().info("Name verification: " + playerName + " was kicked for having an invalid name " + "(to disable, turn off the name-verification feature in the config of 'HCF' plugin)");
                event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "Invalid player name detected.");
            }
        }
    }
}
