package com.medievallords.carbyne.customevents;

import com.medievallords.carbyne.zones.Zone;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ZoneEnterEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private Player player;
    private Zone zone;

    public ZoneEnterEvent(Player player, Zone zone) {
        this.player = player;
        this.zone = zone;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public Zone getZone() {
        return zone;
    }

    public Player getPlayer() {
        return player;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
