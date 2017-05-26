package com.medievallords.carbyne.duels.duel;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.duels.arena.Arena;
import com.medievallords.carbyne.duels.duel.request.DuelRequest;
import com.medievallords.carbyne.duels.duel.types.RegularDuel;
import com.medievallords.carbyne.duels.duel.types.SquadDuel;
import com.medievallords.carbyne.utils.LocationSerialization;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Created by xwiena22 on 2017-03-14.
 *
 */
public class DuelManager {

    private Carbyne main = Carbyne.getInstance();

    private List<Duel> duels = new ArrayList<>();
    private List<Arena> arenas = new ArrayList<>();

    public DuelManager() {
        loadArenas();
    }

    public void loadArenas() {
        ConfigurationSection section = main.getArenaFileConfiguration().getConfigurationSection("Arenas");

        if (section.getKeys(false).size() > 0) {
            main.getLogger().log(Level.INFO, "Preparing to load " + section.getKeys(false).size() + " arenas.");

            for (String id : section.getKeys(false)) {
                Location lobbyLocation = null;
                ArrayList<Location> spawnPointLocations = new ArrayList<>();
                ArrayList<Location> pedastoolLocations = new ArrayList<>();

                if (section.getString(id + ".LobbyLocation") != null && !section.getString(id + ".LobbyLocation").isEmpty()) {
                    lobbyLocation = LocationSerialization.deserializeLocation(section.getString(id + ".LobbyLocation"));
                }

                for (String s : section.getStringList(id + ".SpawnPointLocations")) {
                    spawnPointLocations.add(LocationSerialization.deserializeLocation(s));
                }

                for (String s : section.getStringList(id + ".PedastoolLocations")) {
                    pedastoolLocations.add(LocationSerialization.deserializeLocation(s));
                }

                Arena arena = new Arena(id);
                arena.setLobbyLocation(lobbyLocation);
                arena.setSpawnPointLocations(spawnPointLocations.toArray(new Location[spawnPointLocations.size()]));
                arena.setPedastoolLocations(pedastoolLocations.toArray(new Location[pedastoolLocations.size()]));

                arenas.add(arena);
            }

            main.getLogger().log(Level.INFO, "Successfully loaded " + arenas.size() + " arenas.");
        }
    }

    public Duel getDuel(String arenaName) {
        Arena arena = null;
        for (Arena arenas : arenas) {
            if (arenas.getArenaId().equalsIgnoreCase(arenaName)) {
                arena = arenas;
            }
        }
        for (Duel duel : duels) {
            if (duel.getArena().equals(arena)) {
                return duel;
            }
        }

        return null;
    }

    public Duel getDuelFromUUID(UUID uuid) {
        for (Duel duel : duels) {
            if (duel instanceof RegularDuel) {

                RegularDuel regularDuel = (RegularDuel) duel;

                if (Arrays.asList(regularDuel.getParticipants()).contains(uuid)) {
                    return duel;
                }
            } else if (duel instanceof SquadDuel) {

                SquadDuel squadDuel = (SquadDuel) duel;
                if (squadDuel.getSquadOne().getAllPlayers().contains(uuid) || squadDuel.getSquadTwo().getAllPlayers().contains(uuid)) {
                    return duel;
                }
            }
        }
        return null;
    }

    public Arena getArena(String arenaId) {
        for (Arena arena : arenas) {
            if (arena.getArenaId().equalsIgnoreCase(arenaId)) {
                return arena;
            }
        }

        return null;
    }

    public Arena getArena(Location location) {
        for (Arena arena : arenas) {
            if (arena.getLobbyLocation().equals(location) || Arrays.asList(arena.getSpawnPointLocations()).contains(location) || Arrays.asList(arena.getPedastoolLocations()).contains(location)) {
                return arena;
            }
        }

        return null;
    }

    public List<Arena> getArenas() {
        return arenas;
    }

    public List<Duel> getDuels() {
        return duels;
    }

    public void cancelAll() {
        cancelDuels();
        cancelRequests();
    }

    public void cancelDuels() {
        for (Duel duel : duels) {
            if (duel == null) continue;
            duel.end(null);
        }
    }

    public void cancelRequests() {
        for (DuelRequest request : DuelRequest.requests) {
            if (request == null) continue;
            request.cancel();
        }
    }
}
