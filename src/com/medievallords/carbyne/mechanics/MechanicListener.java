package com.medievallords.carbyne.mechanics;

import com.medievallords.carbyne.mechanics.conditions.PlayerCountCondition;
import com.medievallords.carbyne.mechanics.targeters.RandomPlayerInRadius;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicConditionLoadEvent;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMechanicLoadEvent;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicTargeterLoadEvent;
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
            case "bomb":
                event.register(new BombMechanic(event.getMechanicName(), event.getConfig(), 1));
                break;
            /*case "dropchest":
                event.register(new DropChestMechanic(event.getMechanicName(), event.getConfig(), 1));
                break;*/
            case "shadowstep":
                event.register(new ShadowStepMechanic(event.getMechanicName(), event.getConfig(), 1));
                break;
            case "playsound":
                event.register(new PlaySoundMechanic(event.getMechanicName(), event.getConfig(), 1));
                break;
            case "cgdamage":
                event.register(new DamageMechanic(event.getMechanicName(), event.getConfig(), 1));
                break;
        }
    }

    @EventHandler
    public void onLoadTargeter(MythicTargeterLoadEvent event) {
        switch (event.getTargeterName().toLowerCase()) {
            case "RPIR":
                event.register(new RandomPlayerInRadius(event.getConfig()));
                break;
        }
    }

    @EventHandler
    public void onLoadCondition(MythicConditionLoadEvent event) {
        switch (event.getConditionName().toLowerCase()) {
            case "playercount":
                event.register(new PlayerCountCondition(event.getConditionName(), event.getConfig()));
                break;
        }
    }
}
