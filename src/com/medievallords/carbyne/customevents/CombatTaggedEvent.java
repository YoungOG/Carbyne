package com.medievallords.carbyne.customevents;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by Williams on 2017-06-30
 * for the Carbyne project.
 */
public class CombatTaggedEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    @Getter
    private Player player;

    public CombatTaggedEvent(Player player)
    {
        this.player = player;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
