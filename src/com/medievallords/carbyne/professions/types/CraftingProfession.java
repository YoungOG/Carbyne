package com.medievallords.carbyne.professions.types;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.professions.Profession;
import com.medievallords.carbyne.profiles.Profile;
import com.medievallords.carbyne.utils.Cooldowns;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Williams on 2017-08-09
 * for the Carbyne project.
 */
public class CraftingProfession extends Profession {

    private String[] advanced = new String[]{"DIAMOND", "GOLD", "EMERALD", "IRON", "LEATHER", "REDSTONE", "CHAIN"};

    public CraftingProfession(String name, double chance, int minNuggets, int maxNuggets, String goldMessage) {
        super(name, chance, minNuggets, maxNuggets, goldMessage);
    }

    @EventHandler(ignoreCancelled = true)
    public void onCraft(InventoryClickEvent event) {
        if (event.getInventory() == null)
            return;

        if (event.getInventory().getType() != InventoryType.WORKBENCH)
            return;

        CraftingInventory inventory = (CraftingInventory) event.getInventory();

        if (!(event.getWhoClicked() instanceof Player))
            return;

        Player player = (Player) event.getWhoClicked();

        if (event.getSlot() != 0)
            return;

        ItemStack itemStack = event.getCurrentItem();
        if (itemStack == null || itemStack.getType() == Material.AIR)
            return;

        if (!isAdvanced(itemStack)) {
            if (Math.random() > 0.05) {
                return;
            }

            Profile profile = Carbyne.getInstance().getProfileManager().getProfile(player.getUniqueId());

            if (profile == null || profile.getProfession() == null || profile.getProfession() != this)
                return;

            giveReward(player);
        }

        if (event.getAction() == InventoryAction.PICKUP_SOME || event.getAction() == InventoryAction.PICKUP_ALL || event.getAction() == InventoryAction.PICKUP_HALF || event.getAction() == InventoryAction.PICKUP_ONE) {
            Profile profile = Carbyne.getInstance().getProfileManager().getProfile(player.getUniqueId());

            if (profile == null || profile.getProfession() == null || profile.getProfession() != this)
                return;

            if (Cooldowns.tryCooldown(player.getUniqueId(), player.getUniqueId().toString() + "professions:prof:" + itemStack.getType().toString(), 15000)) {
                giveReward(player);
            }
        }
    }

    private boolean isAdvanced(ItemStack itemStack) {
        for (String s : advanced) {
            if (itemStack.toString().contains(s)) {
                return true;
            }
        }

        return false;
    }

}
