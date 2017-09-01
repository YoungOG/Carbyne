package com.medievallords.carbyne.crates.rewards;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.gear.GearManager;
import com.medievallords.carbyne.packages.Package;
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
public class Reward {

    private Carbyne main = Carbyne.getInstance();
    private GearManager gearManager = main.getGearManager();

    private int id;
    private int itemId;
    private int itemData;
    private int amount;
    private String displayName;
    private String gearCode;
    private List<String> lore = new ArrayList<>();
    private HashMap<Enchantment, Integer> enchantments = new HashMap<>();
    private List<String> commands = new ArrayList<>();
    private boolean displayItemOnly;
    private double chance;

    public Reward(int id, int itemId, int itemData, int amount, String gearCode) {
        this.id = id;
        this.itemId = itemId;
        this.itemData = itemData;
        this.amount = amount;
        this.gearCode = gearCode;
    }

    public ItemStack getItem(boolean displayItem) {
        if (Material.getMaterial(itemId) == gearManager.getTokenMaterial() && itemData == gearManager.getTokenData()) {
            return new ItemBuilder(gearManager.getTokenItem()).amount(amount).build();
        } else if (gearCode.contains("randomgear") && !displayItem) {
            return new ItemBuilder(gearManager.getRandomCarbyneGear(Boolean.valueOf(gearCode.split(":")[1])).getItem(false)).amount(amount).build();
        } else if (gearManager.getCarbyneGear(gearCode) != null) {
            if (gearManager.getCarbyneGear(gearCode).getItem(false) != null) {
                return new ItemBuilder(gearManager.getCarbyneGear(gearCode).getItem(false)).amount(amount).build();
            }
        } else if (Package.getPackage(displayName) != null) {
            return Package.getPackage(displayName).getItem(amount);
        } else {
            return new ItemBuilder(Material.getMaterial(itemId)).durability(itemData).amount(amount).name(displayName).setLore(lore).addEnchantments(enchantments).build();
        }

        return null;
    }

    @Override
    public String toString() {
        return "Reward(itemId: " + itemId + ", itemData: " + itemData + ", amount: " + amount + ", displayName: " + displayName + ", gearCode: " + gearCode + ", lore: " + lore.toString() + ", enchantments: " + enchantments.keySet() + ", commands: " + commands.toString() + ", displayItemOnly: " + displayItemOnly + ")";
    }
}
