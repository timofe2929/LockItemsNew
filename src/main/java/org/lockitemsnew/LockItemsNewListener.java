package org.lockitemsnew;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class LockItemsNewListener implements Listener, CommandExecutor, TabCompleter {

    private final LockItemsNew plugin;
    private final Map<UUID, Map<String, Long>> cooldowns = new HashMap<>();

    public LockItemsNewListener(LockItemsNew plugin) {
        this.plugin = plugin;
        plugin.getCommand("aura").setExecutor(this);
        plugin.getCommand("aura").setTabCompleter(this);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        String worldName = player.getWorld().getName();
        List<String> disabledWorlds = plugin.getConfigManager().getDisabledWorlds();

        // Проверяем, находится ли мир в списке отключенных
        if (disabledWorlds.contains(worldName)) {
            return; // Игнорируем действия в отключенных мирах
        }

        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            ItemStack item = player.getInventory().getItemInMainHand();
            if (item.getType() == Material.AIR) {
                return;
            }

            ItemMeta meta = item.getItemMeta();
            if (meta == null || !meta.hasDisplayName()) {
                return;
            }

            String displayName = meta.getDisplayName();

            // Проверяем, является ли этот предмет одним из тех, для которых предусмотрен кулдаун
            if (displayName.equals(plugin.getConfigManager().getColoredString("items.diz.name")) ||
                    displayName.equals(plugin.getConfigManager().getColoredString("items.dust.name")) ||
                    displayName.equals(plugin.getConfigManager().getColoredString("items.feer.name")) ||
                    displayName.equals(plugin.getConfigManager().getColoredString("items.smerch.name")) ||
                    displayName.equals(plugin.getConfigManager().getColoredString("items.handaura.name"))) {

                if (checkCooldown(player, displayName)) {
                    player.sendMessage(plugin.getMessageManager().getColoredMessage("cooldown")
                            .replace("{time}", String.valueOf(getRemainingCooldown(player, displayName))));
                    return;
                }

                // Выполняем соответствующее действие в зависимости от предмета
                if (displayName.equals(plugin.getConfigManager().getColoredString("items.diz.name"))) {
                    handleDiz(player, item);
                } else if (displayName.equals(plugin.getConfigManager().getColoredString("items.dust.name"))) {
                    handleDust(player, item);
                } else if (displayName.equals(plugin.getConfigManager().getColoredString("items.feer.name"))) {
                    handleFeer(player, item);
                } else if (displayName.equals(plugin.getConfigManager().getColoredString("items.smerch.name"))) {
                    handleSmerch(player, item);
                } else if (displayName.equals(plugin.getConfigManager().getColoredString("items.handaura.name"))) {
                    handleHandaura(player, item);
                }

                setCooldown(player, displayName);
            }
        }
    }

    private boolean checkCooldown(Player player, String itemName) {
        if (!cooldowns.containsKey(player.getUniqueId())) {
            return false;
        }
        Map<String, Long> playerCooldowns = cooldowns.get(player.getUniqueId());
        if (!playerCooldowns.containsKey(itemName)) {
            return false;
        }
        long cooldownEndTime = playerCooldowns.get(itemName);
        return cooldownEndTime > System.currentTimeMillis();
    }

    private long getRemainingCooldown(Player player, String itemName) {
        Map<String, Long> playerCooldowns = cooldowns.get(player.getUniqueId());
        long cooldownEndTime = playerCooldowns.get(itemName);
        return (cooldownEndTime - System.currentTimeMillis()) / 1000;
    }

    private void setCooldown(Player player, String itemName) {
        int cooldownTime = plugin.getConfigManager().getConfig().getInt("items." + itemName.toLowerCase() + ".cooldown", 15);
        cooldowns.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>()).put(itemName, System.currentTimeMillis() + (cooldownTime * 1000));
    }

    private void playItemSound(Player player, String itemKey) {
        String soundName = plugin.getConfigManager().getConfig().getString("items." + itemKey + ".sound");
        if (soundName != null) {
            Sound sound = Sound.valueOf(soundName);
            float volume = (float) plugin.getConfigManager().getConfig().getDouble("items." + itemKey + ".sound_volume", 1.0);
            float pitch = (float) plugin.getConfigManager().getConfig().getDouble("items." + itemKey + ".sound_pitch", 1.0);
            player.playSound(player.getLocation(), sound, volume, pitch);
        }
    }

    // Обработка команды для получения предмета
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("aura")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                ItemStack auraItem = createHandAuraItem();
                player.getInventory().addItem(auraItem);
                player.sendMessage("&aВам успешно был выдан предмет aura");
            } else {
                sender.sendMessage("This command can only be executed by a player.");
            }
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equalsIgnoreCase("aura")) {
            return Collections.emptyList(); // Автодополнение не требуется
        }
        return null;
    }

    // Создание предмета Hand Aura
    private ItemStack createHandAuraItem() {
        String itemName = plugin.getConfigManager().getColoredString("items.handaura.name");
        String materialName = plugin.getConfigManager().getConfig().getString("items.handaura.material", "BLAZE_POWDER");
        Material material = Material.valueOf(materialName);
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(itemName);
            item.setItemMeta(meta);
        }
        return item;
    }

    private void handleDiz(Player player, ItemStack item) {
        String itemKey = "diz";
        playItemSound(player, itemKey);

        int radius = plugin.getConfigManager().getConfig().getInt("items.diz.radius");
        int blindnessDuration = plugin.getConfigManager().getConfig().getInt("items.diz.effects.blindness");
        int witherDuration = plugin.getConfigManager().getConfig().getInt("items.diz.effects.wither");
        int slownessDuration = plugin.getConfigManager().getConfig().getInt("items.diz.effects.slowness");
        int fatigueDuration = plugin.getConfigManager().getConfig().getInt("items.diz.effects.fatigue");
        boolean glow = plugin.getConfigManager().getConfig().getBoolean("items.diz.glow");

        item.setAmount(item.getAmount() - 1);

        Particle particleType = Particle.valueOf(plugin.getConfigManager().getConfig().getString("items.diz.particle_type", "SPELL"));
        int particleCount = plugin.getConfigManager().getConfig().getInt("items.diz.particle_count", 100);
        double particleOffset = plugin.getConfigManager().getConfig().getDouble("items.diz.particle_offset", 0.1);

        player.getWorld().spawnParticle(particleType, player.getLocation(), particleCount, particleOffset, particleOffset, particleOffset, 0.1);
        for (Player p : player.getWorld().getPlayers()) {
            if (p != player && p.getLocation().distance(player.getLocation()) <= radius) {
                p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, blindnessDuration * 20, 1));
                p.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, witherDuration * 20, 1));
                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, slownessDuration * 20, 1));
                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, fatigueDuration * 20, 1));
                if (glow) {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 1000000, 1)); // Большое время, чтобы всегда был эффект
                }
            }
        }
    }

    private void handleDust(Player player, ItemStack item) {
        String itemKey = "dust";
        playItemSound(player, itemKey);

        int radius = plugin.getConfigManager().getConfig().getInt("items.dust.radius");
        int levitationDuration = plugin.getConfigManager().getConfig().getInt("items.dust.effects.levitation");
        int confusionDuration = plugin.getConfigManager().getConfig().getInt("items.dust.effects.confusion");
        int weaknessDuration = plugin.getConfigManager().getConfig().getInt("items.dust.effects.weakness");
        int hungerDuration = plugin.getConfigManager().getConfig().getInt("items.dust.effects.hunger");
        boolean glow = plugin.getConfigManager().getConfig().getBoolean("items.dust.glow");

        item.setAmount(item.getAmount() - 1);

        Particle particleType = Particle.valueOf(plugin.getConfigManager().getConfig().getString("items.dust.particle_type", "SPELL"));
        int particleCount = plugin.getConfigManager().getConfig().getInt("items.dust.particle_count", 100);
        double particleOffset = plugin.getConfigManager().getConfig().getDouble("items.dust.particle_offset", 0.1);

        player.getWorld().spawnParticle(particleType, player.getLocation(), particleCount, particleOffset, particleOffset, particleOffset, 0.1);
        for (Player p : player.getWorld().getPlayers()) {
            if (p != player && p.getLocation().distance(player.getLocation()) <= radius) {
                p.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, levitationDuration * 20, 1));
                p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, confusionDuration * 20, 1));
                p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, weaknessDuration * 20, 1));
                p.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, hungerDuration * 20, 1));
                if (glow) {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 1000000, 1));
                }
            }
        }
    }

    private void handleFeer(Player player, ItemStack item) {
        String itemKey = "feer";
        playItemSound(player, itemKey);

        int radius = plugin.getConfigManager().getConfig().getInt("items.feer.radius");
        int damage = plugin.getConfigManager().getConfig().getInt("items.feer.damage");
        int weaknessDuration = plugin.getConfigManager().getConfig().getInt("items.feer.effects.weakness");
        int slownessDuration = plugin.getConfigManager().getConfig().getInt("items.feer.effects.slowness");
        int fatigueDuration = plugin.getConfigManager().getConfig().getInt("items.feer.effects.fatigue");
        boolean glow = plugin.getConfigManager().getConfig().getBoolean("items.feer.glow");

        item.setAmount(item.getAmount() - 1);

        Particle particleType = Particle.valueOf(plugin.getConfigManager().getConfig().getString("items.feer.particle_type", "SPELL"));
        int particleCount = plugin.getConfigManager().getConfig().getInt("items.feer.particle_count", 100);
        double particleOffset = plugin.getConfigManager().getConfig().getDouble("items.feer.particle_offset", 0.1);

        player.getWorld().spawnParticle(particleType, player.getLocation(), particleCount, particleOffset, particleOffset, particleOffset, 0.1);
        for (Player p : player.getWorld().getPlayers()) {
            if (p != player && p.getLocation().distance(player.getLocation()) <= radius) {
                p.damage(damage, player);
                p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, weaknessDuration * 20, 1));
                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, slownessDuration * 20, 1));
                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, fatigueDuration * 20, 1));
                if (glow) {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 1000000, 1));
                }
            }
        }
    }

    private void handleSmerch(Player player, ItemStack item) {
        String itemKey = "smerch";
        playItemSound(player, itemKey);

        int radius = plugin.getConfigManager().getConfig().getInt("items.smerch.radius");
        int damage = plugin.getConfigManager().getConfig().getInt("items.smerch.damage");
        int nauseaDuration = plugin.getConfigManager().getConfig().getInt("items.smerch.effects.nausea");
        int blindnessDuration = plugin.getConfigManager().getConfig().getInt("items.smerch.effects.blindness");
        int hungerDuration = plugin.getConfigManager().getConfig().getInt("items.smerch.effects.hunger");
        boolean glow = plugin.getConfigManager().getConfig().getBoolean("items.smerch.glow");

        item.setAmount(item.getAmount() - 1);

        Particle particleType = Particle.valueOf(plugin.getConfigManager().getConfig().getString("items.smerch.particle_type", "SPELL"));
        int particleCount = plugin.getConfigManager().getConfig().getInt("items.smerch.particle_count", 100);
        double particleOffset = plugin.getConfigManager().getConfig().getDouble("items.smerch.particle_offset", 0.1);

        player.getWorld().spawnParticle(particleType, player.getLocation(), particleCount, particleOffset, particleOffset, particleOffset, 0.1);
        for (Player p : player.getWorld().getPlayers()) {
            if (p != player && p.getLocation().distance(player.getLocation()) <= radius) {
                p.damage(damage, player);
                p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, nauseaDuration * 20, 1));
                p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, blindnessDuration * 20, 1));
                p.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, hungerDuration * 20, 1));
                if (glow) {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 1000000, 1));
                }
            }
        }
    }

    private void handleHandaura(Player player, ItemStack item) {
        String itemKey = "handaura";
        playItemSound(player, itemKey);

        int radius = plugin.getConfigManager().getConfig().getInt("items.handaura.radius");
        int damage = plugin.getConfigManager().getConfig().getInt("items.handaura.damage");
        int weaknessDuration = plugin.getConfigManager().getConfig().getInt("items.handaura.effects.weakness");
        int slownessDuration = plugin.getConfigManager().getConfig().getInt("items.handaura.effects.slowness");
        int fatigueDuration = plugin.getConfigManager().getConfig().getInt("items.handaura.effects.fatigue");
        boolean glow = plugin.getConfigManager().getConfig().getBoolean("items.handaura.glow");

        item.setAmount(item.getAmount() - 1);

        Particle particleType = Particle.valueOf(plugin.getConfigManager().getConfig().getString("items.handaura.particle_type", "SPELL"));
        int particleCount = plugin.getConfigManager().getConfig().getInt("items.handaura.particle_count", 100);
        double particleOffset = plugin.getConfigManager().getConfig().getDouble("items.handaura.particle_offset", 0.1);

        player.getWorld().spawnParticle(particleType, player.getLocation(), particleCount, particleOffset, particleOffset, particleOffset, 0.1);
        for (Player p : player.getWorld().getPlayers()) {
            if (p != player && p.getLocation().distance(player.getLocation()) <= radius) {
                p.damage(damage, player);
                p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, weaknessDuration * 20, 1));
                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, slownessDuration * 20, 1));
                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, fatigueDuration * 20, 1));
                if (glow) {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 1000000, 1));
                }
            }
        }
    }
}
