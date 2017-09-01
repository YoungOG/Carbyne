package com.medievallords.carbyne.packages;

import com.medievallords.carbyne.Carbyne;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by WE on 2017-08-03.
 */

public class PackageManager {

    private Carbyne carbyne = Carbyne.getInstance();

    public PackageManager() {
        load();
    }

    public void load() {
        Package.packages.clear();

        ConfigurationSection section = carbyne.getPackageFileConfiguration().getConfigurationSection("Packages");
        if (section == null) {
            section = carbyne.getPackageFileConfiguration().createSection("Packages");
        }

        for (String packages : section.getKeys(false)) {
            String packageCode = section.getString(packages + ".PackageCode");

            String displayName = section.getString(packages + ".DisplayName");
            String material = section.getString(packages + ".Material");
            int packageData = section.getInt(packages + ".Data");
            boolean randomItem = section.getBoolean(packages + ".RandomItem");
            List<String> loreAdd = section.getStringList(packages + ".Lore");

            ConfigurationSection rewardsSection = section.getConfigurationSection(packages + ".Rewards");

            List<PackageItem> packageItems = new ArrayList<>();

            for (String reward : rewardsSection.getKeys(false)) {
                int id = Integer.parseInt(reward);
                int itemId = rewardsSection.getInt(reward + ".MaterialId");
                int itemData = rewardsSection.getInt(reward + ".Data");
                int amount = rewardsSection.getInt(reward + ".Amount");
                int slot = rewardsSection.getInt(reward + ".Slot");
                String displayNameReward = rewardsSection.getString(reward + ".DisplayName");
                String gearCode = rewardsSection.getString(reward + ".GearCode");
                List<String> lore = new ArrayList<>();
                lore.addAll(rewardsSection.getStringList(reward + ".Lore"));

                List<String> commands = new ArrayList<>();
                commands.addAll(rewardsSection.getStringList(reward + ".Commands"));

                HashMap<Enchantment, Integer> enchantments = new HashMap<>();
                for (String enchantment : rewardsSection.getStringList(reward + ".Enchantments")) {
                    String[] split = enchantment.split(",");
                    int level = Integer.parseInt(split[1]);
                    enchantments.put(Enchantment.getByName(split[0]), level);
                }

                double chance = rewardsSection.getDouble(reward + ".Chance");
                PackageItemRarity rarity = PackageItemRarity.valueOf(rewardsSection.getString(reward + ".Rarity"));

                packageItems.add(new PackageItem(id, itemId, itemData, amount, displayNameReward, gearCode, lore, enchantments, chance, rarity, slot, commands));
            }

            Package p = new Package(packageItems, packages, packageCode, displayName, material, packageData, randomItem, loreAdd);
            Package.packages.add(p);
        }
    }
}
