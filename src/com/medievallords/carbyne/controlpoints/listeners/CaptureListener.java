package com.medievallords.carbyne.controlpoints.listeners;


import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.controlpoints.ControlPoint;
import com.medievallords.carbyne.controlpoints.ControlPointManager;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Created by Williams on 2017-06-11
 * for the Carbyne project.
 */
public class CaptureListener implements Listener{

    private Carbyne main = Carbyne.getInstance();
    private ControlPointManager manager = main.getControlPointManager();

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();

            for (ControlPoint controlPoint : manager.controlPoints) {
                if (controlPoint.getLocation().equals(block.getLocation())) {
                    controlPoint.startCountdown(event.getPlayer().getUniqueId());
                    break;
                }
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        for (ControlPoint controlPoint : manager.controlPoints) {
            if (controlPoint.getCapper() == (event.getPlayer().getUniqueId())) {
                controlPoint.stopTimer();
                break;
            }
        }
    }
}
