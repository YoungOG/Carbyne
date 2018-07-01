package com.medievallords.carbyne.heartbeat;

import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Calvin on 2/3/2017
 * for the Carbyne-Gear project.
 */
public class HeartbeatRunnable extends BukkitRunnable {

    int current = 0;

    @Override
    public void run() {
        switch (++current) {
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
            case 5:
                break;
            default:
                current = 0;
                break;
        }
    }
}
