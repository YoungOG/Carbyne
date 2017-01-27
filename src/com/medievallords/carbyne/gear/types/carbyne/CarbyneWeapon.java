package com.medievallords.carbyne.gear.types.carbyne;

import com.medievallords.carbyne.gear.types.CarbyneGear;
import com.medievallords.carbyne.utils.*;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
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
public class CarbyneWeapon extends CarbyneGear {

    private List<String> enchantments = new ArrayList<>();
    private String material = "";
    private HashMap<PotionEffect, Double> offensivePotionEffects = new HashMap<>();
    private HashMap<PotionEffect, Double> defensivePotionEffects = new HashMap<>();

    @Override
    public boolean load(ConfigurationSection cs, String index) {
        if ((displayName = cs.getString(index + ".DisplayName")) == null) return false;
        if ((this.type = cs.getString(index + ".Type")) == null) return false;
        if (!type.equalsIgnoreCase("Bow"))
            if ((this.material = cs.getString(index + ".Material")) == null) return false;
        if ((durability = cs.getInt(index + ".Durability")) == -1) return false;
        if ((lore = cs.getStringList(index + ".Lore")) == null || lore.size() <= 0) return false;
        if ((enchantments = cs.getStringList(index + ".Enchantments")) == null || enchantments.size() <= 0)
            return false;
        if ((cost = cs.getInt(index + ".Cost")) == -1) return false;

        displayName = cs.getString(index + ".DisplayName");
        type = cs.getString(index + ".Type");
        if (!type.equalsIgnoreCase("Bow"))
            material = cs.getString(index + ".Material");
        type = cs.getString(index + ".Type");
        durability = cs.getInt(index + ".Durability");
        lore = cs.getStringList(index + ".Lore");
        lore.add(0, "&aDurability&7: &c" + durability);
        lore.add(0, HiddenStringUtils.encodeString(gearCode));
        enchantments = cs.getStringList(index + ".Enchantments");
        hidden = cs.getBoolean(index + ".Hidden");
        cost = cs.getInt(index + ".Cost");

        if (cs.getStringList(index + ".OffensivePotionEffects") != null) {
            for (String potion : cs.getStringList(index + ".OffensivePotionEffects")) {
                String[] split = potion.split(",");

                if (split.length == 3) {
                    offensivePotionEffects.put(new PotionEffect(PotionEffectType.getByName(split[0].toUpperCase()), Integer.parseInt(split[1]), 0, false, true), Double.parseDouble(split[2]));
                } else if (split.length == 4) {
                    offensivePotionEffects.put(new PotionEffect(PotionEffectType.getByName(split[0].toUpperCase()), Integer.parseInt(split[2]), Integer.parseInt(split[1]), false, true), Double.parseDouble(split[3]));
                }
            }
        }

        if (cs.getStringList(index + ".DefensivePotionEffects") != null) {
            for (String potion : cs.getStringList(index + ".DefensivePotionEffects")) {
                String[] split = potion.split(",");

                if (split.length == 3) {
                    defensivePotionEffects.put(new PotionEffect(PotionEffectType.getByName(split[0].toUpperCase()), Integer.parseInt(split[1]), 0, false, true), Double.parseDouble(split[2]));
                } else if (split.length == 4) {
                    defensivePotionEffects.put(new PotionEffect(PotionEffectType.getByName(split[0].toUpperCase()), Integer.parseInt(split[2]), Integer.parseInt(split[1]), false, true), Double.parseDouble(split[3]));
                }
            }
        }

        return true;
    }

    @Override
    public ItemStack getItem(boolean storeItem) {
        Material mat = Material.STONE;

        if (type.equalsIgnoreCase("sword")) {
            if (Material.getMaterial((material + "_SWORD").toUpperCase()) != null) {
                mat = Material.getMaterial((material + "_SWORD").toUpperCase());
            } else {
                return null;
            }
        } else if (type.equalsIgnoreCase("axe")) {
            if (Material.getMaterial((material + "_AXE").toUpperCase()) != null) {
                mat = Material.getMaterial((material + "_AXE").toUpperCase());
            } else {
                return null;
            }
        } else if (type.equalsIgnoreCase("hoe")) {
            if (Material.getMaterial((material + "_HOE").toUpperCase()) != null) {
                mat = Material.getMaterial((material + "_HOE").toUpperCase());
            } else {
                return null;
            }
        } else if (type.equalsIgnoreCase("spade")) {
            if (Material.getMaterial((material + "_SPADE").toUpperCase()) != null) {
                mat = Material.getMaterial((material + "_SPADE").toUpperCase());
            } else {
                return null;
            }
        } else if (type.equalsIgnoreCase("bow")) {
            mat = Material.BOW;
        }


        HashMap<Enchantment, Integer> enchantmentHashMap = new HashMap<>();
        for (String s : enchantments) {
            String[] split = s.split(",");

            if (split.length != 2)
                continue;

            if (type.equalsIgnoreCase("spade")) {
                if (split[0].equalsIgnoreCase("durability")) {
                    enchantmentHashMap.put(Enchantment.DURABILITY, Integer.valueOf(split[1]));
                }
            } else if (!type.equalsIgnoreCase("bow")) {
                if (split[0].equalsIgnoreCase("sharpness")) {
                    enchantmentHashMap.put(Enchantment.DAMAGE_ALL, Integer.valueOf(split[1]));
                } else if (split[0].equalsIgnoreCase("ARTHROPODS")) {
                    enchantmentHashMap.put(Enchantment.DAMAGE_ARTHROPODS, Integer.valueOf(split[1]));
                } else if (split[0].equalsIgnoreCase("undead")) {
                    enchantmentHashMap.put(Enchantment.DAMAGE_UNDEAD, Integer.valueOf(split[1]));
                } else if (split[0].equalsIgnoreCase("fire")) {
                    enchantmentHashMap.put(Enchantment.FIRE_ASPECT, Integer.valueOf(split[1]));
                } else if (split[0].equalsIgnoreCase("loot")) {
                    enchantmentHashMap.put(Enchantment.LOOT_BONUS_MOBS, Integer.valueOf(split[1]));
                } else if (split[0].equalsIgnoreCase("knockback")) {
                    enchantmentHashMap.put(Enchantment.KNOCKBACK, Integer.valueOf(split[1]));
                }
            } else {
                if (split[0].equalsIgnoreCase("damage")) {
                    enchantmentHashMap.put(Enchantment.ARROW_DAMAGE, Integer.valueOf(split[1]));
                } else if (split[0].equalsIgnoreCase("fire")) {
                    enchantmentHashMap.put(Enchantment.ARROW_FIRE, Integer.valueOf(split[1]));
                } else if (split[0].equalsIgnoreCase("infinite")) {
                    enchantmentHashMap.put(Enchantment.ARROW_INFINITE, Integer.valueOf(split[1]));
                } else if (split[0].equalsIgnoreCase("knockback")) {
                    enchantmentHashMap.put(Enchantment.ARROW_KNOCKBACK, Integer.valueOf(split[1]));
                }
            }
        }

        return new ItemBuilder(mat)
                .name(displayName)
                .setLore(lore)
                .addEnchantments(enchantmentHashMap).hideFlags().build();
    }

    public void applyDefensiveEffect(Player target) {
        if (Cooldowns.getCooldown(target.getUniqueId(), "EffectCooldown") > 0L) {
            return;
        }

        for (PotionEffect effect : defensivePotionEffects.keySet()) {
            Double random = Math.random();

            if (random <= defensivePotionEffects.get(effect)) {
                target.addPotionEffect(effect, true);
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
                target.addPotionEffect(effect, true);
                MessageManager.sendMessage(target, "&7[&aCarbyne&7]: &aYou have received &c" + Namer.getPotionEffectName(effect) + " &afor &c" + (effect.getDuration() / 20) + " &asec(s).");
                Cooldowns.setCooldown(target.getUniqueId(), "EffectCooldown", 3000L);
            }
        }
    }
}
