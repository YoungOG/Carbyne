package com.medievallords.carbyne.staff.listeners;

import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.StaticClasses;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Dalton on 6/24/2017.
 */
public class StaffModeListeners implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerInteract(PlayerInteractAtEntityEvent e) {
        if (StaticClasses.staffManager.getStaffModePlayers().contains(e.getPlayer().getUniqueId())) {
            if (e.getRightClicked() instanceof Player) {

                ItemStack tool = e.getPlayer().getInventory().getItemInMainHand();

                switch (tool.getType()) {
                    case BOOK: {
                        e.setCancelled(true);
                        e.getPlayer().performCommand("invsee " + e.getRightClicked().getName());
                        break;
                    }
                    case ICE: {
                        e.setCancelled(true);
                        StaticClasses.staffManager.toggleFreeze((Player) e.getRightClicked(), e.getPlayer());
                        break;
                    }
                }

            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (StaticClasses.staffManager.getStaffModePlayers().contains(e.getPlayer().getUniqueId())) {
            Player staff = e.getPlayer();

            if (e.getItem() == null)
                return;

            if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                Material tool = e.getItem().getType();
                switch (tool) {
                    case INK_SACK:
                        switch (e.getItem().getDurability()) {
                            case 10:
                                StaticClasses.staffManager.toggleVanish(staff);
                                break;
                            default:
                                break;
                        }
                        return;
                    case WATCH:
                        StaticClasses.staffManager.teleportToRandomPlayer(staff);
                        break;
                    case COMPASS:
                        e.getPlayer().performCommand("thru");
                    default:
                        break;
                }
            } else if (e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_AIR) {
                Material tool = e.getItem().getType();
                switch (tool) {
                    case WATCH:
                        StaticClasses.staffManager.teleportToPlayerUnderY30(staff);
                        break;
                    case COMPASS:
                        e.getPlayer().performCommand("thru");
                    default:
                        break;
                }
            }
        }
    }

    /**
     * Prevent players in staff mode from damaging
     *
     * @param e
     */
    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player && StaticClasses.staffManager.getStaffModePlayers().contains(e.getEntity().getUniqueId())) {
            e.setCancelled(true);
            return;
        }

        if (e.getDamager() instanceof Player) {
            if (StaticClasses.staffManager.getStaffModePlayers().contains(e.getDamager().getUniqueId()))
                e.setCancelled(true);
        } else if (e.getDamager() instanceof Projectile && ((Projectile) e.getDamager()).getShooter() != null && ((Projectile) e.getDamager()).getShooter() instanceof Player) {
            Player damager = (Player) (((Projectile) e.getDamager()).getShooter());

            if (StaticClasses.staffManager.getStaffModePlayers().contains(damager.getUniqueId()))
                e.setCancelled(true);
        }
    }

    /**
     * Prevent players in staff mode from breaking blocks
     *
     * @param e
     */
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (StaticClasses.staffManager.getStaffModePlayers().contains(e.getPlayer().getUniqueId()))
            e.setCancelled(true);
    }

    /**
     * Prevent players in staff mode from breaking blocks
     *
     * @param e
     */
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if (StaticClasses.staffManager.getStaffModePlayers().contains(e.getPlayer().getUniqueId()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        if (StaticClasses.staffManager.getStaffModePlayers().contains(e.getPlayer().getUniqueId())) {
            e.getPlayer().getInventory().clear();
            StaticClasses.staffManager.toggleStaffMode(e.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerItemPickUp(PlayerPickupItemEvent e) {
        if (StaticClasses.staffManager.getStaffModePlayers().contains(e.getPlayer().getUniqueId()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (StaticClasses.staffManager.getStaffModePlayers().contains(e.getWhoClicked().getUniqueId())) {
            e.setCancelled(true);
            ((Player) e.getWhoClicked()).updateInventory();
        }
    }

    @EventHandler
    public void onCreativeClick(InventoryCreativeEvent event) {
        if (StaticClasses.staffManager.getStaffModePlayers().contains(event.getWhoClicked().getUniqueId())) {
            event.setCancelled(true);
            ((Player) event.getWhoClicked()).updateInventory();
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (StaticClasses.staffManager.getVanish().contains(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            MessageManager.sendMessage(event.getPlayer(), "&cYou cannot drop items in vanish");
            /*
            ItemStack item = event.getItemDrop().getItemStack().clone();
            item.setAmount(event.getPlayer().getInventory().getInventory().getItemInMainHand().getAmount() + 1);
            event.getItemDrop().remove();
            event.getPlayer().getInventory().setItem(event.getPlayer().getInventory().getHeldItemSlot(), item);
            */
        }
    }

//    @EventHandler
//    public void playerCommandEvent(PlayerCommandPreprocessEvent e) {
//        if (staffManager.getStaffModePlayers().contains(e.getPlayer().getUniqueId())) {
//            String[] split = e.getMessage().split(" ");
//            if (!staffManager.getStaffmodeCommandWhitelist().contains(split[0]))
//                e.setCancelled(true);
//        }
//    }
}
