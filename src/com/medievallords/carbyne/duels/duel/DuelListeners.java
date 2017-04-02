package com.medievallords.carbyne.duels.duel;

import com.medievallords.carbyne.Carbyne;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

/**
 * Created by xwiena22 on 2017-03-14.
 *
 */
public class DuelListeners implements Listener {

    DuelManager duelManager = Carbyne.getInstance().getDuelManager();

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

        }
    }
}
