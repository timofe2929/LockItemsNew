package org.lockitemsnew;

public class VersionUtil {

    public static boolean isVersionAboveOrEqual(String version) {
        String serverVersion = org.bukkit.Bukkit.getBukkitVersion().split("-")[0]; // Получаем основную версию
        return serverVersion.compareTo(version) >= 0;
    }
}
