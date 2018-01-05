package com.medievallords.carbyne.crates;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.crates.keys.Key;
import com.medievallords.carbyne.crates.rewards.Reward;
import com.medievallords.carbyne.utils.LocationSerialization;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class CrateManager {

    private Carbyne main = Carbyne.getInstance();
    private ArrayList<Key> keys = new ArrayList<>();
    private ArrayList<Crate> crates = new ArrayList<>();
    private HashMap<Sound, Double[]> sounds = new HashMap<>();

    public CrateManager() {
        load(main.getCrateFileConfiguration());
    }

    public void load(FileConfiguration crateFileConfiguration) {
        sounds.clear();
        crates.clear();
        keys.clear();

        if (main.getConfig().getStringList("crates.opening-sounds").size() > 0) {
            for (String s : main.getConfig().getStringList("crates.opening-sounds")) {
                String[] args = s.split(",");

                Double[] doubles = { Double.valueOf(args[1]), Double.valueOf(args[1]) };
                sounds.put(Sound.valueOf(args[0]), doubles);
            }

            System.out.println("Successfully loaded " + sounds.size() + " sounds.");
        }

        ConfigurationSection keyConfigurationSection = crateFileConfiguration.getConfigurationSection("Keys");

        if (keyConfigurationSection.getKeys(false).size() > 0) {
            main.getLogger().log(Level.INFO, "Preparing to load " + keyConfigurationSection.getKeys(false).size() + " key(s).");

            for (String name : keyConfigurationSection.getKeys(false)) {
                main.getLogger().log(Level.INFO, "Key found: " + name + ", loading.");

                int itemId = keyConfigurationSection.getInt(name + ".ItemID");
                int itemData = keyConfigurationSection.getInt(name + ".ItemData");
                String displayName = keyConfigurationSection.getString(name + ".DisplayName");
                List<String> lore = keyConfigurationSection.getStringList(name + ".Lore");
                List<String> enchantments = keyConfigurationSection.getStringList(name + ".Enchantments");
                String crate = keyConfigurationSection.getString(name + ".Crate");

                Key key = new Key(name, itemId, itemData);
                key.setDisplayName(displayName);
                key.setLore(lore);
                for (String s : enchantments) {
                    String[] args = s.split(",");
                    key.getEnchantments().put(Enchantment.getByName(args[0]), Integer.valueOf(args[1]) + 1);
                }
                key.setCrate(crate);

                keys.add(key);
            }

            main.getLogger().log(Level.INFO, "Successfully loaded " + keys.size() + " key(s).");
        }

        ConfigurationSection crateConfigurationSection = crateFileConfiguration.getConfigurationSection("Crates");

        if (crateConfigurationSection.getKeys(false).size() > 0) {
            main.getLogger().log(Level.INFO, "Preparing to load " + crateConfigurationSection.getKeys(false).size() + " crate(s).");

            for (String name : crateConfigurationSection.getKeys(false)) {
                Crate crate = new Crate(name);

                main.getLogger().log(Level.INFO, "Crate found: " + crate.getName() + ", loading.");

                Location location = null;
                int rewardsAmount = 1;
                List<Reward> rewards = new ArrayList<>();
                int progressInscreaseP = crateConfigurationSection.getInt(name + ".ProgressIncreaseP");
                int progressInscreaseM = crateConfigurationSection.getInt(name + ".ProgressIncreaseM");

                if (crateConfigurationSection.get(name + ".Location") != null)
                    location = LocationSerialization.deserializeLocation(crateConfigurationSection.getString(name + ".Location"));

                if (crateConfigurationSection.get(name + ".RewardsAmount") != null)
                    rewardsAmount = crateConfigurationSection.getInt(name + ".RewardsAmount");

                if (crateConfigurationSection.get(name + ".Rewards") != null) {
                    ConfigurationSection rewardsSection = crateConfigurationSection.getConfigurationSection(name + ".Rewards");

                    if (rewardsSection.getKeys(false).size() > 0) {
                        for (String rewardId : rewardsSection.getKeys(false)) {
                            int itemId = rewardsSection.getInt(rewardId + ".ItemID");
                            int itemData = rewardsSection.getInt(rewardId + ".ItemData");
                            int amount = rewardsSection.getInt(rewardId + ".Amount");
                            String displayName = rewardsSection.getString(rewardId + ".DisplayName");
                            String gearCode = "";
                            List<String> lore = rewardsSection.getStringList(rewardId + ".Lore");
                            List<String> enchantments = rewardsSection.getStringList(rewardId + ".Enchantments");
                            List<String> commands = rewardsSection.getStringList(rewardId + ".Commands");
                            boolean displayItemOnly = rewardsSection.getBoolean(rewardId + ".DisplayItem");
                            double chance = rewardsSection.getDouble(rewardId + ".Chance");
                            double progress = rewardsSection.getDouble(rewardId + ".Progress");
                            int slot = rewardsSection.getInt(rewardId + ".Slot");

                            if (displayName != null) {
                                if (displayName.contains("randomgear")) {
                                    gearCode = displayName;
                                    displayName = "&6Randomly Selected Gear";

                                    lore.clear();
                                    lore.add("&eGives a random carbyne gear item.");
                                } else {
                                    if (Carbyne.getInstance().getGearManager().getCarbyneGear(displayName) != null) {
                                        gearCode = displayName;
                                        displayName = Carbyne.getInstance().getGearManager().getCarbyneGear(gearCode).getDisplayName();
                                    }
                                }
                            }

                            Reward reward = new Reward(Integer.valueOf(rewardId), itemId, itemData, amount, gearCode);
                            reward.setDisplayName(displayName);
                            reward.setLore(lore);
                            for (String s : enchantments) {
                                String[] args = s.split(",");
                                reward.getEnchantments().put(Enchantment.getByName(args[0]), Integer.valueOf(args[1]) + 1);
                            }
                            reward.setCommands(commands);
                            reward.setDisplayItemOnly(displayItemOnly);
                            reward.setChance(chance);
                            reward.setProgress(progress);
                            reward.setSlot(slot);

                            rewards.add(reward);
                        }

                        main.getLogger().log(Level.INFO, "Loaded " + rewards.size() + " rewards for the crate '" + name + "'.");
                    }
                }

                if (location != null)
                    crate.setLocation(location);

                crate.setRewardsAmount(rewardsAmount);
                crate.setProgressIncreaseP(progressInscreaseP);
                crate.setProgressIncreaseM(progressInscreaseM);

                if (rewards.size() > 0)
                    for (Reward reward : rewards)
                        crate.getRewards().add(reward);

                getCrates().add(crate);
                crate.runEffect(name);
            }

            main.getLogger().log(Level.INFO, "Successfully loaded " + getCrates().size() + " crate(s).");
        }
    }

    public void save(FileConfiguration crateFileConfiguration) {
        if (getCrates().size() > 0) {
            main.getLogger().log(Level.INFO, "Preparing to save " + getCrates().size() + " crate(s).");

            for (Crate crate : getCrates())
                crate.save(crateFileConfiguration);

            main.getLogger().log(Level.INFO, "Successfully saved " + getCrates().size() + " crate(s).");
        }
    }

    public Crate getCrate(String name) {
        for (Crate crate : getCrates())
            if (crate.getName().equalsIgnoreCase(name))
                return crate;

        return null;
    }

    public Crate getCrate(Location location) {
        for (Crate crate : getCrates())
            if (crate.getLocation() != null)
                if (crate.getLocation().getBlockX() == location.getBlockX() && crate.getLocation().getBlockY() == location.getBlockY() && crate.getLocation().getBlockZ() == location.getBlockZ())
                    return crate;

        return null;
    }

    public Crate getCrate(UUID uniqueId) {
        for (Crate crate : getCrates())
            if (crate.getEditors().contains(uniqueId))
                return crate;

        return null;
    }

    public Key getKey(String name) {
        for (Key key : getKeys())
            if (key.getName().equalsIgnoreCase(name))
                return key;

        return null;
    }

    public Key getKey(Crate crate) {
        for (Key key : getKeys())
            if (key.getCrate().equalsIgnoreCase(crate.getName()))
                return key;

        return null;
    }

    public Key getKey(ItemStack itemStack) {
        for (Key key : getKeys())
            if (itemStack.getTypeId() == key.getItemId() && itemStack.getDurability() == key.getItemData())
                if (itemStack.hasItemMeta() && key.getItem().hasItemMeta())
                    if (itemStack.getItemMeta().hasDisplayName() && key.getItem().getItemMeta().hasDisplayName())
                        if (itemStack.getItemMeta().getDisplayName().equalsIgnoreCase(key.getItem().getItemMeta().getDisplayName()))
                            return key;

        return null;
    }

    public boolean isOpeningCrate(Player player) {
        for (Crate crate : getCrates())
            if (crate.getCrateOpeners().containsKey(player.getUniqueId()))
                return true;

        return false;
    }

    public ArrayList<Key> getKeys() {
        return keys;
    }

    public HashMap<Sound, Double[]> getSounds() {
        return sounds;
    }

    public ArrayList<Crate> getCrates() {
        return crates;
    }
}
