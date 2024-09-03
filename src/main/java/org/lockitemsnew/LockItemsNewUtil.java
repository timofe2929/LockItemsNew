package org.lockitemsnew;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class LockItemsNewUtil {

    public static ItemStack createItem(String materialName, String displayName, int amount) {
        Material material = Material.matchMaterial(materialName.toUpperCase());
        if (material == null) {
            return null;
        }

        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(displayName);
            item.setItemMeta(meta);
        }

        return item;
    }
}
