package com.medievallords.carbyne.staff.listeners;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.profiles.Profile;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.PlayerUtility;
import com.medievallords.carbyne.utils.StaticClasses;
import de.slikey.effectlib.util.ParticleEffect;
import net.minecraft.server.v1_12_R1.Container;
import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.IChatBaseComponent;
import net.minecraft.server.v1_12_R1.PacketPlayOutOpenWindow;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_12_R1.event.CraftEventFactory;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftContainer;
import org.bukkit.entity.Bat;
import org.bukkit.entity.EntityType;
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
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

<<<<<<<HEAD
=======
        >>>>>>>Init Commit.
        <<<<<<<HEAD
=======
        >>>>>>>Init Commit.

/**
 * Created by Calvin on 6/11/2017
 * for the Carbyne project.
 */
public class VanishListeners implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        StaticClasses.staffManager.getStaffChatPlayers().remove(player.getUniqueId());

        if (player.hasPermission("carbyne.staff.staffmode"))
            StaticClasses.staffManager.vanishPlayer(player);
        else if (StaticClasses.staffManager.isVanished(player))
            StaticClasses.staffManager.showPlayer(player);

        if (!player.hasPermission("carbyne.staff.canseevanished"))
            for (Player all : PlayerUtility.getOnlinePlayers())
                if (StaticClasses.staffManager.isVanished(all))
                    player.hidePlayer(all);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        if (event.getPlayer().hasPermission("carbyne.staff.staffmode"))
            StaticClasses.staffManager.getVanish().remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onKick(PlayerKickEvent event) {
        if (event.getPlayer().hasPermission("carbyne.staff.staffmode"))
            StaticClasses.staffManager.getVanish().remove(event.getPlayer().getUniqueId());
    }

    @EventHandler(ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event) {
        if (!StaticClasses.staffManager.isVanished(event.getPlayer())) {
            Profile profile = StaticClasses.profileManager.getProfile(event.getPlayer().getUniqueId());

            if (profile == null)
                return;

            if (!profile.isVanishEffect())
                return;

            if (event.getCause() == PlayerTeleportEvent.TeleportCause.COMMAND || event.getCause() == PlayerTeleportEvent.TeleportCause.PLUGIN) {
                if (event.getPlayer().isOp()) {
                    event.getPlayer().getWorld().strikeLightningEffect(event.getFrom());

                    for (int i = 0; i < 10; i++) {
                        Bat bat = (Bat) event.getPlayer().getWorld().spawnEntity(event.getFrom(), EntityType.BAT);
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (!bat.getLocation().getChunk().isLoaded()) {
                                    bat.getLocation().getChunk().load();
                                }

                                bat.setHealth(0);
                            }
                        }.runTaskLater(Carbyne.getInstance(), 80);

                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (bat.isDead() || bat.getHealth() <= 0)
                                    cancel();

                                ParticleEffect.FLAME.display(0.0F, 0.0F, 0.0F, 0.01F, 1, bat.getLocation(), 50);
                            }
                        }.runTaskTimerAsynchronously(Carbyne.getInstance(), 0L, 1L);
                    }

                    event.getPlayer().getWorld().strikeLightningEffect(event.getTo());

                    for (int i = 0; i < 10; i++) {
                        Bat bat = (Bat) event.getPlayer().getWorld().spawnEntity(event.getTo(), EntityType.BAT);
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                bat.setHealth(0);
                            }
                        }.runTaskLater(Carbyne.getInstance(), 80);

                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (bat.isDead() || bat.getHealth() <= 0)
                                    cancel();

                                ParticleEffect.FLAME.display(0.0F, 0.0F, 0.0F, 0.01F, 1, bat.getLocation(), 50);
                            }
                        }.runTaskTimerAsynchronously(Carbyne.getInstance(), 0L, 1L);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent event) {
        if (StaticClasses.staffManager.getVanish().contains(event.getPlayer().getUniqueId()))
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;

        if (StaticClasses.staffManager.getVanish().contains((event.getEntity()).getUniqueId()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (StaticClasses.staffManager.getVanish().contains(event.getPlayer().getUniqueId()) && !event.getPlayer().hasPermission("carbyne.staff.admin") && !StaticClasses.staffManager.getStaffModePlayers().contains(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            MessageManager.sendMessage(event.getPlayer(), "&cYou cannot drop items in vanish");
        } else if (StaticClasses.staffManager.getStaffModePlayers().contains(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            MessageManager.sendMessage(event.getPlayer(), "&cYou cannot drop items in staff mode.");
        }
    }

    @EventHandler
    public void onDrop(PlayerPickupItemEvent event) {
        if (StaticClasses.staffManager.getVanish().contains(event.getPlayer().getUniqueId()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (StaticClasses.staffManager.getVanish().contains(event.getPlayer().getUniqueId()) && !event.getPlayer().hasPermission("carbyne.staff.admin")) {
            event.setCancelled(true);
            MessageManager.sendMessage(event.getPlayer(), "&cYou cannot place blocks in vanish");
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (StaticClasses.staffManager.getVanish().contains(event.getPlayer().getUniqueId()) && !event.getPlayer().hasPermission("carbyne.staff.admin")) {
            event.setCancelled(true);
            MessageManager.sendMessage(event.getPlayer(), "&cYou cannot break blocks in vanish");
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player)
            if (StaticClasses.staffManager.getVanish().contains(event.getDamager().getUniqueId()) && !event.getDamager().hasPermission("carbyne.staff.admin"))
                event.setCancelled(true);
    }

    //@EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();

        if (!p.isSneaking() && (e.getAction() == Action.RIGHT_CLICK_BLOCK) && StaticClasses.staffManager.isVanished(p)) {
            Block b = e.getClickedBlock();
            Inventory inv;
            BlockState blockState = b.getState();

            if (StaticClasses.crateManager.getCrate(b.getLocation()) != null) {
                return;
            }

            switch (b.getType()) {
                case TRAPPED_CHEST:
                case CHEST:
                    inv = ((Chest) blockState).getInventory();
                    e.setCancelled(true);
<<<<<<<HEAD
                    openCustomInventory(inv, ((CraftPlayer) p).getHandle(), "minecraft:chest");
                    break;
=======
//                    openCustomInventory(inv, ((CraftPlayer) p).getHandle(), "minecraft:chest");
>>>>>>>Init Commit.
            }
        }

        if (e.getAction() == Action.PHYSICAL && e.getClickedBlock().getType() == Material.SOIL)
            if (StaticClasses.staffManager.isVanished(p) && !e.getPlayer().hasPermission("carbyne.staff.admin"))
                e.setCancelled(true);
    }
<<<<<<<HEAD

    private void openCustomInventory(Inventory inventory, EntityPlayer player, String windowType) {
        if (player.playerConnection == null)
            return;

        Container container = new CraftContainer(inventory, player, player.nextContainerCounter());
        container = CraftEventFactory.callInventoryOpenEvent(player, container);

        if (container == null)
            return;

        int size = container.getBukkitView().getTopInventory().getSize();

        player.playerConnection.sendPacket(new PacketPlayOutOpenWindow(container.windowId, windowType, IChatBaseComponent.ChatSerializer.a("{'text': 'Chest'}"), size));
        player.activeContainer = container;
        player.activeContainer.addSlotListener(player);
    }
=======
//
//    private void openCustomInventory(Inventory inventory, EntityPlayer player, String windowType) {
//        if (player.playerConnection == null) return;
//        Container container = new CraftContainer();
//        container = CraftEventFactory.callInventoryOpenEvent(player, container);
//        if (container == null) return;
//        String title = container.getBukkitView().getTitle();
//        int size = container.getBukkitView().getTopInventory().getSize();
//        player.playerConnection.sendPacket(new PacketPlayOutOpenWindow(container.windowId, windowType, IChatBaseComponent.ChatSerializer.a("Chest"), size, 1));
//        player.getBukkitEntity().getHandle().activeContainer = container;
//        player.getBukkitEntity().getHandle().activeContainer.addSlotListener(player);
//
//    }
        >>>>>>>
    Init Commit.

    @EventHandler
    public void onTarget(EntityTargetEvent e) {
        if (e.getTarget() instanceof Player)
            if (StaticClasses.staffManager.isVanished(((Player) e.getTarget())))
                e.setCancelled(true);
    }
}
