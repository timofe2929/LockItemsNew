package org.lockitemsnew;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class MessageManager {

    private final LockItemsNew plugin;
    private FileConfiguration messages;

    public MessageManager(LockItemsNew plugin) {
        this.plugin = plugin;
        reloadMessages(); // Загружаем сообщения при инициализации
    }

    public void reloadMessages() {
        File messageFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messageFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        messages = YamlConfiguration.loadConfiguration(messageFile);
    }

    public String getColoredMessage(String path) {
        String message = messages.getString(path);
        if (message == null) {
            return "&cMessages not found: " + path; // Если сообщение не найдено
        }
        return ColorUtil.colorize(message);
    }
}