package com.medievallords.carbyne.duels.arena;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.duels.duel.Duel;
import com.medievallords.carbyne.duels.duel.request.DuelRequest;
import com.medievallords.carbyne.duels.duel.types.RegularDuel;
import com.medievallords.carbyne.duels.duel.types.SquadDuel;
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
import java.util.List;
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
    private Location[] pedastoolLocations;
    private Location[] spawnPointLocations;
    private HashMap<Location, Boolean> activePedastoolLocations = new HashMap<>();
    private Location lobbyLocation;
    private Duel duel;
    private List<UUID> duelists = new ArrayList<>();
    private int cancelId;

    public Arena(String arenaId) {
        this.arenaId = arenaId;
        this.pedastoolLocations = new Location[2];
        this.spawnPointLocations = new Location[2];
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

        if (activePressurePlates >= pedastoolLocations.length) {
            stopCancel();

            HashMap<UUID, Boolean> players = new HashMap<>();
            for (UUID uuid : duelists) {
                players.put(uuid, false);
            }

            DuelRequest request = new DuelRequest(players, false, this);
        }
    }

    public void requestDuel(boolean squadFight, UUID[] players, Squad squadOne, Squad squadTwo, int bet, HashMap<UUID, Integer> playerBets) {
        if (squadFight) {
            if (squadOne != null && squadTwo != null) {
                startSquadFight(squadOne, squadTwo, bet, playerBets);
            }
        } else {
            if (players != null) {
                startRegular(players, bet, playerBets);
            }
        }
    }

    public void startRegular(UUID[] players, int bet, HashMap<UUID, Integer> playerBets) {
        if (duel != null) {

            for (UUID uuid : players) {
                Player player = Bukkit.getServer().getPlayer(uuid);

                if (player != null) {
                    MessageManager.sendMessage(player, "&cA duel is already running");
                }
            }

            return;
        }
        this.duel = new RegularDuel(this, players);
        duel.setBets(bet);
        duel.setPlayerBets(playerBets);
        Carbyne.getInstance().getDuelManager().getDuels().add(duel);
        duel.countdown();
    }

    public void startSquadFight(Squad teamOne, Squad teamTwo, int bets, HashMap<UUID, Integer> playerBets) {
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
        this.duel = new SquadDuel(this, teamOne, teamTwo);
        duel.setBets(bets);
        duel.setPlayerBets(playerBets);
        Carbyne.getInstance().getDuelManager().getDuels().add(duel);
        duel.countdown();
    }

    public void startCancel(Player player) {
        stopCancel();

        cancelId = Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
            @Override
            public void run() {
                player.teleport(getLobbyLocation());
            }
        }, 200);
    }

    public void stopCancel() {
        Bukkit.getServer().getScheduler().cancelTask(cancelId);
    }
}
