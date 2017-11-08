package com.medievallords.carbyne.gates.listeners;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.gates.Gate;
import com.medievallords.carbyne.gates.GateManager;
import net.elseland.xikage.MythicMobs.MythicMobs;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Created by Calvin on 1/30/2017
 * for the Carbyne-Gear project.
 */
public class GateListeners implements Listener {

    private Carbyne carbyne = Carbyne.getInstance();
    private GateManager gateManager = carbyne.getGateManager();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.PHYSICAL) {
            Block block = event.getClickedBlock();
            Gate gate = gateManager.getGate(block.getLocation());

            if (gate == null) {
                return;
            }

            if (gate.getPressurePlateMap().containsKey(block.getLocation())) {
                if (!gate.getPressurePlateMap().get(block.getLocation())) {
                    gate.pressurePlateActivated(block.getLocation(), true);
                }
            }
        }
    }

    @EventHandler
    public void onRedstone(BlockRedstoneEvent event) {
        Block block = event.getBlock();
        Gate gate = gateManager.getGate(block.getLocation());

        if (gate == null) {
            return;
        }

        if (gate.getPressurePlateMap().containsKey(block.getLocation())) {
            if (gate.getPressurePlateMap().get(block.getLocation()) && event.getOldCurrent() > 0) {
                gate.pressurePlateActivated(block.getLocation(), false);
            }
        }

        if (event.getOldCurrent() > 0) {
            if (gate.getButtonLocations().contains(block.getLocation())) {
                gate.buttonActivated(block.getLocation());
            }
        }
    }
}
