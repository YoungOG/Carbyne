package com.medievallords.carbyne.packages;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.gear.GearManager;
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
 * Created by WE on 2017-08-03.
 */
@Setter
@Getter
public class PackageItem {

    private GearManager gearManager = Carbyne.getInstance().getGearManager();

    private int id;
    private int itemId;
    private int itemData;
    private int amount;
    private int slot;
    private String displayName;
    private String gearCode;
    private List<String> lore = new ArrayList<>();
    private HashMap<Enchantment, Integer> enchantments = new HashMap<>();
    private double chance;
    private PackageItemRarity rarity;
    private List<String> commands = new ArrayList<>();

    public PackageItem(int id, int itemId, int itemData, int amount, String displayName, String gearCode, List<String> lore, HashMap<Enchantment, Integer> enchantments, double chance, PackageItemRarity rarity, int slot, List<String> commands) {
        this.id = id;
        this.itemData = itemData;
        this.itemId = itemId;
        this.amount = amount;
        this.displayName = displayName;
        this.gearCode = gearCode;
        this.lore = lore;
        this.enchantments = enchantments;
        this.chance = chance;
        this.rarity = rarity;
        this.slot = slot;
        this.commands = commands;
    }

    public ItemStack getItem() {
        if (Material.getMaterial(itemId) == gearManager.getTokenMaterial() && itemData == gearManager.getTokenData()) {
            return new ItemBuilder(gearManager.getTokenItem()).amount(amount).build();
        } else if (Material.getMaterial(itemId) == gearManager.getPolishMaterial() && itemData == gearManager.getPolishData()) {
            return new ItemBuilder(gearManager.getPolishItem()).amount(amount).build();
        } else if (gearManager.getCarbyneGear(gearCode) != null) {
            if (gearManager.getCarbyneGear(gearCode).getItem(false) != null) {
                return new ItemBuilder(gearManager.getCarbyneGear(gearCode).getItem(false)).amount(amount).build();
            }
        } else {
            if (itemId == 403) {
                return new ItemBuilder(Material.getMaterial(itemId)).durability(itemData).amount(amount).name(displayName).setLore(lore).build();
            } else {
                return new ItemBuilder(Material.getMaterial(itemId)).durability(itemData).amount(amount).name(displayName).setLore(lore).addEnchantments(enchantments).build();
            }
        }

        return null;
    }

}
