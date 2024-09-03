package org.lockitemsnew;

import org.bukkit.ChatColor;

public class ColorUtil {

    public static String colorize(String message) {
        if (message == null) {
            return null;
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}