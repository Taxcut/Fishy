package com.fishy.hcf.listener.fixes;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockRedstoneEvent;

import com.fishy.hcf.HCF;

import lombok.Getter;

public class RedstoneReducer implements Listener {

    public static final int MAX_TICKS = 500;
    @Getter
    private static final Map<Chunk, Integer> chunks = new HashMap<>();

    public RedstoneReducer() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(HCF.getPlugin(), () -> {
            RedstoneReducer.getChunks().clear();
        }, 20L, 20L);

    }

    @EventHandler
    public void onRedstone(BlockRedstoneEvent event) {
        final Chunk chunk = event.getBlock().getChunk();

        if (!chunks.containsKey(chunk)) {
            chunks.put(chunk, 0);
        }

        chunks.put(chunk, chunks.get(chunk) + 1);

        if (chunks.get(chunk) >= MAX_TICKS) {
            event.setNewCurrent(0);

            handleExcess(chunk);
        }

    }

    @EventHandler
    public void onPiston(BlockPistonExtendEvent event) {
        final Chunk chunk = event.getBlock().getChunk();

        if (!chunks.containsKey(chunk)) {
            chunks.put(chunk, 0);
        }

        chunks.put(chunk, chunks.get(chunk) + 1);

        if (chunks.get(chunk) >= MAX_TICKS) {
            event.setCancelled(true);

            handleExcess(chunk);
        }

    }

    @EventHandler
    public void onPiston(BlockPistonRetractEvent event) {
        final Chunk chunk = event.getBlock().getChunk();

        if (!chunks.containsKey(chunk)) {
            chunks.put(chunk, 0);
        }

        chunks.put(chunk, chunks.get(chunk) + 1);

        if (chunks.get(chunk) >= MAX_TICKS) {
            event.setCancelled(true);

            handleExcess(chunk);
        }

    }

    public void handleExcess(Chunk chunk) {
        if (chunks.get(chunk) >= MAX_TICKS * 2) {
            Player player = null;

            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (onlinePlayer.getLocation().getChunk() == chunk) {
                    player = onlinePlayer;
                }
            }

            System.out.println("Found chunk with excess redstone ticks. Location: " + (player == null ? "Could not find player within chunk" : player.getLocation().toString()));
        }
    }

}
