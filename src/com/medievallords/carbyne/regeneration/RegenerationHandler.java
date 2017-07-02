package com.medievallords.carbyne.regeneration;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.utils.DateUtil;
import com.medievallords.carbyne.utils.LocationSerialization;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.WorldCoord;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Created by Calvin on 3/23/2017
 * for the Carbyne project.
 * Dalton was here on 5/30/2017.
 */
public class RegenerationHandler {

    private Carbyne main = Carbyne.getInstance();
    private MongoCollection<Document> taskCollection = main.getMongoDatabase().getCollection("regen-tasks");
    @Getter
    private List<UUID> bypassers = new ArrayList<>();
    private HashMap<Material, Material> regenIntoData = new HashMap<>();
    private HashMap<Material, String> timeData = new HashMap<>();

    /**
     * Init variables. Load information from regeneration config. Log if any errors occur.
     */
    public RegenerationHandler() {
        load();
    }

    public void reload()
    {
        bypassers = new ArrayList<>();
        regenIntoData = new HashMap<>();
        timeData = new HashMap<>();
        load();
    }

    public void load()
    {
        FileConfiguration configuration = main.getConfig();

        if (configuration.getStringList("regeneration").size() > 0)
            for (String line : configuration.getStringList("regeneration")) {
                String[] args = line.split(",");
                if (args.length != 3) continue;

                try {
                    Material oldMat = Material.getMaterial(args[0].toUpperCase()), newMat = Material.getMaterial(args[1].toUpperCase());
                    String time = args[2];
                    regenIntoData.put(oldMat, newMat);
                    timeData.put(oldMat, time);
                } catch (Exception e) {
                    main.getLogger().log(Level.SEVERE, "RegenerationData for string\"" + line + "\" could not be found.");
                }
            }
    }

    /**
     * Requests block to regenerate. Checks if the location has regeneration data ? returns if so : processes data.
     *
     * @param block Block to regenerate and parse information from. PRECONDITION: The blocks location is in the player world.
     * @param type  NOT NULL RegenerationType enum.
     */
    public void request(Block block, RegenerationType type) {
        if(!timeData.containsKey(block.getType())) return;
        WorldCoord wc = WorldCoord.parseWorldCoord(block);
        if (!isLocationEmpty(wc, block.getLocation())) return;
        storeRegenerationInformation(wc, block, type);
    }

    /**
     * This method checks all of the data in a WorldCoord to see if it is time to regenerate. If it is time to regenerate,
     * the regeneration will be executed and the document will be removed from the list of documents. The list of documents
     * is only updated in the database when a change is made.
     *
     * @param wc WorldCoord to check and execute data from. PRECONDITION: The world of the WorldCoord is the player world.
     */
    public void checkAndAct(WorldCoord wc) {
        try {
            if (wc.getTownBlock().hasTown()) return;
        } catch (NotRegisteredException ignore) {
        }

        Document wcDoc = taskCollection.find(Filters.and(new Document("x", wc.getX()), new Document("z", wc.getZ()))).first();
        if (wcDoc == null) return;

        final ArrayList<Document> data = (ArrayList<Document>) wcDoc.get("data");
        for (int i = 0; i < data.size(); i++) {
            Document aData = data.get(i);
            new BukkitRunnable() {
                public void run() {
                    Material mat = Material.getMaterial(aData.getString("material").toUpperCase());
                    long last = aData.getLong("time");
                    if (last < System.currentTimeMillis()) {
                        main.getServer().getScheduler().runTask(main, () -> {
                            execute(aData, mat);
                            data.remove(aData);

                            wcDoc.put("data", data);

                            taskCollection.replaceOne(Filters.and(new Document("x", wc.getX()), new Document("z", wc.getZ())), wcDoc, new UpdateOptions().upsert(true));
                        });
                    }
                }
            }.runTaskAsynchronously(main);
        }
    }

    /**
     * Method that regenerates a block. Does not remove data from the database. That is done in checkAndAct.
     *
     * @param information Block regeneration information from the database.
     * @param regenMat    Material that was originally broken or placed. Method can be rewritten to exclude this as it
     *                    is part of the information document but it is already loaded in checkAndExecute.
     */
    private void execute(Document information, Material regenMat) {
        RegenerationType regenType = RegenerationType.valueOf(information.get("regenerationType", String.class));
        if (regenType.equals(RegenerationType.BROKEN))
            regenMat = regenIntoData.get(regenMat);
        else if (regenType.equals(RegenerationType.PLACED))
            regenMat = Material.AIR;
        Location blockLocation = LocationSerialization.deserializeLocation(information.get("location", String.class));
        blockLocation.getBlock().setType(regenMat);
    }

    /**
     * Store the information for block regeneration so it can be pulled from the database later.
     *
     * @param wc    WorldCoord that contains the block location. Used as a key for the regeneration data in the WorldCoord.
     * @param block The block broken or placed to store for regeneration.
     * @param type  Type of Regeneration from the RegenerationType enum.
     */
    @SuppressWarnings("unchecked")
    private void storeRegenerationInformation(WorldCoord wc, Block block, RegenerationType type) {
        Document wcDoc = taskCollection.find(Filters.and(new Document("x", wc.getX()), new Document("z", wc.getZ()))).first();
        ArrayList<Document> data;

        if (wcDoc == null) {
            wcDoc = new Document();
            wcDoc.put("x", wc.getX());
            wcDoc.put("z", wc.getZ());
            data = new ArrayList<>();
        } else data = (ArrayList<Document>) wcDoc.get("data");

        Document regenInfo = new Document();
        regenInfo.put("location", LocationSerialization.serializeLocation(block.getLocation()));
        regenInfo.put("material", block.getType().toString().toUpperCase());
        regenInfo.put("regenerationType", type.name());
        try {
            regenInfo.put("time", DateUtil.parseDateDiff(timeData.get(block.getType()), true));
        } catch (Exception ex) {}

        data.add(regenInfo);
        wcDoc.put("data", data);
        taskCollection.replaceOne(Filters.and(new Document("x", wc.getX()), new Document("z", wc.getZ())), wcDoc, new UpdateOptions().upsert(true));
    }

    /**
     * Checks if a location has regeneration data present.
     *
     * @param wc       WorldCoord of location, used to search database for location.
     * @param location The location to check in the WorldCoord key Arraylist of documents which contains locations.
     * @return true if a location matches the location provided, false otherwise.
     */
    public boolean isLocationEmpty(WorldCoord wc, Location location) {
        Document wcDoc = taskCollection.find(Filters.and(new Document("x", wc.getX()), new Document("z", wc.getZ()))).first();
        if (wcDoc == null) return true;

        ArrayList<Document> regenData = (ArrayList<Document>) wcDoc.get("data");
        String data = LocationSerialization.serializeLocation(location);

        for (Document aRegenData : regenData) if (data.equals(aRegenData.get("location", String.class))) return false;

        return true;
    }

}
