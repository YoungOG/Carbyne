package com.medievallords.carbyne.mechanics;

import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMechanicLoadEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Created by WE on 2017-08-19.
 */
public class MechanicListener implements Listener {

    @EventHandler
    public void onLoadMechanic(MythicMechanicLoadEvent event) {
        switch (event.getMechanicName().toLowerCase()) {
            case "storm":
                event.register(new StormMechanic(event.getMechanicName(), event.getConfig(), 1));
                break;
            case "coding":
                event.register(new CodingMechanic(event.getMechanicName(), event.getConfig(), 1));
                break;
        }
    }
}
