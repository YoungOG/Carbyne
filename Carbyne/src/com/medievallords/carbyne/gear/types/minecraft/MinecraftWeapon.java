package com.medievallords.carbyne.gear.types.minecraft;

import com.medievallords.carbyne.gear.types.CarbyneGear;
import com.medievallords.carbyne.utils.HiddenStringUtils;
import com.medievallords.carbyne.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class MinecraftWeapon extends CarbyneGear {

	private String material = null;
	
	@Override
	public boolean load(ConfigurationSection cs, String type) {
		material = cs.getName();

		if ((this.type = type) == null)
            return false;

		if ((durability = cs.getInt(type + ".Durability")) == -1)
            return false;

		this.lore = new ArrayList<>();
		this.lore.add(0, "&aDurability&7: &c" + cs.getInt(type + ".Durability"));
		this.lore.add(0, HiddenStringUtils.encodeString(gearCode));

		return true;
	}

    @Override
    public ItemStack getItem(boolean storeItem) {
        return new ItemBuilder(Material.getMaterial((material + "_" + type).toUpperCase())).setLore(lore).build();
    }
}
