package com.medievallords.carbyne.duels.arena;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.duels.duel.Duel;
import com.medievallords.carbyne.utils.LocationSerialization;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

/**
 * Created by Calvin on 3/15/2017
 * for the Carbyne project.
 */
@Getter
@Setter
public class Arena {

    // START DUEL, THEN ADD TO LIST!

    private String arenaId;
    private Location[] spawnPointLocations;
    private Location lobbyLocation;
    private Set<Duel> duels = new HashSet<>();
    private int cancelId;

    public Arena(String arenaId) {
        this.arenaId = arenaId;
        this.spawnPointLocations = new Location[2];
    }

    public void save() {
        ConfigurationSection section = Carbyne.getInstance().getArenasFileConfiguration().getConfigurationSection("Arenas");

        if (!section.isSet(arenaId)) {
            section.createSection(arenaId);
        }

        if (!section.isSet(arenaId + ".LobbyLocation")) {
            section.createSection(arenaId + ".LobbyLocation");
        }

        if (!section.isSet(arenaId + ".SpawnPointLocations")) {
            section.createSection(arenaId + ".SpawnPointLocations");
            section.set(arenaId + ".SpawnPointLocations", new ArrayList<String>());
        }

        if (lobbyLocation != null) {
            section.set(arenaId + ".LobbyLocation", LocationSerialization.serializeLocation(lobbyLocation));
        }

        if (spawnPointLocations.length > 0) {
            ArrayList<String> locationStrings = new ArrayList<>();

            for (Location location : getSpawnPointLocations()) {
                if (location != null) {
                    locationStrings.add(LocationSerialization.serializeLocation(location));
                }
            }

            section.set(arenaId + ".SpawnPointLocations", locationStrings);
        }

        try {
            Carbyne.getInstance().getArenasFileConfiguration().save(Carbyne.getInstance().getArenasFile());
        } catch (IOException e) {
            e.printStackTrace();
            Carbyne.getInstance().getLogger().log(Level.WARNING, "Failed to save arena " + arenaId + "!");
        }
    }
}
