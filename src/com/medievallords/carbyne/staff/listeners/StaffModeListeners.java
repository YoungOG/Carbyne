package com.medievallords.carbyne.staff.listeners;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.staff.StaffManager;
import com.medievallords.carbyne.utils.MessageManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Dalton on 6/24/2017.
 */
public class StaffModeListeners implements Listener {

    private StaffManager staffManager;

    public StaffModeListeners() {
        staffManager = Carbyne.getInstance().getStaffManager();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerInteract(PlayerInteractAtEntityEvent e) {
        if (staffManager.getStaffModePlayers().contains(e.getPlayer().getUniqueId())) {
            if (e.getRightClicked() instanceof Player) {

                ItemStack tool = e.getPlayer().getItemInHand();

                switch (tool.getType()) {
                    case BOOK: {
                        e.setCancelled(true);
                        staffManager.showPlayerInventory((Player) e.getRightClicked(), e.getPlayer());
                        break;
                    }
                    case ICE: {
                        e.setCancelled(true);
                        staffManager.toggleFreeze((Player) e.getRightClicked(), e.getPlayer());
                        break;
                    }
                }

            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (staffManager.getStaffModePlayers().contains(e.getPlayer().getUniqueId())) {
            Player staff = e.getPlayer();

            if (e.getItem() == null)
                return;

            if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                Material tool = e.getItem().getType();
                switch (tool) {
                    case INK_SACK: {
                        switch (e.getItem().getDurability()) {
                            case 10: {
                                staffManager.toggleVanish(staff);
                                break;
                            }
                            default:
                                break;
                        }
                        return;
                    }
                    case WATCH: {
                        staffManager.teleportToRandomPlayer(staff);
                        break;
                    }
                    default:
                        break;
                }
            }
            else if (e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_AIR) {
                Material tool = e.getItem().getType();
                switch(tool)
                {
                    case WATCH:
                    {
                        staffManager.teleportToPlayerUnderY30(staff);
                        break;
                    }
                    case PAPER: {
                        Carbyne.getInstance().getTicketManager().openTicketGUI(staff.getUniqueId(), true);
                        break;
                    }
                    default: break;
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
        if (e.getEntity() instanceof Player && staffManager.getStaffModePlayers().contains(e.getEntity().getUniqueId())) {
            e.setCancelled(true);
            return;
        }

        if (e.getDamager() instanceof Player) {
            if (staffManager.getStaffModePlayers().contains(e.getDamager().getUniqueId()))
                e.setCancelled(true);
        } else if (e.getDamager() instanceof Projectile && ((Projectile) e.getDamager()).getShooter() != null && ((Projectile) e.getDamager()).getShooter() instanceof Player) {
            Player damager = (Player) (((Projectile) e.getDamager()).getShooter());
            if (staffManager.getStaffModePlayers().contains(damager.getUniqueId()))
                e.setCancelled(true);
        }
    }

    /**
     * Prevent players in staff mode from breaking blocks
     *
     * @param e
     */
    /*@EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (staffManager.getStaffModePlayers().contains(e.getPlayer().getUniqueId()))
            e.setCancelled(true);
    }

    /**
     * Prevent players in staff mode from breaking blocks
     *
     * @param e
     */
    /*@EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if (staffManager.getStaffModePlayers().contains(e.getPlayer().getUniqueId()))
            e.setCancelled(true);
    }*/

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        if (staffManager.getStaffModePlayers().contains(e.getPlayer().getUniqueId())) {
            e.getPlayer().getInventory().clear();
            staffManager.toggleStaffMode(e.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerItemPickUp(PlayerPickupItemEvent e) {
        if (staffManager.getStaffModePlayers().contains(e.getPlayer().getUniqueId()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (staffManager.getStaffModePlayers().contains(e.getWhoClicked().getUniqueId())) {
            e.setCancelled(true);
            ((Player) e.getWhoClicked()).updateInventory();
        }
    }

    @EventHandler
    public void onCreativeClick(InventoryCreativeEvent event) {
        if (staffManager.getStaffModePlayers().contains(event.getWhoClicked().getUniqueId())) {
            event.setCancelled(true);
            ((Player) event.getWhoClicked()).updateInventory();
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (staffManager.getVanish().contains(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            MessageManager.sendMessage(event.getPlayer(), "&cYou cannot drop items in vanish");
            /*
            ItemStack item = event.getItemDrop().getItemStack().clone();
            item.setAmount(event.getPlayer().getInventory().getItemInHand().getAmount() + 1);
            event.getItemDrop().remove();
            event.getPlayer().getInventory().setItem(event.getPlayer().getInventory().getHeldItemSlot(), item);
            */
        }
    }

    @EventHandler
    public void playerCommandEvent(PlayerCommandPreprocessEvent e) {
        if (staffManager.getStaffModePlayers().contains(e.getPlayer())) {
            String[] split = e.getMessage().split(" ");
            if (!staffManager.getStaffmodeCommandWhitelist().contains(split[0]))
                e.setCancelled(true);
        }
    }
}
