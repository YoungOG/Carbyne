package com.medievallords.carbyne.gear;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.gear.effects.PotionEffects;
import com.medievallords.carbyne.gear.types.CarbyneGear;
import com.medievallords.carbyne.gear.types.carbyne.CarbyneArmor;
import com.medievallords.carbyne.gear.types.carbyne.CarbyneWeapon;
import com.medievallords.carbyne.gear.types.minecraft.MinecraftArmor;
import com.medievallords.carbyne.gear.types.minecraft.MinecraftWeapon;
import com.medievallords.carbyne.utils.HiddenStringUtils;
import com.medievallords.carbyne.utils.Namer;
import com.medievallords.carbyne.utils.PlayerUtility;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;

public class GearManager {

    private Carbyne carbyne = Carbyne.getInstance();
    private List<CarbyneGear> carbyneGear = new ArrayList<>();
    private List<CarbyneGear> defaultArmors = new ArrayList<>();
    private List<CarbyneGear> defaultWeapons = new ArrayList<>();

    public GearManager() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player all : PlayerUtility.getOnlinePlayers()) {
                    PotionEffects.runEffect(all);
                }
            }
        }.runTaskTimerAsynchronously(carbyne, 0L, 5L);

        load(carbyne.getGearData());
        loadStoreOptions(carbyne.getStoreData());
    }

    public void load(FileConfiguration configuration) {
        carbyneGear.clear();
        defaultArmors.clear();
        defaultWeapons.clear();

        ConfigurationSection cs;
        String type = "";

        cs = configuration.getConfigurationSection("Carbyne-Armor");
        for (String id : cs.getKeys(false)) {
            for (String typeId : cs.getConfigurationSection(id).getKeys(false)) {
                CarbyneGear cg = new CarbyneArmor();

                if (cg.load(cs.getConfigurationSection(id), typeId)) {
                    carbyneGear.add(cg);
                } else {
                    Carbyne.getInstance().getLogger().log(Level.SEVERE, "The carbyne configuration has failed to load Carbyne-Armor." + id + "." + typeId + "!");
                }
            }
        }

        cs = configuration.getConfigurationSection("Carbyne-Weapons");
        for (String id : cs.getKeys(false)) {
            CarbyneGear cg = new CarbyneWeapon();

            if (cg.load(configuration.getConfigurationSection("Carbyne-Weapons"), id)) {
                carbyneGear.add(cg);
            } else {
                Carbyne.getInstance().getLogger().log(Level.SEVERE, "The carbyne configuration has failed to load Carbyne-Weapon." + id + "!");
            }
        }

        cs = configuration.getConfigurationSection("Minecraft-Armor");
        for (String material : cs.getKeys(false)) {
            for (int i = 0; i < 4; i++) {
                switch (i) {
                    case 0:
                        type = "Helmet";
                        break;
                    case 1:
                        type = "Chestplate";
                        break;
                    case 2:
                        type = "Leggings";
                        break;
                    case 3:
                        type = "Boots";
                        break;
                }

                CarbyneGear ma = new MinecraftArmor();

                if (ma.load(cs.getConfigurationSection(material), type)) {
                    defaultArmors.add(ma);
                } else {
                    Carbyne.getInstance().getLogger().log(Level.SEVERE, "Minecraft-Armor configuration has failed to load " + cs + "." + type + "!");
                }
            }
        }

        cs = configuration.getConfigurationSection("Minecraft-Weapons");
        for (String material : cs.getKeys(false)) {
            for (int i = 0; i < 4; i++) {
                switch (i) {
                    case 0:
                        type = "Sword";
                        break;
                    case 1:
                        type = "Axe";
                        break;
                    case 2:
                        type = "Hoe";
                        break;
                }

                CarbyneGear mw = new MinecraftWeapon();

                if (mw.load(cs.getConfigurationSection(material), type)) {
                    defaultWeapons.add(mw);
                } else {
                    Carbyne.getInstance().getLogger().log(Level.SEVERE, "Minecraft-Weapons configuration has failed to load " + cs + "." + type + "!");
                }
            }
        }

        carbyneGear.sort(Comparator.comparing(CarbyneGear::getGearCode));

        Carbyne.getInstance().getLogger().info(carbyneGear.size() + " carbyne gear loaded");
    }

    public CarbyneGear getCarbyneGear(String gearCode) {
        for (CarbyneGear cg : carbyneGear) {
            if (cg.getGearCode().equalsIgnoreCase(gearCode)) {
                return cg;
            }
        }
        return null;
    }

    public CarbyneGear getCarbyneGear(ItemStack is) {
        if (is.getItemMeta() == null) {
            return null;
        }

        List<String> lore = Namer.getLore(is);

        if (lore == null || lore.isEmpty()) {
            return null;
        }

        for (CarbyneGear eg : carbyneGear) {
            if (eg.getGearCode().equalsIgnoreCase(HiddenStringUtils.extractHiddenString(lore.get(0)))) {
                return eg;
            }
        }

        return null;
    }

    public int getDurability(ItemStack is) {
        try {
            String key = ChatColor.stripColor(Namer.getLore(is).get(1));

            if (key.contains("Durability:")) {
                return Integer.valueOf(key.split(" ")[1]);
            }

            return -1;
        } catch (Exception ez) {
            return -1;
        }
    }

    public CarbyneArmor getCarbyneArmor(ItemStack is) {
        if (is.getItemMeta() == null) {
            return null;
        }

        if (is.getItemMeta().getDisplayName() == null) {
            return null;
        }

        List<String> lore = is.getItemMeta().getLore();

        if (lore == null || lore.isEmpty()) {
            return null;
        }

        for (CarbyneGear cg : carbyneGear) {
            if (!(cg instanceof CarbyneArmor)) {
                continue;
            }

            if (cg.getItem(false).getType() == is.getType()) {
                if (cg.getDisplayName().equalsIgnoreCase(is.getItemMeta().getDisplayName().replace('§', '&'))) {
                    if (cg.getGearCode().equalsIgnoreCase(HiddenStringUtils.extractHiddenString(lore.get(0)))) {
                        return (CarbyneArmor) cg;
                    }
                }
            }
        }

        return null;
    }

    public CarbyneArmor getCarbyneArmor(String gearCode) {
        for (CarbyneGear cg : carbyneGear) {
            if (!(cg instanceof CarbyneArmor)) {
                continue;
            }

            if (cg.getGearCode().equalsIgnoreCase(gearCode)) {
                return (CarbyneArmor) cg;
            }
        }

        return null;
    }

    public List<CarbyneArmor> getCarbyneArmor() {
        List<CarbyneArmor> carbyneArmorList = new ArrayList<>();

        for (CarbyneGear carbyneGear : carbyneGear) {
            if (carbyneGear instanceof CarbyneArmor) {
                CarbyneArmor carbyneArmor = (CarbyneArmor) carbyneGear;

                if (carbyneArmor.getItem(false).getType() == Material.LEATHER_CHESTPLATE && !carbyneArmor.isHidden()) {
                    carbyneArmorList.add(carbyneArmor);
                }
            }
        }

        carbyneArmorList.sort(Comparator.comparingDouble(CarbyneArmor::getArmorRating));

        return carbyneArmorList;
    }

    public CarbyneWeapon getCarbyneWeapon(ItemStack is) {
        if (is.getItemMeta() == null) {
            return null;
        }

        if (is.getItemMeta().getDisplayName() == null) {
            return null;
        }

        List<String> lore = is.getItemMeta().getLore();

        if (lore == null || lore.isEmpty()) {
            return null;
        }

        for (CarbyneGear cg : carbyneGear) {
            if (!(cg instanceof CarbyneWeapon)) {
                continue;
            }

            if (cg.getDisplayName().equalsIgnoreCase(is.getItemMeta().getDisplayName().replace('§', '&'))) {
                if (cg.getGearCode().equalsIgnoreCase(HiddenStringUtils.extractHiddenString(lore.get(0)))) {
                    return (CarbyneWeapon) cg;
                }
            }
        }

        return null;
    }

    public CarbyneWeapon getCarbyneWeapon(String gearCode) {
        for (CarbyneGear cg : carbyneGear) {
            if (!(cg instanceof CarbyneWeapon)) {
                continue;
            }

            if (cg.getGearCode().equalsIgnoreCase(gearCode)) {
                return (CarbyneWeapon) cg;
            }
        }

        return null;
    }

    public List<CarbyneWeapon> getCarbyneWeapon() {
        List<CarbyneWeapon> carbyneWeapons = new ArrayList<>();

        for (CarbyneGear cg : carbyneGear) {
            if (cg instanceof CarbyneWeapon) {

                if (!cg.isHidden()) {
                    carbyneWeapons.add((CarbyneWeapon) cg);
                }
            }
        }

        return carbyneWeapons;
    }

    public boolean isCarbyneWeapon(ItemStack is) {
        if (is.getItemMeta() == null) {
            return false;
        }

        if (is.getItemMeta().getDisplayName() == null) {
            return false;
        }

        List<String> lore = is.getItemMeta().getLore();

        if (lore == null || lore.isEmpty()) {
            return false;
        }

        for (CarbyneGear cg : carbyneGear) {
            if (!(cg instanceof CarbyneWeapon)) {
                continue;
            }

            if (cg.getDisplayName().equalsIgnoreCase(is.getItemMeta().getDisplayName().replace('§', '&'))) {
                if (cg.getGearCode().equalsIgnoreCase(HiddenStringUtils.extractHiddenString(lore.get(0)))) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean isCarbyneArmor(ItemStack is) {
        if (is.getItemMeta() == null) {
            return false;
        }

        if (is.getItemMeta().getDisplayName() == null) {
            return false;
        }

        List<String> lore = is.getItemMeta().getLore();

        if (lore == null || lore.isEmpty()) {
            return false;
        }

        for (CarbyneGear cg : carbyneGear) {
            if (!(cg instanceof CarbyneArmor)) {
                continue;
            }

            if (cg.getDisplayName().equalsIgnoreCase(is.getItemMeta().getDisplayName().replace('§', '&'))) {
                if (cg.getGearCode().equalsIgnoreCase(HiddenStringUtils.extractHiddenString(lore.get(0)))) {
                    return true;
                }
            }
        }

        return false;
    }

    public MinecraftArmor getDefaultArmor(ItemStack itemStack) {
        if (itemStack.getItemMeta() == null) {
            return null;
        }

        if (isCarbyneArmor(itemStack)) {
            return null;
        }

        for (CarbyneGear cg : defaultArmors) {
            if (!(cg instanceof MinecraftArmor)) {
                continue;
            }

            if (cg.getItem(false).getType().equals(itemStack.getType())) {
                return (MinecraftArmor) cg;
            }
        }

        return null;
    }

    public boolean isDefaultArmor(ItemStack itemStack) {
        if (itemStack.getItemMeta() == null) {
            return false;
        }

        if (isCarbyneArmor(itemStack)) {
            return false;
        }

        for (CarbyneGear cg : defaultArmors) {
            if (!(cg instanceof MinecraftArmor)) {
                continue;
            }

            if (cg.getItem(false).getType().equals(itemStack.getType())) {
                return true;
            }
        }

        return false;
    }

    public boolean isDefaultWeapon(ItemStack itemStack) {
        if (itemStack.getItemMeta() == null) {
            return false;
        }

        if (isCarbyneWeapon(itemStack)) {
            return false;
        }

        for (CarbyneGear cg : defaultWeapons) {
            if (!(cg instanceof MinecraftWeapon)) {
                continue;
            }

            if (cg.getItem(false).getType().equals(itemStack.getType())) {
                return true;
            }
        }

        return false;
    }

    public MinecraftWeapon getDefaultWeapon(ItemStack itemStack) {
        if (itemStack.getItemMeta() == null) {
            return null;
        }

        if (isCarbyneWeapon(itemStack)) {
            return null;
        }

        for (CarbyneGear cg : defaultWeapons) {
            if (!(cg instanceof MinecraftWeapon)) {
                continue;
            }

            if (cg.getItem(false).getType().equals(itemStack.getType())) {
                return (MinecraftWeapon) cg;
            }
        }

        return null;
    }

    public List<CarbyneArmor> getCarbyneArmorByColor(Color color) {
        List<CarbyneArmor> carbyneArmorList = new ArrayList<>();
        for (CarbyneGear carbyneGear : carbyneGear) {
            if (carbyneGear instanceof CarbyneArmor) {
                CarbyneArmor carbyneArmor = (CarbyneArmor) carbyneGear;

                if (carbyneArmor.getColor().equals(color)) {
                    carbyneArmorList.add(carbyneArmor);
                }
            }
        }

        return carbyneArmorList;
    }

    public ItemStack convertDefaultItem(ItemStack item) {
        ItemStack replacement = null;

        if (isDefaultWeapon(item)) {
            replacement = getDefaultWeapon(item).getItem(false);
        } else if (isDefaultArmor(item)) {
            replacement = getDefaultArmor(item).getItem(false);
        }

        if (replacement != null) {
            for (Enchantment enchantment : item.getEnchantments().keySet()) {
                replacement.addUnsafeEnchantment(enchantment, item.getEnchantments().get(enchantment));
            }

            if (item.hasItemMeta()) {
                ItemMeta im = replacement.getItemMeta();

                if (item.getItemMeta().hasDisplayName()) {
                    im.setDisplayName(item.getItemMeta().getDisplayName());
                }

                if (item.getItemMeta().hasLore()) {
                    List<String> lore = im.getLore();

                    for (String line : item.getItemMeta().getLore()) {
                        if (!lore.contains(line)) {
                            lore.add(line);
                        }
                    }

                    im.setLore(lore);
                }

                replacement.setItemMeta(im);
            }
        }

        return replacement;
    }

    // STORE RELATED STUFF BELOW

    private String name = "";
    private List<String> lore;
    private String moneyCode = "";
    private boolean enableStore;
    private Material moneyItem;

    public boolean loadStoreOptions(FileConfiguration cs) {
        enableStore = cs.getBoolean("EnableStore");

        if (!enableStore)
            return false;
        if (cs.getString("MoneyItem.Material") == null && Material.getMaterial(cs.getString("MoneyItem.Material")) == null)
            return false;
        if (cs.getString("MoneyItem.Name") == null)
            return false;
        if (cs.getStringList("MoneyItem.Lore") == null)
            return false;
        if (cs.getString("MoneyItem.MoneyCode") == null)
            return false;

        moneyCode = cs.getString("MoneyItem.MoneyCode");
        moneyItem = Material.getMaterial(cs.getString("MoneyItem.Material"));
        name = cs.getString("MoneyItem.Name");
        lore = cs.getStringList("MoneyItem.Lore");
        lore.add(0, HiddenStringUtils.encodeString(moneyCode));

        Carbyne.getInstance().getLogger().info("Store loaded");
        return true;
    }

    public ItemStack getMoney() {
        ItemStack is = new ItemStack(moneyItem);

        Namer.setName(is, name);
        Namer.setLore(is, lore);

        return is;
    }

    public void convertToMoneyItem(ItemStack itemStack) {
        if (itemStack != null && itemStack.getType() == moneyItem) {
            Namer.setName(itemStack, name);
            Namer.setLore(itemStack, lore);
        }
    }

    public boolean isEnableStore() {
        return enableStore;
    }

    public Material getMoneyItem() {
        return moneyItem;
    }

    public String getMoneyCode() {
        return moneyCode;
    }

    public List<CarbyneGear> getCarbyneGear() {
        return carbyneGear;
    }

    public List<CarbyneGear> getDefaultArmors() {
        return defaultArmors;
    }

    public List<CarbyneGear> getDefaultWeapons() {
        return defaultWeapons;
    }
}
