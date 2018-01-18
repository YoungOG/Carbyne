package com.medievallords.carbyne.gear.types.carbyne;

import com.medievallords.carbyne.gear.effects.carbyne.CarbyneEffect;
import com.medievallords.carbyne.gear.types.CarbyneGear;
import com.medievallords.carbyne.utils.*;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
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
    private double armorRating = -1;

    @Override
    public boolean load(ConfigurationSection cs, String type) {
        if ((displayName = cs.getString(type + ".DisplayName")) == null) return false;
        if ((gearCode = cs.getString(type + ".GearCode")) == null) return false;
        if ((maxDurability = cs.getInt(type + ".Durability")) == -1) return false;
        if ((cost = cs.getInt(type + ".Cost")) == -1) return false;
        if ((armorRating = cs.getDouble(type + ".ArmorRating")) == -1) return false;

        this.type = type;
        this.displayName = cs.getString(type + ".DisplayName");
        this.gearCode = cs.getString(type + ".GearCode");
        this.maxDurability = cs.getInt(type + ".Durability");
        this.lore = cs.getStringList(type + ".Lore");
        this.enchantments = cs.getStringList(type + ".Enchantments");
        this.cost = cs.getInt(type + ".Cost");
        this.hidden = cs.getBoolean(type + ".Hidden");
        this.armorRating = cs.getDouble(type + ".ArmorRating");

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

        return true;
    }

    @Override
    public ItemStack getItem(boolean storeItem) {
        List<String> loreDupe = new ArrayList<>();

        loreDupe.add(HiddenStringUtils.encodeString(gearCode));
        loreDupe.add("&aDamage Reduction&7: &b" + (int) (armorRating * 100) + "%");
        loreDupe.add("&aDurability&7: &c" + getMaxDurability() + "/" + getMaxDurability());
        loreDupe.add("&aPolished&7: &cfalse");

        if (offensivePotionEffects.keySet().size() > 0 || defensivePotionEffects.keySet().size() > 0) {
            if (defensivePotionEffects.keySet().size() > 0) {
                loreDupe.add("");
                loreDupe.add("&aDefensive Effects&7:");

                for (PotionEffect effect : defensivePotionEffects.keySet())
                    loreDupe.add("  &7- &3" + MessageManager.getPotionTypeFriendlyName(effect.getType()) + " &b" + MessageManager.getPotionAmplifierInRomanNumerals(effect.getAmplifier() + 1) + " &6" + (effect.getDuration() / 20) + "s &c" + defensivePotionEffects.get(effect) + "% &f(On Hit)");
            }

            if (offensivePotionEffects.keySet().size() > 0) {
                loreDupe.add("");
                loreDupe.add("&aOffensive Effects&7:");

                for (PotionEffect effect : offensivePotionEffects.keySet())
                    loreDupe.add("  &7- &3" + MessageManager.getPotionTypeFriendlyName(effect.getType()) + " &b" + MessageManager.getPotionAmplifierInRomanNumerals(effect.getAmplifier() + 1) + " &6" + (effect.getDuration() / 20) + "s &c" + offensivePotionEffects.get(effect) + "% &f(On Hit)");
            }
        }

        if (lore != null && lore.size() > 0) {
            loreDupe.add("");
            loreDupe.addAll(lore);
        }

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
        if (Cooldowns.getCooldown(target.getUniqueId(), "EffectCooldown") > 0L)
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
                    Cooldowns.setCooldown(target.getUniqueId(), "EffectCooldown", 3000L);
                }
            }
        }
    }

    public void applyOffensiveEffect(Player target) {
        if (Cooldowns.getCooldown(target.getUniqueId(), "EffectCooldown") > 0L)
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
                    Cooldowns.setCooldown(target.getUniqueId(), "EffectCooldown", 3000L);
                }
            }
        }
    }

    @Override
    public void damageItem(Player wielder, ItemStack itemStack) {
        if (isPolished(itemStack))
            if (ThreadLocalRandom.current().nextDouble(1.0) <= 0.15)
                return;

        int durability = getDurability(itemStack);

        if (durability == -1)
            return;

        if (durability >= 1) {
            durability--;
            Namer.setLore(itemStack, "&aDurability&7: &c" + durability + "/" + getMaxDurability(), 2);
            itemStack.setDurability((short) durabilityScale(itemStack));

            if (itemStack.getDurability() <= 0)
                itemStack.setDurability((short) 0);
            else if (itemStack.getDurability() >= itemStack.getType().getMaxDurability())
                itemStack.setDurability(itemStack.getType().getMaxDurability());
        } else {
            wielder.getInventory().remove(itemStack);
            wielder.updateInventory();
            wielder.playSound(wielder.getLocation(), Sound.ITEM_BREAK, 1, 1);
        }
    }

    @Override
    public int getDurability(ItemStack itemStack) {
        if (itemStack == null)
            return -1;

        try {
            return Integer.valueOf(ChatColor.stripColor(itemStack.getItemMeta().getLore().get(2)).replace(" ", "").split(":")[1].split("/")[0]);
        } catch (Exception ez) {
            return -1;
        }
    }

    @Override
    public int getRepairCost(ItemStack itemStack) {
        int maxAmount = (int) Math.round(cost * 0.7);

        double per = (double) maxDurability / (double) maxAmount;
        double dura = ((double) (getDurability(itemStack)));

        for (int i = 1; i <= maxAmount; i++)
            if (dura < per * i)
                return (maxAmount + 1) - i;

        return 0;
    }

    @Override
    public void setDurability(ItemStack itemStack, int durability) {
        Namer.setLore(itemStack, "&aDurability&7: &c" + durability + "/" + getMaxDurability(), 2);
    }

    public int durabilityScale(ItemStack itemStack) {
        double scale = ((double) (getDurability(itemStack))) / ((double) (getMaxDurability()));
        double durability = ((double) (itemStack.getType().getMaxDurability())) * scale;
        return itemStack.getType().getMaxDurability() - (int) Math.round(durability);
    }

    public ItemStack getPolishedItem() {
        return new ItemBuilder(getItem(false)).setLore(3, "&aPolished: &ctrue").color(getMinFadeColor()).build();
    }

    public boolean isPolished(ItemStack itemStack) {
        if (itemStack == null)
            return false;

        if (!itemStack.hasItemMeta())
            return false;

        if (!itemStack.getItemMeta().hasLore())
            return false;

        if (itemStack.getItemMeta().getLore().size() < 4)
            return false;

        try {
            return Boolean.valueOf(ChatColor.stripColor(itemStack.getItemMeta().getLore().get(3)).replace(" ", "").split(":")[1]);
        } catch (Exception ex) {
            return false;
        }
    }
}
