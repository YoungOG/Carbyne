package com.medievallords.carbyne.portals;

import com.medievallords.carbyne.customevents.PortalEnterEvent;
import com.medievallords.carbyne.utils.StaticClasses;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;

public class PortalListeners implements Listener {

    @EventHandler
    public void onPortal(PlayerPortalEvent event) {
        Location location = new Location(event.getFrom().getWorld(), event.getFrom().getX(), event.getFrom().getY(), event.getFrom().getZ());
        Portal portal = StaticClasses.portalManager.getPortal(location);
        if (portal == null) {
            return;
        }

        event.useTravelAgent(false);

        event.setCancelled(true);
        event.getPlayer().teleport(portal.getTargetLocation());
        PortalEnterEvent enterEvent = new PortalEnterEvent(event.getPlayer(), portal);
        Bukkit.getPluginManager().callEvent(enterEvent);
    }
}
