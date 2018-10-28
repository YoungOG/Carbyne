package com.medievallords.carbyne.gear;

import com.medievallords.carbyne.gear.artifacts.Artifact;
import com.medievallords.carbyne.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

public class GearGuiManager {

    private GearManager gearManager;
    public static final String TITLE = "§5§lCarbyne Forge";

    public GearGuiManager(GearManager gearManager) {
        this.gearManager = gearManager;
    }

    public Inventory getForgeInventory() {
        Inventory inventory = Bukkit.createInventory(null, 45, TITLE);

        for (int i = 0; i < inventory.getSize(); i++)
            inventory.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE).durability(15).name("&7").build());

        inventory.setItem(19, null); // Clear the artifact-spot.
        inventory.setItem(12, null); // Clear the crafting-space so that you know where to put the ingredients.
        inventory.setItem(13, null);
        inventory.setItem(14, null);
        inventory.setItem(21, null);
        inventory.setItem(22, null);
        inventory.setItem(23, null);
        inventory.setItem(30, null);
        inventory.setItem(31, null);
        inventory.setItem(32, null);
        inventory.setItem(25, null); // Clear the recipe-result slot.


        // Set a few more panes for aesthetics.
        ItemBuilder paneBuilder = new ItemBuilder(Material.STAINED_GLASS_PANE).durability(14)
                .name("&4&lPlace an Artifact in the &7&7empty &4&lslot.")
                .addLore("&7Place an Artifact in the first slot section")
                .addLore("&7on the far left to continue.")
                .addLore(" ")
                .addLore("&a&lAvailable Artifacts&7&l(&6&l" + gearManager.getArtifacts().values().size() + "&7&l): ");
        for (Artifact artifact : gearManager.getArtifacts().values())
            paneBuilder.addLore("  &7&l- &r" + artifact.getCustomRecipe().getResult().getItemMeta().getDisplayName());

        inventory.setItem(1, paneBuilder.build());
        inventory.setItem(10, paneBuilder.build());
        inventory.setItem(28, paneBuilder.build());
        inventory.setItem(37, paneBuilder.build());

        inventory.setItem(3, paneBuilder.build());
        inventory.setItem(4, paneBuilder.build());
        inventory.setItem(5, paneBuilder.build());
        inventory.setItem(39, paneBuilder.build());
        inventory.setItem(40, paneBuilder.build());
        inventory.setItem(41, paneBuilder.build());

        inventory.setItem(7, paneBuilder.build());
        inventory.setItem(16, paneBuilder.build());
        inventory.setItem(34, paneBuilder.build());
        inventory.setItem(43, paneBuilder.build());

        return inventory;
    }
}