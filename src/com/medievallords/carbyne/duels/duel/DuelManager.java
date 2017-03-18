package com.medievallords.carbyne.duels.duel;

import com.medievallords.carbyne.duels.arena.Arena;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xwiena22 on 2017-03-14.
 *
 */
public class DuelManager {

    private List<Duel> duels = new ArrayList<>();

    public void createDuel(String name, Location location) {
        if (getDuel(name) != null) {
            return;
        }


    }

    public Duel getDuel(String arenaName) {
        Arena arena = null;
        for (Arena arenas : Arena.getArenas()) {
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

    /*public Duel getDuel(UUID player) {
        for (Duel duel : duels) {
            if (duel.getAllPlayers().contains(player)) {
                return duel;
            }
        }
        return null;
    }*/

    /*public List<UUID> getTeam(UUID player) {
        Duel duel = getDuel(player);
        if (duel == null) {
            return null;
        }
        if (duel.getTeamOne().contains(player)) {
            return duel.getTeamOne();
        }
        if (duel.getTeamTwo().contains(player)) {
            return duel.getTeamTwo();
        }
        else {
            return null;
        }
    }*/
}
