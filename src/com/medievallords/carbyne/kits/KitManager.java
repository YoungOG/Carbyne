package com.medievallords.carbyne.kits;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.profiles.Profile;
import com.medievallords.carbyne.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class KitManager {

    private File file;
    private FileConfiguration config;
    private final List<Kit> kits = new ArrayList<>();

    public KitManager() {
        file = new File(Carbyne.getInstance().getDataFolder(), "kits.yml");
        config = YamlConfiguration.loadConfiguration(file);
        loadKits();
    }

    public void loadKits() {
        if (!config.contains("kits"))
            return;

        for (String kitName : config.getConfigurationSection("kits").getKeys(false)) {
            Kit kit = new Kit(kitName);
            String materialData = config.contains("kits." + kitName + ".materialData") ? config.getString("kits." + kitName + ".materialData") : "";
            int cost = config.contains("kits." + kitName + ".cost") ? config.getInt("kits." + kitName + ".cost") : 0;
            int delay = config.contains("kits." + kitName + ".delay") ? config.getInt("kits." + kitName + ".delay") : 0;
            boolean isHidden = config.contains("kits." + kitName + ".isHidden") && config.getBoolean("kits." + kitName + ".isHidden");
            List<ItemStack> kitContents = (List<ItemStack>) config.getList("kits." + kitName + ".inventory");

            kit.setMaterialData(materialData);
            kit.setContents(kitContents);
            kit.setCost(cost);
            kit.setDelay(delay);
            kit.setHidden(isHidden);

            kits.add(kit);
        }

        List<String> kitNames = new ArrayList<>();
        for (Kit kit : kits)
            kitNames.add(kit.getName());

        if (kits.size() != 0)
            Carbyne.getInstance().getLogger().log(Level.INFO, "Kits Loaded: " + kitNames.toString().replace("[", "").replace("]", ""));
    }

    public void saveAllKits() {
        config.set("kits", null);

        for (Kit kit : kits) {
            config.set("kits." + kit.getName() + ".materialData", kit.getMaterialData());
            config.set("kits." + kit.getName() + ".cost", kit.getCost());
            config.set("kits." + kit.getName() + ".delay", kit.getDelay());
            config.set("kits." + kit.getName() + ".inventory", kit.getContents());
            config.set("kits." + kit.getName() + ".isHidden", kit.isHidden());
        }

        try {
            config.save(file);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        List<String> kitNames = kits.stream().map(Kit::getName).collect(Collectors.toList());

        if (kits.size() != 0)
            Carbyne.getInstance().getLogger().log(Level.INFO, "Kits Saved: " + kitNames.toString().replace("[", "").replace("]", ""));
    }

    public void openKitMenuGui(Player player) {
        Inventory inventory = Bukkit.createInventory(player, NumberUtil.getCompatibleSize(kits.size()), ChatColor.translateAlternateColorCodes('&', "&5&lKit Selection"));
        Profile profile = StaticClasses.profileManager.getProfile(player.getUniqueId());

        for (int i = 0; i < inventory.getSize(); i++)
            inventory.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE).durability(7).name("&7").build());

        for (int i = 0; i < kits.size(); i++) {
            Kit kit = kits.get(i);

            if (kit.isHidden())
                continue;

            long cooldown = kit.getNextUse(profile);
            boolean canClaim = cooldown == 0;
            String[] materialData = kit.getMaterialData().split(";");
            int data = Integer.parseInt(materialData[1]);
            Material material = Material.valueOf(materialData[0]);
            if (!canClaim) {
                material = Material.REDSTONE_BLOCK;
                data = 0;
            }

            String status = (canClaim ? "&aAvailable" : (cooldown > 0 ? "&7Available in: " + DateUtil.readableTime(cooldown, false) + "&7." : "&7Unavailable"));
            if (!player.hasPermission("carbyne.kits." + kit.getName()) && !player.hasPermission("carbyne.kits.*"))
                status = "&7Unavailable";

            ItemBuilder itemBuilder = new ItemBuilder(material).durability(data);
            itemBuilder.name("&b&l" + kit.getName()).addLore(" ").addLore("&6Kit Information: ");

            if (kit.getCost() > 0) {
                itemBuilder.addLore("  &6* &eKitpoint Cost&7: " + kit.getCost());
            }

            if (kit.getDelay() == -1)
                itemBuilder.addLore("  &6* &eOne Time Use ");
            else
                itemBuilder.addLore("  &6* &eCooldown&7: " + (kit.getDelay() > 0 ? DateUtil.readableTime((long) kit.getDelay() * 1000, false) : "none"));

            itemBuilder.addLore("  &6* &eNumber of Items&7: " + kit.getContents().size())
                    .addLore(" ")
                    .addLore("&eStatus&7: " + status)
                    .addLore(" ")
                    .addLore("&7&o&nRight-Click&r &7&oto view the contents of this kit.");

            inventory.setItem(i, itemBuilder.build());
        }

        new BukkitRunnable() {
            public void run() {
                if (inventory.getViewers().size() == 0)
                    cancel();
                else {
                    for (int i = 0; i < inventory.getSize(); i++) {
                        ItemStack itemStack = inventory.getItem(i);
                        if (itemStack == null) continue;

                        Kit kit = getKit(ChatColor.stripColor(itemStack.getItemMeta().getDisplayName()));
                        if (kit == null) continue;

                        if (kit.getDelay() == -1)
                            return;

                        long cooldown = kit.getNextUse(profile);
                        boolean canClaim = cooldown == 0;
                        String[] materialData = kit.getMaterialData().split(";");
                        int data = Integer.parseInt(materialData[1]);
                        Material material = Material.valueOf(materialData[0]);
                        if (!canClaim) {
                            material = Material.REDSTONE_BLOCK;
                            data = 0;
                        }

                        if (itemStack.getType() != material) {
                            itemStack.setType(material);
                            itemStack.setDurability((short) data);
                        }

                        String status = (canClaim ? "&aAvailable" : (cooldown > 0 ? "&7Available in: " + DateUtil.readableTime(cooldown, false) + "&7." : "&7Unavailable"));
                        if (!player.hasPermission("carbyne.kits." + kit.getName()) && !player.hasPermission("carbyne.kits.*"))
                            status = "&7Unavailable";

                        Namer.setLore(itemStack, "&eStatus&7: " + status, kit.getCost() > 0 ? 6 : 5);
                    }
                }
            }
        }.runTaskTimerAsynchronously(Carbyne.getInstance(), 0L, 20L);

        player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1.0F, 1.0F);
        player.openInventory(inventory);
    }

    public void openKitPreviewGui(Player player, Kit kit) {
        Inventory inventory = Bukkit.createInventory(player, NumberUtil.getCompatibleSize(kit.getContents().size()), ChatColor.translateAlternateColorCodes('&', "&5&lPreview Kit&7&l: &b&l" + kit.getName()));

        for (int i = 0; i < inventory.getSize(); i++)
            inventory.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE).durability(7).name("&7").build());

        for (int i = 0; i < kit.getContents().size(); i++)
            inventory.setItem(i, kit.getContents().get(i));

        player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1.0F, 1.0F);
        player.openInventory(inventory);
    }

    public Kit getKit(String kitName) {
        for (Kit kit : kits)
            if (kit.getName().equalsIgnoreCase(kitName))
                return kit;

        return null;
    }

    public List<Kit> getKits() {
        return kits;
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public File getFile() {
        return file;
    }
}
