package com.fishy.hcf.events.faction;

import com.fishy.hcf.HCF;
import com.fishy.hcf.events.CaptureZone;
import com.fishy.hcf.events.EventType;
import com.fishy.hcf.faction.claim.Claim;
import com.fishy.hcf.faction.type.ClaimableFaction;
import com.fishy.hcf.faction.type.PlayerFaction;
import com.fishy.hcf.util.base.BukkitUtils;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public class KothFaction extends CapturableFaction implements ConfigurationSerializable {

    private CaptureZone captureZone;

    public KothFaction(String name) {
        super(name);
    }

    public KothFaction(Map<String, Object> map) {
        super(map);
        this.captureZone = (CaptureZone) map.get("captureZone");
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = super.serialize();
        map.put("captureZone", captureZone);
        return map;
    }

    @Override
    public List<CaptureZone> getCaptureZones() {
        return captureZone == null ? ImmutableList.of() : ImmutableList.of(captureZone);
    }

    @Override
    public EventType getEventType() {
        return EventType.KOTH;
    }

    @Override
    public void printDetails(CommandSender sender) {
        sender.sendMessage(ChatColor.DARK_GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
        sender.sendMessage(getDisplayName(sender));

        for (Claim claim : claims) {
            Location location = claim.getCenter();
            sender.sendMessage(ChatColor.WHITE + "  Location : " + ChatColor.RED + ClaimableFaction.ENVIRONMENT_MAPPINGS.get(location.getWorld().getEnvironment()) + ", " + location.getBlockX() + " | " + location.getBlockZ());
        }

        if (captureZone != null) {
            long remainingCaptureMillis = captureZone.getRemainingCaptureMillis();
            long defaultCaptureMillis = captureZone.getDefaultCaptureMillis();
            if (remainingCaptureMillis > 0L && remainingCaptureMillis != defaultCaptureMillis) {
                sender.sendMessage(ChatColor.WHITE + "  Time Left: " + ChatColor.GRAY + DurationFormatUtils.formatDurationWords(remainingCaptureMillis, true, true));
            }

            sender.sendMessage(ChatColor.WHITE + "  Original Time: " + ChatColor.GRAY + captureZone.getDefaultCaptureWords());
            if (captureZone.getCappingPlayer() != null && sender.hasPermission("hcf.koth.checkcapper")) {
                Player capping = captureZone.getCappingPlayer();
                PlayerFaction playerFaction = HCF.getPlugin().getFactionManager().getPlayerFaction(capping);
                String factionTag = "[" + (playerFaction == null ? "*" : playerFaction.getName()) + "]";
                sender.sendMessage(ChatColor.WHITE + "  Current Capper: " + ChatColor.WHITE + capping.getName() + ChatColor.GRAY + " " + factionTag);
            }
        }

        sender.sendMessage(ChatColor.DARK_GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
    }
    public CaptureZone getCaptureZone() {
        return captureZone;
    }
    public void setCaptureZone(CaptureZone captureZone) {
        this.captureZone = captureZone;
    }
}
