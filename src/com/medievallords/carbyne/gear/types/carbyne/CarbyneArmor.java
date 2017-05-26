package com.medievallords.carbyne.gear.types.carbyne;

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

@Getter
@Setter
public class CarbyneArmor extends CarbyneGear {

    private Color color;
    private List<String> enchantments = new ArrayList<>();
    private HashMap<PotionEffect, Double> offensivePotionEffects = new HashMap<>();
    private HashMap<PotionEffect, Double> defensivePotionEffects = new HashMap<>();
    private double armorRating = -1;
    private boolean rainbow;

    @Override
    public boolean load(ConfigurationSection cs, String type) {
        if ((displayName = cs.getString(type + ".DisplayName")) == null) return false;
        if ((gearCode = cs.getString(type + ".GearCode")) == null) return false;
        if ((maxDurability = cs.getInt(type + ".Durability")) == -1) return false;
        if ((cost = cs.getInt(type + ".Cost")) == -1) return false;
        if ((armorRating = cs.getDouble(type + ".ArmorRating")) == -1) return false;

        this.type = type;
        displayName = cs.getString(type + ".DisplayName");
        gearCode = cs.getString(type + ".GearCode");
        maxDurability = cs.getInt(type + ".Durability");
        lore = cs.getStringList(type + ".Lore");
        enchantments = cs.getStringList(type + ".Enchantments");
        cost = cs.getInt(type + ".Cost");
        armorRating = cs.getDouble(type + ".ArmorRating");

        if (cs.getString(type + ".Color") != null) {
            String[] split = cs.getString(type + ".Color").split(",");

            if (split.length == 3) {
                color = Color.fromRGB(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
            } else {
                color = Color.WHITE;
            }
        }

        if (type.equalsIgnoreCase("chestplate") || type.equalsIgnoreCase("leggings")) {
            if (cs.getStringList(type + ".OffensivePotionEffects") != null) {
                for (String potion : cs.getStringList(type + ".OffensivePotionEffects")) {
                    String[] split = potion.split(",");

                    if (split.length == 3) {
                        offensivePotionEffects.put(new PotionEffect(PotionEffectType.getByName(split[0].toUpperCase()), Integer.parseInt(split[1]), 0, false, false), Double.parseDouble(split[2]));
                    } else if (split.length == 4) {
                        offensivePotionEffects.put(new PotionEffect(PotionEffectType.getByName(split[0].toUpperCase()), Integer.parseInt(split[2]), Integer.parseInt(split[1]), false, false), Double.parseDouble(split[3]));
                    }
                }
            }
            if (cs.getStringList(type + ".DefensivePotionEffects") != null) {
                for (String potion : cs.getStringList(type + ".DefensivePotionEffects")) {
                    String[] split = potion.split(",");

                    if (split.length == 3) {
                        defensivePotionEffects.put(new PotionEffect(PotionEffectType.getByName(split[0].toUpperCase()), Integer.parseInt(split[1]), 0, false, false), Double.parseDouble(split[2]));
                    } else if (split.length == 4) {
                        defensivePotionEffects.put(new PotionEffect(PotionEffectType.getByName(split[0].toUpperCase()), Integer.parseInt(split[2]), Integer.parseInt(split[1]), false, false), Double.parseDouble(split[3]));
                    }
                }
            }
        }
        if (cs.get(type + ".Rainbow") != null) {
            rainbow = cs.getBoolean(type + ".Rainbow");
        }

        return true;
    }

    @Override
    public ItemStack getItem(boolean storeItem) {
        List<String> loreDupe = new ArrayList<>();

        loreDupe.addAll(lore);

        loreDupe.add(0, "");
        loreDupe.add(0, "&aDurability&7: &c" + getMaxDurability() + "/" + getMaxDurability());
        loreDupe.add(0, "&aDamage Reduction&7: &b" + (int) (armorRating * 100) + "%");
        loreDupe.add(0, HiddenStringUtils.encodeString(gearCode));

        HashMap<Enchantment, Integer> enchantmentHashMap = new HashMap<>();
        for (String s : enchantments) {
            String[] split = s.split(",");
            if (split.length != 2)
                continue;
            if (split[0].equalsIgnoreCase("protection")) {
                enchantmentHashMap.put(Enchantment.PROTECTION_ENVIRONMENTAL, Integer.valueOf(split[1]));
            } else if (split[0].equalsIgnoreCase("fireprotection")) {
                enchantmentHashMap.put(Enchantment.PROTECTION_FIRE, Integer.valueOf(split[1]));
            } else if (split[0].equalsIgnoreCase("featherfalling")) {
                enchantmentHashMap.put(Enchantment.PROTECTION_FALL, Integer.valueOf(split[1]));
            } else if (split[0].equalsIgnoreCase("blastprotection")) {
                enchantmentHashMap.put(Enchantment.PROTECTION_EXPLOSIONS, Integer.valueOf(split[1]));
            } else if (split[0].equalsIgnoreCase("projectileprotection")) {
                enchantmentHashMap.put(Enchantment.PROTECTION_PROJECTILE, Integer.valueOf(split[1]));
            } else if (split[0].equalsIgnoreCase("respiration")) {
                enchantmentHashMap.put(Enchantment.OXYGEN, Integer.valueOf(split[1]));
            } else if (split[0].equalsIgnoreCase("aquaaffinity")) {
                enchantmentHashMap.put(Enchantment.WATER_WORKER, Integer.valueOf(split[1]));
            } else if (split[0].equalsIgnoreCase("thorns")) {
                enchantmentHashMap.put(Enchantment.THORNS, Integer.valueOf(split[1]));
            } else if (split[0].equalsIgnoreCase("unbreaking")) {
                enchantmentHashMap.put(Enchantment.DURABILITY, Integer.valueOf(split[1]));
            } else {
                enchantmentHashMap.put(Enchantment.getByName(split[0].toUpperCase()), Integer.parseInt(split[1]));
            }
        }

        return new ItemBuilder(Material.getMaterial(("leather_" + type).toUpperCase()))
                .name(displayName)
                .setLore((loreDupe.size() > 0 ? loreDupe : lore))
                .addEnchantments(enchantmentHashMap)
                .color(color).build();
    }

    public void applyDefensiveEffect(Player target) {
        if (Cooldowns.getCooldown(target.getUniqueId(), "EffectCooldown") > 0L) {
            return;
        }

        for (PotionEffect effect : defensivePotionEffects.keySet()) {
            Double random = Math.random();

            if (random <= defensivePotionEffects.get(effect)) {
                target.addPotionEffect(effect);
                MessageManager.sendMessage(target, "&7[&aCarbyne&7]: &aYou have received &b" + Namer.getPotionEffectName(effect) + " &afor &b" + (effect.getDuration() / 20) + " &asec(s).");
                Cooldowns.setCooldown(target.getUniqueId(), "EffectCooldown", 3000L);
            }
        }
    }

    public void applyOffensiveEffect(Player target) {
        if (Cooldowns.getCooldown(target.getUniqueId(), "EffectCooldown") > 0L) {
            return;
        }

        for (PotionEffect effect : offensivePotionEffects.keySet()) {
            Double random = Math.random();

            if (random <= offensivePotionEffects.get(effect)) {
                target.addPotionEffect(effect);
                MessageManager.sendMessage(target, "&7[&aCarbyne&7]: &aYou have received &c" + Namer.getPotionEffectName(effect) + " &afor &c" + (effect.getDuration() / 20) + " &asec(s).");
                Cooldowns.setCooldown(target.getUniqueId(), "EffectCooldown", 3000L);
            }
        }
    }

    @Override
    public void damageItem(Player wielder, ItemStack itemStack) {
        int durability = getDurability(itemStack);

        if (durability == -1) {
            return;
        }

        if (durability >= 1) {
            durability--;
            Namer.setLore(itemStack, "&aDurability&7: &c" + durability + "/" + getMaxDurability(), 2);
        } else {
            wielder.getInventory().remove(itemStack);
            wielder.playSound(wielder.getLocation(), Sound.ITEM_BREAK, 1, 1);
        }
    }

    @Override
    public int getDurability(ItemStack itemStack) {
        if (itemStack == null) {
            return -1;
        }

        try {
            return Integer.valueOf(ChatColor.stripColor(itemStack.getItemMeta().getLore().get(2)).replace(" ", "").split(":")[1].split("/")[0]);
        } catch (Exception ez) {
            return -1;
        }
    }
}
