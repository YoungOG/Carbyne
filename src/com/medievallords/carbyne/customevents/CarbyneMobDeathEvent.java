package com.medievallords.carbyne.customevents;


import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CarbyneMobDeathEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    //    private CarbyneMob carbyneMob;
//    private MobInstance mobInstance;
    private Location location;
    private boolean isCancelled;

    public CarbyneMobDeathEvent(/*CarbyneMob carbyneMob, MobInstance mobInstance, */Location location) {
//        this.carbyneMob = carbyneMob;
//        this.mobInstance = mobInstance;
        this.location = location;
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
