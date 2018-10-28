package com.medievallords.carbyne.zones;

import com.medievallords.carbyne.customevents.ZoneEnterEvent;
import com.medievallords.carbyne.utils.PlayerHealth;
import com.medievallords.carbyne.utils.StaticClasses;
import com.palmergames.bukkit.towny.event.PlayerChangePlotEvent;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.mobs.MythicMob;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.List;

public class ZoneListeners implements Listener {

    @EventHandler
    public void onMobSpawn(final CreatureSpawnEvent event) {
        if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.SPAWNER)
            return;

        final Zone zone = getZone(event.getLocation());

        if (zone == null)
            return;

        event.getEntity().remove();

        final List<MobData> mobData = zone.getMobs().get(event.getEntity().getType());

        if (mobData == null) {
            final MythicMob randomMob = zone.getRandomMob();

            if (randomMob != null)
                randomMob.spawn(BukkitAdapter.adapt(event.getLocation()), 1);

            return;
        }

        final MythicMob randomMob = zone.getRandomMob(mobData);

        if (randomMob != null)
            randomMob.spawn(BukkitAdapter.adapt(event.getLocation()), 1);
    }

    @EventHandler(ignoreCancelled = true)
    public void onChunk(final PlayerChangePlotEvent event) {
        final Zone lastZone = getZone(event.getPlayer());
        final Zone currentZone = getZone(event.getMoveEvent().getTo());

        final PlayerHealth playerHealth = PlayerHealth.getPlayerHealth(event.getPlayer().getUniqueId());

        if (playerHealth == null)
            return;

        if (currentZone == null) {
            playerHealth.setZone(null);

            if (lastZone != null)
                lastZone.getPlayersInZone().remove(event.getPlayer().getUniqueId());
        } else
            if (currentZone != lastZone) {
                if (lastZone != null)
                    lastZone.getPlayersInZone().remove(event.getPlayer().getUniqueId());

                currentZone.getPlayersInZone().add(event.getPlayer().getUniqueId());
                playerHealth.setZone(currentZone);
                ZoneEnterEvent zoneEnterEvent = new ZoneEnterEvent(event.getPlayer(), currentZone);
                Bukkit.getPluginManager().callEvent(zoneEnterEvent);
            }
    }

    private Zone getZone(final Location location) {
        for (final Zone zone : StaticClasses.zoneManager.getZones())
            if (zone.isInZone(location))
                return zone;

        return null;
    }

    private Zone getZone(final Player player) {
        for (final Zone zone : StaticClasses.zoneManager.getZones())
            if (zone.getPlayersInZone().contains(player.getUniqueId()))
                return zone;

        return null;
    }
}
