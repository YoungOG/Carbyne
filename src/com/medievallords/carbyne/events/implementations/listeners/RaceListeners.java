package com.medievallords.carbyne.events.implementations.listeners;

import com.medievallords.carbyne.events.implementations.Race;
import com.medievallords.carbyne.utils.MessageManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Created by Dalton on 7/10/2017.
 */
public class RaceListeners implements Listener {

    private Race race;

    public RaceListeners(Race race) {
        this.race = race;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.PHYSICAL) {
            if (event.getClickedBlock().getLocation().equals(race.getCurrentRace().getWinningLocation())) {
                if (race.getParticipants().contains(event.getPlayer()) && race.getWinner() == null && race.isAfterCountdown()) {
                    String msg = new String("&2You have cleared the %race%!").replace("%race%", race.getCurrentRace().getName());
                    MessageManager.sendMessage(event.getPlayer(), msg);
                    race.setWinner(event.getPlayer());
                }
            }
        }
    }

}
