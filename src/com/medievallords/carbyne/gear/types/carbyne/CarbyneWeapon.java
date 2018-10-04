package com.medievallords.carbyne.gear.types.carbyne;

import com.medievallords.carbyne.gear.GearManager;
import com.medievallords.carbyne.gear.artifacts.Artifact;
import com.medievallords.carbyne.gear.effects.carbyne.CarbyneEffect;
import com.medievallords.carbyne.gear.specials.Special;
import com.medievallords.carbyne.gear.types.CarbyneGear;
import com.medievallords.carbyne.gear.types.GearState;
import com.medievallords.carbyne.recipes.CustomCarbyneRecipe;
import com.medievallords.carbyne.utils.*;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Getter
@Setter
public class CarbyneWeapon extends CarbyneGear {

    private List<String> enchantments = new ArrayList<>();
    private String material = "";
    private HashMap<PotionEffect, Double> offensivePotionEffects = new HashMap<>();
    private HashMap<PotionEffect, Double> defensivePotionEffects = new HashMap<>();
    private List<CarbyneEffect> carbyneEffects = new ArrayList<>();
    private Special special;
    private boolean allowInDisabledZones;
    private double damage = 25;
    private Artifact artifact;
    private final List<CustomCarbyneRecipe> recipes = new ArrayList<>();

    @Override
    public boolean load(ConfigurationSection cs, String index, GearManager gearManager) {
        if ((displayName = cs.getString(index + ".DisplayName")) == null) return false;
        if ((type = cs.getString(index + ".Type")) == null) return false;
        if ((gearCode = cs.getString(index + ".GearCode")) == null) return false;
        if (!type.equalsIgnoreCase("Bow"))
            if ((material = cs.getString(index + ".Material")) == null) return false;
        if ((maxDurability = cs.getInt(index + ".Durability")) == -1) return false;
        if ((enchantments = cs.getStringList(index + ".Enchantments")) == null || enchantments.size() <= 0)
            return false;
        if ((cost = cs.getInt(index + ".Cost")) == -1) return false;
        if ((damage = cs.getDouble(index + ".Damage")) == -1) return false;

        displayName = cs.getString(index + ".DisplayName");
        type = cs.getString(index + ".Type");
        if (!type.equalsIgnoreCase("Bow"))
            material = cs.getString(index + ".Material");
        type = cs.getString(index + ".Type");
        maxDurability = cs.getInt(index + ".Durability");
        lore = cs.getStringList(index + ".Lore");
        enchantments = cs.getStringList(index + ".Enchantments");
        if (cs.contains(index + ".State")) {
            this.state = GearState.valueOf(cs.getString(index + ".State").toUpperCase());
        }
        gearCode = cs.getString(index + ".GearCode");
        cost = cs.getInt(index + ".Cost");
        allowInDisabledZones = cs.getBoolean(index + ".AllowInDisabledZones");
        damage = cs.getDouble(index + ".Damage");

        if (cs.contains(index + ".RepairMaterial")) {
            repairType = Material.getMaterial(cs.getString(index + ".RepairMaterial"));
        }

        if (cs.contains(index + ".RepairData")) {
            repairData = cs.getInt(index + ".RepairData");
        }

        if (cs.contains(index + ".RepairCost")) {
            repairCost = cs.getInt(index + ".RepairCost");
        }

        if (cs.getStringList(index + ".OffensivePotionEffects") != null)
            for (String potion : cs.getStringList(index + ".OffensivePotionEffects")) {
                String[] split = potion.split(",");

                if (split.length == 3)
                    offensivePotionEffects.put(new PotionEffect(PotionEffectType.getByName(split[0].toUpperCase()), Integer.parseInt(split[1]), 0, false, false), Double.parseDouble(split[2]));
                else if (split.length == 4)
                    offensivePotionEffects.put(new PotionEffect(PotionEffectType.getByName(split[0].toUpperCase()), Integer.parseInt(split[2]), Integer.parseInt(split[1]), false, false), Double.parseDouble(split[3]));
            }

        if (cs.getStringList(index + ".DefensivePotionEffects") != null)
            for (String potion : cs.getStringList(index + ".DefensivePotionEffects")) {
                String[] split = potion.split(",");

                if (split.length == 3)
                    defensivePotionEffects.put(new PotionEffect(PotionEffectType.getByName(split[0].toUpperCase()), Integer.parseInt(split[1]), 0, false, false), Double.parseDouble(split[2]));
                else if (split.length == 4)
                    defensivePotionEffects.put(new PotionEffect(PotionEffectType.getByName(split[0].toUpperCase()), Integer.parseInt(split[2]), Integer.parseInt(split[1]), false, false), Double.parseDouble(split[3]));
            }

        if (cs.getString(index + ".Special") != null)
            if (gearManager.getSpecialByName(cs.getString(index + ".Special")) != null)
                special = gearManager.getSpecialByName(cs.getString(index + ".Special"));

        if (!cs.contains(index + ".Artifact")) {
            return true;
        }

        Artifact artifactF = gearManager.getArtifact(cs.getString(index + ".Artifact"));
        if (artifactF == null) {
            return true;
        }

        this.artifact = artifactF;

        if (!cs.contains(index + ".RecipeIngredients")) {
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

        ConfigurationSection recipeSection = cs.getConfigurationSection(index + ".RecipeIngredients");
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

            if (!type.equalsIgnoreCase("bow")) {
                if (split[0].equalsIgnoreCase("sharpness"))
                    enchantmentHashMap.put(Enchantment.DAMAGE_ALL, Integer.valueOf(split[1]));
                else if (split[0].equalsIgnoreCase("arthropod"))
                    enchantmentHashMap.put(Enchantment.DAMAGE_ARTHROPODS, Integer.valueOf(split[1]));
                else if (split[0].equalsIgnoreCase("undead"))
                    enchantmentHashMap.put(Enchantment.DAMAGE_UNDEAD, Integer.valueOf(split[1]));
                else if (split[0].equalsIgnoreCase("fire"))
                    enchantmentHashMap.put(Enchantment.FIRE_ASPECT, Integer.valueOf(split[1]));
                else if (split[0].equalsIgnoreCase("loot"))
                    enchantmentHashMap.put(Enchantment.LOOT_BONUS_MOBS, Integer.valueOf(split[1]));
                else if (split[0].equalsIgnoreCase("knockback"))
                    enchantmentHashMap.put(Enchantment.KNOCKBACK, Integer.valueOf(split[1]));
                else if (split[0].equalsIgnoreCase("durability"))
                    enchantmentHashMap.put(Enchantment.DURABILITY, Integer.valueOf(split[1]));
                else if (split[0].equalsIgnoreCase("efficiency"))
                    enchantmentHashMap.put(Enchantment.DIG_SPEED, Integer.valueOf(split[1]));
                else if (split[0].equalsIgnoreCase("fortune"))
                    enchantmentHashMap.put(Enchantment.LOOT_BONUS_BLOCKS, Integer.valueOf(split[1]));
            } else if (split[0].equalsIgnoreCase("damage"))
                enchantmentHashMap.put(Enchantment.ARROW_DAMAGE, Integer.valueOf(split[1]));
            else if (split[0].equalsIgnoreCase("fire"))
                enchantmentHashMap.put(Enchantment.ARROW_FIRE, Integer.valueOf(split[1]));
            else if (split[0].equalsIgnoreCase("infinite"))
                enchantmentHashMap.put(Enchantment.ARROW_INFINITE, Integer.valueOf(split[1]));
            else if (split[0].equalsIgnoreCase("knockback"))
                enchantmentHashMap.put(Enchantment.ARROW_KNOCKBACK, Integer.valueOf(split[1]));
        }

        loreDupe.add(HiddenStringUtils.encodeString(gearCode + "," + maxDurability));
        double totalDamage = damage;
        if (enchantmentHashMap.containsKey(Enchantment.DAMAGE_ALL))
            totalDamage += enchantmentHashMap.get(Enchantment.DAMAGE_ALL) * GearManager.SHARPNESS_DAMAGE;

        loreDupe.add("&aDamage&7: &c" + ((int) (totalDamage)) + "-" + ((int) totalDamage + (int)(totalDamage * 0.8)));

        if (special != null)
            loreDupe.add("&aSpecial&7: &c" + special.getSpecialName().replace("_", " "));

        if (!storeItem)
            if (special != null)
                loreDupe.add("&aSpecial Charge&7: &c0/" + special.getRequiredCharge());

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
            if (!loreDupe.isEmpty()) {
                loreDupe.add("");
            }

            loreDupe.addAll(lore);
        }

        Material mat = Material.STONE;
        if (type.equalsIgnoreCase("sword"))
            if (Material.getMaterial((material + "_SWORD").toUpperCase()) != null)
                mat = Material.getMaterial((material + "_SWORD").toUpperCase());
            else
                return null;
        else if (type.equalsIgnoreCase("axe"))
            if (Material.getMaterial((material + "_AXE").toUpperCase()) != null)
                mat = Material.getMaterial((material + "_AXE").toUpperCase());
            else
                return null;
        else if (type.equalsIgnoreCase("hoe"))
            if (Material.getMaterial((material + "_HOE").toUpperCase()) != null)
                mat = Material.getMaterial((material + "_HOE").toUpperCase());
            else
                return null;
        else if (type.equalsIgnoreCase("spade"))
            if (Material.getMaterial((material + "_SPADE").toUpperCase()) != null)
                mat = Material.getMaterial((material + "_SPADE").toUpperCase());
            else
                return null;
        else if (type.equalsIgnoreCase("pickaxe"))
            if (Material.getMaterial((material + "_PICKAXE").toUpperCase()) != null)
                mat = Material.getMaterial((material + "_PICKAXE").toUpperCase());
            else
                return null;
        else if (type.equalsIgnoreCase("bow"))
            mat = Material.BOW;

        ItemBuilder builder = new ItemBuilder(mat)
                .name(displayName)
                .addEnchantments(enchantmentHashMap).hideFlags();

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

    public double getTotalDamage() {
        return damage;
    }

    @Override
    public void damageItem(Player wielder, ItemStack itemStack) {
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

    public void setSpecialCharge(ItemStack itemStack, int amount) {
        int charge = getSpecialCharge(itemStack);

        if (charge == -1)
            return;

        Namer.setLore(itemStack, "&aSpecial Charge&7: &c" + amount + "/" + special.getRequiredCharge(), 3);
    }

    public int getSpecialCharge(ItemStack itemStack) {
        if (itemStack == null)
            return 0;
        try {
            return Integer.valueOf(ChatColor.stripColor(itemStack.getItemMeta().getLore().get(3)).replace(" ", "").split(":")[1].split("/")[0]);
        } catch (Exception ez) {
            return 0;
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
}
