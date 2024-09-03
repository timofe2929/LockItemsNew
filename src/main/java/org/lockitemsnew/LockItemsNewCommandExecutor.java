package org.lockitemsnew;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class LockItemsNewCommandExecutor implements CommandExecutor {

    private final LockItemsNew plugin;

    public LockItemsNewCommandExecutor(LockItemsNew plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("lockitemsnew")) {
            if (args.length > 0) {
                switch (args[0].toLowerCase()) {
                    case "give":
                        return handleGiveCommand(sender, args);
                    case "reload":
                        return handleReloadCommand(sender);
                    default:
                        return false;
                }
            }
        }
        return false;
    }

    private boolean handleGiveCommand(CommandSender sender, String[] args) {
        if (args.length != 4) {
            return false;
        }

        if (sender.hasPermission("lockitemsnew.give")) {
            Player target = Bukkit.getPlayer(args[2]);
            if (target == null) {
                sender.sendMessage(plugin.getMessageManager().getColoredMessage("item_not_found").replace("{item}", args[1]));
                return true;
            }

            int amount = Integer.parseInt(args[3]);
            String itemKey = args[1].toLowerCase();
            giveItem(target, itemKey, amount);
            sender.sendMessage(plugin.getMessageManager().getColoredMessage("item_given").replace("{item}", itemKey));
            return true;
        } else {
            sender.sendMessage(plugin.getMessageManager().getColoredMessage("no_permission"));
            return true;
        }
    }

    private boolean handleReloadCommand(CommandSender sender) {
        if (sender.hasPermission("lockitemsnew.reload")) {
            plugin.reloadPluginConfigs();
            sender.sendMessage(plugin.getMessageManager().getColoredMessage("reload_success"));
            return true;
        } else {
            sender.sendMessage(plugin.getMessageManager().getColoredMessage("no_permission"));
            return true;
        }
    }

    private void giveItem(Player player, String itemKey, int amount) {
        Material material = Material.valueOf(plugin.getConfigManager().getConfig().getString("items." + itemKey + ".material"));
        String name = plugin.getConfigManager().getColoredString("items." + itemKey + ".name");

        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            item.setItemMeta(meta);
        }

        player.getInventory().addItem(item);
    }
}