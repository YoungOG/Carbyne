package com.medievallords.carbyne.gear.types;

import com.medievallords.carbyne.gear.GearManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
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
    protected GearState state = GearState.VISIBLE;
	protected int cost = 0;
    protected Material repairType = Material.NETHER_STAR;
    protected int repairData = 0, repairCost = 1;

    public abstract boolean load(ConfigurationSection cs, String type, GearManager gearManager);
	
	public abstract ItemStack getItem(boolean storeItem);

	public abstract int getDurability(ItemStack itemStack);

	public abstract void damageItem(Player wielder, ItemStack itemStack);

	public abstract int getRepairCost(ItemStack itemStack);

	public abstract void setDurability(ItemStack itemStack, int durability);
}
