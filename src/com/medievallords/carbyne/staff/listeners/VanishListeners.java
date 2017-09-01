package com.medievallords.carbyne.staff.listeners;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.staff.StaffManager;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.PlayerUtility;
import net.minecraft.server.v1_8_R3.Container;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutOpenWindow;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.event.CraftEventFactory;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftContainer;
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

    private HashMap<Player, Boolean> silentOpens = new HashMap<>();


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
        if (staffManager.getVanish().contains(event.getPlayer().getUniqueId()) && !event.getPlayer().hasPermission("carbyne.staff.admin")) {
            event.setCancelled(true);
            MessageManager.sendMessage(event.getPlayer(), "&cYou cannot drop items in vanish");
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
        if (staffManager.getVanish().contains(event.getPlayer().getUniqueId()) && !event.getPlayer().hasPermission("carbyne.staff.admin")) {
            event.setCancelled(true);
            MessageManager.sendMessage(event.getPlayer(), "&cYou cannot place blocks in vanish");
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (staffManager.getVanish().contains(event.getPlayer().getUniqueId()) && !event.getPlayer().hasPermission("carbyne.staff.admin")) {
            event.setCancelled(true);
            MessageManager.sendMessage(event.getPlayer(), "&cYou cannot break blocks in vanish");
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            if (staffManager.getVanish().contains(event.getDamager().getUniqueId()) && !event.getDamager().hasPermission("carbyne.staff.admin")) {
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
            Inventory silentInv = null;

            switch (b.getType()) {
                case TRAPPED_CHEST:
                case CHEST:
                    inv = ((Chest) blockState).getInventory();
                    e.setCancelled(true);
                    openCustomInventory(inv, ((CraftPlayer) p).getHandle(), "minecraft:chest");
            }

        }

        if (e.getAction() == Action.PHYSICAL && e.getClickedBlock().getType() == Material.SOIL) {
            if (staffManager.isVanished(p) && !e.getPlayer().hasPermission("carbyne.staff.admin")) {
                e.setCancelled(true);
            }
        }
    }

    private void openCustomInventory(Inventory inventory, EntityPlayer player, String windowType) {
        if (player.playerConnection == null) return;
        Container container = new CraftContainer(inventory, player.getBukkitEntity(), player.nextContainerCounter());
        container = CraftEventFactory.callInventoryOpenEvent(player, container);
        if (container == null) return;
        String title = container.getBukkitView().getTitle();
        int size = container.getBukkitView().getTopInventory().getSize();
        player.playerConnection.sendPacket(new PacketPlayOutOpenWindow(container.windowId, windowType, IChatBaseComponent.ChatSerializer.a("Chest"), size, 1));
        player.getBukkitEntity().getHandle().activeContainer = container;
        player.getBukkitEntity().getHandle().activeContainer.addSlotListener(player);

    }

    @EventHandler
    public void onTarget(EntityTargetEvent e) {
        if (e.getTarget() instanceof Player) {
            if (staffManager.isVanished(((Player) e.getTarget()))) {
                e.setCancelled(true);
            }
        }
    }


}
