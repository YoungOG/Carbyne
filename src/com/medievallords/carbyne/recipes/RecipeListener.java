package com.medievallords.carbyne.recipes;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

public class RecipeListener implements Listener {

    @EventHandler
    public void onItemCraftEvent(PrepareItemCraftEvent event) {

        if (event.getInventory().getResult() == null || event.getInventory().getResult().getType() == Material.AIR) return;

        CustomRecipe customRecipe = CustomRecipe.getCustomRecipe(event.getInventory().getResult());

        if (customRecipe == null) return;

        for (int i = 0; i < 9; i++) {
            ItemStack inInventory = event.getInventory().getItem(i + 1);
            ItemStack inRecipe = customRecipe.getIngredients().get(i);
            if (inInventory != null) {
                if (!inInventory.isSimilar(inRecipe)) {
                    event.getInventory().setResult(new ItemStack(Material.AIR));
                    return;
                }
            } else {
                if (inRecipe.getType() != Material.AIR) {
                    event.getInventory().setResult(new ItemStack(Material.AIR));
                    return;
                }
            }
//            ItemStack inInventory = event.getInventory().getItem(i + 1);
//            ItemStack inRecipe = customRecipe.getIngredients().get(i);
//            if (inInventory.getType() != inRecipe.getType()) {
//                event.getInventory().setResult(new ItemStack(Material.AIR));
//                return;
//            } else if (inInventory.getAmount() != inRecipe.getAmount()) {
//                event.getInventory().setResult(new ItemStack(Material.AIR));
//                return;
//            } else if (inInventory.getDurability() != inRecipe.getDurability()) {
//                event.getInventory().setResult(new ItemStack(Material.AIR));
//                return;
//            }
//
//            if (inInventory.getEnchantments() != inRecipe.getEnchantments()) {
//                event.getInventory().setResult(new ItemStack(Material.AIR));
//                return;
//            }
//
//            if (inInventory.hasItemMeta() && inRecipe.hasItemMeta()) {
//                if (inInventory.getItemMeta().hasDisplayName() && inRecipe.getItemMeta().hasDisplayName()) {
//                    if (inInventory.getItemMeta().getDisplayName() != inRecipe.getItemMeta().getDisplayName()) {
//                        event.getInventory().setResult(new ItemStack(Material.AIR));
//                        return;
//                    }
//                }
//
//                if (inInventory.getItemMeta().hasDisplayName() || inRecipe.getItemMeta().hasDisplayName()) {
//                    event.getInventory().setResult(new ItemStack(Material.AIR));
//                    return;
//                }
//
//                if (inInventory.getItemMeta().hasLore() && inRecipe.getItemMeta().hasLore()) {
//                    if (inInventory.getItemMeta().getLore() != inRecipe.getItemMeta().getLore()) {
//                        event.getInventory().setResult(new ItemStack(Material.AIR));
//                        return;
//                    }
//                }
//
//                if (inInventory.getItemMeta().hasLore() || inRecipe.getItemMeta().hasLore()) {
//                    event.getInventory().setResult(new ItemStack(Material.AIR));
//                    return;
//                }
//            }
        }
    }
}
