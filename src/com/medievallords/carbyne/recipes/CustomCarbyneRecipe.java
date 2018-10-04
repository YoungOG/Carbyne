package com.medievallords.carbyne.recipes;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

@Getter
public class CustomCarbyneRecipe {

    public static ArrayList<CustomCarbyneRecipe> carbyneRecipes = new ArrayList<>();

    private String name;
    private ItemStack result;
    private ItemStack[] ingredients;

    public CustomCarbyneRecipe(String name, ItemStack item, ArrayList<ItemStack> ingredients) {
        this.name = name; // The name of the recipe. It has no special meaning.
        int amount = ingredients.size();
        this.ingredients = new ItemStack[amount];
        for (int i = 0; i < amount; i++) { // Requires 10 items - 9 items for the fragment part + an artifact.
            this.ingredients[i] = ingredients.get(i);
        }

        this.result = item; // The item that you get from crafting using this recipe.

        carbyneRecipes.add(this); //Add this recipe to the global recipe list.
    }

    public static CustomCarbyneRecipe getRecipe(ItemStack[] ingredients, ItemStack artifact) {
        for (CustomCarbyneRecipe recipe : carbyneRecipes) { // Go through all created recipes to check if there is a recipe from the specified ingredients
            boolean found = true;
            for (int i = 0; i < 9; i++) {
                if (!ingredients[i].isSimilar(recipe.ingredients[i])) {
                    found = false;
                    break;
                }
            }

            if (found) {
                if (recipe.ingredients[9].isSimilar(artifact)) {
                    return recipe;
                }
            }
        }

        return null;
    }

}
