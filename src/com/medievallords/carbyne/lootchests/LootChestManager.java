package com.medievallords.carbyne.lootchests;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.utils.LocationSerialization;
import com.medievallords.carbyne.utils.ParticleEffect;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

public class LootChestManager {

    private Carbyne main = Carbyne.getInstance();
    @Getter
    private HashMap<String, List<Loot>> lootTables = new HashMap<>();
    @Getter
    private HashMap<Location, LootChest> lootChests = new HashMap<>();

    public LootChestManager() {
        load(main.getLootChestFileConfiguration());

        new BukkitRunnable() {
            public void run() {
                for (Location location : lootChests.keySet()) {
                    LootChest lc = lootChests.get(location);

                    if (lc.isHidden() && lc.shouldChestSpawn())
                        lc.showChest();

                    if (!lc.isHidden())
                        ParticleEffect.VILLAGER_HAPPY.display(0.3f, 0.3f, 0.3f, 0.02f, 10, lc.getCenter(), 50, true);
                }
            }
        }.runTaskTimerAsynchronously(main, 0L, 20L);
    }

    public void reload() {
        lootTables.clear();
        lootChests.clear();

        if (main.getLootChestFile() == null) {
            main.setLootChestFile(new File(main.getDataFolder(), "lootchests.yml"));
        }

        main.setLootChestFileConfiguration(YamlConfiguration.loadConfiguration(main.getLootChestFile()));

        load(YamlConfiguration.loadConfiguration(main.getLootChestFile()));
    }

    public void load(FileConfiguration fc) {
        // Begin by loading items
        int totalLoots = 0;
        List<Loot> loots = new ArrayList<>();
        for (String itemName : fc.getConfigurationSection("Items").getKeys(false)) {
            try {
                Material material = Material.getMaterial(fc.getString("Items." + itemName + ".Material"));
                if (material == null) throw new Exception();
                String displayName = fc.getString("Items." + itemName + ".DisplayName");
                List<String> lore = fc.getStringList("Items." + itemName + ".Lore");
                List<String> enchants = fc.getStringList("Items." + itemName + ".Enchantments");
                int data = fc.getInt("Items." + itemName + ".Data");
                HashMap<Enchantment, Integer> enchantments = new HashMap<>();

                for (String enchantString : enchants) {
                    String[] split = enchantString.split(",");
                    Enchantment e = Enchantment.getByName(split[0].toUpperCase());
                    Integer i = new Integer(split[1]);
                    enchantments.put(e, i);
                }

                double chanceToSpawn = fc.getDouble("Items." + itemName + ".ChanceToSpawn");
                int amount = fc.getInt("Items." + itemName + ".Amount");

                loots.add(new Loot(itemName, material, displayName, lore, enchantments, chanceToSpawn, amount, data));
                totalLoots++;
            } catch (Exception ex) {
                main.getLogger().log(Level.SEVERE, "Failed to load item " + itemName + "!");
            }
        }
        main.getLogger().log(Level.INFO, "Loaded " + totalLoots + "!");

        // Assign the items to loot tables
        int totalLootTables = 0;
        for (String lootTableName : fc.getConfigurationSection("LootTables").getKeys(false)) {
            List<Loot> itemTableLoots = new ArrayList<>();

            for (String itemName : fc.getStringList("LootTables." + lootTableName + ".Items")) {
                Loot loot = loots.stream().filter(l -> itemName.equalsIgnoreCase(l.getConfigName())).findAny().orElse(null);

                if (loot != null)
                    itemTableLoots.add(loot);
            }

            lootTables.put(lootTableName, itemTableLoots);
            totalLootTables++;
        }
        main.getLogger().log(Level.INFO, "Loaded " + totalLootTables + "!");

        // Load chests
        int totalChests = 0;
        if (fc.getConfigurationSection("LootChests").getKeys(false).size() > 0) {
            for (String lootChest : fc.getConfigurationSection("LootChests").getKeys(false)) {
                try {
                    String lootTableName = fc.getString("LootChests." + lootChest + ".LootTable");
                    Location location = LocationSerialization.deserializeLocation(fc.getString("LootChests." + lootChest + ".Location"));
                    String respawnString = fc.getString("LootChests." + lootChest + ".RespawnTime");
                    int maxItems = fc.getInt("LootChests." + lootChest + ".MaxItems");
                    BlockFace blockFace = BlockFace.valueOf(fc.getString("LootChests." + lootChest + ".Face"));
                    lootChests.put(location, new LootChest(this, lootChest, lootTableName, location, respawnString, maxItems, blockFace));
                    totalChests++;
                } catch (Exception e) {
                    main.getLogger().log(Level.SEVERE, "Failed to load loot chest with the name " + lootChest + "!");
                }
            }
        }

        main.getLogger().log(Level.INFO, "Loaded " + totalChests + "!");
    }

    public LootChest findLootChestWithName(String un) {
        for (Location l : lootChests.keySet()) {
            LootChest lc = lootChests.get(l);

            if (lc.getChestConfigName().equalsIgnoreCase(un))
                return lc;
        }

        return null;
    }
}