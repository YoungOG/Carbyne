package com.medievallords.carbyne.lootchests;

import com.medievallords.carbyne.utils.ItemBuilder;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Dalton on 6/5/2017.
 */
public class Loot {

    @Getter
    private String configName;
    private Material material;
    private int data;
    private String displayName;
    private List<String> lore;
    private HashMap<Enchantment, Integer> enchantments;
    @Getter
    private double chanceToSpawn;
    @Getter
    private int amount;

    public Loot(String configName, Material material, String displayName, List<String> lore, HashMap<Enchantment, Integer> enchantments, double chanceToSpawn, int amount, int data) {
        this.chanceToSpawn = chanceToSpawn;
        this.configName = configName;
        this.material = material;
        this.displayName = displayName;
        this.lore = lore;
        this.enchantments = enchantments;
        this.amount = amount;
        this.data = data;
    }

    public boolean shouldSpawnItem() { return Math.random() < chanceToSpawn; }

    public ItemStack getItem() {
        return new ItemBuilder(material).durability(data).amount(amount).name(displayName).setLore(lore).addEnchantments(enchantments).build();
    }
}