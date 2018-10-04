package com.medievallords.carbyne.recipes;

import com.medievallords.carbyne.Carbyne;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;

public class CustomRecipe {

    public static ArrayList<CustomRecipe> recipes = new ArrayList<>();

    private String name;
    @Getter
    private ItemStack result;
    @Getter
    private ArrayList<ItemStack> ingredients;

    @Getter
    private ShapedRecipe recipe;

    public CustomRecipe(String name, ItemStack item, ArrayList<ItemStack> ingredients) {
        this.name = name;
        this.ingredients = ingredients;
        this.result = item;

        recipe = new ShapedRecipe(new NamespacedKey(Carbyne.getInstance(), name), result);

        recipe.shape("abc", "def", "ghi");

        for (int i = 0; i < 9; i++) {
            if (ingredients.get(i) == null || ingredients.get(i).getType() == Material.AIR) continue;
            MaterialData materialData = new MaterialData(ingredients.get(i).getType());
            materialData.setData((byte) ingredients.get(i).getDurability());
            recipe.setIngredient((char)(i+'a'), materialData);
        }

        Bukkit.getServer().addRecipe(this.recipe);
        recipes.add(this);
    }

    public static CustomRecipe getCustomRecipe(ItemStack result) {

        if (result == null || result.getType() == Material.AIR) return null;

        for (CustomRecipe cr : recipes) {

            if (cr.getResult().isSimilar(result)) return cr;

        }

        return null;
    }
}
