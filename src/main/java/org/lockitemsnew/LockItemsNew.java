package org.lockitemsnew;

import org.bukkit.plugin.java.JavaPlugin;

public class LockItemsNew extends JavaPlugin {

    private ConfigManager configManager;
    private MessageManager messageManager;

    @Override
    public void onEnable() {
        // Инициализация менеджеров
        configManager = new ConfigManager(this);
        messageManager = new MessageManager(this);

        // Регистрация слушателей
        getServer().getPluginManager().registerEvents(new LockItemsNewListener(this), this);

        // Регистрация команд
        if (getCommand("lockitemsnew") != null) {
            getCommand("lockitemsnew").setExecutor(new LockItemsNewCommandExecutor(this));
            // Если нужен TabCompleter, добавьте его реализацию
        }
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public void reloadPluginConfigs() {
        // Перезагружаем config.yml и messages.yml
        configManager.loadConfig();
        messageManager.reloadMessages();
    }
}
