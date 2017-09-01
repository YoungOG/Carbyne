package com.medievallords.carbyne.customevents;

import com.medievallords.carbyne.gear.types.CarbyneGear;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by Williams on 2017-08-09
 * for the Carbyne project.
 */
public class CarbyneRepairedEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    @Getter
    private Player player;
    private CarbyneGear gear;
    private boolean isCancelled;

    public CarbyneRepairedEvent(Player player, CarbyneGear gear) {
        this.player = player;
        this.gear = gear;
        this.isCancelled = false;
    }

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override
    public void setCancelled(boolean arg0) {
        this.isCancelled = arg0;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
