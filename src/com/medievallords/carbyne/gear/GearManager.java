package com.medievallords.carbyne.gear;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.gear.listeners.GearGuiListeners;
import com.medievallords.carbyne.gear.specials.Special;
import com.medievallords.carbyne.gear.specials.types.*;
import com.medievallords.carbyne.gear.types.CarbyneGear;
import com.medievallords.carbyne.gear.types.carbyne.CarbyneArmor;
import com.medievallords.carbyne.gear.types.carbyne.CarbyneWeapon;
import com.medievallords.carbyne.gear.types.minecraft.MinecraftArmor;
import com.medievallords.carbyne.gear.types.minecraft.MinecraftWeapon;
import com.medievallords.carbyne.utils.HiddenStringUtils;
import com.medievallords.carbyne.utils.ItemBuilder;
import com.medievallords.carbyne.utils.Namer;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;

public class GearManager {

    private Carbyne carbyne = Carbyne.getInstance();
    private GearGuiManager gearGuiManager;

    private List<CarbyneGear> carbyneGear = new ArrayList<>();
    private List<CarbyneGear> defaultArmors = new ArrayList<>();
    private List<CarbyneGear> defaultWeapons = new ArrayList<>();
    private List<Special> specials = new ArrayList<>();

    private int tokenId;
    private int tokenData;
    private String tokenDisplayName;
    private List<String> tokenLore;
    private String tokenCode;

    public GearManager() {
        load(carbyne.getGearFileConfiguration());
        loadStoreOptions(carbyne.getGearFileConfiguration());

        gearGuiManager = new GearGuiManager(this);

        Bukkit.getPluginManager().registerEvents(new GearGuiListeners(this), carbyne);
    }

    public void load(FileConfiguration configuration) {
        specials.add(new FireStorm());
        specials.add(new WitherStorm());
        specials.add(new LightningStorm());
        specials.add(new BastionOfHealth());
        specials.add(new HinderingShot());
        specials.add(new Frostbite());
        specials.add(new WallSpecial());

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
            CarbyneGear cg = new CarbyneWeapon(this);

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

    public void loadStoreOptions(FileConfiguration cs) {
        tokenId = cs.getInt("Store.TokenItem.ItemId");
        tokenData = cs.getInt("Store.TokenItem.ItemData");
        tokenDisplayName = cs.getString("Store.TokenItem.DisplayName");
        tokenCode = cs.getString("Store.TokenItem.Code");
        tokenLore = cs.getStringList("Store.TokenItem.Lore");
        tokenLore.add(0, HiddenStringUtils.encodeString(tokenCode));
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

    public int getDurability(ItemStack itemStack) {
        if (itemStack == null) {
            return -1;
        }

        if (isCarbyneArmor(itemStack)) {
            return getCarbyneArmor(itemStack).getDurability(itemStack);
        } else if (isCarbyneWeapon(itemStack)) {
            return getCarbyneWeapon(itemStack).getDurability(itemStack);
        } else if (isDefaultArmor(itemStack)) {
            return getDefaultArmor(itemStack).getDurability(itemStack);
        } else if (isDefaultWeapon(itemStack)) {
            return getDefaultWeapon(itemStack).getDurability(itemStack);
        } else {
            return -1;
        }
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

    public CarbyneGear getRandomCarbyneGear(boolean includeHidden) {
        ArrayList<CarbyneGear> gears = new ArrayList<>();
        for (CarbyneGear gear : getCarbyneGear()) {
            if (gear.isHidden() && includeHidden) {
                gears.add(gear);
            } else {
                gears.add(gear);
            }
        }

        return gears.get(ThreadLocalRandom.current().nextInt(0, gears.size()));
    }

    public Special getSpecialByName(String name) {
        for (Special special : specials) {
            if (special.getSpecialName().equalsIgnoreCase(name)) {
                return special;
            }
        }

        return null;
    }

    public ItemStack getTokenItem() {
        return new ItemBuilder(Material.getMaterial(tokenId)).durability(tokenData).name(tokenDisplayName).setLore(tokenLore).build();
    }

    public void convertToMoneyItem(ItemStack itemStack) {
        if (itemStack != null && (itemStack.getType() == getTokenMaterial() && itemStack.getDurability() == tokenData)) {
            Namer.setName(itemStack, tokenDisplayName);
            Namer.setLore(itemStack, tokenLore);
        }
    }

    public Material getTokenMaterial() {
        return Material.getMaterial(tokenId);
    }

    public int getTokenData() {
        return tokenData;
    }

    public String getTokenCode() {
        return tokenCode;
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

    public GearGuiManager getGearGuiManager() {
        return gearGuiManager;
    }
}
