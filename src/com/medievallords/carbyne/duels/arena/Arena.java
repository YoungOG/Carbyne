package com.medievallords.carbyne.duels.arena;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.duels.duel.Duel;
import com.medievallords.carbyne.squads.Squad;
import com.medievallords.carbyne.utils.LocationSerialization;
import com.medievallords.carbyne.utils.MessageManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Created by Calvin on 3/15/2017
 * for the Carbyne project.
 */
@Getter
@Setter
public class Arena {

    private Carbyne main = Carbyne.getInstance();

    private String arenaId;
    private Location[] pedastoolLocations = new Location[2], spawnPointLocations = new Location[2];
    private HashMap<Location, Boolean> activePedastoolLocations = new HashMap<>();
    private Location lobbyLocation;
    private Duel duel;
    private UUID[] duelists = new UUID[2];

    public Arena(String arenaId) {
        this.arenaId = arenaId;
    }

    public void save() {
        ConfigurationSection section = main.getArenaFileConfiguration().getConfigurationSection("Arenas");

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

        if (!section.isSet(arenaId + ".PedastoolLocations")) {
            section.createSection(arenaId + ".PedastoolLocations");
            section.set(arenaId + ".PedastoolLocations", new ArrayList<String>());
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

        if (pedastoolLocations.length > 0) {
            ArrayList<String> locationStrings = new ArrayList<>();

            for (Location location : getPedastoolLocations()) {
                if (location != null) {
                    locationStrings.add(LocationSerialization.serializeLocation(location));
                }
            }

            section.set(arenaId + ".PedastoolLocations", locationStrings);
        }

        try {
            main.getArenaFileConfiguration().save(main.getArenaFile());
        } catch (IOException e) {
            e.printStackTrace();
            main.getLogger().log(Level.WARNING, "Failed to save arena " + arenaId + "!");
        }
    }

    public void activatePedastool(Location location, boolean active) {
        activePedastoolLocations.put(location, active);

        int activePressurePlates = 0;

        for (Location locations : activePedastoolLocations.keySet()) {
            if (activePedastoolLocations.get(locations)) {
                activePressurePlates++;
            }
        }

        if (activePressurePlates < activePedastoolLocations.keySet().size()) {

        }

        if (activePressurePlates >= activePedastoolLocations.keySet().size()) {

        }

        Bukkit.broadcastMessage("Activated PressurePlates: " + activePedastoolLocations.size());

        for (Location loc : activePedastoolLocations.keySet()) {
            Bukkit.broadcastMessage("Location: (World: " + loc.getWorld().getName() + ", X: " + loc.getX() + ", Y: " + loc.getY() + ", Z: " + loc.getZ() + ")");
        }
    }

    public void requestDuel(boolean squadFight, UUID[] players, Squad squadOne, Squad squadTwo, Duel duel) {
        if (squadFight) {
            if (squadOne != null && squadTwo != null) {
                startSquadFight(squadOne, squadTwo, duel);
            }
        } else {
            if (players != null) {
                startRegular(players, duel);
            }
        }
    }

    public void startRegular(UUID[] players, Duel duel) {
        if (duel != null) {

            for (UUID uuid : players) {
                Player player = Bukkit.getServer().getPlayer(uuid);

                if (player != null) {
                    MessageManager.sendMessage(player, "&cA duel is already running");
                }
            }

            return;
        }

        Carbyne.getInstance().getDuelManager().getDuels().add(duel);
        duel.countdown();
    }

    public void startSquadFight(Squad teamOne, Squad teamTwo, Duel duel) {
        if (duel != null) {

            for (UUID uuid : teamOne.getAllPlayers()) {
                Player player = Bukkit.getServer().getPlayer(uuid);

                if (player != null) {
                    MessageManager.sendMessage(player, "&cA duel is already running");
                }
            }

            for (UUID uuid : teamTwo.getAllPlayers()) {
                Player player = Bukkit.getServer().getPlayer(uuid);

                if (player != null) {
                    MessageManager.sendMessage(player, "&cA duel is already running");
                }
            }
            return;
        }

        Carbyne.getInstance().getDuelManager().getDuels().add(duel);
        duel.countdown();
    }
}
