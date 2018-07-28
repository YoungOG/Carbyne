package com.medievallords.carbyne.gear.types.minecraft;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.gear.types.CarbyneGear;
import com.medievallords.carbyne.utils.HiddenStringUtils;
import com.medievallords.carbyne.utils.ItemBuilder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;


public class MinecraftArmor extends CarbyneGear {

	private String material = null;
	@Getter @Setter private double armorRating = -1;


	@Override
	public boolean load(ConfigurationSection cs, String type) {
		material = cs.getName();

		if ((this.type = type) == null)
            return false;

		if ((maxDurability = cs.getInt(type + ".Durability")) == -1)
            return false;

		if ((armorRating = cs.getDouble(type + ".ArmorRating")) == -1)
            return false;

        this.armorRating = cs.getDouble(type + ".ArmorRating");
		this.lore = new ArrayList<>();
        //this.lore.add(0, "&aDurability&7: &c" + maxDurability + "/" + maxDurability);
		this.lore.add(0, "&aDamage Reduction&7: &b" + (int) (armorRating * 100) + "%");
		//this.lore.add(0, "&aDurability&7: &c" + cs.getInt(type + ".Durability") + "/" + getMaxDurability());
        this.lore.add(0, HiddenStringUtils.encodeString(maxDurability + ""));

		cost = cs.getInt(type + ".Cost");

		return true;
	}

	@Override
	public ItemStack getItem(boolean storeItem) {
		return new ItemBuilder(Material.getMaterial((material + "_" + type).toUpperCase())).setLore(lore).build();
	}

	@Override
	public void damageItem(Player wielder, ItemStack itemStack) {
        int durability = getDurability(itemStack);

		if (durability == -1) {
			return;
		}

		if (durability >= 1) {
			durability--;
            ItemMeta meta = itemStack.getItemMeta();
            List<String> lore = meta.getLore();
            lore.set(0, HiddenStringUtils.encodeString(durability + ""));
            meta.setLore(lore);
            itemStack.setItemMeta(meta);
			itemStack.setDurability((short) durabilityScale(itemStack));

            if (itemStack.getDurability() <= 0)
				itemStack.setDurability((short) 0);
            else if (itemStack.getDurability() >= itemStack.getType().getMaxDurability())
                itemStack.setDurability(itemStack.getType().getMaxDurability());
		} else {
			wielder.getInventory().remove(itemStack);
            wielder.updateInventory();
			wielder.playSound(wielder.getLocation(), Sound.ITEM_BREAK, 1, 1);
        }
    }

    public int durabilityScale(ItemStack itemStack) {
        double scale = ((double) (getDurability(itemStack))) / ((double) (getMaxDurability()));
        double durability = ((double) (itemStack.getType().getMaxDurability())) * scale;
        return itemStack.getType().getMaxDurability() - (int) Math.round(durability);
	}

	@Override
	public int getDurability(ItemStack itemStack) {
        if (itemStack == null)
			return -1;

		try {
            return Integer.valueOf(HiddenStringUtils.extractHiddenString(itemStack.getItemMeta().getLore().get(0)));
		} catch (Exception ez) {
            Carbyne.getInstance().getGearManager().convertDefaultItem(itemStack);
            //Namer.setLore(itemStack, HiddenStringUtils.encodeString(maxDurability + ""), 0);
            //ez.printStackTrace();
			return -1;
		}
	}

	@Override
	public int getRepairCost(ItemStack itemStack) {
		int maxAmount = (int) Math.round(cost * 0.7);
		double per = maxDurability / maxAmount;
		double dura = getDurability(itemStack);

        for (int i = 1; i <= maxAmount; i++)
            if (dura < per * i)
				return (maxAmount + 1) - i;
		return 0;
	}

	@Override
	public void setDurability(ItemStack itemStack, int durability) {
        ItemMeta meta = itemStack.getItemMeta();
        List<String> lore = meta.getLore();
        lore.set(0, HiddenStringUtils.encodeString(durability + ""));
        meta.setLore(lore);
        itemStack.setItemMeta(meta);
        itemStack.setDurability((short) durabilityScale(itemStack));
	}
}
