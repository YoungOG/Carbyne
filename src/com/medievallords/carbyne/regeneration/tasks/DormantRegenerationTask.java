package com.medievallords.carbyne.regeneration.tasks;

import com.medievallords.carbyne.regeneration.RegenerationData;
import com.medievallords.carbyne.regeneration.RegenerationHandler;
import com.medievallords.carbyne.regeneration.RegenerationType;
import com.medievallords.carbyne.utils.DateUtil;
import com.palmergames.bukkit.towny.object.WorldCoord;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Calvin on 3/26/2017
 * for the Carbyne project.
 */
@Getter
@Setter
public class DormantRegenerationTask extends BukkitRunnable {

    private RegenerationHandler regenerationHandler;

    private RegenerationType regenerationType;
    private Location blockLocation;
    private RegenerationData regenerationData;
    private WorldCoord worldCoord;
    private byte blockData;
    private int remainingTime;
    private boolean active;
    private boolean paused = false;

    public DormantRegenerationTask(RegenerationHandler regenerationHandler, RegenerationTask regenerationTask) {
        this.regenerationHandler = regenerationHandler;
        this.regenerationType = regenerationTask.getRegenerationType();
        this.blockLocation = regenerationTask.getBlockLocation();
        this.regenerationData = regenerationTask.getRegenerationData();
        this.worldCoord = regenerationTask.getWorldCoord();
        this.blockData = regenerationTask.getBlockData();
        this.remainingTime = regenerationTask.getRemainingTime();
        this.active = regenerationTask.isActive();
        this.paused = regenerationTask.isPaused();
    }

    @Override
    public void run() {
//        Bukkit.broadcastMessage("Dormant RegenerationTask:\n" +
//                "RemainingTime: " + remainingTime + "\n" +
//                "Active Contains: " + regenerationHandler.dormantRegenerationTasksContainsTask(this));
//

        if (remainingTime > 0) {
            remainingTime-=5;
        } else {
            try {
                this.remainingTime = (int) ((DateUtil.parseDateDiff(regenerationData.getRegenerationTimeString(), true) - System.currentTimeMillis()) / 1000);
            } catch (Exception e) {
                this.cancel();
                e.printStackTrace();
            }
        }
    }

    @Override
    public void cancel() {
        super.cancel();
    }
}