package com.medievallords.carbyne.events.component;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.events.Event;
import com.medievallords.carbyne.events.EventComponent;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Dalton on 8/19/2017.
 */
@Getter
@Setter
public class InventoryComponent implements EventComponent {

    private ItemStack[] contents;
    private ItemStack[] armorContents;

    private Event event;

    public InventoryComponent(Event event) {
        this.event = event;
    }

    @Override
    public void start() {

    }

    @Override
    public void tick() {
        if (contents == null || armorContents == null) return;
        new BukkitRunnable() {
            public void run() {
                giveInventoryToPlayers();
            }
        }.runTask(Carbyne.getInstance());
    }

    @Override
    public void stop() {
        new BukkitRunnable() {
            public void run() {
                for (int i = 0; i < event.getParticipants().size(); i++) {
                    event.getParticipants().get(i).getInventory().clear();
                }
            }
        }.runTask(Carbyne.getInstance());
        contents = null;
        armorContents = null;
    }

    private void giveInventoryToPlayers() {
        for (int i = 0; i < event.getParticipants().size(); i++) {
            Player player = event.getParticipants().get(i);
            player.getInventory().setContents(contents);
            player.getInventory().setArmorContents(armorContents);
        }
    }

}
