package com.medievallords.carbyne.staff.listeners;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.staff.StaffManager;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.PlayerUtility;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;

/**
 * Created by Calvin on 6/11/2017
 * for the Carbyne project.
 */
public class VanishListeners implements Listener {

    private StaffManager staffManager = Carbyne.getInstance().getStaffManager();

    private HashMap<Player, Block> silentOpens = new HashMap<>();

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        for (Player all : PlayerUtility.getOnlinePlayers()) {
            if (staffManager.isVanished(all)) {
                player.hidePlayer(all);
            }
        }
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent event) {
        if (staffManager.getVanish().contains(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        if (staffManager.getVanish().contains((event.getEntity()).getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (staffManager.getVanish().contains(event.getPlayer().getUniqueId())) {
            event.getItemDrop().remove();
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrop(PlayerPickupItemEvent event) {
        if (staffManager.getVanish().contains(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (staffManager.getVanish().contains(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (staffManager.getVanish().contains(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            if (staffManager.getVanish().contains(event.getDamager().getUniqueId())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (!p.isSneaking() && (e.getAction() == Action.RIGHT_CLICK_BLOCK) && staffManager.isVanished(p)) {
            Block b = e.getClickedBlock();
            Inventory inv = null;
            BlockState blockState = b.getState();

            switch (b.getType()) {
                case TRAPPED_CHEST:
                case CHEST:
                    Chest chest = (Chest) blockState;
                    inv = Bukkit.getServer().createInventory(p, chest.getInventory().getSize());
                    inv.setContents(chest.getInventory().getContents());
                    break;
                case ENDER_CHEST:
                    inv = p.getEnderChest();
                    break;
                case DISPENSER:
                    inv = ((Dispenser) blockState).getInventory();
                    break;
                case HOPPER:
                    inv = ((Hopper) blockState).getInventory();
                    break;
                case DROPPER:
                    inv = ((Dropper) blockState).getInventory();
                    break;
                case FURNACE:
                    inv = ((Furnace) blockState).getInventory();
                    break;
                case BREWING_STAND:
                    inv = ((BrewingStand) blockState).getInventory();
                    break;
                case BEACON:
                    inv = ((Beacon) blockState).getInventory();
                    break;
            }

            if (inv != null) {
                e.setCancelled(true);
                p.openInventory(inv);
                MessageManager.sendMessage(p, "&7Container opened silently.");
                silentOpens.put(p, b);

                return;
            }


        }

        if (e.getAction() == Action.PHYSICAL && e.getClickedBlock().getType() == Material.SOIL) {
            if (staffManager.isVanished(p)) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onTarget(EntityTargetEvent e) {
        if (e.getTarget() instanceof Player) {
            if (staffManager.isVanished(((Player) e.getTarget()))) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryOpen(InventoryOpenEvent e)
    {
        if(staffManager.getVanish().contains(e.getPlayer().getUniqueId())) e.setCancelled(false);
    }

    /*@EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getPlayer();

        if (silentOpens.containsKey(player)) {
            Block block = silentOpens.get(player);
            Inventory inv = event.getInventory();
            BlockState blockState = block.getState();

            switch (block.getType()) {
                case TRAPPED_CHEST:
                case CHEST:
                    Chest chest = (Chest) blockState;
                    chest.getInventory().setContents(inv.getContents());
                    break;
                case DISPENSER:
                    ((Dispenser) blockState).getInventory().setContents(inv.getContents());
                    break;
                case HOPPER:
                    ((Hopper) blockState).getInventory().setContents(inv.getContents());
                    break;
                case DROPPER:
                    ((Dropper) blockState).getInventory().setContents(inv.getContents());
                    break;
                case FURNACE:
                    ((Furnace) blockState).getInventory().setContents(inv.getContents());
                    break;
                case BREWING_STAND:
                    ((BrewingStand) blockState).getInventory().setContents(inv.getContents());
                    break;
                case BEACON:
                    ((Beacon) blockState).getInventory().setContents(inv.getContents());
                    break;
            }

            silentOpens.remove(player);
        }
    }*/

}
