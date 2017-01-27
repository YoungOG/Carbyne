package com.medievallords.carbyne.gear.types.minecraft;

import com.medievallords.carbyne.gear.types.CarbyneGear;
import com.medievallords.carbyne.utils.HiddenStringUtils;
import com.medievallords.carbyne.utils.ItemBuilder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;


public class MinecraftArmor extends CarbyneGear {

	private String material = null;
	@Getter @Setter private double armorRating = -1;

	@Override
	public boolean load(ConfigurationSection cs, String type) {
		material = cs.getName();

		if ((this.type = type) == null)
            return false;

		if ((durability = cs.getInt(type + ".Durability")) == -1)
            return false;

		if ((armorRating = cs.getDouble(type + ".ArmorRating")) == -1)
            return false;

        this.armorRating = cs.getDouble(type + ".ArmorRating");
		this.lore = new ArrayList<>();
		this.lore.add(0, "&aDamage Reduction&7: &b" + (int) (armorRating * 100) + "%");
		this.lore.add(0, "&aDurability&7: &c" + cs.getInt(type + ".Durability"));
		this.lore.add(0, HiddenStringUtils.encodeString(gearCode));

		return true;
	}

	@Override
	public ItemStack getItem(boolean storeItem) {
		return new ItemBuilder(Material.getMaterial((material + "_" + type).toUpperCase())).setLore(lore).build();
	}
}
