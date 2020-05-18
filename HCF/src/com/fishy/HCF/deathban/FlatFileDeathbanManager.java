package com.fishy.hcf.deathban;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;

import com.fishy.hcf.HCF;
import com.fishy.hcf.faction.type.Faction;
import com.fishy.hcf.util.base.Config;
import com.fishy.hcf.util.base.JavaUtils;
import com.fishy.hcf.util.base.PersistableLocation;

import gnu.trove.impl.Constants;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import gnu.trove.procedure.TObjectIntProcedure;

public class FlatFileDeathbanManager implements DeathbanManager {

    private static final int MAX_DEATHBAN_MULTIPLIER = 300;

    private final HCF plugin;

    private TObjectIntMap<UUID> livesMap;
    private Config livesConfig;

    public FlatFileDeathbanManager(HCF plugin) {
        this.plugin = plugin;
        reloadDeathbanData();
    }

    @Override
    public TObjectIntMap<UUID> getLivesMap() {
        return livesMap;
    }

    @Override
    public int getLives(UUID uuid) {
        return livesMap.get(uuid);
    }

    @Override
    public int setLives(UUID uuid, int lives) {
        livesMap.put(uuid, lives);
        return lives;
    }

    @Override
    public int addLives(UUID uuid, int amount) {
        return livesMap.adjustOrPutValue(uuid, amount, amount);
    }

    @Override
    public int takeLives(UUID uuid, int amount) {
        return setLives(uuid, getLives(uuid) - amount);
    }

    @Override
    public double getDeathBanMultiplier(Player player) {
        for (int i = 5; i < MAX_DEATHBAN_MULTIPLIER; i++) {
            if (player.hasPermission("hcf.deathban.multiplier." + i)) {
                return (i) / 100.0;
            }
        }

        return 1.0D;
    }

    @Override
    public Deathban applyDeathBan(Player player, String reason) {
        Location location = player.getLocation();
        Faction factionAt = plugin.getFactionManager().getFactionAt(location);
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("DEATHBAN");

        long duration;
        if (plugin.getEotwHandler().isEndOfTheWorld()) {
            duration = MAX_DEATHBAN_TIME;
        } else {
            duration = TimeUnit.MINUTES.toMillis(60L);
            if (!factionAt.isDeathban()) {
                duration /= 2L; // non-deathban factions should be 50% quicker
            } else if (section != null) {
	            for (String key : section.getKeys(false)) {
	                if (player.hasPermission(plugin.getConfig().getString("DEATHBAN." + key + ".PERMISSION"))) {
	                duration = JavaUtils.parse(plugin.getConfig().getString("DEATHBAN." + key + ".TIME"));
	                }
	            }
	        }

            duration *= getDeathBanMultiplier(player);
            duration *= factionAt.getDeathbanMultiplier();
        }

        return applyDeathBan(player.getUniqueId(), new Deathban(reason, Math.min(MAX_DEATHBAN_TIME, duration),
                new PersistableLocation(location), plugin.getEotwHandler().isEndOfTheWorld()));
    }

    @Override
    public Deathban applyDeathBan(UUID uuid, Deathban deathban) {
        plugin.getUserManager().getUser(uuid).setDeathban(deathban);
        return deathban;
    }

    @Override
    public void reloadDeathbanData() {
        livesConfig = new Config(plugin, "lives");
        Object object = livesConfig.get("lives");
        if (object instanceof MemorySection) {
            MemorySection section = (MemorySection) object;
            Set<String> keys = section.getKeys(false);
            livesMap = new TObjectIntHashMap<>(keys.size(), Constants.DEFAULT_LOAD_FACTOR, 0);
            for (String id : keys) {
                livesMap.put(UUID.fromString(id), livesConfig.getInt(section.getCurrentPath() + "." + id));
            }
        } else {
            livesMap = new TObjectIntHashMap<>(Constants.DEFAULT_CAPACITY, Constants.DEFAULT_LOAD_FACTOR, 0);
        }
    }

    @Override
    public void saveDeathbanData() {
        Map<String, Integer> saveMap = new LinkedHashMap<>(livesMap.size());
        livesMap.forEachEntry(new TObjectIntProcedure<UUID>() {
            @Override
            public boolean execute(UUID uuid, int i) {
                saveMap.put(uuid.toString(), i);
                return true;
            }
        });

        livesConfig.set("lives", saveMap);
        livesConfig.save();
    }
}
