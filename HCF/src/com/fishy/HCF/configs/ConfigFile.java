package com.fishy.hcf.configs;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.fishy.hcf.HCF;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
public class ConfigFile {

    @Getter
    private static Set<ConfigFile> files = new HashSet<>();

    private final String name;
    private final File file;
    private final FileConfiguration config;

    public ConfigFile(String name) {
        this.name = name;
        this.file = new File(HCF.getPlugin().getDataFolder(), name);
        this.config = YamlConfiguration.loadConfiguration(file);
        HCF.getPlugin().getLogger().info("Loaded file " + name);
        files.add(this);
    }

    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveDefault() {
        HCF.getPlugin().saveResource(name, false);
    }

    public List<String> getStringList(String path) {
        List<String> toReturn = new ArrayList<>();

        for (String str : config.getStringList(path)) {
            toReturn.add( ChatColor.translateAlternateColorCodes('&', str));
        }

        return toReturn;
    }

    public String getString(String path) {
        return ChatColor.translateAlternateColorCodes('&', config.getString(path));
    }

    public Double getDouble(String path) {
        return config.getDouble(path);
    }

    public int getInt(String s) {
        return config.getInt(s);
    }
}
