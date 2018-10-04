package com.medievallords.carbyne.gear.types.minecraft;

import com.medievallords.carbyne.gear.GearManager;
import com.medievallords.carbyne.gear.types.CarbyneGear;
import com.medievallords.carbyne.utils.HiddenStringUtils;
import com.medievallords.carbyne.utils.ItemBuilder;
import com.medievallords.carbyne.utils.Namer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class MinecraftWeapon extends CarbyneGear {

    private String material = null;
    private double damage = 25;

    @Override
    public boolean load(ConfigurationSection cs, String type, GearManager gearManager) {
        material = cs.getName();

        if ((damage = cs.getDouble(type + ".Damage")) == -1) return false;

        damage = cs.getDouble(type + ".Damage");

        if ((this.type = type) == null)
            return false;

        if ((maxDurability = cs.getInt(type + ".Durability")) == -1)
            return false;

        if (cs.contains(type + ".RepairMaterial"))
            setRepairType(Material.getMaterial(cs.getString(type + ".RepairMaterial")));

        if (cs.contains(type + ".RepairData"))
            setRepairData(cs.getInt(type + ".RepairData"));

        if (cs.contains(type + ".RepairCost"))
            setRepairCost(cs.getInt(type + ".RepairCost"));

        this.lore = new ArrayList<>();
        this.lore.add(0, HiddenStringUtils.encodeString(maxDurability + ""));
        this.lore.add("&aDamage&7: &c" + ((int) (damage)) + "-" + ((int) damage + (int)(damage * 0.8)));

        cost = cs.getInt(type + ".Cost");

        return true;
    }

    @Override
    public ItemStack getItem(boolean storeItem) {
        return new ItemBuilder(Material.getMaterial((material + "_" + type).toUpperCase())).setLore(lore).build();
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
            ItemMeta meta = itemStack.getItemMeta();
            List<String> lore = meta.getLore();
            lore.set(0, HiddenStringUtils.encodeString(durability + ""));
            meta.setLore(lore);
            itemStack.setItemMeta(meta);
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

    public int durabilityScale(ItemStack itemStack) {
        double scale = ((double) (getDurability(itemStack))) / ((double) (getMaxDurability()));
        double durability = ((double) (itemStack.getType().getMaxDurability())) * scale;
        return itemStack.getType().getMaxDurability() - (int) Math.round(durability);
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
        Namer.setLore(itemStack, HiddenStringUtils.encodeString(durability + ""), 0);
        itemStack.setDurability((short) durabilityScale(itemStack));
    }

    @Override
    public int getDurability(ItemStack itemStack) {
        if (itemStack == null)
            return -1;

        try {
            return Integer.valueOf(HiddenStringUtils.extractHiddenString(itemStack.getItemMeta().getLore().get(0)));
        } catch (Exception ez) {
            List<String> lore = new ArrayList<>();
            lore.addAll(this.lore);
            Namer.setLore(itemStack, lore);
            //ez.printStackTrace();
            return maxDurability;
        }
    }
}
