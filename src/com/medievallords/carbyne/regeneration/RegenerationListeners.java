package com.medievallords.carbyne.regeneration;

import com.medievallords.carbyne.Carbyne;
import com.palmergames.bukkit.towny.event.PlayerChangePlotEvent;
import com.palmergames.bukkit.towny.object.WorldCoord;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.material.MaterialData;
import org.bukkit.material.PistonBaseMaterial;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Calvin on 3/23/2017
 * for the Carbyne project.
 */
public class RegenerationListeners implements Listener {

    private Carbyne main = Carbyne.getInstance();
    private RegenerationHandler regenerationHandler = main.getRegenerationHandler();

    /**
     * Requests a block broken to regenerate
     *
     * @param e
     */
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();

        new BukkitRunnable() {
            public void run() {
                if (player.getWorld().getName().equalsIgnoreCase("player_world") && !regenerationHandler.getBypassers().contains(player.getUniqueId()))
                    regenerationHandler.request(e.getBlock(), RegenerationType.BROKEN);
            }
        }.runTaskAsynchronously(main);

    }

    /**
     * Requests a block placed to regenerate to air
     *
     * @param e
     */
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        Player player = e.getPlayer();

        new BukkitRunnable() {
            public void run() {
                if (player.getWorld().getName().equalsIgnoreCase("player_world") && !regenerationHandler.getBypassers().contains(player.getUniqueId()))
                    regenerationHandler.request(e.getBlock(), RegenerationType.PLACED);
            }
        }.runTaskAsynchronously(main);

    }

    /**
     * When a player moves onto another WorldCoord, check if regeneration needs to occur.
     *
     * @param e
     */
    @EventHandler
    public void onPlayerChangeCoord(PlayerChangePlotEvent e) {
        if (e.getTo().getWorldName().equalsIgnoreCase("player_world")) {
            WorldCoord wc = e.getTo();
            regenerationHandler.checkAndAct(wc);
        }
    }

    /**
     * Prevent sand and gravel from falling.
     * EXCEPT IN THE BUILD WORLD WHERE BUILDERS NEED IT
     * @param e
     */
    @EventHandler
    public void onBlockPhysics(BlockPhysicsEvent e) {
        if(e.getBlock().getWorld().getName().equalsIgnoreCase("build_world")) return;
        if (e.getBlock().getType().equals(Material.SAND) || e.getBlock().getType().equals(Material.GRAVEL))
            e.setCancelled(true);
    }

    /**
     * Creates regeneration data for blocks placed pushed by a piston.
     *
     * @param e
     */
    @EventHandler
    public void onPistonExtend(BlockPistonExtendEvent e) {
        if (e.isCancelled())
            return;

        if (e.getBlock().getWorld().getName().equalsIgnoreCase("player_world")) {
            Block piston = e.getBlock();
            BlockState state = piston.getState();
            MaterialData data = state.getData();

            if (data instanceof PistonBaseMaterial) {
                if (e.getBlocks().size() > 0) {
                    Block moved = e.getBlocks().get(0);
                    WorldCoord worldCoord = WorldCoord.parseWorldCoord(moved);

                    if (e.getBlocks().size() <= 1)
                        if (regenerationHandler.isLocationEmpty(worldCoord, moved.getLocation()))
                            regenerationHandler.request(moved, RegenerationType.PLACED);
                }
            }
        }
    }
}
