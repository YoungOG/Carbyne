package com.medievallords.carbyne.utils;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ItemBuilder {

    private ItemStack item;

    public ItemBuilder(Material material) {
        item = new ItemStack(material);
    }

    public ItemBuilder(ItemStack itemstack) {
        item = itemstack;
    }

    public ItemBuilder name(String name) {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder amount(int amount) {
        item.setAmount(amount);
        return this;
    }

    public ItemBuilder addLore(String line) {
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();

        if (lore == null) {
            lore = new ArrayList<>();
        }

        lore.add(ChatColor.translateAlternateColorCodes('&', line));
        meta.setLore(lore);
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder addLore(int index, String line) {
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();

        if (lore == null) {
            lore = new ArrayList<>();
        }

        lore.add(index, ChatColor.translateAlternateColorCodes('&', line));
        meta.setLore(lore);
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        ItemMeta meta = item.getItemMeta();
        List<String> lores = new ArrayList<>();

        for (String l : lore) {
            lores.add(ChatColor.translateAlternateColorCodes('&', l));
        }

        meta.setLore(lores);
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder durability(int durability) {
        item.setDurability((short) durability);
        return this;
    }

    public ItemBuilder data(int data) {
        item.setData(new MaterialData(item.getType(), (byte) data));
        return this;
    }

    public ItemBuilder addEnchantment(Enchantment enchantment, int level) {
        item.addUnsafeEnchantment(enchantment, level);
        return this;
    }

    public ItemBuilder addEnchantment(Enchantment enchantment) {
        item.addUnsafeEnchantment(enchantment, 1);
        return this;
    }

    public ItemBuilder addEnchantments(HashMap<Enchantment, Integer> enchantments) {
        for (Enchantment enchantment : enchantments.keySet()) {
            item.addUnsafeEnchantment(enchantment, enchantments.get(enchantment));
        }

        return this;
    }

    public ItemBuilder material(Material material) {
        item.setType(material);
        return this;
    }

    public ItemBuilder clearLore() {
        ItemMeta meta = item.getItemMeta();
        meta.setLore(new ArrayList<String>());
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder removeOne() {
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();
        if (lore.isEmpty()) {
            return this;
        }
        lore.remove(lore.size() - 1);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder clearEnchantments() {
        for (Enchantment e : item.getEnchantments().keySet()) {
            item.removeEnchantment(e);
        }

        return this;
    }

    public ItemBuilder color(Color color) {
        if (item.getType() == Material.LEATHER_HELMET || item.getType() == Material.LEATHER_CHESTPLATE
                || item.getType() == Material.LEATHER_LEGGINGS || item.getType() == Material.LEATHER_BOOTS) {
            LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
            meta.setColor(color);
            item.setItemMeta(meta);
            return this;
        } else
            throw new IllegalArgumentException("You can only apply color to leather armor!");
    }

    public ItemBuilder unbreakable(boolean unbreakable) {
        ItemMeta meta = item.getItemMeta();

        if (unbreakable) {
            meta.spigot().setUnbreakable(true);

            item.setItemMeta(meta);

            return this;
        } else {
            meta.spigot().setUnbreakable(false);

            item.setItemMeta(meta);

            return this;
        }
    }

    public ItemBuilder hideFlags() {
        ItemMeta meta = item.getItemMeta();

        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
        meta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);

        item.setItemMeta(meta);

        return this;
    }

    public ItemStack build() {
        return item;
    }
}
