package com.medievallords.carbyne.crates.events;

import com.medievallords.carbyne.crates.Crate;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by Calvin on 2017-12-18
 * for the Carbyne project.
 */
public class CrateOpenedEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    @Getter
    private Player player;
    @Getter
    private Crate crate;
    @Getter
    @Setter
    private boolean isCancelled;

    public CrateOpenedEvent(Player player, Crate crate) {
        this.player = player;
        this.crate = crate;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
