package com.medievallords.carbyne.professions.types;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.professions.Profession;
import com.medievallords.carbyne.profiles.Profile;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Williams on 2017-08-09
 * for the Carbyne project.
 */
public class SmeltingProfession extends Profession {

    public SmeltingProfession(String name, double chance, int minNuggets, int maxNuggets, String goldMessage) {
        super(name, chance, minNuggets, maxNuggets, goldMessage);
    }

    @EventHandler(ignoreCancelled = true)
    public void onSmelt(InventoryClickEvent event) {
        if (event.getInventory() == null)
            return;

        if (event.getInventory().getType() != InventoryType.FURNACE)
            return;

        if (!(event.getWhoClicked() instanceof Player))
            return;

        Player player = (Player) event.getWhoClicked();

        FurnaceInventory furnaceInventory = (FurnaceInventory) event.getInventory();
        ItemStack itemStack = furnaceInventory.getResult();

        if (itemStack == null)
            return;

        if (event.getSlot() != 2) {
            return;
        }

        if (event.getAction() == InventoryAction.PICKUP_SOME || event.getAction() == InventoryAction.PICKUP_ALL || event.getAction() == InventoryAction.PICKUP_HALF || event.getAction() == InventoryAction.PICKUP_ONE) {

            Profile profile = Carbyne.getInstance().getProfileManager().getProfile(player.getUniqueId());
            if (profile == null || profile.getProfession() == null || profile.getProfession() != this)
                return;

            for (int i = 0; i < itemStack.getAmount(); i++) {
                giveReward(player);
            }
        }
    }

}
