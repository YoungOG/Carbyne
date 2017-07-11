package com.medievallords.carbyne.gear.types.minecraft;

import com.medievallords.carbyne.gear.types.CarbyneGear;
import com.medievallords.carbyne.utils.HiddenStringUtils;
import com.medievallords.carbyne.utils.ItemBuilder;
import com.medievallords.carbyne.utils.Namer;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
		this.lore.add(0, "&aDamage Reduction&7: &b" + (int) (armorRating * 100) + "%");
		//this.lore.add(0, "&aDurability&7: &c" + cs.getInt(type + ".Durability") + "/" + getMaxDurability());
		this.lore.add(0, HiddenStringUtils.encodeString(gearCode));

		cost = cs.getInt(type + ".Cost");

		return true;
	}

	@Override
	public ItemStack getItem(boolean storeItem) {
		return new ItemBuilder(Material.getMaterial((material + "_" + type).toUpperCase())).setLore(lore).build();
	}

	@Override
	public void damageItem(Player wielder, ItemStack itemStack) {
		/*int durability = getDurability(itemStack);
		double chance = 1;

		if (itemStack.containsEnchantment(Enchantment.DURABILITY)) {
			int level = itemStack.getEnchantmentLevel(Enchantment.DURABILITY);
			double calc = 100 - (60 + (40/(level+1)));
			chance -= calc / 100;
		}

		if (durability == -1) {
			return;
		}

		if (Math.random() < chance) {
			return;
		}

		if (durability >= 1) {
			durability--;
			Namer.setLore(itemStack, "&aDurability&7: &c" + durability + "/" + getMaxDurability(), 1);
			itemStack.setDurability((short) durabilityScale(itemStack));

			if (itemStack.getDurability() <= 0) {
				itemStack.setDurability((short) 0);
			} else if (itemStack.getDurability() >= itemStack.getType().getMaxDurability()) {
				itemStack.setDurability((short) itemStack.getType().getMaxDurability());
			}
		} else {
			wielder.getInventory().remove(itemStack);
			wielder.playSound(wielder.getLocation(), Sound.ITEM_BREAK, 1, 1);
		}*/
	}

	@Override
	public double getDurability(ItemStack itemStack) {
		if (itemStack == null) {
			return -1;
		}

		try {
			return Double.valueOf(ChatColor.stripColor(itemStack.getItemMeta().getLore().get(1)).replace(" ", "").split(":")[1].split("/")[0]);
		} catch (Exception ez) {
			return -1;
		}
	}

	@Override
	public int getRepairCost(ItemStack itemStack) {
		int maxAmount = (int) Math.round(cost * 0.7);

		double per = maxDurability / maxAmount;
		double dura = getDurability(itemStack);
		for (int i = 1; i <= maxAmount; i++) {
			if (dura < per * i) {
				return (maxAmount + 1) - i;
			}
		}

		return 0;
	}

	@Override
	public void setDurability(ItemStack itemStack, double durability) {
		if (itemStack.hasItemMeta() && !itemStack.getItemMeta().hasLore()) {
			List<String> loreList = new ArrayList<>();
			itemStack.getItemMeta().setLore(loreList);
		}

		if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasLore() && itemStack.getItemMeta().getLore().size() < 2) {
			for (int i = 0; i < itemStack.getItemMeta().getLore().size(); i++) {
				if (itemStack.getItemMeta().getLore().size() >= 2) {
					break;
				}

				itemStack.getItemMeta().getLore().add("");
			}
		}

		if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasLore()) {
			Namer.setLore(itemStack, "&aDurability&7: &c" + durability + "/" + getMaxDurability(), 1);
		}
	}

}
