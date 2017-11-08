package com.medievallords.carbyne.gear;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.gear.effects.GearEffects;
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
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;

@Getter
public class GearManager {

    private Carbyne carbyne = Carbyne.getInstance();
    private GearGuiManager gearGuiManager;
    private GearEffects gearEffects;

    private List<CarbyneGear> carbyneGear = new ArrayList<>();
    private List<CarbyneGear> defaultArmors = new ArrayList<>();
    private List<CarbyneGear> defaultWeapons = new ArrayList<>();
    private List<Special> specials = new ArrayList<>();
    private List<Item> repairItems = new ArrayList<>();

    private HashMap<UUID, BukkitTask> playerGearFadeSchedulers = new HashMap<>();

    private int tokenId, polishId;
    private int tokenData, polishData;
    private String tokenDisplayName, polishDisplayName;
    private List<String> tokenLore, polishLore;
    private String tokenCode, polishCode;

    public GearManager() {
        gearEffects = new GearEffects();

        load(carbyne.getGearFileConfiguration());
        loadTokenOptions(carbyne.getGearFileConfiguration());
        loadPolishOptions(carbyne.getGearFileConfiguration());

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
        specials.add(new InfernalExplosion());

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

    public void loadTokenOptions(FileConfiguration cs) {
        tokenId = cs.getInt("TokenItem.ItemId");
        tokenData = cs.getInt("TokenItem.ItemData");
        tokenCode = cs.getString("TokenItem.Code");
        tokenDisplayName = cs.getString("TokenItem.DisplayName");
        tokenLore = cs.getStringList("TokenItem.Lore");
        tokenLore.add(0, HiddenStringUtils.encodeString(tokenCode));
    }

    public void loadPolishOptions(FileConfiguration cs) {
        polishId = cs.getInt("PolishItem.ItemId");
        polishData = cs.getInt("PolishItem.ItemData");
        polishCode = cs.getString("PolishItem.Code");
        polishDisplayName = cs.getString("PolishItem.DisplayName");
        polishLore = cs.getStringList("PolishItem.Lore");
        polishLore.add(0, HiddenStringUtils.encodeString(polishCode));
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
        if (is == null || is.getType() == Material.AIR)
            return null;

        if (!is.hasItemMeta())
            return null;

        if (is.getItemMeta() == null)
            return null;

        if (!is.getItemMeta().hasLore())
            return null;

        if (is.getItemMeta().getLore() == null)
            return null;

        List<String> lore = is.getItemMeta().getLore();

        if (lore == null || lore.isEmpty())
            return null;

        for (CarbyneGear cg : carbyneGear) {
            if (!(cg instanceof CarbyneArmor))
                continue;

            if (cg.getItem(false).getType() == is.getType())
                if (cg.getGearCode().equalsIgnoreCase(HiddenStringUtils.extractHiddenString(lore.get(0))))
                    return (CarbyneArmor) cg;
        }

        return null;
    }

    public CarbyneArmor getCarbyneArmor(String gearCode) {
        for (CarbyneGear cg : carbyneGear) {
            if (!(cg instanceof CarbyneArmor))
                continue;

            if (cg.getGearCode().equalsIgnoreCase(gearCode))
                return (CarbyneArmor) cg;
        }

        return null;
    }

    public List<CarbyneArmor> getCarbyneArmor() {
        List<CarbyneArmor> carbyneArmorList = new ArrayList<>();

        for (CarbyneGear carbyneGear : carbyneGear) {
            if (carbyneGear instanceof CarbyneArmor) {
                CarbyneArmor carbyneArmor = (CarbyneArmor) carbyneGear;

                if (carbyneArmor.getItem(false).getType() == Material.LEATHER_CHESTPLATE)
                    carbyneArmorList.add(carbyneArmor);
            }
        }

        carbyneArmorList.sort(Comparator.comparingDouble(CarbyneArmor::getArmorRating));

        return carbyneArmorList;
    }

    public CarbyneWeapon getCarbyneWeapon(ItemStack is) {
        if (is == null || is.getType() == Material.AIR)
            return null;

        if (!is.hasItemMeta())
            return null;

        if (is.getItemMeta() == null)
            return null;

        if (!is.getItemMeta().hasLore())
            return null;

        if (is.getItemMeta().getLore() == null)
            return null;

        List<String> lore = is.getItemMeta().getLore();

        if (lore == null || lore.isEmpty())
            return null;

        for (CarbyneGear cg : carbyneGear) {
            if (!(cg instanceof CarbyneWeapon))
                continue;

            if (cg.getGearCode().equalsIgnoreCase(HiddenStringUtils.extractHiddenString(lore.get(0))))
                return (CarbyneWeapon) cg;
        }

        return null;
    }

    public CarbyneWeapon getCarbyneWeapon(String gearCode) {
        for (CarbyneGear cg : carbyneGear) {
            if (!(cg instanceof CarbyneWeapon))
                continue;

            if (cg.getGearCode().equalsIgnoreCase(gearCode))
                return (CarbyneWeapon) cg;
        }

        return null;
    }

    public List<CarbyneWeapon> getCarbyneWeapon() {
        List<CarbyneWeapon> carbyneWeapons = new ArrayList<>();

        for (CarbyneGear cg : carbyneGear) {
            if (cg instanceof CarbyneWeapon)
                carbyneWeapons.add((CarbyneWeapon) cg);
        }

        carbyneWeapons.sort((o1, o2) -> Boolean.compare(o1.isHidden(), o2.isHidden()));

        return carbyneWeapons;
    }

    public boolean isCarbyneWeapon(ItemStack is) {
        if (is == null || is.getType() == Material.AIR)
            return false;

        if (!is.hasItemMeta())
            return false;

        if (is.getItemMeta() == null)
            return false;

        if (!is.getItemMeta().hasLore())
            return false;

        if (is.getItemMeta().getLore() == null)
            return false;

        List<String> lore = is.getItemMeta().getLore();

        if (lore == null || lore.isEmpty())
            return false;

        if (lore.get(0) == null)
            return false;

        for (CarbyneGear cg : carbyneGear) {
            if (!(cg instanceof CarbyneWeapon))
                continue;

            if (cg.getGearCode().equalsIgnoreCase(HiddenStringUtils.extractHiddenString(lore.get(0))))
                return true;
        }

        return false;
    }

    public boolean isCarbyneArmor(ItemStack is) {
        if (is == null || is.getType() == Material.AIR)
            return false;

        if (!is.hasItemMeta())
            return false;

        if (is.getItemMeta() == null)
            return false;

        if (!is.getItemMeta().hasLore())
            return false;

        if (is.getItemMeta().getLore() == null)
            return false;

        List<String> lore = is.getItemMeta().getLore();

        if (lore == null || lore.isEmpty())
            return false;

        if (lore.get(0) == null)
            return false;

        for (CarbyneGear cg : carbyneGear) {
            if (!(cg instanceof CarbyneArmor))
                continue;

            if (cg.getGearCode().equalsIgnoreCase(HiddenStringUtils.extractHiddenString(lore.get(0))))
                return true;
        }

        return false;
    }

    public MinecraftArmor getDefaultArmor(ItemStack itemStack) {
        if (itemStack.getItemMeta() == null)
            return null;

        if (isCarbyneArmor(itemStack))
            return null;

        for (CarbyneGear cg : defaultArmors) {
            if (!(cg instanceof MinecraftArmor))
                continue;

            if (cg.getItem(false).getType().equals(itemStack.getType()))
                return (MinecraftArmor) cg;
        }

        return null;
    }

    public boolean isDefaultArmor(ItemStack itemStack) {
        if (itemStack.getItemMeta() == null)
            return false;

        if (isCarbyneArmor(itemStack))
            return false;

        for (CarbyneGear cg : defaultArmors) {
            if (!(cg instanceof MinecraftArmor)) {
                continue;
            }

            if (cg.getItem(false).getType().equals(itemStack.getType()))
                return true;
        }

        return false;
    }

    public boolean isDefaultWeapon(ItemStack itemStack) {
        if (itemStack.getItemMeta() == null)
            return false;

        if (isCarbyneWeapon(itemStack))
            return false;

        for (CarbyneGear cg : defaultWeapons) {
            if (!(cg instanceof MinecraftWeapon))
                continue;

            if (cg.getItem(false).getType().equals(itemStack.getType()))
                return true;
        }

        return false;
    }

    public MinecraftWeapon getDefaultWeapon(ItemStack itemStack) {
        if (itemStack.getItemMeta() == null)
            return null;

        if (isCarbyneWeapon(itemStack))
            return null;

        for (CarbyneGear cg : defaultWeapons) {
            if (!(cg instanceof MinecraftWeapon))
                continue;

            if (cg.getItem(false).getType().equals(itemStack.getType()))
                return (MinecraftWeapon) cg;
        }

        return null;
    }

    public double getDurability(ItemStack itemStack) {
        if (itemStack == null)
            return -1;

        if (isCarbyneArmor(itemStack))
            return getCarbyneArmor(itemStack).getDurability(itemStack);
        else if (isCarbyneWeapon(itemStack))
            return getCarbyneWeapon(itemStack).getDurability(itemStack);
        else if (isDefaultArmor(itemStack))
            return (getDefaultArmor(itemStack) != null ? itemStack.getType().getMaxDurability() - itemStack.getDurability() : -1);
        else if (isDefaultWeapon(itemStack))
            return (getDefaultWeapon(itemStack) != null ? itemStack.getType().getMaxDurability() - itemStack.getDurability() : -1);
        else if (itemStack.getType().getMaxDurability() > 0)
            return itemStack.getType().getMaxDurability() - itemStack.getDurability();
        else
            return -1;
    }

    public List<CarbyneArmor> getCarbyneArmorByColor(Color color) {
        List<CarbyneArmor> carbyneArmorList = new ArrayList<>();

        for (CarbyneGear carbyneGear : carbyneGear) {
            if (carbyneGear instanceof CarbyneArmor) {
                CarbyneArmor carbyneArmor = (CarbyneArmor) carbyneGear;

                if (carbyneArmor.getBaseColor().equals(color))
                    carbyneArmorList.add(carbyneArmor);
            }
        }

        return carbyneArmorList;
    }

    public ItemStack convertDefaultItem(ItemStack item) {
        ItemStack replacement = null;

        if (isDefaultWeapon(item))
            replacement = getDefaultWeapon(item).getItem(false);
        else if (isDefaultArmor(item))
            replacement = getDefaultArmor(item).getItem(false);

        if (replacement != null) {
            for (Enchantment enchantment : item.getEnchantments().keySet())
                replacement.addUnsafeEnchantment(enchantment, item.getEnchantments().get(enchantment));

            if (item.hasItemMeta()) {
                ItemMeta im = replacement.getItemMeta();

                if (item.getItemMeta().hasDisplayName())
                    im.setDisplayName(item.getItemMeta().getDisplayName());

                if (item.getItemMeta().hasLore()) {
                    List<String> lore = im.getLore();

                    for (String line : item.getItemMeta().getLore()) {
                        if (!lore.contains(line) && !line.contains("Damage Reduction"))
                            lore.add(line);
                    }

                    im.setLore(lore);
                }

                replacement.setItemMeta(im);
            }

            replacement.setDurability(item.getDurability());
        }

        return replacement;
    }

    public CarbyneGear getRandomCarbyneGear(boolean includeHidden) {
        ArrayList<CarbyneGear> gears = new ArrayList<>();
        for (CarbyneGear gear : getCarbyneGear()) {
            if (gear.isHidden() && includeHidden)
                gears.add(gear);
            else
                gears.add(gear);
        }

        return gears.get(ThreadLocalRandom.current().nextInt(0, gears.size()));
    }

    public Special getSpecialByName(String name) {
        for (Special special : specials) {
            if (special.getSpecialName().equalsIgnoreCase(name))
                return special;
        }

        return null;
    }

    public ItemStack getTokenItem() {
        return new ItemBuilder(Material.getMaterial(tokenId)).durability(tokenData).name(tokenDisplayName).setLore(tokenLore).build();
    }

    public ItemStack getPolishItem() {
        return new ItemBuilder(Material.getMaterial(polishId)).durability(polishData).name(polishDisplayName).setLore(polishLore).build();
    }

    public void convertToMoneyItem(ItemStack itemStack) {
        if (itemStack != null && (itemStack.getType() == getTokenMaterial() && itemStack.getDurability() == tokenData)) {
            Namer.setName(itemStack, tokenDisplayName);
            Namer.setLore(itemStack, tokenLore);
        }
    }

    public void convertToPolishItem(ItemStack itemStack) {
        if (itemStack != null && (itemStack.getType() == getPolishMaterial() && itemStack.getDurability() == polishData)) {
            Namer.setName(itemStack, polishDisplayName);
            Namer.setLore(itemStack, polishLore);
        }
    }

    public boolean isInFullCarbyne(Player player) {
        ItemStack[] armorContents = player.getInventory().getArmorContents();
        boolean helmet = false, chestplate = false, leggings = false, boots = false;

        if (armorContents != null && armorContents.length > 0)
            for (ItemStack item : armorContents)
                if (item != null && (item.getType().toString().contains("HELMET") || item.getType().toString().contains("CHESTPLATE") || item.getType().toString().contains("LEGGINGS") || item.getType().toString().contains("BOOTS")))
                    if (getCarbyneGear(item) != null)
                        switch (item.getType().toString()) {
                            case "HELMET":
                                helmet = true;
                                break;
                            case "CHESTPLATE":
                                chestplate = true;
                                break;
                            case "LEGGINGS":
                                leggings = true;
                                break;
                            case "BOOTS":
                                boots = true;
                                break;
                        }

        return (helmet && chestplate && leggings && boots);
    }

    public Material getTokenMaterial() {
        return Material.getMaterial(tokenId);
    }

    public Material getPolishMaterial() {
        return Material.getMaterial(polishId);
    }

    public double getDamageReduction(Player player) {
        double damageReduction = 0.0;

        for (ItemStack itemStack : player.getInventory().getArmorContents()) {
            if (itemStack == null)
                continue;

            if (itemStack.getType().equals(Material.AIR))
                continue;

            if (isCarbyneArmor(itemStack)) {
                CarbyneArmor carbyneArmor = getCarbyneArmor(itemStack);

                if (carbyneArmor != null)
                    damageReduction += carbyneArmor.getArmorRating();
            }

            if (isDefaultArmor(itemStack)) {
                MinecraftArmor minecraftArmor = getDefaultArmor(itemStack);

                if (minecraftArmor != null)
                    damageReduction += minecraftArmor.getArmorRating();
            }
        }

        return damageReduction;
    }

    public double getProtectionReduction(Player player) {
        String[] types = {"HELMET", "CHESTPLATE", "LEGGINGS", "BOOTS"};
        double damageReduction = 0.0;

        for (ItemStack is : player.getInventory().getArmorContents()) {
            int index;
            for (index = 0; index < types.length; index++)
                if (is.getType().toString().contains(types[index]))
                    break;

            if (is == null)
                continue;

            if (is.getType().equals(Material.AIR))
                continue;

            switch (is.getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL)) {
                case 1: //0.125% / 4 = 0.03125
                    switch (index) {
                        case 0:
                            damageReduction += 0.03125;
                            break;
                        case 1:
                            damageReduction += 0.03125;
                            break;
                        case 2:
                            damageReduction += 0.03125;
                            break;
                        case 3:
                            damageReduction += 0.03125;
                            break;
                    }
                    break;
                case 2: //0.15 / 4 = 0.0375
                    switch (index) {
                        case 0:
                            damageReduction += 0.0375;
                            break;
                        case 1:
                            damageReduction += 0.0375;
                            break;
                        case 2:
                            damageReduction += 0.0375;
                            break;
                        case 3:
                            damageReduction += 0.0375;
                            break;
                    }
                    break;
                case 3: //0.175 / 4 = 0.04375
                    switch (index) {
                        case 0:
                            damageReduction += 0.04375;
                            break;
                        case 1:
                            damageReduction += 0.04375;
                            break;
                        case 2:
                            damageReduction += 0.04375;
                            break;
                        case 3:
                            damageReduction += 0.04375;
                            break;
                    }
                    break;
                case 4: //0.20 / 4 = 0.05
                    switch (index) {
                        case 0:
                            damageReduction += 0.05;
                            break;
                        case 1:
                            damageReduction += 0.05;
                            break;
                        case 2:
                            damageReduction += 0.05;
                            break;
                        case 3:
                            damageReduction += 0.05;
                            break;
                    }
                    break;
                case 5: //0.235 / 4 = 0.05875
                    switch (index) {
                        case 0:
                            damageReduction += 0.05875;
                            break;
                        case 1:
                            damageReduction += 0.05875;
                            break;
                        case 2:
                            damageReduction += 0.05875;
                            break;
                        case 3:
                            damageReduction += 0.05875;
                            break;
                    }
                    break;
                case 6: //0.25 / 4 = 0.0625
                    switch (index) {
                        case 0:
                            damageReduction += 0.0625;
                            break;
                        case 1:
                            damageReduction += 0.0625;
                            break;
                        case 2:
                            damageReduction += 0.0625;
                            break;
                        case 3:
                            damageReduction += 0.0625;
                            break;
                    }
                    break;
                case 7: //0.275 / 4 = 0.06875
                    switch (index) {
                        case 0:
                            damageReduction += 0.06875;
                            break;
                        case 1:
                            damageReduction += 0.06875;
                            break;
                        case 2:
                            damageReduction += 0.06875;
                            break;
                        case 3:
                            damageReduction += 0.06875;
                            break;
                    }
                    break;
                case 8: //0.30 / 4 = 0.075
                    switch (index) {
                        case 0:
                            damageReduction += 0.075;
                            break;
                        case 1:
                            damageReduction += 0.075;
                            break;
                        case 2:
                            damageReduction += 0.075;
                            break;
                        case 3:
                            damageReduction += 0.075;
                            break;
                    }
                    break;
                case 9: //0.335 / 4 = 0.08375
                    switch (index) {
                        case 0:
                            damageReduction += 0.08375;
                            break;
                        case 1:
                            damageReduction += 0.08375;
                            break;
                        case 2:
                            damageReduction += 0.08375;
                            break;
                        case 3:
                            damageReduction += 0.08375;
                            break;
                    }
                    break;
                case 10: //0.35 / 4 = 0.0875
                    switch (index) {
                        case 0:
                            damageReduction += 0.0875;
                            break;
                        case 1:
                            damageReduction += 0.0875;
                            break;
                        case 2:
                            damageReduction += 0.0875;
                            break;
                        case 3:
                            damageReduction += 0.0875;
                            break;
                    }
                    break;
            }
        }

        return damageReduction;
    }

    public float calculatePotionEffects(float damage, Player player) {
        if (player.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE))
            for (PotionEffect effect : player.getActivePotionEffects())
                if (effect.getType().equals(PotionEffectType.DAMAGE_RESISTANCE))
                    return damage - (damage * (0.1f * (float) effect.getAmplifier()));

        return damage;
    }

    public float getFeatherFallingCalculation(Player player) {
        float damageReduction = 1.0f;

        ItemStack itemStack = player.getInventory().getBoots();

        if (itemStack == null)
            return 1;

        int level = itemStack.getEnchantmentLevel(Enchantment.PROTECTION_FALL);

        return damageReduction * (1f - (0.05f * (float) level));
    }
}
