package com.medievallords.carbyne.events.implementations.listeners;

import com.medievallords.carbyne.events.implementations.LastAlive;
import org.bukkit.event.Listener;

/**
 * Created by Dalton on 8/19/2017.
 */
public class LastAliveListeners implements Listener {

    private LastAlive lastAlive;

    public LastAliveListeners(LastAlive lastAlive) {
        this.lastAlive = lastAlive;
    }

}
