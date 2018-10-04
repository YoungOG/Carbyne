package com.medievallords.carbyne.customevents;

import com.medievallords.carbyne.portals.Portal;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PortalEnterEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private Player player;
    private Portal portal;

    public PortalEnterEvent(Player player, Portal portal)
    {
        this.player = player;
        this.portal = portal;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public Portal getPortal() {
        return portal;
    }

    public Player getPlayer() {
        return player;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
