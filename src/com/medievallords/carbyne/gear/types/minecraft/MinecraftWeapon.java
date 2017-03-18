package com.medievallords.carbyne.gear.types.minecraft;

import com.medievallords.carbyne.gear.types.CarbyneGear;
import com.medievallords.carbyne.utils.HiddenStringUtils;
import com.medievallords.carbyne.utils.ItemBuilder;
import com.medievallords.carbyne.utils.Namer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class MinecraftWeapon extends CarbyneGear {

	private String material = null;
	
	@Override
	public boolean load(ConfigurationSection cs, String type) {
		material = cs.getName();

		if ((this.type = type) == null)
            return false;

		if ((maxDurability = cs.getInt(type + ".Durability")) == -1)
            return false;

		this.lore = new ArrayList<>();
		this.lore.add(0, "&aDurability&7: &c0/" + cs.getInt(type + ".Durability"));
		this.lore.add(0, HiddenStringUtils.encodeString(gearCode));

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
			Namer.setLore(itemStack, "&aDurability&7: &c" + durability + "/" + getMaxDurability(), 1);
		} else {
			wielder.getInventory().remove(itemStack);
			wielder.playSound(wielder.getLocation(), Sound.ITEM_BREAK, 1, 1);
		}
	}

	@Override
	public int getDurability(ItemStack itemStack) {
		if (itemStack == null) {
			return -1;
		}

		try {
			return Integer.valueOf(ChatColor.stripColor(itemStack.getItemMeta().getLore().get(1)).replace(" ", "").split(":")[1].split("/")[0]);
		} catch (Exception ez) {
			return -1;
		}
	}
}
