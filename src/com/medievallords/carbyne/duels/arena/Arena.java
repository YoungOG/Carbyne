package com.medievallords.carbyne.duels.arena;

import com.medievallords.carbyne.duels.duel.Duel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

import java.util.ArrayList;

/**
 * Created by Calvin on 3/15/2017
 * for the Carbyne project.
 */
@Getter
@Setter
public class Arena {

    private static ArrayList<Arena> arenas = new ArrayList<>();

    private String arenaId;
    private Location[] pedastoolLocations, spawnPointLocation;
    private Location lobbyLocation;
    private Duel duel;

    public Arena(String arenaId) {
        this.arenaId = arenaId;

        arenas.add(this);
    }

    public void removeArena() {
        arenas.remove(this);
    }

    public static ArrayList<Arena> getArenas() {
        return arenas;
    }
}
