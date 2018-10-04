package com.medievallords.carbyne.gear.types.carbyne;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.gear.GearManager;
import com.medievallords.carbyne.gear.artifacts.Artifact;
import com.medievallords.carbyne.gear.effects.carbyne.CarbyneEffect;
import com.medievallords.carbyne.gear.types.CarbyneGear;
import com.medievallords.carbyne.gear.types.GearState;
import com.medievallords.carbyne.recipes.CustomCarbyneRecipe;
import com.medievallords.carbyne.utils.*;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Getter
@Setter
public class CarbyneArmor extends CarbyneGear {

    private Color baseColor, minFadeColor, maxFadeColor;
    private int[] tickFadeColor;
    private List<String> enchantments = new ArrayList<>();
    private HashMap<PotionEffect, Double> offensivePotionEffects = new HashMap<>();
    private HashMap<PotionEffect, Double> defensivePotionEffects = new HashMap<>();
    private List<CarbyneEffect> carbyneEffects = new ArrayList<>();
    private double health = 1;
    private Artifact artifact;
    private final List<CustomCarbyneRecipe> recipes = new ArrayList<>();

    @Override
    public boolean load(ConfigurationSection cs, String type, GearManager gearManager) {
        if ((displayName = cs.getString(type + ".DisplayName")) == null) return false;
        if ((gearCode = cs.getString(type + ".GearCode")) == null) return false;
        if ((maxDurability = cs.getInt(type + ".Durability")) == -1) return false;
        if ((cost = cs.getInt(type + ".Cost")) == -1) return false;
        if ((health = cs.getDouble(type + ".Health")) == -1) return false;

        this.type = type;
        this.displayName = cs.getString(type + ".DisplayName");
        this.gearCode = cs.getString(type + ".GearCode");
        this.maxDurability = cs.getInt(type + ".Durability");
        this.lore = cs.getStringList(type + ".Lore");
        this.enchantments = cs.getStringList(type + ".Enchantments");
        this.cost = cs.getInt(type + ".Cost");
        if (cs.contains(type + ".State")) {
            this.state = GearState.valueOf(cs.getString(type + ".State").toUpperCase());
        }
        this.health = cs.getDouble(type + ".Health");

        if (cs.contains(type + ".RepairMaterial"))
            setRepairType(Material.getMaterial(cs.getString(type + ".RepairMaterial")));

        if (cs.contains(type + ".RepairData"))
            setRepairData(cs.getInt(type + ".RepairData"));

        if (cs.contains(type + ".RepairCost"))
            setRepairCost(cs.getInt(type + ".RepairCost"));

        if (cs.getString(type + ".BaseColor") != null) {
            String[] split = cs.getString(type + ".BaseColor").split(",");

            if (split.length == 3)
                baseColor = Color.fromRGB(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
            else
                baseColor = Color.WHITE;
        }

        if (cs.getString(type + ".MinFadeColor") != null) {
            String[] split = cs.getString(type + ".MinFadeColor").split(",");

            if (split.length == 3)
                minFadeColor = Color.fromRGB(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
            else
                minFadeColor = Color.WHITE;
        }

        if (cs.getString(type + ".MaxFadeColor") != null) {
            String[] split = cs.getString(type + ".MaxFadeColor").split(",");

            if (split.length == 3)
                maxFadeColor = Color.fromRGB(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
            else
                maxFadeColor = Color.WHITE;
        }

        if (cs.getString(type + ".TickFadeColor") != null) {
            String[] split = cs.getString(type + ".TickFadeColor").split(",");

            if (split.length == 3)
                tickFadeColor = new int[]{Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2])};
            else
                tickFadeColor = new int[]{0, 0, 0};
        }

        //if (type.equalsIgnoreCase("chestplate") || type.equalsIgnoreCase("leggings")) {
        if (cs.getStringList(type + ".OffensivePotionEffects") != null) {
            for (String potion : cs.getStringList(type + ".OffensivePotionEffects")) {
                String[] split = potion.split(",");

                if (split.length == 3)
                    offensivePotionEffects.put(new PotionEffect(PotionEffectType.getByName(split[0].toUpperCase()), Integer.parseInt(split[1]), 0, false, false), Double.parseDouble(split[2]));
                else if (split.length == 4)
                    offensivePotionEffects.put(new PotionEffect(PotionEffectType.getByName(split[0].toUpperCase()), Integer.parseInt(split[2]), Integer.parseInt(split[1]), false, false), Double.parseDouble(split[3]));
            }
        }
        if (cs.getStringList(type + ".DefensivePotionEffects") != null) {
            for (String potion : cs.getStringList(type + ".DefensivePotionEffects")) {
                String[] split = potion.split(",");

                if (split.length == 3)
                    defensivePotionEffects.put(new PotionEffect(PotionEffectType.getByName(split[0].toUpperCase()), Integer.parseInt(split[1]), 0, false, false), Double.parseDouble(split[2]));
                else if (split.length == 4)
                    defensivePotionEffects.put(new PotionEffect(PotionEffectType.getByName(split[0].toUpperCase()), Integer.parseInt(split[2]), Integer.parseInt(split[1]), false, false), Double.parseDouble(split[3]));

            }
        }

        if (!cs.contains(type + ".Artifact")) {
            return true;
        }

        Artifact artifactF = gearManager.getArtifact(cs.getString(type + ".Artifact"));
        if (artifactF == null) {
            return true;
        }

        this.artifact = artifactF;

        if (!cs.contains(type + ".RecipeIngredients")) {
            return true;
        }

        /* Crafting Recipe Format

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

        ConfigurationSection recipeSection = cs.getConfigurationSection(type + ".RecipeIngredients");
        for (String recipeName : recipeSection.getKeys(false)) {
            ArrayList<ItemStack> ingredients = new ArrayList<>();
            for (String recipeItemString : recipeSection.getConfigurationSection(recipeName).getKeys(false)) {
                ConfigurationSection itemSection = recipeSection.getConfigurationSection(recipeName).getConfigurationSection(recipeItemString);

                boolean carbyneToken = false;
                String resultItemDisplay = "";
                if (itemSection.contains("DisplayName")) {
                    String disp = itemSection.getString("DisplayName");
                    if (disp.equalsIgnoreCase("token")) {
                        carbyneToken = true;
                    } else {
                        resultItemDisplay = disp;
                    }
                }
                if (!carbyneToken) {
                    Material resultItemMaterial = Material.getMaterial(itemSection.contains("ItemMat") ? itemSection.getString("ItemMat") : "AIR");


                    if (resultItemMaterial == Material.AIR) {
                        ingredients.add(new ItemBuilder(resultItemMaterial).build());
                        continue;
                    }

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
                } else {
                    ingredients.add(gearManager.getTokenItem());
                }

            }

            ingredients.add(this.artifact.getCustomRecipe().getResult());

            this.recipes.add(new CustomCarbyneRecipe("recipe:" + recipeName + ":" + gearCode, getItem(false), ingredients));
        }

        return true;
    }

    @Override
    public ItemStack getItem(boolean storeItem) {
        List<String> loreDupe = new ArrayList<>();

        HashMap<Enchantment, Integer> enchantmentHashMap = new HashMap<>();
        for (String s : enchantments) {
            String[] split = s.split(",");

            if (split.length != 2)
                continue;
            if (split[0].equalsIgnoreCase("protection"))
                enchantmentHashMap.put(Enchantment.PROTECTION_ENVIRONMENTAL, Integer.valueOf(split[1]));
            else if (split[0].equalsIgnoreCase("fireprotection"))
                enchantmentHashMap.put(Enchantment.PROTECTION_FIRE, Integer.valueOf(split[1]));
            else if (split[0].equalsIgnoreCase("featherfalling"))
                enchantmentHashMap.put(Enchantment.PROTECTION_FALL, Integer.valueOf(split[1]));
            else if (split[0].equalsIgnoreCase("blastprotection"))
                enchantmentHashMap.put(Enchantment.PROTECTION_EXPLOSIONS, Integer.valueOf(split[1]));
            else if (split[0].equalsIgnoreCase("projectileprotection"))
                enchantmentHashMap.put(Enchantment.PROTECTION_PROJECTILE, Integer.valueOf(split[1]));
            else if (split[0].equalsIgnoreCase("respiration"))
                enchantmentHashMap.put(Enchantment.OXYGEN, Integer.valueOf(split[1]));
            else if (split[0].equalsIgnoreCase("aquaaffinity"))
                enchantmentHashMap.put(Enchantment.WATER_WORKER, Integer.valueOf(split[1]));
            else if (split[0].equalsIgnoreCase("thorns"))
                enchantmentHashMap.put(Enchantment.THORNS, Integer.valueOf(split[1]));
            else if (split[0].equalsIgnoreCase("unbreaking"))
                enchantmentHashMap.put(Enchantment.DURABILITY, Integer.valueOf(split[1]));
            else
                enchantmentHashMap.put(Enchantment.getByName(split[0].toUpperCase()), Integer.parseInt(split[1]));
        }

        loreDupe.add(HiddenStringUtils.encodeString(gearCode + "," + maxDurability));
        int totalHealth = (int) health;
        if (enchantmentHashMap.containsKey(Enchantment.PROTECTION_ENVIRONMENTAL)) {
            totalHealth += (double) (enchantmentHashMap.get(Enchantment.PROTECTION_ENVIRONMENTAL) * GearManager.PROTECTION_HEALTH);
        }

        loreDupe.add("&aHealth&7: &c" + totalHealth);
        loreDupe.add("&aPolished&7: &cfalse");

        if (offensivePotionEffects.keySet().size() > 0 || defensivePotionEffects.keySet().size() > 0) {
            if (defensivePotionEffects.keySet().size() > 0) {
                loreDupe.add("");
                loreDupe.add("&aDefensive Effects&7:");

                for (PotionEffect effect : defensivePotionEffects.keySet())
                    loreDupe.add("  &7- &3" + MessageManager.getPotionTypeFriendlyName(effect.getType()) + " &b" + MessageManager.getPotionAmplifierInRomanNumerals(effect.getAmplifier() + 1) + " &6" + (effect.getDuration() / 20) + "s &c" + StringUtils.round(defensivePotionEffects.get(effect) * 100.0, 2) + "% &f(On Hit)");
            }

            if (offensivePotionEffects.keySet().size() > 0) {
                loreDupe.add("");
                loreDupe.add("&aOffensive Effects&7:");

                for (PotionEffect effect : offensivePotionEffects.keySet())
                    loreDupe.add("  &7- &3" + MessageManager.getPotionTypeFriendlyName(effect.getType()) + " &b" + MessageManager.getPotionAmplifierInRomanNumerals(effect.getAmplifier() + 1) + " &6" + (effect.getDuration() / 20) + "s &c" + StringUtils.round(offensivePotionEffects.get(effect) * 100.0, 2) + "% &f(On Hit)");
            }
        }

        if (lore != null && lore.size() > 0) {
            loreDupe.add("");
            loreDupe.addAll(lore);
        }

        ItemBuilder builder = new ItemBuilder(Material.getMaterial(("leather_" + type).toUpperCase()))
                .name(displayName)
                .addEnchantments(enchantmentHashMap)
                .color(baseColor);

        if (lore != null && lore.size() > 0)
            builder.setLore((loreDupe.size() > 0 ? loreDupe : lore));
        else if (loreDupe.size() > 0)
            builder.setLore(loreDupe);

        return builder.build();
    }

    public void applyDefensiveEffect(Player target) {
        if (Cooldowns.getCooldown(target.getUniqueId(), "DefensiveEffect") > 0L)
            return;

        for (PotionEffect effect : defensivePotionEffects.keySet()) {
            Double random = Math.random();

            if (random <= defensivePotionEffects.get(effect)) {
                boolean apply = true;

                for (PotionEffect potionEffect : target.getActivePotionEffects())
                    if (potionEffect.getType() == effect.getType())
                        if (potionEffect.getAmplifier() >= effect.getAmplifier() | potionEffect.getDuration() >= effect.getDuration())
                            apply = false;

                if (apply) {
                    target.addPotionEffect(effect);
                    String s = ChatColor.translateAlternateColorCodes('&', "&aYou have received &b" + Namer.getPotionEffectName(effect) + " &afor &b" + (effect.getDuration() / 20) + " &asec(s).");
                    JSONMessage json = JSONMessage.create(s);
                    json.actionbar(target);
                    Cooldowns.setCooldown(target.getUniqueId(), "DefensiveEffect", 3000L);
                }
            }
        }
    }

    public void applyOffensiveEffect(Player target) {
        if (Cooldowns.getCooldown(target.getUniqueId(), "OffensiveEffect") > 0L)
            return;

        for (PotionEffect effect : offensivePotionEffects.keySet()) {
            Double random = Math.random();

            if (random <= offensivePotionEffects.get(effect)) {
                boolean apply = true;

                for (PotionEffect potionEffect : target.getActivePotionEffects())
                    if (potionEffect.getType() == effect.getType())
                        if (potionEffect.getAmplifier() >= effect.getAmplifier() | potionEffect.getDuration() >= effect.getDuration())
                            apply = false;

                if (apply) {
                    target.addPotionEffect(effect);
                    String s = ChatColor.translateAlternateColorCodes('&', "&aYou have received &c" + Namer.getPotionEffectName(effect) + " &afor &b" + (effect.getDuration() / 20) + " &asec(s).");
                    JSONMessage json = JSONMessage.create(s);
                    json.actionbar(target);
                    Cooldowns.setCooldown(target.getUniqueId(), "OffensiveEffect", 3000L);
                }
            }
        }
    }


    public double getTotalHealth(ItemStack itemStack, boolean nerfed) {
        double total = health;

        if (isPolished(itemStack))
            total += 50.0;

        if (nerfed)
            total = total * GearManager.NERFED_PERCENTAGE_ARMOR;

        return total;
    }

    @Override
    public void damageItem(Player wielder, ItemStack itemStack) {
        if (isPolished(itemStack))
            if (ThreadLocalRandom.current().nextDouble(1.0) <= 0.15)
                return;

        int durability = getDurability(itemStack);

        if (durability == -1)
            return;

        double chance = 1;

        if (itemStack.containsEnchantment(Enchantment.DURABILITY)) {
            int level = itemStack.getEnchantmentLevel(Enchantment.DURABILITY);
            double calc = (100 / (level + 1));
            chance = calc / 100;
        }

        if (chance < Math.random())
            return;

        if (durability >= 1) {
            if (durability > maxDurability)
                durability = maxDurability;

            durability--;
            Namer.setLore(itemStack, HiddenStringUtils.encodeString(gearCode + "," + durability), 0);
            itemStack.setDurability((short) durabilityScale(itemStack));

            if (itemStack.getDurability() <= 0)
                itemStack.setDurability((short) 0);
            else if (itemStack.getDurability() >= itemStack.getType().getMaxDurability())
                itemStack.setDurability(itemStack.getType().getMaxDurability());
        } else {
            PlayerItemBreakEvent event = new PlayerItemBreakEvent(wielder, itemStack);
            Bukkit.getPluginManager().callEvent(event);
            wielder.getInventory().remove(itemStack);
            wielder.updateInventory();
            wielder.playSound(wielder.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 1);
        }
    }

    @Override
    public int getDurability(ItemStack itemStack) {
        if (itemStack == null)
            return -1;

        String line = HiddenStringUtils.extractHiddenString(itemStack.getItemMeta().getLore().get(0));
        String[] split = line.split(",");
        if (split.length != 2) {
            Namer.setLore(itemStack, HiddenStringUtils.encodeString(gearCode + "," + maxDurability), 0);
            return maxDurability;
        }

        try {
            return Integer.valueOf(split[1]);
        } catch (Exception ez) {
            Namer.setLore(itemStack, HiddenStringUtils.encodeString(gearCode + "," + maxDurability), 0);
            return maxDurability;
        }
    }

    @Override
    public int getRepairCost(ItemStack itemStack) {
        int maxAmount = getRepairCost();
        double per = (double) maxDurability / (double) maxAmount;
        double dura = ((double) (getDurability(itemStack)));
        for (int i = 1; i <= maxAmount; i++)
            if (dura < per * i)
                return (maxAmount + 1) - i;

        return 0;
    }

    @Override
    public void setDurability(ItemStack itemStack, int durability) {
        Namer.setLore(itemStack, HiddenStringUtils.encodeString(gearCode + "," + durability), 0);
        itemStack.setDurability((short) durabilityScale(itemStack));
    }

    public int durabilityScale(ItemStack itemStack) {
        double scale = ((double) (getDurability(itemStack))) / ((double) (getMaxDurability()));
        double durability = ((double) (itemStack.getType().getMaxDurability())) * scale;
        return itemStack.getType().getMaxDurability() - (int) Math.round(durability);
    }

    public ItemStack getPolishedItem() {
        return new ItemBuilder(getItem(false)).setLore(2, "&aPolished: &ctrue").color(getMinFadeColor()).build();
    }

    public boolean isPolished(ItemStack itemStack) {
        if (itemStack == null)
            return false;

        if (!itemStack.hasItemMeta())
            return false;

        if (!itemStack.getItemMeta().hasLore())
            return false;

        try {
            return Boolean.valueOf(ChatColor.stripColor(itemStack.getItemMeta().getLore().get(2)).replace(" ", "").split(":")[1]);
        } catch (Exception ex) {
            return false;
        }
    }
}