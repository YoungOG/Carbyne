package com.medievallords.carbyne.professions.alchemy;

import com.medievallords.carbyne.professions.types.AlchemyProfession;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Williams on 2017-08-11
 * for the Carbyne project.
 */
@Getter
@Setter
public class AlchemyTask extends BukkitRunnable {

    private BrewerInventory inventory;
    private Player player;
    private int timeViewed = 0, timeNotViewed = 0, itemsPutIn = 0;
    private AlchemyProfession alchemy;

    public AlchemyTask(BrewerInventory inventory, Player player, AlchemyProfession alchemy) {
        this.inventory = inventory;
        this.player = player;
        this.alchemy = alchemy;
    }

    @Override
    public void run() {

        if (getTimeNotViewed() > 15) {
            alchemy.getAlchemyTasks().remove(this);
        }

        if (player == null || !player.isOnline()) {
            cancel();
            alchemy.getAlchemyTasks().remove(this);
            return;
        }

        if (inventory.getViewers().contains(player)) {
            timeViewed++;
            timeNotViewed = 0;
        } else {
            timeNotViewed++;
        }
    }
}
