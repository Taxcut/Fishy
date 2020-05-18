package com.fishy.hcf.events.crate;

import com.fishy.hcf.HCF;
import com.fishy.hcf.util.base.Config;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import org.bukkit.configuration.MemorySection;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class KeyManager {

    private final EventKey eventKey;

    private final Table<UUID, String, Integer> depositedCrateMap = HashBasedTable.create();
    private final Set<Key> keys;
    private final Config config;

    public KeyManager(HCF plugin) {
        eventKey = new EventKey();

        this.config = new Config(plugin, "key-data");
        this.keys = Sets.newHashSet(eventKey);
        reloadKeyData();
    }
    public Map<String, Integer> getDepositedCrateMap(UUID uuid) {
        return depositedCrateMap.row(uuid);
    }
    public Set<Key> getKeys() {
        return keys;
    }
    public EventKey getEventKey() {
        return eventKey;
    }
    public Key getKey(String name) {
        for (Key key : keys) {
            if (key.getName().equalsIgnoreCase(name)) {
                return key;
            }
        }

        return null;
    }
    @Deprecated
    public Key getKey(Class<? extends Key> clazz) {
        for (Key key : keys) {
            if (clazz.isAssignableFrom(key.getClass())) {
                return key;
            }
        }

        return null;
    }
    public Key getKey(ItemStack stack) {
        if (stack == null || !stack.hasItemMeta()) {
            return null;
        }

        for (Key key : keys) {
            ItemStack item = key.getItemStack();
            if (item.getItemMeta().getDisplayName().equals(stack.getItemMeta().getDisplayName())) {
                return key;
            }
        }

        return null;
    }
    public void reloadKeyData() {
        for (Key key : keys) {
            key.load(config);
        }

        Object object = config.get("deposited-key-map");
        if (object instanceof MemorySection) {
            MemorySection section = (MemorySection) object;
            for (String id : section.getKeys(false)) {
                object = config.get(section.getCurrentPath() + '.' + id);
                if (object instanceof MemorySection) {
                    section = (MemorySection) object;
                    for (String key : section.getKeys(false)) {
                        depositedCrateMap.put(UUID.fromString(id), key, config.getInt("deposited-key-map." + id + '.' + key));
                    }
                }
            }
        }
    }
    public void saveKeyData() {
        for (Key key : keys) {
            key.save(config);
        }

        Map<String, Map<String, Integer>> saveMap = new LinkedHashMap<>(depositedCrateMap.size());
        for (Map.Entry<UUID, Map<String, Integer>> entry : depositedCrateMap.rowMap().entrySet()) {
            saveMap.put(entry.getKey().toString(), entry.getValue());
        }

        config.set("deposited-key-map", saveMap);
        config.save();
    }
}