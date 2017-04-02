package com.medievallords.carbyne.regeneration.tasks;

import com.medievallords.carbyne.regeneration.RegenerationData;
import com.medievallords.carbyne.regeneration.RegenerationHandler;
import com.medievallords.carbyne.regeneration.RegenerationType;
import com.medievallords.carbyne.utils.DateUtil;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.WorldCoord;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;

/**
 * Created by Calvin on 3/24/2017
 * for the Carbyne project.
 */
@Getter
@Setter
public class RegenerationTask extends BukkitRunnable {

    private RegenerationHandler regenerationHandler;

    private RegenerationType regenerationType;
    private Location blockLocation;
    private RegenerationData regenerationData;
    private WorldCoord worldCoord;
    private byte blockData;
    private int remainingTime;
    private boolean active;
    private boolean paused = false;

    public RegenerationTask(RegenerationHandler regenerationHandler, RegenerationType regenerationType, Location blockLocation, RegenerationData regenerationData, WorldCoord worldCoord, byte blockData) {
        this.regenerationHandler = regenerationHandler;
        this.regenerationType = regenerationType;
        this.blockLocation = blockLocation;
        this.regenerationData = regenerationData;
        this.worldCoord = worldCoord;
        this.blockData = blockData;

        try {
            this.remainingTime = (int) ((DateUtil.parseDateDiff(regenerationData.getRegenerationTimeString(), true) - System.currentTimeMillis()) / 1000);
            this.active = (remainingTime <= 0);
        } catch (Exception e) {
            this.cancel();
            e.printStackTrace();
        }
    }

    public RegenerationTask(RegenerationHandler regenerationHandler, DormantRegenerationTask dormantRegenerationTask) {
        this.regenerationHandler = regenerationHandler;
        this.regenerationType = dormantRegenerationTask.getRegenerationType();
        this.blockLocation = dormantRegenerationTask.getBlockLocation();
        this.regenerationData = dormantRegenerationTask.getRegenerationData();
        this.worldCoord = dormantRegenerationTask.getWorldCoord();
        this.blockData = dormantRegenerationTask.getBlockData();
        this.remainingTime = dormantRegenerationTask.getRemainingTime();
        this.active = dormantRegenerationTask.isActive();
        this.paused = dormantRegenerationTask.isPaused();
    }

    @Override
    public void run() {
        TownBlock townBlock = null;
        Town town = null;

        try {
            townBlock = worldCoord.getTownBlock();

            if (townBlock != null) {
                town = townBlock.getTown();
            }
        } catch (NotRegisteredException ignored) {}

//        Bukkit.broadcastMessage("Active RegenerationTask:\n" +
//                "Should be active: " + (remainingTime <= 0 && !paused) + "(" + active + ") (" + remainingTime + ")\n" +
//                "Paused: " + paused + "\n" +
//                "Active Contains: " + regenerationHandler.activeRegenerationTasksContainsTask(this) + "\n" +
//                "Paused Contains: " + regenerationHandler.pausedRegenerationTasksContainsTask(this) + "\n" +
//                "" + (worldCoord != null ? "WorldCoord(X: " + worldCoord.getX() + ", Z: " + worldCoord.getZ() + "), " : "Null") + ("TownBlock(" + (townBlock != null ? "X: " + townBlock.getWorldCoord().getX() + ", Z: " + townBlock.getWorldCoord().getZ() : "Null") + ")"));

        if (townBlock != null && town != null && remainingTime <= 0) {
            this.active = false;

            if (!paused) {
                this.paused = true;

                if (worldCoord != null) {
                    if (regenerationHandler.activeRegenerationTasksContainsTask(this)) {
                        if (regenerationHandler.getActiveRegenerationTasks().get(worldCoord).size() >= 1) {
                            regenerationHandler.getActiveRegenerationTasks().get(worldCoord).remove(this);
                        } else {
                            regenerationHandler.getActiveRegenerationTasks().remove(worldCoord);
                        }
                    }

                    if (!regenerationHandler.pausedRegenerationTasksContainsTask(this)) {
                        if (!regenerationHandler.getPausedRegenerationTasks().containsKey(worldCoord)) {
                            HashSet<RegenerationTask> tasks = new HashSet<>();
                            tasks.add(this);

                            regenerationHandler.getPausedRegenerationTasks().put(worldCoord, tasks);
                        } else {
                            regenerationHandler.getPausedRegenerationTasks().get(worldCoord).add(this);
                        }
                    }
                }
            }
        } else {
            if (paused) {
                this.paused = false;

                if (worldCoord != null) {
                    if (regenerationHandler.pausedRegenerationTasksContainsTask(this)) {
                        if (regenerationHandler.getPausedRegenerationTasks().get(worldCoord).size() >= 1) {
                            regenerationHandler.getPausedRegenerationTasks().get(worldCoord).remove(this);
                        } else {
                            regenerationHandler.getPausedRegenerationTasks().remove(worldCoord);
                        }
                    }

                    if (!regenerationHandler.activeRegenerationTasksContainsTask(this)) {
                        if (!regenerationHandler.getActiveRegenerationTasks().containsKey(worldCoord)) {
                            HashSet<RegenerationTask> tasks = new HashSet<>();
                            tasks.add(this);

                            regenerationHandler.getActiveRegenerationTasks().put(worldCoord, tasks);
                        } else {
                            regenerationHandler.getActiveRegenerationTasks().get(worldCoord).add(this);
                        }
                    }
                }
            }
        }

        if (remainingTime > 0) {
            remainingTime--;
        } else {
            if (paused) {
                try {
                    this.remainingTime = (int) ((DateUtil.parseDateDiff(regenerationData.getRegenerationTimeString(), true) - System.currentTimeMillis()) / 1000);
                    return;
                } catch (Exception e) {
                    this.cancel();
                    e.printStackTrace();
                }
            }

            this.active = true;
        }

        if (active) {
            blockLocation.getBlock().setType(regenerationData.getNewMaterial());
            blockLocation.getBlock().setData(blockData);

            finish();
        }
    }

    @Override
    public void cancel() {
        super.cancel();
    }

    public void finish() {
        if (worldCoord != null) {
            if (regenerationHandler.activeRegenerationTasksContainsTask(this)) {
                if (regenerationHandler.getActiveRegenerationTasks().get(worldCoord).size() >= 1) {
                    regenerationHandler.getActiveRegenerationTasks().get(worldCoord).remove(this);
                } else {
                    regenerationHandler.getActiveRegenerationTasks().remove(worldCoord);
                }
            }

            if (regenerationHandler.pausedRegenerationTasksContainsTask(this)) {
                if (regenerationHandler.getPausedRegenerationTasks().get(worldCoord).size() >= 1) {
                    regenerationHandler.getPausedRegenerationTasks().get(worldCoord).remove(this);
                } else {
                    regenerationHandler.getPausedRegenerationTasks().remove(worldCoord);
                }
            }
        }

        this.cancel();
    }
}
