package com.medievallords.carbyne.regeneration;

import com.medievallords.carbyne.Carbyne;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashSet;
import java.util.logging.Level;

/**
 * Created by Calvin on 3/23/2017
 * for the Carbyne project.
 */
public class RegenerationHandler {

    private Carbyne main = Carbyne.getInstance();
    private HashSet<BlockRegenerationTask> regenerationTasks = new HashSet<>();
    private HashSet<BlockRegenerationData> replacements = new HashSet<>();

    public RegenerationHandler() {
        FileConfiguration configuration = main.getConfig();

        if (configuration.getStringList("regeneration").size() > 0) {
            for (String line : configuration.getStringList("regeneration")) {
                String[] args = line.toUpperCase().split(",");

                if (args.length != 3) {
                    continue;
                }

                try {
                    replacements.add(new BlockRegenerationData(Material.getMaterial(args[0]), Material.getMaterial(args[1]), Integer.parseInt(args[2])));
                } catch (Exception e) {
                    main.getLogger().log(Level.SEVERE, "RegenerationData for string \"" + line + "\" could not be found.");
                }
            }
        }
    }

    public void loadTasks() {

    }

    public void saveTasks() {

    }

    public boolean request(Block block, BlockRegenerationType blockRegenerationType) {
        if (alreadyScheduled(block.getLocation())) {
            return false;
        }

        BlockRegenerationData blockRegenerationData = null;

        try {
            blockRegenerationData = (BlockRegenerationData) getRegenerationData(block).clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        if (blockRegenerationData == null || blockRegenerationData.getPreviousMaterial() == null || blockRegenerationData.getNewMaterial() == null) {
            return false;
        }

        if (blockRegenerationType == BlockRegenerationType.PLACED) {
            blockRegenerationData.setNewMaterial(Material.AIR);
        }

        BlockRegenerationTask regenerationTask = new BlockRegenerationTask(this, blockRegenerationType, block.getLocation(), blockRegenerationData, block.getState().getData().getData(), blockRegenerationData.getRegenerationTime());
        regenerationTask.runTaskLater(main, blockRegenerationData.getRegenerationTime() * 20L);

        Bukkit.broadcastMessage("RegenerationTask[Type(" + blockRegenerationType.toString() + "), Location(" + block.getLocation().getWorld() + "," + block.getLocation().getBlockX() + "," + block.getLocation().getBlockY() + "," + block.getLocation().getBlockZ() + ")" + ", BlockRegenData(" + blockRegenerationData.getPreviousMaterial() + "," + blockRegenerationData.getNewMaterial() + "," + blockRegenerationData.getRegenerationTime() + "), BlockData(" + block.getState().getData().getData() + ")");

        return regenerationTasks.add(regenerationTask);
    }

    public boolean request(Block block, BlockRegenerationType blockRegenerationType, int time) {
        if (alreadyScheduled(block.getLocation())) {
            return false;
        }

        BlockRegenerationData blockRegenerationData = null;

        try {
            blockRegenerationData = (BlockRegenerationData) getRegenerationData(block).clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        if (blockRegenerationData == null || blockRegenerationData.getPreviousMaterial() == null || blockRegenerationData.getNewMaterial() == null) {
            return false;
        }

        if (blockRegenerationType == BlockRegenerationType.PLACED) {
            blockRegenerationData.setNewMaterial(Material.AIR);
        }

        blockRegenerationData.setRegenerationTime(time);

        BlockRegenerationTask regenerationTask = new BlockRegenerationTask(this, blockRegenerationType, block.getLocation(), blockRegenerationData, block.getState().getData().getData(), blockRegenerationData.getRegenerationTime());
        regenerationTask.runTaskLater(main, blockRegenerationData.getRegenerationTime() * 20L);

        Bukkit.broadcastMessage("RegenerationTask[Type(" + blockRegenerationType.toString() + "), Location(" + block.getLocation().getWorld() + "," + block.getLocation().getBlockX() + "," + block.getLocation().getBlockY() + "," + block.getLocation().getBlockZ() + ")" + ", BlockRegenData(" + blockRegenerationData.getPreviousMaterial() + "," + blockRegenerationData.getNewMaterial() + "," + blockRegenerationData.getRegenerationTime() + "), BlockData(" + block.getState().getData().getData() + ")");

        return regenerationTasks.add(regenerationTask);
    }

    public boolean alreadyScheduled(Location location) {
        for (BlockRegenerationTask regenerationTask : regenerationTasks) {
             if (regenerationTask.getBlockLocation().equals(location)) {
                 return true;
             }
        }

        return false;
    }

    public BlockRegenerationData getRegenerationData(Block block) {
        for (BlockRegenerationData blockRegenerationData : replacements) {
            if (blockRegenerationData.getPreviousMaterial() != null && blockRegenerationData.getNewMaterial() != null) {
                if (blockRegenerationData.getPreviousMaterial().equals(block.getType())) {
                    return blockRegenerationData;
                }
            }
        }

        return null;
    }

    public HashSet<BlockRegenerationTask> getRegenerationTasks() {
        return regenerationTasks;
    }

    public HashSet<BlockRegenerationData> getReplacements() {
        return replacements;
    }
}
