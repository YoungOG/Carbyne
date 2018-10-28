package com.medievallords.carbyne.gear.artifacts;

import com.medievallords.carbyne.recipes.CustomRecipe;
import com.medievallords.carbyne.utils.ItemBuilder;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Artifact {

    // Artifact
    private String name;
    @Getter
    private boolean rare;
    @Getter
    private CustomRecipe customRecipe;

    public Artifact(String name) {
        this.name = name;
    }

    public void load(ConfigurationSection cs) {
        this.rare = cs.contains("Rare") && cs.getBoolean("Rare");

        // Load artifact recipe data
        if (!cs.contains("ArtifactRecipeIngredients"))
            return;

        if (!cs.contains("ArtifactRecipeResult"))
            return;

        // Artifact Crafting Recipe Format

        ArrayList<ItemStack> ingredients = new ArrayList<>();
        for (String recipeItemString : cs.getConfigurationSection("ArtifactRecipeIngredients").getKeys(false)) {
            ConfigurationSection itemSection = cs.getConfigurationSection("ArtifactRecipeIngredients").getConfigurationSection(recipeItemString);

            Material resultItemMaterial = Material.getMaterial(itemSection.contains("ItemMat") ? itemSection.getString("ItemMat") : "AIR");


            if (resultItemMaterial == Material.AIR) {
                ingredients.add(new ItemBuilder(resultItemMaterial).build());
                continue;
            }

            String resultItemDisplay = itemSection.contains("DisplayName") ? itemSection.getString("DisplayName") : "";
            int resultItemData = itemSection.contains("ItemData") ? itemSection.getInt("ItemData") : 0;
            int resultItemAmount = itemSection.contains("Amount") ? itemSection.getInt("Amount") : 1;
            List<String> resultItemLore = itemSection.contains("Lore") ? itemSection.getStringList("Lore") : new ArrayList<>();

            ItemBuilder itemBuilder = new ItemBuilder(resultItemMaterial);
            itemBuilder.durability(resultItemData);
            if (!resultItemDisplay.isEmpty())
                itemBuilder.name(resultItemDisplay);

            if (!resultItemLore.isEmpty())
                itemBuilder.setLore(resultItemLore);

            itemBuilder.amount(resultItemAmount);

            if (itemSection.contains("Enchantments")) {
                HashMap<Enchantment, Integer> resultItemEnchantments = new HashMap<>();
                for (String enchantmentString : itemSection.getStringList("Enchantments")) {
                    String[] enchSplit = enchantmentString.split(",");
                    resultItemEnchantments.put(Enchantment.getByName(enchSplit[0].toUpperCase()), Integer.parseInt(enchSplit[1]));
                }

                itemBuilder.addEnchantments(resultItemEnchantments);
            }

            ingredients.add(itemBuilder.build());
        }

        ConfigurationSection resultSection = cs.getConfigurationSection("ArtifactRecipeResult");
        String resultItemDisplay = resultSection.contains("DisplayName") ? resultSection.getString("DisplayName") : "";
        int resultItemData = resultSection.contains("ItemData") ? resultSection.getInt("ItemData") : 0;
        int resultItemAmount = resultSection.contains("Amount") ? resultSection.getInt("Amount") : 1;
        Material resultItemMaterial = Material.getMaterial(resultSection.contains("ItemMat") ? resultSection.getString("ItemMat") : "STONE");
        List<String> resultItemLore = resultSection.contains("Lore") ? resultSection.getStringList("Lore") : new ArrayList<>();

        ItemBuilder itemBuilder = new ItemBuilder(resultItemMaterial);
        itemBuilder.durability(resultItemData);
        itemBuilder.name(resultItemDisplay);
        itemBuilder.setLore(resultItemLore);
        itemBuilder.amount(resultItemAmount);
        itemBuilder.addGlow();

        if (resultSection.contains("Enchantments")) {
            HashMap<Enchantment, Integer> resultItemEnchantments = new HashMap<>();
            for (String enchantmentString : resultSection.getStringList("Enchantments")) {
                String[] enchSplit = enchantmentString.split(",");
                resultItemEnchantments.put(Enchantment.getByName(enchSplit[0].toUpperCase()), Integer.parseInt(enchSplit[1]));
            }

            itemBuilder.addEnchantments(resultItemEnchantments);
        }

        ItemStack result = itemBuilder.build();

        // Create the recipe for the artifact.
        this.customRecipe = new CustomRecipe(name, result, ingredients);
    }
}
