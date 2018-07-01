package com.medievallords.carbyne.zones;

import com.medievallords.carbyne.Carbyne;
import com.palmergames.bukkit.towny.event.PlayerChangePlotEvent;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ZoneListeners implements Listener {

    private final ZoneManager zoneManager = Carbyne.getInstance().getZoneManager();

    @EventHandler
    public void onMobDeath(MythicMobDeathEvent event) {
        for (Zone zone : zoneManager.getZones())
            if (zone.isInZone(BukkitAdapter.adapt(event.getMob().getLocation()))) {
                zone.reduceMob();
                break;
            }
    }

    @EventHandler(ignoreCancelled = true)
    public void onChunk(final PlayerChangePlotEvent event) {
        final Zone lastZone = getZone(event.getPlayer());
        final Zone currentZone = getZone(event.getMoveEvent().getTo());
        if (currentZone == null) {
            if (lastZone != null) {
                lastZone.getPlayersInZone().remove(event.getPlayer().getUniqueId());
            }
        } else {
            if (currentZone != lastZone) {
                if (lastZone != null) {
                    lastZone.getPlayersInZone().remove(event.getPlayer().getUniqueId());
                }

                currentZone.getPlayersInZone().add(event.getPlayer().getUniqueId());
            }
        }
    }

    private Zone getZone(final Location location) {
        for (Zone zone : zoneManager.getZones()) {
            if (zone.isInZone(location)) {
                return zone;
            }
        }

        return null;
    }

    private Zone getZone(final Player player) {
        for (Zone zone : zoneManager.getZones()) {
            if (zone.getPlayersInZone().contains(player.getUniqueId())) {
                return zone;
            }
        }

        return null;
    }
}
