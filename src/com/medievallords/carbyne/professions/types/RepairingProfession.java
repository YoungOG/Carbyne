package com.medievallords.carbyne.professions.types;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.customevents.CarbyneRepairedEvent;
import com.medievallords.carbyne.professions.Profession;
import com.medievallords.carbyne.profiles.Profile;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Williams on 2017-08-09
 * for the Carbyne project.
 */
public class RepairingProfession extends Profession {


    public RepairingProfession(String name, double chance, int minNuggets, int maxNuggets, String goldMessage) {
        super(name, chance, minNuggets, maxNuggets, goldMessage);
    }

    @EventHandler(ignoreCancelled = true)
    public void onRepairCabrbyne(CarbyneRepairedEvent event) {
        Player player = event.getPlayer();

        Profile playerData = Carbyne.getInstance().getProfileManager().getProfile(player.getUniqueId());
        if (playerData == null || playerData.getProfession() == null || playerData.getProfession() != this) {
            return;
        }

        giveReward(player);
    }

    @EventHandler(ignoreCancelled = true)
    public void onRepairVanilla(InventoryClickEvent event) {
        if (event.getInventory().getType() != InventoryType.ANVIL || event.getAction() != InventoryAction.PICKUP_ALL) {
            return;
        }

        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();

        ItemStack itemStack = event.getCurrentItem();
        if (itemStack == null || itemStack.getType() == Material.AIR) {
            return;
        }

        if (event.getSlot() == 2) {
            Profile playerData = Carbyne.getInstance().getProfileManager().getProfile(player.getUniqueId());
            if (playerData == null || playerData.getProfession() == null || playerData.getProfession() != this) {
                return;
            }

            giveReward(player);
        }
    }
}
