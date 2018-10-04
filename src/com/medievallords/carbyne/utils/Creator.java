package com.medievallords.carbyne.utils;

import com.boydti.fawe.bukkit.wrapper.AsyncWorld;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.craftbukkit.v1_12_R1.CraftServer;

import java.io.File;
import java.util.logging.Level;

/**
 * Created by WE on 2017-09-30.
 */

public class Creator {

    private CraftServer server = ((CraftServer) Bukkit.getServer());

    public World createWorld(World template, int id) {
        long current = System.currentTimeMillis();
        template.save();
        WorldCreator creator = new WorldCreator("dungeonInstance_" + id + "_" + template.getName() + "_" + System.currentTimeMillis());
        WorldLoader.copyWorld(template.getWorldFolder(), new File(server.getWorldContainer(), creator.name()));
        AsyncWorld world = AsyncWorld.create(creator);
        world.commit();
        Bukkit.getLogger().log(Level.INFO, "Dungeon world loaded in: " + (System.currentTimeMillis() - current) + " ms");
        return world.getBukkitWorld();
        //final CGWorld world = createWorld(creator);
        //plugin.getServer().unloadWorld(world[0], true);
        //Bukkit.broadcastMessage(System.currentTimeMillis() - current + " ms");
        //return world;
    }
}
