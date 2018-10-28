package com.medievallords.carbyne.gear;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.gear.artifacts.Artifact;
import com.medievallords.carbyne.gear.effects.GearEffects;
import com.medievallords.carbyne.gear.listeners.GearGuiListeners;
import com.medievallords.carbyne.gear.specials.Special;
import com.medievallords.carbyne.gear.specials.types.*;
import com.medievallords.carbyne.gear.types.CarbyneGear;
import com.medievallords.carbyne.gear.types.GearState;
import com.medievallords.carbyne.gear.types.carbyne.CarbyneArmor;
import com.medievallords.carbyne.gear.types.carbyne.CarbyneWeapon;
import com.medievallords.carbyne.gear.types.minecraft.MinecraftArmor;
import com.medievallords.carbyne.gear.types.minecraft.MinecraftWeapon;
import com.medievallords.carbyne.recipes.CustomCarbyneRecipe;
import com.medievallords.carbyne.recipes.CustomRecipe;
import com.medievallords.carbyne.region.Region;
import com.medievallords.carbyne.utils.*;
import com.medievallords.carbyne.zones.Zone;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;

@Getter
public class GearManager {

    private final GearGuiManager gearGuiManager;
    private final GearEffects gearEffects;

    public static int
            PROTECTION_HEALTH = 0,
            SHARPNESS_DAMAGE = 0,
            DAMAGE_RESISTANCE = 0,
            WEAKNESS = 0,
            POTION_HEALING = 0,
            EXPLOSION_DAMAGE = 0,
            POWER_DAMAGE = 0;

    public static double
            POISON_DAMAGE = 0,
            VOID_DAMAGE = 0,
            WITHER_DAMAGE = 0,
            LIGHTNING_DAMAGE = 0,
            SUFFOCATION_DAMAGE = 0,
            STARVATION_DAMAGE = 0,
            FIRE_DAMAGE = 0,
            LAVA_DAMAGE = 0.,
            DROWNING_DAMAGE = 0,
            INCREASE_DAMAGE = 0,
            REGENERATION = 0,
            NERFED_PERCENTAGE_ARMOR = 0,
            NERFED_PERCENTAGE_WEAPON = 0,
            HIDDEN_GEAR_CHANCE = 0;

    private List<CarbyneGear>
            carbyneGear = new ArrayList<>(),
            defaultArmors = new ArrayList<>(),
            defaultWeapons = new ArrayList<>();
    private List<Special> specials = new ArrayList<>();
    private List<Item> repairItems = new ArrayList<>();
    private final List<Location> forgeLocations = new ArrayList<>();

    private HashMap<String, Artifact> artifacts = new HashMap<>();

    private List<Region> nerfedRegions = new ArrayList<>();
    private HashMap<UUID, BukkitTask> playerGearFadeSchedulers = new HashMap<>();

    private int tokenId, tokenData, polishId, polishData, prizeEggId, prizeEggData;
    private String tokenDisplayName, polishDisplayName, prizeEggDisplayName;
    private List<String> tokenLore, polishLore, prizeEggLore;
    private String tokenCode, polishCode, prizeEggCode;

    public GearManager() {
        gearEffects = new GearEffects();

        load(Carbyne.getInstance().getGearFileConfiguration());

        gearGuiManager = new GearGuiManager(this);

        Bukkit.getPluginManager().registerEvents(new GearGuiListeners(this), Carbyne.getInstance());
    }

    public void load(FileConfiguration configuration) {

        Bukkit.resetRecipes();
        CustomRecipe.recipes.clear();
        CustomCarbyneRecipe.carbyneRecipes.clear();

        loadTokenOptions(configuration);
        loadPolishOptions(configuration);
        loadPrizeEggOptions(configuration);

        specials.add(new FireStorm());
        specials.add(new LightningStorm());
        specials.add(new BastionOfHealth());
        specials.add(new HinderingShot());
        specials.add(new Frostbite());
        specials.add(new ShootingStar());
        specials.add(new WitherStorm());
        specials.add(new InfernalExplosion());
        specials.add(new TestBlockSpecial());
        specials.add(new BreathOfIce());
        specials.add(new FrostBolt());

        carbyneGear.clear();
        defaultArmors.clear();
        defaultWeapons.clear();

        ConfigurationSection cs;
        String type = "";

        cs = configuration.getConfigurationSection("Artifacts");
        if (cs != null) {
            artifacts.clear();
            for (String artifactName : cs.getKeys(false)) {
                Artifact artifact = new Artifact(artifactName);
                artifact.load(cs.getConfigurationSection(artifactName));

                this.artifacts.put(artifactName.toLowerCase(), artifact);
            }
        }

        forgeLocations.clear();
        if (configuration.contains("ForgeLocations")) {
            for (String locString : configuration.getStringList("ForgeLocations")) {
                Location location = LocationSerialization.deserializeLocation(locString);
                forgeLocations.add(location);
            }
        }

        if (configuration.contains("Protection-Health"))
            PROTECTION_HEALTH = configuration.getInt("Protection-Health");

        if (configuration.contains("Sharpness-Damage"))
            SHARPNESS_DAMAGE = configuration.getInt("Sharpness-Damage");

        if (configuration.contains("Power-Damage"))
            POWER_DAMAGE = configuration.getInt("Power-Damage");

        if (configuration.contains("Damage-Resistance"))
            DAMAGE_RESISTANCE = configuration.getInt("Damage-Resistance");

        if (configuration.contains("Weakness"))
            WEAKNESS = configuration.getInt("Weakness");

        if (configuration.contains("Explosion"))
            EXPLOSION_DAMAGE = configuration.getInt("Explosion");

        if (configuration.contains("Potion-Healing"))
            POTION_HEALING = configuration.getInt("Potion-Healing");

        if (configuration.contains("Nerf-Armor"))
            NERFED_PERCENTAGE_ARMOR = configuration.getDouble("Nerf-Armor");

        if (configuration.contains("Nerf-Weapon"))
            NERFED_PERCENTAGE_WEAPON = configuration.getDouble("Nerf-Weapon");

        if (configuration.contains("Hidden-Gear-Chance"))
            HIDDEN_GEAR_CHANCE = configuration.getDouble("Hidden-Gear-Chance");

        if (configuration.contains("Poison"))
            POISON_DAMAGE = configuration.getDouble("Poison");

        if (configuration.contains("Void"))
            VOID_DAMAGE = configuration.getDouble("Void");

        if (configuration.contains("Wither"))
            WITHER_DAMAGE = configuration.getDouble("Wither");

        if (configuration.contains("Lightning"))
            LIGHTNING_DAMAGE = configuration.getDouble("Lightning");

        if (configuration.contains("Suffocation"))
            SUFFOCATION_DAMAGE = configuration.getDouble("Suffocation");

        if (configuration.contains("Starvation"))
            STARVATION_DAMAGE = configuration.getDouble("Starvation");

        if (configuration.contains("Fire"))
            FIRE_DAMAGE = configuration.getDouble("Fire");

        if (configuration.contains("Lava"))
            LAVA_DAMAGE = configuration.getDouble("Lava");

        if (configuration.contains("Drowning"))
            DROWNING_DAMAGE = configuration.getDouble("Drowning");

        if (configuration.contains("Strength"))
            INCREASE_DAMAGE = configuration.getDouble("Strength");

        if (configuration.contains("Regeneration"))
            REGENERATION = configuration.getDouble("Regeneration");

        cs = configuration.getConfigurationSection("Carbyne-Armor");
        for (String id : cs.getKeys(false)) {
            for (String typeId : cs.getConfigurationSection(id).getKeys(false)) {
                CarbyneGear cg = new CarbyneArmor();

                if (cg.load(cs.getConfigurationSection(id), typeId, this))
                    carbyneGear.add(cg);
                else
                    Carbyne.getInstance().getLogger().log(Level.SEVERE, "The carbyne configuration has failed to load Carbyne-Armor." + id + "." + typeId + "!");
            }
        }

        cs = configuration.getConfigurationSection("Carbyne-Weapons");
        for (String id : cs.getKeys(false)) {
            CarbyneGear cg = new CarbyneWeapon();

            if (cg.load(configuration.getConfigurationSection("Carbyne-Weapons"), id, this))
                carbyneGear.add(cg);
            else
                Carbyne.getInstance().getLogger().log(Level.SEVERE, "The carbyne configuration has failed to load Carbyne-Weapon." + id + "!");
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

                if (ma.load(cs.getConfigurationSection(material), type, this))
                    defaultArmors.add(ma);
                else
                    Carbyne.getInstance().getLogger().log(Level.SEVERE, "Minecraft-Armor configuration has failed to load " + cs + "." + type + "!");
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

                if (mw.load(cs.getConfigurationSection(material), type, this))
                    defaultWeapons.add(mw);
                else
                    Carbyne.getInstance().getLogger().log(Level.SEVERE, "Minecraft-Weapons configuration has failed to load " + cs + "." + type + "!");
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

        ConfigurationSection recipeSection = cs.getConfigurationSection("TokenItem");

        // Load artifact recipe data

        if (!recipeSection.contains("ArtifactRecipeIngredients")) {
            return;
        }

        /* Artifact Crafting Recipe Format

        ArtifactRecipe: aba,aca,ada
        ArtifactRecipeIngredients:
        - 'a=GOLD_BLOCK:0'
        - 'b=EMERALD:0'
        - 'c=QUARTZ:0'
        - 'd=NETHER_STAR:0'
        ArtifactRecipeResult:
          ItemMat: QUARTZ
          ItemData: 0
          Amount: 1
          DisplayName: '&l&bCarbyne Fragment'
          Lore:
          - 'Line 1'
          - 'Line 2'
          Enchantments:
          - 'DURABILITY,1'

         */

        ArrayList<ItemStack> ingredients = new ArrayList<>();
        for (String recipeItemString : recipeSection.getConfigurationSection("ArtifactRecipeIngredients").getKeys(false)) {
            ConfigurationSection itemSection = recipeSection.getConfigurationSection("ArtifactRecipeIngredients").getConfigurationSection(recipeItemString);

            Material resultItemMaterial = Material.getMaterial(itemSection.contains("ItemMat") ? itemSection.getString("ItemMat") : "AIR");
            if (resultItemMaterial == Material.AIR) {
                ingredients.add(new ItemBuilder(resultItemMaterial).build());
                continue;
            }

            String resultItemDisplay = itemSection.contains("DisplayName") ? itemSection.getString("DisplayName") : "";
            int resultItemData = itemSection.contains("ItemData") ? itemSection.getInt("ItemData") : 0;
            int resultItemAmount = itemSection.contains("Amount") ? itemSection.getInt("Amount") : 1;
            List<String> resultItemLore = itemSection.contains("Lore") ? itemSection.getStringList("Lore") : new ArrayList<>();

            ItemBuilder itemBuilder = new ItemBuilder(resultItemMaterial);
            itemBuilder.durability(resultItemData);
            if (!resultItemDisplay.isEmpty()) {
                itemBuilder.name(resultItemDisplay);
            }
            if (!resultItemLore.isEmpty()) {
                itemBuilder.setLore(resultItemLore);
            }
            itemBuilder.amount(resultItemAmount);

            if (itemSection.contains("Enchantments")) {
                HashMap<Enchantment, Integer> resultItemEnchantments = new HashMap<>();
                for (String enchantmentString : itemSection.getStringList("Enchantments")) {
                    String[] enchSplit = enchantmentString.split(",");
                    resultItemEnchantments.put(Enchantment.getByName(enchSplit[0].toUpperCase()), Integer.parseInt(enchSplit[1]));
                }

                itemBuilder.addEnchantments(resultItemEnchantments);
            }

            ingredients.add(itemBuilder.build());

        }

        // Create the recipe for the artifact.

        new CustomRecipe("tokenRecipe", getTokenItem(), ingredients);
    }

    public void loadPolishOptions(FileConfiguration cs) {
        polishId = cs.getInt("PolishItem.ItemId");
        polishData = cs.getInt("PolishItem.ItemData");
        polishCode = cs.getString("PolishItem.Code");
        polishDisplayName = cs.getString("PolishItem.DisplayName");
        polishLore = cs.getStringList("PolishItem.Lore");
        polishLore.add(0, HiddenStringUtils.encodeString(polishCode));
    }

    public void loadPrizeEggOptions(FileConfiguration cs) {
        prizeEggId = cs.getInt("PrizeEgg.ItemId");
        prizeEggData = cs.getInt("PrizeEgg.ItemData");
        prizeEggCode = cs.getString("PrizeEgg.Code");
        prizeEggDisplayName = cs.getString("PrizeEgg.DisplayName");
        prizeEggLore = cs.getStringList("PrizeEgg.Lore");
        prizeEggLore.add(0, HiddenStringUtils.encodeString(prizeEggCode));
    }

    public boolean isWearingCarbyne(Player player) {
        boolean wearing = false;

        for (ItemStack item : player.getInventory().getContents())
            if (isCarbyneArmor(item) || isCarbyneWeapon(item))
                wearing = true;

        for (ItemStack item : player.getInventory().getArmorContents())
            if (isCarbyneArmor(item) || isCarbyneWeapon(item))
                wearing = true;

        return wearing;
    }

//    public boolean isWearingCarbyne(Player player) {
//        boolean wearing = false;
//
//        for (ItemStack item : player.getInventory().getContents()) {
//            if (item == null)
//                continue;
//
//            if (isCarbyneArmor(item) || (isCarbyneWeapon(item) && !getCarbyneWeapon(item).isAllowInDisabledZones()))
//                wearing = true;
//        }
//
//        for (ItemStack item : player.getInventory().getArmorContents()) {
//            if (item == null)
//                continue;
//
//            if (isCarbyneArmor(item) || (isCarbyneWeapon(item) && !getCarbyneWeapon(item).isAllowInDisabledZones()))
//                wearing = true;
//        }
//
//        return wearing;
//    }

    public CarbyneGear getCarbyneGear(String gearCode) {
        for (CarbyneGear cg : carbyneGear)
            if (cg.getGearCode().equalsIgnoreCase(gearCode))
                return cg;

        return null;
    }

    public CarbyneGear getCarbyneGear(ItemStack is) {
        if (is.getItemMeta() == null)
            return null;

        List<String> lore = Namer.getLore(is);

        if (lore == null || lore.isEmpty())
            return null;

        for (CarbyneGear eg : carbyneGear)
            if (eg.getGearCode().equalsIgnoreCase(getGearCode(is)))
                return eg;

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
                if (cg.getGearCode().equalsIgnoreCase(getGearCode(is)))
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

        for (CarbyneGear carbyneGear : carbyneGear)
            if (carbyneGear instanceof CarbyneArmor) {
                CarbyneArmor carbyneArmor = (CarbyneArmor) carbyneGear;

                if (carbyneArmor.getItem(false).getType() == Material.LEATHER_CHESTPLATE)
                    carbyneArmorList.add(carbyneArmor);
            }

        carbyneArmorList.sort(Comparator.comparingDouble(CarbyneArmor::getHealth));

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

            if (cg.getGearCode().equalsIgnoreCase(getGearCode(is)))
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

        for (CarbyneGear cg : carbyneGear)
            if (cg instanceof CarbyneWeapon)
                carbyneWeapons.add((CarbyneWeapon) cg);

        //carbyneWeapons.sort((o1, o2) -> Boolean.compare(o1.isHidden(), o2.isHidden()));

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

            if (cg.getGearCode().equalsIgnoreCase(getGearCode(is)))
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

            if (cg.getGearCode().equalsIgnoreCase(getGearCode(is)))
                return true;
        }

        return false;
    }

    public MinecraftArmor getDefaultArmor(ItemStack itemStack) {
        if (itemStack == null) {
            return null;
        }

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
        if (itemStack == null) {
            return false;
        }

        if (itemStack.getItemMeta() == null)
            return false;

        if (isCarbyneArmor(itemStack))
            return false;

        for (CarbyneGear cg : defaultArmors) {
            if (!(cg instanceof MinecraftArmor))
                continue;

            if (cg.getItem(false).getType().equals(itemStack.getType()))
                return true;
        }

        return false;
    }

    public boolean isDefaultWeapon(ItemStack itemStack) {
        if (itemStack == null) {
            return false;
        }

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
        if (itemStack == null) {
            return null;
        }

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

        for (CarbyneGear carbyneGear : carbyneGear)
            if (carbyneGear instanceof CarbyneArmor) {
                CarbyneArmor carbyneArmor = (CarbyneArmor) carbyneGear;

                if (carbyneArmor.getBaseColor().equals(color))
                    carbyneArmorList.add(carbyneArmor);
            }

        return carbyneArmorList;
    }

//    public ItemStack convertDefaultItem(ItemStack item) {
//        ItemStack replacement = null;
//
//        if (isDefaultWeapon(item))
//            replacement = getDefaultWeapon(item).getItem(false);
//        else if (isDefaultArmor(item))
//            replacement = getDefaultArmor(item).getItem(false);
//
//        if (replacement != null) {
//            for (Enchantment enchantment : item.getEnchantments().keySet())
//                replacement.addUnsafeEnchantment(enchantment, item.getEnchantments().get(enchantment));
//
//            if (item.hasItemMeta()) {
//                ItemMeta im = replacement.getItemMeta();
//
//                if (item.getItemMeta().hasDisplayName())
//                    im.setDisplayName(item.getItemMeta().getDisplayName());
//
//                if (item.getItemMeta().hasLore()) {
//                    List<String> lore = im.getLore();
//
//                    for (int i = 1; i < item.getItemMeta().getLore().size(); i++) {
//                        String line = item.getItemMeta().getLore().get(i);
//                        if (line == null) {
//                            continue;
//                        }
//
//                        if (!lore.contains(line) && !line.contains("Damage Reduction") && !line.contains("Durability")) {
//                            lore.add(line);
//                        }
//                    }
//
//                    im.setLore(lore);
//                }
//
//                replacement.setItemMeta(im);
//            }
//
//            replacement.setDurability(item.getDurability());
//        }
//
//        return replacement;
//    }

    public CarbyneGear getRandomCarbyneGear(boolean includeHidden) {
        ArrayList<CarbyneGear> gears = new ArrayList<>();

        if (includeHidden) {
            double random = Math.random();

            if (random < HIDDEN_GEAR_CHANCE) {
                for (CarbyneGear gear : getCarbyneGear())
                    if (gear.getState() == GearState.HIDDEN)
                        gears.add(gear);

            } else {
                for (CarbyneGear gear : getCarbyneGear())
                    if (gear.getState() == GearState.VISIBLE)
                        gears.add(gear);
            }
        } else
            for (CarbyneGear gear : getCarbyneGear())
                if (gear.getState() == GearState.VISIBLE)
                    gears.add(gear);

        if (gears.isEmpty()) {
            return getCarbyneGear().get(0);
        }

        return gears.get(ThreadLocalRandom.current().nextInt(0, gears.size()));
    }

    public Artifact getRandomArtifact(boolean includeRare) {
        final ArrayList<Artifact> randomArtifacts = new ArrayList<>();

        if (includeRare) {
            double random = Math.random();

            if (random < HIDDEN_GEAR_CHANCE) {
                for (Artifact artifact : artifacts.values())
                    if (artifact.isRare())
                        randomArtifacts.add(artifact);

            } else {
                for (Artifact artifact : artifacts.values())
                    if (!artifact.isRare())
                        randomArtifacts.add(artifact);
            }
        } else
            for (Artifact artifact : artifacts.values())
                if (!artifact.isRare())
                    randomArtifacts.add(artifact);

        if (randomArtifacts.isEmpty()) {
            return artifacts.values().stream().findAny().get();
        }

        return randomArtifacts.get(ThreadLocalRandom.current().nextInt(0, randomArtifacts.size()));
    }

    public List<CarbyneGear> getHiddenCarbyneGear() {
        final ArrayList<CarbyneGear> gears = new ArrayList<>();
        for (CarbyneGear gear : getCarbyneGear())
            if (gear.getState() == GearState.HIDDEN)
                gears.add(gear);

        return gears;
    }

    public Special getSpecialByName(String name) {
        for (Special special : specials)
            if (special.getSpecialName().equalsIgnoreCase(name))
                return special;

        return null;
    }

    public ItemStack getTokenItem() {
        return new ItemBuilder(Material.getMaterial(tokenId)).durability(tokenData).name(tokenDisplayName).setLore(tokenLore).addGlow().build();
    }

    public ItemStack getPolishItem() {
        return new ItemBuilder(Material.getMaterial(polishId)).durability(polishData).name(polishDisplayName).setLore(polishLore).addGlow().build();
    }

    public ItemStack getPrizeEggItem() {
        return new ItemBuilder(Material.getMaterial(prizeEggId)).durability(prizeEggData).name(prizeEggDisplayName).setLore(prizeEggLore).addGlow().build();
    }

    public void convertToMoneyItem(ItemStack itemStack) {
        if (itemStack == null) {
            return;
        }

        if ((itemStack.getType() == getTokenMaterial() && itemStack.getDurability() == tokenData)) {
            Namer.setName(itemStack, tokenDisplayName);
            Namer.setLore(itemStack, tokenLore);
        } else if ((itemStack.getType() == getPrizeEggMaterial() && itemStack.getDurability() == prizeEggData)) {
            Namer.setName(itemStack, prizeEggDisplayName);
            Namer.setLore(itemStack, prizeEggLore);
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

        int i = 0;

        if (armorContents != null)
            for (ItemStack item : armorContents)
                if (item != null && getCarbyneArmor(item) != null)
                    i++;

        return (i == 4);
    }

    public Material getTokenMaterial() {
        return Material.getMaterial(tokenId);
    }

    public Material getPrizeEggMaterial() {
        return Material.getMaterial(prizeEggId);
    }

    public Material getPolishMaterial() {
        return Material.getMaterial(polishId);
    }

    public double calculateMaxHealth(Player player, boolean nerfed) {
        double maxHealth = 500.0;

        for (int i = 0; i < 4; i++) {
            ItemStack itemStack = player.getInventory().getArmorContents()[i];
            if (itemStack == null)
                continue;

            if (itemStack.containsEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL))
                maxHealth += (double) (itemStack.getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL) * PROTECTION_HEALTH);

            CarbyneArmor carbyneArmor = getCarbyneArmor(itemStack);
            if (carbyneArmor != null) {
                maxHealth += carbyneArmor.getTotalHealth(itemStack, nerfed);
                continue;
            }

            MinecraftArmor minecraftArmor = getDefaultArmor(itemStack);
            if (minecraftArmor != null)
                maxHealth += minecraftArmor.getTotalHealth();
        }

        return maxHealth;
    }

    public double calculateDamage(Player player) {
        ItemStack inHand = player.getInventory().getItemInMainHand();
        double damage = 25.0;

        if (inHand == null)
            return damage;

        damage += inHand.getEnchantmentLevel(Enchantment.DAMAGE_ALL) * SHARPNESS_DAMAGE;

        CarbyneWeapon carbyneWeapon = getCarbyneWeapon(inHand);
        if (carbyneWeapon != null)
            damage += carbyneWeapon.getTotalDamage();

        MinecraftWeapon minecraftWeapon = getDefaultWeapon(inHand);
        if (minecraftWeapon != null)
            damage += minecraftWeapon.getTotalDamage();

        if (player.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE))
            for (PotionEffect effect : player.getActivePotionEffects())
                if (effect.getType().equals(PotionEffectType.INCREASE_DAMAGE)) {
                    damage *= (INCREASE_DAMAGE * (effect.getAmplifier() + 1)) + 1;
                    break;
                }

        Zone zone = StaticClasses.zoneManager.getZone(player.getLocation());
        if (zone != null && zone.isNerfedZone()) {
            damage *= NERFED_PERCENTAGE_WEAPON;
        }

        return damage;
    }

    public String getDamage(ItemStack itemStack) {
        double damage = 25.0;

        if (itemStack == null)
            return "25";

        if (itemStack.getType() == Material.BOW) {
            damage = 100;
            damage += itemStack.getEnchantmentLevel(Enchantment.ARROW_DAMAGE) * POWER_DAMAGE;
            return (int) (damage * 0.5) + "-" + (int) damage;
        }

        damage += itemStack.getEnchantmentLevel(Enchantment.DAMAGE_ALL) * SHARPNESS_DAMAGE;

        CarbyneWeapon carbyneWeapon = getCarbyneWeapon(itemStack);
        if (carbyneWeapon != null)
            damage += carbyneWeapon.getTotalDamage();

        MinecraftWeapon minecraftWeapon = getDefaultWeapon(itemStack);
        if (minecraftWeapon != null)
            damage += minecraftWeapon.getTotalDamage();

        return ((int) (damage)) + "-" + ((int) damage + (int) (damage * 0.8));
    }

    public void calculateDamage(EntityDamageEvent event, PlayerHealth playerHealth, Player player) {
        double flatDamage = 0;

        switch (event.getCause()) {
            case FIRE_TICK:
                flatDamage = FIRE_DAMAGE;
                for (ItemStack item : player.getInventory().getArmorContents()) {
                    if (item != null && item.containsEnchantment(Enchantment.PROTECTION_FIRE)) {
                        flatDamage -= item.getEnchantmentLevel(Enchantment.PROTECTION_FIRE);
                    }
                }
                break;
            case LAVA:
                flatDamage = LAVA_DAMAGE * playerHealth.getMaxHealth();
                event.setDamage(0);
                playerHealth.setHealth(playerHealth.getHealth() - flatDamage, player);
                return;
            case LIGHTNING:
                flatDamage = LIGHTNING_DAMAGE * playerHealth.getMaxHealth();
                event.setDamage(0);
                playerHealth.setHealth(playerHealth.getHealth() - flatDamage, player);
                return;
            case DROWNING:
                flatDamage = DROWNING_DAMAGE * playerHealth.getMaxHealth();
                break;
            case STARVATION:
                flatDamage = STARVATION_DAMAGE * playerHealth.getMaxHealth();
                break;
            case VOID:
                flatDamage = VOID_DAMAGE * playerHealth.getMaxHealth();
                event.setDamage(0);
                playerHealth.setHealth(playerHealth.getHealth() - flatDamage, player);
                return;
            case POISON:
                flatDamage = POISON_DAMAGE * playerHealth.getMaxHealth();
                break;
            case WITHER:
                flatDamage = WITHER_DAMAGE * playerHealth.getMaxHealth();
                break;
            case SUFFOCATION:
                flatDamage = SUFFOCATION_DAMAGE * playerHealth.getMaxHealth();
                event.setDamage(0);
                playerHealth.setHealth(playerHealth.getHealth() - flatDamage, player);
                return;
            case BLOCK_EXPLOSION:
                flatDamage = EXPLOSION_DAMAGE;
                break;
            case ENTITY_EXPLOSION:
                flatDamage = EXPLOSION_DAMAGE;
                break;
            case FALL:
                event.setCancelled(true);
                return;
        }

        if (flatDamage > 0) {
            event.setDamage(0);
            playerHealth.setHealth(playerHealth.getHealth() - flatDamage, player);
            int prev = player.getNoDamageTicks();
            event.setCancelled(true);
            player.damage(0.0);
            player.setNoDamageTicks(prev);
        } else {
            playerHealth.setHealth(playerHealth.getHealth() - event.getDamage(), player);
            event.setDamage(0.0);
        }
    }

    public double calculateWeakness(double damage, LivingEntity player) {
        if (player.hasPotionEffect(PotionEffectType.WEAKNESS))
            for (PotionEffect effect : player.getActivePotionEffects())
                if (effect.getType().equals(PotionEffectType.WEAKNESS)) {
                    damage -= (WEAKNESS * effect.getAmplifier());
                    break;
                }

        return damage;
    }

    public double calculateDamageResistance(double damage, LivingEntity player) {
        if (player.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE))
            for (PotionEffect effect : player.getActivePotionEffects())
                if (effect.getType().equals(PotionEffectType.DAMAGE_RESISTANCE)) {
                    damage -= (DAMAGE_RESISTANCE * effect.getAmplifier());
                    break;
                }

        return damage;
    }

    public void updateHealth(ItemStack item) {
        CarbyneArmor armor = getCarbyneArmor(item);
        if (armor != null) {
            List<String> currentLore = item.getItemMeta().getLore();
            double health = armor.getTotalHealth(item, false);

            if (currentLore.size() >= 2 && currentLore.get(1).contains("Health"))
                Namer.setLore(item, "&aHealth&7: &c" + (int) health, 1);
            else {
                Namer.setName(item, armor.getDisplayName());
                int durability = armor.getDurability(item);
                Namer.setLore(item, armor.getItem(false).getItemMeta().getLore());
                armor.setDurability(item, durability);
                Namer.setLore(item, "&aHealth&7: &c" + (int) health, 1);
            }

            return;
        }

        MinecraftArmor minecraftArmor = getDefaultArmor(item);
        if (minecraftArmor != null) {
            if (!item.hasItemMeta() || !item.getItemMeta().hasLore()) {
                double health = minecraftArmor.getTotalHealth();
                Namer.setName(item, minecraftArmor.getDisplayName());
                int durability = minecraftArmor.getDurability(item);
                Namer.setLore(item, minecraftArmor.getItem(false).getItemMeta().getLore());

                minecraftArmor.setDurability(item, durability);
                Namer.setLore(item, "&aHealth&7: &c" + (int) health, 1);
            } else {
                List<String> currentLore = item.getItemMeta().getLore();
                double health = minecraftArmor.getTotalHealth();

                if (currentLore.size() >= 2 && currentLore.get(1).contains("Health"))
                    Namer.setLore(item, "&aHealth&7: &c" + (int) health, 1);
                else {
                    int durability = minecraftArmor.getDurability(item);
                    Namer.setLore(item, minecraftArmor.getItem(false).getItemMeta().getLore());

                    minecraftArmor.setDurability(item, durability);
                }
            }
        }
    }

    public void updateDamage(ItemStack item) {
        CarbyneWeapon weapon = getCarbyneWeapon(item);
        if (weapon != null) {
            List<String> currentLore = item.getItemMeta().getLore();
            String damage = getDamage(item);

            if (currentLore.size() >= 2 && currentLore.get(1).contains("Damage"))
                Namer.setLore(item, "&aDamage&7: &c" + damage, 1);
            else {
                Namer.setName(item, weapon.getDisplayName());
                int durability = weapon.getDurability(item);
                Namer.setLore(item, weapon.getItem(false).getItemMeta().getLore());

                weapon.setDurability(item, durability);
                Namer.setLore(item, "&aDamage&7: &c" + damage, 1);
            }

            return;
        }

        MinecraftWeapon minecraftWeapon = getDefaultWeapon(item);
        if (minecraftWeapon != null) {
            if (!item.hasItemMeta() || !item.getItemMeta().hasLore()) {
                Namer.setName(item, minecraftWeapon.getDisplayName());
                int durability = minecraftWeapon.getDurability(item);
                Namer.setLore(item, minecraftWeapon.getItem(false).getItemMeta().getLore());

                minecraftWeapon.setDurability(item, durability);
                String damage = getDamage(item);
                Namer.setLore(item, "&aDamage&7: &c" + damage, 1);
            } else {
                List<String> currentLore = item.getItemMeta().getLore();
                String damage = getDamage(item);

                if (currentLore.size() >= 2 && currentLore.get(1).contains("Damage"))
                    Namer.setLore(item, "&aDamage&7: &c" + damage, 1);
                else {
                    int durability = minecraftWeapon.getDurability(item);
                    Namer.setLore(item, minecraftWeapon.getItem(false).getItemMeta().getLore());

                    minecraftWeapon.setDurability(item, durability);
                    Namer.setLore(item, "&aDamage&7: &c" + damage, 1);
                }
            }
        }
    }

    public String getGearCode(ItemStack itemStack) {
        if (!itemStack.hasItemMeta())
            return null;

        if (!itemStack.getItemMeta().hasLore())
            return null;

        if (itemStack.getItemMeta().getLore().isEmpty())
            return null;

        String line = HiddenStringUtils.extractHiddenString(itemStack.getItemMeta().getLore().get(0));
        if (line == null)
            return "NULL";

        String[] split = line.split(",");

        if (split.length != 2)
            return line;

        return split[0];
    }

    public Artifact getArtifact(String name) {
        return artifacts.get(name.toLowerCase());
    }

    public Artifact getArtifact(ItemStack itemStack) {
        for (Artifact artifact : artifacts.values()) {
            if (artifact.getCustomRecipe().getResult().isSimilar(itemStack)) {
                return artifact;
            }
        }

        return null;
    }

    public Location getForgeByDistance(Location location) {
        Location loc = new Location(location.getWorld(), 0, 0, 0);
        for (Location forge : forgeLocations) {
            if (forge.distance(location) < loc.distance(location)) {
                loc = forge;
            }
        }

        return loc;
    }

    public Location getForge(Location location) {
        for (Location forge : forgeLocations) {
            if (forge.equals(location)) {
                return forge;
            }
        }

        return null;
    }
}
