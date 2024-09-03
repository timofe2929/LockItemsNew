package org.lockitemsnew;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ConfigManager {

    private final LockItemsNew plugin;
    private FileConfiguration config;

    public ConfigManager(LockItemsNew plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public void loadConfig() {
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public void saveConfig() {
        try {
            config.save(new File(plugin.getDataFolder(), "config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getColoredString(String path) {
        return ColorUtil.colorize(config.getString(path));
    }

    public List<String> getDisabledWorlds() {
        return config.getStringList("disabled_worlds");
    }
}
