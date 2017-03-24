package com.medievallords.carbyne.regeneration;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Calvin on 3/24/2017
 * for the Carbyne project.
 */
@Getter
@Setter
public class BlockRegenerationTask extends BukkitRunnable {

    private RegenerationHandler regenerationHandler;

    private BlockRegenerationType blockRegenerationType;
    private Location blockLocation;
    private BlockRegenerationData blockRegenerationData;
    private byte blockData;
    private int remainingTime;
    private boolean hasRun = false;

    public BlockRegenerationTask(RegenerationHandler regenerationHandler, BlockRegenerationType blockRegenerationType, Location blockLocation, BlockRegenerationData blockRegenerationData, byte blockData, int remainingTime) {
        this.regenerationHandler = regenerationHandler;
        this.blockRegenerationType = blockRegenerationType;
        this.blockLocation = blockLocation;
        this.blockRegenerationData = blockRegenerationData;
        this.blockData = blockData;
        this.remainingTime = remainingTime;
    }

    @Override
    public void run() {
        blockLocation.getBlock().setType(blockRegenerationData.getNewMaterial());
        blockLocation.getBlock().setData(blockData);

        finish();
    }

    @Override
    public void cancel() {
        if (!hasRun) {
            run();
        }

        super.cancel();
    }

    public void finish() {
        this.hasRun = true;

        if (regenerationHandler.getRegenerationTasks().contains(this)) {
            regenerationHandler.getRegenerationTasks().remove(this);
        }

//        if (tasks.contains(this)) {
//            tasks.remove(this);
//        }
    }
}
