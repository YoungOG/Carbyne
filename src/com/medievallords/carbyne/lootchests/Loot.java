package com.medievallords.carbyne.lootchests;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.gear.types.CarbyneGear;
import com.medievallords.carbyne.utils.ItemBuilder;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

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
        if (displayName != null && !displayName.equals("")) {
            String[] gear = displayName.split(",");
            if (gear.length > 1) {
                int random = new Random().nextInt(gear.length);
                CarbyneGear carbyneGear = Carbyne.getInstance().getGearManager().getCarbyneGear(gear[random]);
                if (carbyneGear != null) {
                    return carbyneGear.getItem(false);
                }
            }

            if (Carbyne.getInstance().getGearManager().getCarbyneGear(displayName) != null) {
                return Carbyne.getInstance().getGearManager().getCarbyneGear(displayName).getItem(false);
            } else if (displayName.contains("randomGear")) {
                String name = displayName;
                String[] split = name.split(":");
                if (split.length > 1) {
                    return Carbyne.getInstance().getGearManager().getRandomCarbyneGear(Boolean.parseBoolean(split[1])).getItem(false);

                }
            }
        }

        return new ItemBuilder(material).durability(data).amount(amount).name(displayName).setLore(lore).addEnchantments(enchantments).build();
    }
}