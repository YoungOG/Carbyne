package com.medievallords.carbyne.regeneration;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.regeneration.tasks.DormantRegenerationTask;
import com.medievallords.carbyne.regeneration.tasks.RegenerationTask;
import com.palmergames.bukkit.towny.object.WorldCoord;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.material.MaterialData;
import org.bukkit.material.PistonBaseMaterial;

import java.util.HashSet;

/**
 * Created by Calvin on 3/24/2017
 * for the Carbyne project.
 */
public class RegenerationListeners implements Listener {

    private Carbyne main = Carbyne.getInstance();
    private RegenerationHandler regenerationHandler = main.getRegenerationHandler();

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (block.getWorld().getName().equalsIgnoreCase("player_world") && !regenerationHandler.getBypassers().contains(player.getUniqueId())) {
            regenerationHandler.request(block, RegenerationType.BROKEN);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (block.getWorld().getName().equalsIgnoreCase("player_world") && !regenerationHandler.getBypassers().contains(player.getUniqueId())) {
            regenerationHandler.request(block, RegenerationType.PLACED);
        }
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        Chunk chunk = event.getChunk();

        if (event.getWorld().getName().equals("player_world")) {
            if (regenerationHandler.getRegenerationTaskFromChunk(chunk).size() > 0) {
//                Bukkit.broadcastMessage("Chunk(X: " + chunk.getX() + ", Z: " + chunk.getZ() + ", RegenerationTasks: " + regenerationHandler.getRegenerationTaskFromChunk(chunk).size() + ") unloaded. ActiveTasks: " + regenerationHandler.getNumActiveRegenerationTasks() + ", ActiveWorldCoords: " + regenerationHandler.getActiveRegenerationTasks().keySet().size() + ", DormantTasks: " + regenerationHandler.getNumDormantRegenerationTasks() + ", DormantWorldCoords: " + regenerationHandler.getDormantRegenerationTasks().keySet().size());

                for (RegenerationTask regenerationTask : regenerationHandler.getRegenerationTaskFromChunk(chunk)) {
                    if (regenerationHandler.activeRegenerationTasksContainsTask(regenerationTask)) {
                        if (regenerationHandler.getActiveRegenerationTasks().size() >= 1) {
                            regenerationHandler.getActiveRegenerationTasks().get(regenerationTask.getWorldCoord()).remove(regenerationTask);
                        } else {
                            regenerationHandler.getActiveRegenerationTasks().remove(regenerationTask.getWorldCoord());
                        }
                    }

                    if (regenerationHandler.pausedRegenerationTasksContainsTask(regenerationTask)) {
                        if (regenerationHandler.getPausedRegenerationTasks().size() >= 1) {
                            regenerationHandler.getPausedRegenerationTasks().get(regenerationTask.getWorldCoord()).remove(regenerationTask);
                        } else {
                            regenerationHandler.getPausedRegenerationTasks().remove(regenerationTask.getWorldCoord());
                        }
                    }

                    regenerationTask.cancel();

                    DormantRegenerationTask dormantRegenerationTask = new DormantRegenerationTask(regenerationHandler, regenerationTask);
                    dormantRegenerationTask.runTaskTimerAsynchronously(Carbyne.getInstance(), 0L, 5 * 20L);

                    if (!regenerationHandler.dormantRegenerationTasksContainsTask(dormantRegenerationTask)) {
                        if (!regenerationHandler.getDormantRegenerationTasks().containsKey(regenerationTask.getWorldCoord())) {
                            HashSet<DormantRegenerationTask> tasks = new HashSet<>();
                            tasks.add(dormantRegenerationTask);

                            regenerationHandler.getDormantRegenerationTasks().put(regenerationTask.getWorldCoord(), tasks);
                        } else {
                            regenerationHandler.getDormantRegenerationTasks().get(regenerationTask.getWorldCoord()).add(dormantRegenerationTask);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        Chunk chunk = event.getChunk();

        if (event.getWorld().getName().equals("player_world")) {
            if (regenerationHandler.getDormantRegenerationTaskFromChunk(chunk).size() > 0) {
//                Bukkit.broadcastMessage("Chunk(X: " + chunk.getX() + ", Z: " + chunk.getZ() + ", RegenerationTasks: " + regenerationHandler.getDormantRegenerationTaskFromChunk(chunk).size() + ") loaded. ActiveTasks: " + regenerationHandler.getNumActiveRegenerationTasks() + ", ActiveWorldCoords: " + regenerationHandler.getActiveRegenerationTasks().keySet().size() + ", DormantTasks: " + regenerationHandler.getNumDormantRegenerationTasks() + ", DormantWorldCoords: " + regenerationHandler.getDormantRegenerationTasks().keySet().size());

                for (DormantRegenerationTask dormantRegenerationTask : regenerationHandler.getDormantRegenerationTaskFromChunk(chunk)) {
                    if (regenerationHandler.dormantRegenerationTasksContainsTask(dormantRegenerationTask)) {
                        if (regenerationHandler.getDormantRegenerationTasks().size() >= 1) {
                            regenerationHandler.getDormantRegenerationTasks().get(dormantRegenerationTask.getWorldCoord()).remove(dormantRegenerationTask);
                        } else {
                            regenerationHandler.getDormantRegenerationTasks().remove(dormantRegenerationTask.getWorldCoord());
                        }
                    }

                    dormantRegenerationTask.cancel();

                    RegenerationTask regenerationTask = new RegenerationTask(regenerationHandler, dormantRegenerationTask);
                    regenerationTask.runTaskTimer(main, 0L, 20L);

                    if (!regenerationHandler.activeRegenerationTasksContainsTask(regenerationTask)) {
                        if (!regenerationHandler.getActiveRegenerationTasks().containsKey(regenerationTask.getWorldCoord())) {
                            HashSet<RegenerationTask> tasks = new HashSet<>();
                            tasks.add(regenerationTask);

                            regenerationHandler.getActiveRegenerationTasks().put(regenerationTask.getWorldCoord(), tasks);
                        } else {
                            regenerationHandler.getActiveRegenerationTasks().get(regenerationTask.getWorldCoord()).add(regenerationTask);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPistonExtend(BlockPistonExtendEvent event) {
        if (event.getBlock().getWorld().getName().equalsIgnoreCase("player_world")) {
            if (event.isCancelled()) {
                return;
            }

            Block piston = event.getBlock();
            BlockState state = piston.getState();
            MaterialData data = state.getData();
            BlockFace direction = null;

//            Bukkit.broadcastMessage("Piston Extended(X: " + piston.getX() + ", Y: " + piston.getY() + ", Z: " + piston.getZ() + "):");

            if (data instanceof PistonBaseMaterial) {
                direction = ((PistonBaseMaterial) data).getFacing();
                Block block = piston.getRelative(direction);

                if (event.getBlocks().size() <= 1) {
//                    Bukkit.broadcastMessage("From Block (X:" + block.getX() + ", Y: " + block.getY() + ", Z: " + block.getZ() + ")");
//                    Bukkit.broadcastMessage("To Block (X:" + block.getRelative(direction).getLocation().getX() + ", Y: " + block.getRelative(direction).getLocation().getY() + ", Z: " + block.getRelative(direction).getLocation().getZ() + ")");

                    HashSet<RegenerationTask> regenerationTasks = regenerationHandler.getRegenerationTaskFromChunk(block.getChunk());

                    if (regenerationTasks.size() > 0) {
//                        Bukkit.broadcastMessage("Test");
                        for (RegenerationTask regenerationTask : regenerationTasks) {
//                            Bukkit.broadcastMessage("Test 1");
                            if (regenerationTask.getBlockLocation().equals(block.getLocation())) {
//                                Bukkit.broadcastMessage("Test 2");
                                regenerationTask.setBlockLocation(block.getRelative(direction).getLocation());

                                WorldCoord worldCoord = WorldCoord.parseWorldCoord(block.getLocation());

                                if (!(regenerationTask.getWorldCoord().getX() == worldCoord.getX() && regenerationTask.getWorldCoord().getZ() == worldCoord.getX())) {
                                    if (regenerationHandler.activeRegenerationTasksContainsTask(regenerationTask)) {
                                        if (regenerationHandler.getActiveRegenerationTasks().get(worldCoord).size() >= 1) {
                                            regenerationHandler.getActiveRegenerationTasks().get(regenerationTask.getWorldCoord()).remove(regenerationTask);
                                        } else {
                                            regenerationHandler.getActiveRegenerationTasks().remove(regenerationTask.getWorldCoord());
                                        }
                                    }

                                    regenerationTask.setWorldCoord(worldCoord);

                                    if (!regenerationHandler.activeRegenerationTasksContainsTask(regenerationTask)) {
                                        if (!regenerationHandler.getActiveRegenerationTasks().containsKey(worldCoord)) {
                                            HashSet<RegenerationTask> tasks = new HashSet<>();
                                            tasks.add(regenerationTask);

                                            regenerationHandler.getActiveRegenerationTasks().put(worldCoord, tasks);
                                        } else {
                                            regenerationHandler.getActiveRegenerationTasks().get(worldCoord).add(regenerationTask);
                                        }
                                    }
                                }

//                                Bukkit.broadcastMessage("Test 3");

                                break;
                            }
                        }
                    }
                }
            }

            if (direction == null) {
                return;
            }

//            Bukkit.broadcastMessage(" Length: " + event.getBlocks().size());
//            Bukkit.broadcastMessage(" RegenTasks: " + regenerationHandler.getRegenerationTaskFromChunk(piston.getChunk()).size());

            if (event.getBlocks().size() >= 2) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockPhysics(BlockPhysicsEvent event) {
        if (event.getBlock().getType() == Material.SAND || event.getBlock().getType() == Material.GRAVEL) {
            event.setCancelled(true);
        }
    }
}
