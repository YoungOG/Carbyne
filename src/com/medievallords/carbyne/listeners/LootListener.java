package com.medievallords.carbyne.listeners;

import com.codisimus.plugins.phatloots.events.PlayerLootEvent;
import com.medievallords.carbyne.Carbyne;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Williams on 2017-03-15.
 * for the Carbyne project.
 */
public class LootListener implements Listener {

    @EventHandler
    public void onLoot(PlayerLootEvent lootEvent) {
        for (ItemStack itemStack : lootEvent.getItemList()) {
            if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName() && itemStack.getType() != null && itemStack.getType() == Material.QUARTZ) {
                ItemStack replacement = Carbyne.getInstance().getGearManager().getCarbyneGear(itemStack.getItemMeta().getDisplayName()).getItem(false);

                if (replacement == null) {
                    return;
                }

                lootEvent.getItemList().remove(itemStack);
                lootEvent.getItemList().add(replacement);
            }
        }
    }
}
