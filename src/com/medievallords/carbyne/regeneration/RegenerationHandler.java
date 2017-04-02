package com.medievallords.carbyne.regeneration;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.regeneration.tasks.DormantRegenerationTask;
import com.medievallords.carbyne.regeneration.tasks.RegenerationTask;
import com.medievallords.carbyne.utils.LocationSerialization;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.palmergames.bukkit.towny.object.WorldCoord;
import org.bson.Document;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Created by Calvin on 3/23/2017
 * for the Carbyne project.
 */
public class RegenerationHandler {

    private Carbyne main = Carbyne.getInstance();
    private MongoCollection<Document> activeTaskCollection = main.getMongoDatabase().getCollection("active-task");
    private MongoCollection<Document> pausedTaskCollection = main.getMongoDatabase().getCollection("paused-tasks");
    private MongoCollection<Document> dormantTaskCollection = main.getMongoDatabase().getCollection("dormant-tasks");
    private HashMap<WorldCoord, HashSet<RegenerationTask>> activeRegenerationTasks = new HashMap<>();
    private HashMap<WorldCoord, HashSet<RegenerationTask>> pausedRegenerationTasks = new HashMap<>();
    private HashMap<WorldCoord, HashSet<DormantRegenerationTask>> dormantRegenerationTasks = new HashMap<>();
    private HashSet<RegenerationData> replacements = new HashSet<>();
    private ArrayList<UUID> bypassers = new ArrayList<>();

    public RegenerationHandler() {
        for (Material type : Material.values()) {
            main.getLogger().log(Level.INFO, type.toString());
        }

        FileConfiguration configuration = main.getConfig();

        if (configuration.getStringList("regeneration").size() > 0) {
            for (String line : configuration.getStringList("regeneration")) {
                String[] args = line.split(",");

                if (args.length != 3) {
                    continue;
                }

                try {
                    replacements.add(new RegenerationData(Material.getMaterial(args[0].toUpperCase()), Material.getMaterial(args[1].toUpperCase()), args[2]));
                } catch (Exception e) {
                    main.getLogger().log(Level.SEVERE, "RegenerationData for string \"" + line + "\" could not be found.");
                }
            }
        }

        loadTasks();
    }

    @SuppressWarnings("unchecked")
    public void loadTasks() {
        long overallStartMillis = System.currentTimeMillis();
        int overallAmount = 0;

        if (activeTaskCollection.count() > 0) {
            main.getLogger().log(Level.INFO, "Preparing to load " + activeTaskCollection.count() + " Active WorldCoord documents.");

            long startMillis = System.currentTimeMillis();
            int amount = 0;

            for (Document worldCoordDoc : activeTaskCollection.find()) {
                int x = worldCoordDoc.getInteger("x");
                int z = worldCoordDoc.getInteger("z");
                ArrayList<Document> taskDocs = (ArrayList<Document>) worldCoordDoc.get("tasks");

                if (taskDocs.size() > 0) {
                    for (Document taskDoc : taskDocs) {
                        RegenerationType regenerationType = RegenerationType.valueOf(taskDoc.getString("regenerationType"));
                        Location blockLocation = LocationSerialization.deserializeLocation(taskDoc.getString("blockLocation"));
                        Document regenDataDoc = (Document) taskDoc.get("regenerationData");
                        RegenerationData regenerationData = new RegenerationData(Material.getMaterial(regenDataDoc.getString("previousMaterial")), Material.getMaterial(regenDataDoc.getString("newMaterial")), regenDataDoc.getString("remainingTimeString"));
                        WorldCoord worldCoord = WorldCoord.parseWorldCoord(blockLocation);
                        int blockData = taskDoc.getInteger("blockData");
                        int remainingTime = taskDoc.getInteger("remainingTime");
                        boolean active = taskDoc.getBoolean("active");
                        boolean paused = taskDoc.getBoolean("paused");

                        RegenerationTask regenerationTask = new RegenerationTask(this, regenerationType, blockLocation, regenerationData, worldCoord, (byte) blockData);
                        regenerationTask.setRemainingTime(remainingTime);
                        regenerationTask.setActive(active);
                        regenerationTask.setPaused(paused);

                        if (activeRegenerationTasks.containsKey(worldCoord)) {
                            activeRegenerationTasks.get(worldCoord).add(regenerationTask);
                        } else {
                            HashSet<RegenerationTask> taskHashSet = new HashSet<>();
                            taskHashSet.add(regenerationTask);

                            activeRegenerationTasks.put(worldCoord, taskHashSet);
                        }

                        regenerationTask.runTaskTimer(main, 0L, 20L);

                        amount++;
                        overallAmount++;
                    }
                } else {
                    activeTaskCollection.deleteOne(worldCoordDoc);
                }
            }

            main.getLogger().log(Level.INFO, "Successfully loaded " + activeRegenerationTasks.keySet().size() + " Active WorldCoords and " + amount + " Active Regeneration Tasks. Took (" + (System.currentTimeMillis() - startMillis) + "ms).");
        }

        if (pausedTaskCollection.count() > 0) {
            main.getLogger().log(Level.INFO, "Preparing to load " + pausedTaskCollection.count() + " Paused WorldCoord documents.");

            long startMillis = System.currentTimeMillis();
            int amount = 0;

            for (Document worldCoordDoc : pausedTaskCollection.find()) {
                int x = worldCoordDoc.getInteger("x");
                int z = worldCoordDoc.getInteger("z");
                ArrayList<Document> taskDocs = (ArrayList<Document>) worldCoordDoc.get("tasks");

                if (taskDocs.size() > 0) {
                    for (Document taskDoc : taskDocs) {
                        RegenerationType regenerationType = RegenerationType.valueOf(taskDoc.getString("regenerationType"));
                        Location blockLocation = LocationSerialization.deserializeLocation(taskDoc.getString("blockLocation"));
                        Document regenDataDoc = (Document) taskDoc.get("regenerationData");
                        RegenerationData regenerationData = new RegenerationData(Material.getMaterial(regenDataDoc.getString("previousMaterial")), Material.getMaterial(regenDataDoc.getString("newMaterial")), regenDataDoc.getString("remainingTimeString"));
                        WorldCoord worldCoord = WorldCoord.parseWorldCoord(blockLocation);
                        int blockData = taskDoc.getInteger("blockData");
                        int remainingTime = taskDoc.getInteger("remainingTime");
                        boolean active = taskDoc.getBoolean("active");
                        boolean paused = taskDoc.getBoolean("paused");

                        RegenerationTask regenerationTask = new RegenerationTask(this, regenerationType, blockLocation, regenerationData, worldCoord, (byte) blockData);
                        regenerationTask.setRemainingTime(remainingTime);
                        regenerationTask.setActive(active);
                        regenerationTask.setPaused(paused);

                        if (pausedRegenerationTasks.containsKey(worldCoord)) {
                            pausedRegenerationTasks.get(worldCoord).add(regenerationTask);
                        } else {
                            HashSet<RegenerationTask> taskHashSet = new HashSet<>();
                            taskHashSet.add(regenerationTask);

                            pausedRegenerationTasks.put(worldCoord, taskHashSet);
                        }

                        regenerationTask.runTaskTimer(main, 0L, 20L);

                        amount++;
                        overallAmount++;
                    }
                } else {
                    pausedTaskCollection.deleteOne(worldCoordDoc);
                }
            }

            main.getLogger().log(Level.INFO, "Successfully loaded " + pausedRegenerationTasks.keySet().size() + " Paused WorldCoords and " + amount + " Paused Regeneration Tasks. Took (" + (System.currentTimeMillis() - startMillis) + "ms).");
        }

        if (dormantTaskCollection.count() > 0) {
            main.getLogger().log(Level.INFO, "Preparing to load " + dormantTaskCollection.count() + " Dormant WorldCoord documents.");

            long startMillis = System.currentTimeMillis();
            int amount = 0;

            for (Document worldCoordDoc : dormantTaskCollection.find()) {
                int x = worldCoordDoc.getInteger("x");
                int z = worldCoordDoc.getInteger("z");
                ArrayList<Document> taskDocs = (ArrayList<Document>) worldCoordDoc.get("tasks");

                if (taskDocs.size() > 0) {
                    for (Document taskDoc : taskDocs) {
                        RegenerationType regenerationType = RegenerationType.valueOf(taskDoc.getString("regenerationType"));
                        Location blockLocation = LocationSerialization.deserializeLocation(taskDoc.getString("blockLocation"));
                        Document regenDataDoc = (Document) taskDoc.get("regenerationData");
                        RegenerationData regenerationData = new RegenerationData(Material.getMaterial(regenDataDoc.getString("previousMaterial")), Material.getMaterial(regenDataDoc.getString("newMaterial")), regenDataDoc.getString("remainingTimeString"));
                        WorldCoord worldCoord = WorldCoord.parseWorldCoord(blockLocation);
                        int blockData = taskDoc.getInteger("blockData");
                        int remainingTime = taskDoc.getInteger("remainingTime");
                        boolean active = taskDoc.getBoolean("active");
                        boolean paused = taskDoc.getBoolean("paused");

                        RegenerationTask regenerationTask = new RegenerationTask(this, regenerationType, blockLocation, regenerationData, worldCoord, (byte) blockData);
                        regenerationTask.setRemainingTime(remainingTime);
                        regenerationTask.setActive(active);
                        regenerationTask.setPaused(paused);

                        DormantRegenerationTask dormantRegenerationTask = new DormantRegenerationTask(this, regenerationTask);

                        dormantRegenerationTask.runTaskTimerAsynchronously(Carbyne.getInstance(), 0L, 5 * 20L);

                        if (!dormantRegenerationTasks.containsKey(regenerationTask.getWorldCoord())) {
                            HashSet<DormantRegenerationTask> tasks = new HashSet<>();
                            tasks.add(dormantRegenerationTask);

                            dormantRegenerationTasks.put(regenerationTask.getWorldCoord(), tasks);
                        } else {
                            dormantRegenerationTasks.get(regenerationTask.getWorldCoord()).add(dormantRegenerationTask);
                        }

                        amount++;
                        overallAmount++;
                    }
                } else {
                    dormantTaskCollection.deleteOne(worldCoordDoc);
                }
            }

            main.getLogger().log(Level.INFO, "Successfully loaded " + dormantRegenerationTasks.keySet().size() + " Dormant WorldCoords and " + amount + " Dormant Regeneration Tasks.  Took (" + (System.currentTimeMillis() - startMillis) + "ms).");
        }

        main.getLogger().log(Level.INFO, "Finished loading " + (activeRegenerationTasks.size() + pausedRegenerationTasks.size() + dormantRegenerationTasks.size()) + " WorldCoords and " + overallAmount + " Regeneration Tasks.  Took (" + (System.currentTimeMillis() - overallStartMillis) + "ms).");
    }

    public void saveTasks() {
        long overallStartMillis = System.currentTimeMillis();
        int overallAmount = 0;

        if (activeRegenerationTasks.size() > 0) {
            main.getLogger().log(Level.INFO, "Preparing to save " + activeRegenerationTasks.size() + " Active WorldCoords.");

            long startMillis = System.currentTimeMillis();
            int amount = 0;

            for (WorldCoord worldCoord : activeRegenerationTasks.keySet()) {
                if (activeRegenerationTasks.get(worldCoord).size() > 0) {
                    Document worldCoordDoc = new Document();
                    worldCoordDoc.put("x", worldCoord.getX());
                    worldCoordDoc.put("z", worldCoord.getZ());
                    ArrayList<Document> regenDocs = new ArrayList<>();

                    for (RegenerationTask regenerationTask : activeRegenerationTasks.get(worldCoord)) {
                        Document document = new Document();
                        document.put("regenerationType", regenerationTask.getRegenerationType().toString());
                        document.put("blockLocation", LocationSerialization.serializeLocation(regenerationTask.getBlockLocation()));
                        Document regenDataDoc = new Document();
                        regenDataDoc.put("previousMaterial", regenerationTask.getRegenerationData().getPreviousMaterial().toString());
                        regenDataDoc.put("newMaterial", regenerationTask.getRegenerationData().getNewMaterial().toString());
                        regenDataDoc.put("remainingTimeString", regenerationTask.getRegenerationData().getRegenerationTimeString());
                        document.put("regenerationData", regenDataDoc);
                        document.put("blockData", (int) regenerationTask.getBlockData());
                        document.put("remainingTime", regenerationTask.getRemainingTime());
                        document.put("active", regenerationTask.isActive());
                        document.put("paused", regenerationTask.isPaused());

                        regenDocs.add(document);

                        amount++;
                        overallAmount++;
                    }

                    worldCoordDoc.put("tasks", regenDocs);

                    activeTaskCollection.replaceOne(Filters.and(new Document("x", worldCoord.getX()), new Document("z", worldCoord.getZ())), worldCoordDoc, new UpdateOptions().upsert(true));
                }
            }

            main.getLogger().log(Level.INFO, "Successfully saved " + activeTaskCollection.count() + " Active WorldCoords and " + amount + " Active Regeneration Tasks. Took (" + (System.currentTimeMillis() - startMillis) + "ms).");
        }

        if (pausedRegenerationTasks.size() > 0) {
            main.getLogger().log(Level.INFO, "Preparing to save " + pausedRegenerationTasks.size() + " Paused WorldCoords.");

            long startMillis = System.currentTimeMillis();
            int amount = 0;

            for (WorldCoord worldCoord : pausedRegenerationTasks.keySet()) {
                if (pausedRegenerationTasks.get(worldCoord).size() > 0) {
                    Document worldCoordDoc = new Document();
                    worldCoordDoc.put("x", worldCoord.getX());
                    worldCoordDoc.put("z", worldCoord.getZ());
                    ArrayList<Document> regenDocs = new ArrayList<>();

                    for (RegenerationTask regenerationTask : pausedRegenerationTasks.get(worldCoord)) {
                        Document document = new Document();
                        document.put("regenerationType", regenerationTask.getRegenerationType().toString());
                        document.put("blockLocation", LocationSerialization.serializeLocation(regenerationTask.getBlockLocation()));
                        Document regenDataDoc = new Document();
                        regenDataDoc.put("previousMaterial", regenerationTask.getRegenerationData().getPreviousMaterial().toString());
                        regenDataDoc.put("newMaterial", regenerationTask.getRegenerationData().getNewMaterial().toString());
                        regenDataDoc.put("remainingTimeString", regenerationTask.getRegenerationData().getRegenerationTimeString());
                        document.put("regenerationData", regenDataDoc);
                        document.put("blockData", (int) regenerationTask.getBlockData());
                        document.put("remainingTime", regenerationTask.getRemainingTime());
                        document.put("active", regenerationTask.isActive());
                        document.put("paused", regenerationTask.isPaused());

                        regenDocs.add(document);

                        amount++;
                        overallAmount++;
                    }

                    worldCoordDoc.put("tasks", regenDocs);

                    pausedTaskCollection.replaceOne(Filters.and(new Document("x", worldCoord.getX()), new Document("z", worldCoord.getZ())), worldCoordDoc, new UpdateOptions().upsert(true));
                }
            }

            main.getLogger().log(Level.INFO, "Successfully saved " + pausedTaskCollection.count() + " Paused WorldCoords and " + amount + " Paused Regeneration Tasks. Took (" + (System.currentTimeMillis() - startMillis) + "ms).");
        }

        if (dormantRegenerationTasks.size() > 0) {
            main.getLogger().log(Level.INFO, "Preparing to save " + dormantRegenerationTasks.size() + " Dormant WorldCoords.");

            long startMillis = System.currentTimeMillis();
            int amount = 0;

            for (WorldCoord worldCoord : dormantRegenerationTasks.keySet()) {
                if (dormantRegenerationTasks.get(worldCoord).size() > 0) {
                    Document worldCoordDoc = new Document();
                    worldCoordDoc.put("x", worldCoord.getX());
                    worldCoordDoc.put("z", worldCoord.getZ());
                    ArrayList<Document> regenDocs = new ArrayList<>();

                    for (DormantRegenerationTask regenerationTask : dormantRegenerationTasks.get(worldCoord)) {
                        Document document = new Document();
                        document.put("regenerationType", regenerationTask.getRegenerationType().toString());
                        document.put("blockLocation", LocationSerialization.serializeLocation(regenerationTask.getBlockLocation()));
                        Document regenDataDoc = new Document();
                        regenDataDoc.put("previousMaterial", regenerationTask.getRegenerationData().getPreviousMaterial().toString());
                        regenDataDoc.put("newMaterial", regenerationTask.getRegenerationData().getNewMaterial().toString());
                        regenDataDoc.put("remainingTimeString", regenerationTask.getRegenerationData().getRegenerationTimeString());
                        document.put("regenerationData", regenDataDoc);
                        document.put("blockData", (int) regenerationTask.getBlockData());
                        document.put("remainingTime", regenerationTask.getRemainingTime());
                        document.put("active", regenerationTask.isActive());
                        document.put("paused", regenerationTask.isPaused());

                        regenDocs.add(document);

                        amount++;
                        overallAmount++;
                    }

                    worldCoordDoc.put("tasks", regenDocs);

                    dormantTaskCollection.replaceOne(Filters.and(new Document("x", worldCoord.getX()), new Document("z", worldCoord.getZ())), worldCoordDoc, new UpdateOptions().upsert(true));
                }
            }

            main.getLogger().log(Level.INFO, "Successfully saved " + dormantTaskCollection.count() + " Dormant WorldCoords and " + amount + " Dormant Regeneration Tasks. Took (" + (System.currentTimeMillis() - startMillis) + "ms).");
        }

        main.getLogger().log(Level.INFO, "Finished loading " + (activeTaskCollection.count() + pausedTaskCollection.count() + dormantTaskCollection.count()) + " WorldCoords and " + overallAmount + " Regeneration Tasks.  Took (" + (System.currentTimeMillis() - overallStartMillis) + "ms).");
    }

    public boolean request(Block block, RegenerationType regenerationType) {
        return request(block, regenerationType, "now");
    }

    public boolean request(Block block, RegenerationType regenerationType, String time) {
        if (alreadyScheduled(block.getLocation())) {
            return false;
        }

        RegenerationData regenerationData = null;

        try {
            if (getRegenerationData(block) != null)
                regenerationData = (RegenerationData) getRegenerationData(block).clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        if (regenerationData == null || regenerationData.getPreviousMaterial() == null || regenerationData.getNewMaterial() == null) {
            return false;
        }

        if (regenerationType == RegenerationType.PLACED) {
            regenerationData.setNewMaterial(Material.AIR);
        }

        if (!time.equalsIgnoreCase("now")) {
            regenerationData.setRegenerationTimeString(time);
        } else {
            regenerationData.setRegenerationTimeString(getRegenerationData(block).getRegenerationTimeString());
        }

        WorldCoord worldCoord = WorldCoord.parseWorldCoord(block);
//        Bukkit.broadcastMessage("RegenerationTask created at WorldCoord(X: " + worldCoord.getX() + ", Z: " + worldCoord.getZ() + ")");

        RegenerationTask regenerationTask = new RegenerationTask(this, regenerationType, block.getLocation(), regenerationData, worldCoord, block.getState().getData().getData());

        if (activeRegenerationTasks.containsKey(worldCoord)) {
            activeRegenerationTasks.get(worldCoord).add(regenerationTask);
        } else {
            HashSet<RegenerationTask> taskHashSet = new HashSet<>();
            taskHashSet.add(regenerationTask);

            activeRegenerationTasks.put(worldCoord, taskHashSet);
        }

        regenerationTask.runTaskTimer(main, 0L, 20L);

        return true;
    }

    public boolean alreadyScheduled(Location location) {
        for (WorldCoord worldCoord : activeRegenerationTasks.keySet()) {
            for (RegenerationTask regenerationTask : activeRegenerationTasks.get(worldCoord)) {
                if (regenerationTask.getBlockLocation().getX() == location.getX() && regenerationTask.getBlockLocation().getY() == location.getY() && regenerationTask.getBlockLocation().getZ() == location.getZ()) {
                    return true;
                }
            }
        }

        for (WorldCoord worldCoord : pausedRegenerationTasks.keySet()) {
            for (RegenerationTask regenerationTask : pausedRegenerationTasks.get(worldCoord)) {
                if (regenerationTask.getBlockLocation().getX() == location.getX() && regenerationTask.getBlockLocation().getY() == location.getY() && regenerationTask.getBlockLocation().getZ() == location.getZ()) {
                    return true;
                }
            }
        }

        return false;
    }

    public RegenerationData getRegenerationData(Block block) {
        for (RegenerationData regenerationData : replacements) {
            if (regenerationData.getPreviousMaterial() != null && regenerationData.getNewMaterial() != null) {
                if (regenerationData.getPreviousMaterial().equals(block.getType())) {
                    return regenerationData;
                }
            }
        }

        return null;
    }

    public boolean activeRegenerationTasksContainsTask(RegenerationTask regenerationTask) {
        for (WorldCoord worldCoord : activeRegenerationTasks.keySet()) {
            if (activeRegenerationTasks.get(worldCoord).contains(regenerationTask)) {
                return true;
            }
        }

        return false;
    }

    public boolean pausedRegenerationTasksContainsTask(RegenerationTask regenerationTask) {
        for (WorldCoord worldCoord : pausedRegenerationTasks.keySet()) {
            if (pausedRegenerationTasks.get(worldCoord).contains(regenerationTask)) {
                return true;
            }
        }

        return false;
    }

    public boolean dormantRegenerationTasksContainsTask(DormantRegenerationTask dormantRegenerationTask) {
        for (WorldCoord worldCoord : dormantRegenerationTasks.keySet()) {
            if (dormantRegenerationTasks.get(worldCoord).contains(dormantRegenerationTask)) {
                return true;
            }
        }

        return false;
    }

    public HashSet<RegenerationTask> getRegenerationTaskFromChunk(Chunk chunk) {
        WorldCoord worldCoord = WorldCoord.parseWorldCoord(chunk.getBlock(0, 0, 0).getLocation());
        HashSet<RegenerationTask> regenerationTasks = new HashSet<>();

        if (activeRegenerationTasks.containsKey(worldCoord)) {
            for (RegenerationTask regenerationTask : activeRegenerationTasks.get(worldCoord)) {
                regenerationTasks.add(regenerationTask);
            }
        } else if (pausedRegenerationTasks.containsKey(worldCoord)) {
            for (RegenerationTask regenerationTask : pausedRegenerationTasks.get(worldCoord)) {
                regenerationTasks.add(regenerationTask);
            }
        }

        return regenerationTasks;
    }

    public HashSet<DormantRegenerationTask> getDormantRegenerationTaskFromChunk(Chunk chunk) {
        WorldCoord worldCoord = WorldCoord.parseWorldCoord(chunk.getBlock(0, 0, 0).getLocation());
        HashSet<DormantRegenerationTask> regenerationTasks = new HashSet<>();

        if (dormantRegenerationTasks.containsKey(worldCoord)) {
            for (DormantRegenerationTask dormantRegenerationTask : dormantRegenerationTasks.get(worldCoord)) {
                regenerationTasks.add(dormantRegenerationTask);
            }
        }

        return regenerationTasks;
    }

    public int getNumActiveRegenerationTasks() {
        int amount = 0;

        for (WorldCoord worldCoord : activeRegenerationTasks.keySet()) {
            amount+=activeRegenerationTasks.get(worldCoord).size();
        }

        return amount;
    }

    public int getNumDormantRegenerationTasks() {
        int amount = 0;

        for (WorldCoord worldCoord : dormantRegenerationTasks.keySet()) {
            amount+=dormantRegenerationTasks.get(worldCoord).size();
        }

        return amount;
    }

    public HashMap<WorldCoord, HashSet<RegenerationTask>> getActiveRegenerationTasks() {
        return activeRegenerationTasks;
    }

    public HashMap<WorldCoord, HashSet<RegenerationTask>> getPausedRegenerationTasks() {
        return pausedRegenerationTasks;
    }

    public HashMap<WorldCoord, HashSet<DormantRegenerationTask>> getDormantRegenerationTasks() {
        return dormantRegenerationTasks;
    }

    public HashSet<RegenerationData> getReplacements() {
        return replacements;
    }

    public ArrayList<UUID> getBypassers() {
        return bypassers;
    }
}
