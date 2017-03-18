package com.medievallords.carbyne.gear.types;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public abstract class CarbyneGear {

	protected String displayName = "";
	protected String gearCode = "";
	protected List<String> lore = new ArrayList<>();
	protected String type = "";
	protected final String secretCode = "carbyne-gear";
	protected int maxDurability = -1;
	protected boolean hidden = false;
	protected int cost = 0;
	
	public abstract boolean load(ConfigurationSection cs, String type);
	
	public abstract ItemStack getItem(boolean storeItem);

	public abstract int getDurability(ItemStack itemStack);

	public abstract void damageItem(Player wielder, ItemStack itemStack);
}
