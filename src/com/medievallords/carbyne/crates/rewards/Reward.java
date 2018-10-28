package com.medievallords.carbyne.crates.rewards;

import com.medievallords.carbyne.gear.artifacts.Artifact;
import com.medievallords.carbyne.utils.ItemBuilder;
import com.medievallords.carbyne.utils.StaticClasses;
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
public class Reward {

    private int id;
    private int itemId;
    private int itemData;
    private int amount, slot;
    private String displayName;
    private String gearCode;
    private List<String> lore = new ArrayList<>();
    private HashMap<Enchantment, Integer> enchantments = new HashMap<>();
    private List<String> commands = new ArrayList<>();
    private boolean displayItemOnly;
    private int chance;

    public Reward(int id, int itemId, int itemData, int amount, String gearCode) {
        this.id = id;
        this.itemId = itemId;
        this.itemData = itemData;
        this.amount = amount;
        this.gearCode = gearCode;
    }

    public ItemStack getItem(boolean displayItem) {
        if (Material.getMaterial(itemId) == StaticClasses.gearManager.getTokenMaterial() && itemData == StaticClasses.gearManager.getTokenData())
            return new ItemBuilder(StaticClasses.gearManager.getTokenItem()).amount(amount).build();

        else if (Material.getMaterial(itemId) == StaticClasses.gearManager.getPolishMaterial() && itemData == StaticClasses.gearManager.getPolishData())
            return new ItemBuilder(StaticClasses.gearManager.getPolishItem()).amount(amount).build();
        else if (gearCode.contains("randomgear") && !displayItem)
            return new ItemBuilder(StaticClasses.gearManager.getRandomCarbyneGear(Boolean.valueOf(gearCode.split(":")[1])).getItem(false)).amount(amount).build();
        else if (StaticClasses.gearManager.getCarbyneGear(gearCode) != null) {
            if (StaticClasses.gearManager.getCarbyneGear(gearCode).getItem(false) != null)
                return new ItemBuilder(StaticClasses.gearManager.getCarbyneGear(gearCode).getItem(false)).amount(amount).build();
        } else if (displayName.contains("randomartifact")) {
            String name = displayName;
            String[] split = name.split(":");

            if (split.length > 1) {
                return StaticClasses.gearManager.getRandomArtifact(Boolean.parseBoolean(split[1])).getCustomRecipe().getResult();
            }
        }
//        else if (Package.getPackage(displayName) != null)
//            return Package.getPackage(displayName).getItem(amount);

        Artifact artifact = StaticClasses.gearManager.getArtifact(displayName);
        if (artifact != null) {
            return artifact.getCustomRecipe().getResult();
        } else
            return new ItemBuilder(Material.getMaterial(itemId)).durability(itemData).amount(amount).name(displayName).setLore(lore).addEnchantments(enchantments).build();
        //return null;
    }

    @Override
    public String toString() {
        return "QuestReward(itemId: " + itemId + ", itemData: " + itemData + ", amount: " + amount + ", displayName: " + displayName + ", gearCode: " + gearCode + ", lore: " + lore.toString() + ", enchantments: " + enchantments.keySet() + ", commands: " + commands.toString() + ", displayItemOnly: " + displayItemOnly + ")";
    }
}
