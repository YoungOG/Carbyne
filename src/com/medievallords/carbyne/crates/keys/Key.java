package com.medievallords.carbyne.crates.keys;

import com.medievallords.carbyne.utils.ItemBuilder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Calvin on 11/19/2016
 * for the Utils project.
 */

@Getter
@Setter
public class Key {

    private String name;
    private int itemId;
    private int itemData;
    private String displayName;
    private List<String> lore = new ArrayList<>();
    private HashMap<Enchantment, Integer> enchantments = new HashMap<>();
    private String crate;

    public Key(String name, int itemId, int itemData) {
        this.name = name;
        this.itemId = itemId;
        this.itemData = itemData;
    }

    public ItemStack getItem() {
        return new ItemBuilder(Material.getMaterial(itemId)).durability(itemData).name(displayName).setLore(lore).build();
    }

    public ItemStack getItem(int amount) {
        return new ItemBuilder(Material.getMaterial(itemId)).durability(itemData).amount(amount).name(displayName).setLore(lore).addEnchantments(enchantments).build();
    }
}
