package com.fishy.hcf.events;

import com.fishy.hcf.DateTimeFormats;
import com.fishy.hcf.util.base.cuboid.Cuboid;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;

public class CaptureZone implements ConfigurationSerializable {

    public static final int MINIMUM_SIZE_AREA = 2;

    private final Object lock = new Object();
    private String scoreboardRemaining;
    private String name;
    private String prefix;
    private Cuboid cuboid;
    private Player cappingPlayer;

    private long defaultCaptureMillis;
    private String defaultCaptureWords;
    private long endMillis;

    public CaptureZone(String name, Cuboid cuboid, long defaultCaptureMillis) {
        this(name, "", cuboid, defaultCaptureMillis);
    }
    public CaptureZone(String name, String prefix, Cuboid cuboid, long defaultCaptureMillis) {
        this.name = name;
        this.prefix = prefix;
        this.cuboid = cuboid;
        this.setDefaultCaptureMillis(defaultCaptureMillis);
    }

    public CaptureZone(Map<String, Object> map) {
        this.name = (String) map.get("name");

        Object obj = map.get("prefix");
        if (obj instanceof String) {
            this.prefix = (String) obj;
        }

        obj = map.get("cuboid");
        if (obj instanceof Cuboid) {
            this.cuboid = (Cuboid) obj;
        }

        this.setDefaultCaptureMillis(Long.parseLong((String) map.get("captureMillis")));
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("name", name);

        if (prefix != null) {
            map.put("prefix", prefix);
        }

        if (cuboid != null) {
            map.put("cuboid", cuboid);
        }

        map.put("captureMillis", Long.toString(defaultCaptureMillis));
        return map;
    }

    public String getScoreboardRemaining() {
        synchronized (lock) {
            return scoreboardRemaining;
        }
    }

    public void updateScoreboardRemaining() {
        synchronized (lock) {
            scoreboardRemaining = DateTimeFormats.KOTH_FORMAT.format(getRemainingCaptureMillis());
        }
    }

    public boolean isActive() {
        return getRemainingCaptureMillis() > 0L;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrefix() {
        if (prefix == null) {
            prefix = ""; // safeguard
        }

        return prefix;
    }

    public String getDisplayName() {
        return getPrefix() + name;
    }
    public Cuboid getCuboid() {
        return cuboid;
    }
    public long getRemainingCaptureMillis() {
        if (endMillis == Long.MIN_VALUE) {
            return -1L;
        } else if (cappingPlayer == null) {
            return defaultCaptureMillis;
        } else {
            return endMillis - System.currentTimeMillis();
        }
    }

    public void setRemainingCaptureMillis(long millis) {
        endMillis = System.currentTimeMillis() + millis;
    }

    public long getDefaultCaptureMillis() {
        return defaultCaptureMillis;
    }

    public String getDefaultCaptureWords() {
        return defaultCaptureWords;
    }

    public void setDefaultCaptureMillis(long millis) {
        if (defaultCaptureMillis != millis) {
            defaultCaptureMillis = millis;
            defaultCaptureWords = DurationFormatUtils.formatDurationWords(millis, true, true);
        }
    }

    public Player getCappingPlayer() {
        return cappingPlayer;
    }

    public void setCappingPlayer(@Nullable Player player) {
        cappingPlayer = player;
        if (player == null) {
            endMillis = defaultCaptureMillis;
        } else {
            endMillis = System.currentTimeMillis() + defaultCaptureMillis;
        }
    }
}
