package com.medievallords.carbyne.duels.arena.listeners;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.duels.arena.Arena;
import com.medievallords.carbyne.duels.duel.DuelManager;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Calvin on 4/2/2017
 * for the Carbyne project.
 */
public class ArenaListeners implements Listener {

    private Carbyne carbyne = Carbyne.getInstance();
    private DuelManager duelManager = carbyne.getDuelManager();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.PHYSICAL) {
            Block block = event.getClickedBlock();
            Arena arena = duelManager.getArena(block.getLocation());

            if (arena == null) {
                return;
            }

            List<Location> pedastoolLocations = Arrays.asList(arena.getPedastoolLocations());

            if (pedastoolLocations.contains(block.getLocation())) {
                if (!arena.getActivePedastoolLocations().containsKey(block.getLocation())) {
                    arena.activatePedastool(block.getLocation(), true);
                }
            }
        }
    }

    @EventHandler
    public void onRedstone(BlockRedstoneEvent event) {
        Block block = event.getBlock();
        Arena arena = duelManager.getArena(block.getLocation());

        if (arena == null) {
            return;
        }

        List<Location> pedastoolLocations = Arrays.asList(arena.getPedastoolLocations());

        if (pedastoolLocations.contains(block.getLocation())) {
            if (arena.getActivePedastoolLocations().containsKey(block.getLocation())) {
                arena.activatePedastool(block.getLocation(), false);
            }
        }
    }
}
