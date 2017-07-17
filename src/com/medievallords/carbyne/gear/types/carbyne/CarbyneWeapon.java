package com.medievallords.carbyne.gear.types.carbyne;

import com.medievallords.carbyne.gear.GearManager;
import com.medievallords.carbyne.gear.specials.Special;
import com.medievallords.carbyne.gear.types.CarbyneGear;
import com.medievallords.carbyne.utils.*;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
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
public class CarbyneWeapon extends CarbyneGear {

    private GearManager gearManager;
    private List<String> enchantments = new ArrayList<>();
    private String material = "";
    private HashMap<PotionEffect, Double> offensivePotionEffects = new HashMap<>();
    private HashMap<PotionEffect, Double> defensivePotionEffects = new HashMap<>();
    private Special special;

    public CarbyneWeapon(GearManager gearManager) {
        this.gearManager = gearManager;
    }

    @Override
    public boolean load(ConfigurationSection cs, String index) {
        if ((displayName = cs.getString(index + ".DisplayName")) == null) return false;
        if ((this.type = cs.getString(index + ".Type")) == null) return false;
        if ((gearCode = cs.getString(index + ".GearCode")) == null) return false;
        if (!type.equalsIgnoreCase("Bow"))
            if ((this.material = cs.getString(index + ".Material")) == null) return false;
        if ((maxDurability = cs.getInt(index + ".Durability")) == -1) return false;
        if ((enchantments = cs.getStringList(index + ".Enchantments")) == null || enchantments.size() <= 0)
            return false;
        if ((cost = cs.getInt(index + ".Cost")) == -1) return false;

        displayName = cs.getString(index + ".DisplayName");
        type = cs.getString(index + ".Type");
        if (!type.equalsIgnoreCase("Bow"))
            material = cs.getString(index + ".Material");
        type = cs.getString(index + ".Type");
        maxDurability = cs.getInt(index + ".Durability");
        lore = cs.getStringList(index + ".Lore");
        enchantments = cs.getStringList(index + ".Enchantments");
        hidden = cs.getBoolean(index + ".Hidden");
        gearCode = cs.getString(index + ".GearCode");
        cost = cs.getInt(index + ".Cost");

        if (cs.getStringList(index + ".OffensivePotionEffects") != null) {
            for (String potion : cs.getStringList(index + ".OffensivePotionEffects")) {
                String[] split = potion.split(",");

                if (split.length == 3) {
                    offensivePotionEffects.put(new PotionEffect(PotionEffectType.getByName(split[0].toUpperCase()), Integer.parseInt(split[1]), 0, false, false), Double.parseDouble(split[2]));
                } else if (split.length == 4) {
                    offensivePotionEffects.put(new PotionEffect(PotionEffectType.getByName(split[0].toUpperCase()), Integer.parseInt(split[2]), Integer.parseInt(split[1]), false, false), Double.parseDouble(split[3]));
                }
            }
        }

        if (cs.getStringList(index + ".DefensivePotionEffects") != null) {
            for (String potion : cs.getStringList(index + ".DefensivePotionEffects")) {
                String[] split = potion.split(",");

                if (split.length == 3) {
                    defensivePotionEffects.put(new PotionEffect(PotionEffectType.getByName(split[0].toUpperCase()), Integer.parseInt(split[1]), 0, false, false), Double.parseDouble(split[2]));
                } else if (split.length == 4) {
                    defensivePotionEffects.put(new PotionEffect(PotionEffectType.getByName(split[0].toUpperCase()), Integer.parseInt(split[2]), Integer.parseInt(split[1]), false, false), Double.parseDouble(split[3]));
                }
            }
        }

        if (cs.getString(index + ".Special") != null) {
            if (gearManager.getSpecialByName(cs.getString(index + ".Special")) != null) {
                special = gearManager.getSpecialByName(cs.getString(index + ".Special"));
            }
        }

        return true;
    }

    @Override
    public ItemStack getItem(boolean storeItem) {
        List<String> loreDupe = new ArrayList<>();

        loreDupe.add(HiddenStringUtils.encodeString(gearCode));
        loreDupe.add("&aDurability&7: &c" + getMaxDurability() + "/" + getMaxDurability());

        if (special != null) {
            loreDupe.add("&aSpecial&7: &c" + special.getSpecialName().replace("_", " "));
        }

        if (!storeItem) {
            if (special != null) {
                loreDupe.add("&aSpecial Charge&7: &c0/" + special.getRequiredCharge());
            }
        }

        if (offensivePotionEffects.keySet().size() > 0 || defensivePotionEffects.keySet().size() > 0) {
            if (defensivePotionEffects.keySet().size() > 0) {
                loreDupe.add("");
                loreDupe.add("&aDefensive Effects&7:");

                for (PotionEffect effect : defensivePotionEffects.keySet()) {
                    loreDupe.add("  &7- &3" + MessageManager.getPotionTypeFriendlyName(effect.getType()) + " &b" + MessageManager.getPotionAmplifierInRomanNumerals(effect.getAmplifier() + 1) + " &6" + (effect.getDuration() / 20) + "s &c" + defensivePotionEffects.get(effect) + "% &f(On Hit)");
                }
            }

            if (offensivePotionEffects.keySet().size() > 0) {
                loreDupe.add("");
                loreDupe.add("&aOffensive Effects&7:");

                for (PotionEffect effect : offensivePotionEffects.keySet()) {
                    loreDupe.add("  &7- &3" + MessageManager.getPotionTypeFriendlyName(effect.getType()) + " &b" + MessageManager.getPotionAmplifierInRomanNumerals(effect.getAmplifier() + 1) + " &6" + (effect.getDuration() / 20) + "s &c" + offensivePotionEffects.get(effect) + "% &f(On Hit)");
                }
            }
        }

        if (lore != null && lore.size() > 0) {
            loreDupe.add("");
            loreDupe.addAll(lore);
        }

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

        ItemBuilder builder = new ItemBuilder(mat)
                .name(displayName)
                .addEnchantments(enchantmentHashMap).hideFlags();

        if (lore != null && lore.size() > 0) {
            builder.setLore((loreDupe.size() > 0 ? loreDupe : lore));
        } else {
            if (loreDupe.size() > 0) {
                builder.setLore(loreDupe);
            }
        }

        return builder.build();
    }

    public void applyDefensiveEffect(Player target) {
        if (Cooldowns.getCooldown(target.getUniqueId(), "EffectCooldown") > 0L) {
            return;
        }

        for (PotionEffect effect : defensivePotionEffects.keySet()) {
            Double random = Math.random();

            if (random <= defensivePotionEffects.get(effect)) {
                for (PotionEffect potionEffect : target.getActivePotionEffects()) {
                    if (potionEffect.getType() != effect.getType() && (potionEffect.getAmplifier() < effect.getAmplifier() && potionEffect.getDuration() < effect.getDuration()))
                        return;

                    if (potionEffect.getType() == effect.getType() && effect.getAmplifier() < potionEffect.getAmplifier() && effect.getDuration() < potionEffect.getDuration()) {
                        return;
                    }
                }


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
                for (PotionEffect potionEffect : target.getActivePotionEffects())
                    if (potionEffect.getType() != effect.getType() && (potionEffect.getAmplifier() < effect.getAmplifier() && potionEffect.getDuration() < effect.getDuration()))
                        return;

                target.addPotionEffect(effect, true);
                MessageManager.sendMessage(target, "&7[&aCarbyne&7]: &aYou have received &c" + Namer.getPotionEffectName(effect) + " &afor &c" + (effect.getDuration() / 20) + " &asec(s).");
                Cooldowns.setCooldown(target.getUniqueId(), "EffectCooldown", 3000L);
            }
        }
    }

    @Override
    public void damageItem(Player wielder, ItemStack itemStack) {
        int durability = getDurability(itemStack);
        double chance = 0;

        if (itemStack.containsEnchantment(Enchantment.DURABILITY)) {
            int level = itemStack.getEnchantmentLevel(Enchantment.DURABILITY);
            double calc = (100 / (level + 1));
            chance = calc / 100;
        }

        if (durability == -1) {
            return;
        }

        if (Math.random() < chance) {
            return;
        }

        if (durability >= 1) {
            durability--;
            Namer.setLore(itemStack, "&aDurability&7: &c" + durability + "/" + getMaxDurability(), 1);
            itemStack.setDurability((short) (itemStack.getType().getMaxDurability() - (durabilityScale(itemStack))));

            if (itemStack.getDurability() <= 0) {
                itemStack.setDurability((short) 0);
            } else if (itemStack.getDurability() >= itemStack.getType().getMaxDurability()) {
                itemStack.setDurability(itemStack.getType().getMaxDurability());
            }

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
            return Integer.valueOf(ChatColor.stripColor(itemStack.getItemMeta().getLore().get(1)).replace(" ", "").split(":")[1].split("/")[0]);
        } catch (Exception ez) {
            return -1;
        }
    }

    public void setSpecialCharge(ItemStack itemStack, int amount) {
        int charge = getSpecialCharge(itemStack);

        if (charge == -1) {
            return;
        }

        Namer.setLore(itemStack, "&aSpecial Charge&7: &c" + amount + "/" + special.getRequiredCharge(), 3);
    }

    public int getSpecialCharge(ItemStack itemStack) {
        if (itemStack == null) {
            return 0;
        }

        try {
            return Integer.valueOf(ChatColor.stripColor(itemStack.getItemMeta().getLore().get(3)).replace(" ", "").split(":")[1].split("/")[0]);
        } catch (Exception ez) {
            return 0;
        }
    }

    @Override
    public int getRepairCost(ItemStack itemStack) {
        int maxAmount = (int) Math.round(cost * 0.7);

        double per = (double) maxDurability / (double) maxAmount;
        double dura = ((double) (getDurability(itemStack)));
        for (int i = 1; i <= maxAmount; i++) {
            if (dura < per * i) {
                return (maxAmount + 1) - i;
            }
        }

        return 0;
    }

    @Override
    public void setDurability(ItemStack itemStack, int durability) {
        Namer.setLore(itemStack, "&aDurability&7: &c" + durability + "/" + getMaxDurability(), 1);
    }

    public int durabilityScale(ItemStack itemStack) {
        double scale = ((double) (getDurability(itemStack))) / ((double) (getMaxDurability()));
        double durability = ((double) (itemStack.getType().getMaxDurability())) * scale;
        return (int) Math.round(durability);
    }

}
