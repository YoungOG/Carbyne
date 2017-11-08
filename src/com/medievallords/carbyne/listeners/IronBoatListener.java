package com.medievallords.carbyne.listeners;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.utils.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.event.vehicle.VehicleUpdateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.HashSet;

public class IronBoatListener implements Listener {

    private Carbyne main = Carbyne.getInstance();

    public IronBoatListener() {
        ItemStack ironboat = new ItemBuilder(Material.MINECART).name("&7Iron Boat").build();


        ShapedRecipe s1 = new ShapedRecipe(ironboat).shape("III", "IMI", "III").setIngredient('I', Material.IRON_INGOT).setIngredient('M', Material.MINECART);
        main.getServer().addRecipe(s1);
    }

    @EventHandler
    public void onPlayerPlaceLavaMinecart(PlayerInteractEvent e) {
        Player p = e.getPlayer();

        if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {
            if (e.hasBlock()) {
                if (e.getItem() != null) {
                    if (e.getItem().getType() == Material.MINECART) {
                        if (e.getItem().hasItemMeta()) {
                            if (e.getItem().getItemMeta().hasDisplayName()) {
                                if (e.getItem().getItemMeta().getDisplayName().contains(ChatColor.GRAY + "Iron Boat")) {
                                    e.setUseItemInHand(Event.Result.DENY);
                                    e.setCancelled(true);

                                    Block block = e.getClickedBlock();
                                    Minecart cart = block.getWorld().spawn(block.getLocation().add(0.0D, 2.0D, 0.0D), Minecart.class);
                                    cart.setMetadata("IronBoat", new FixedMetadataValue(main, true));

                                    if (p.getGameMode() == GameMode.SURVIVAL) {
                                        p.getInventory().removeItem(p.getItemInHand());
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                try {
                    for (Block b : p.getLineOfSight((HashSet<Byte>) null, 5)) {
                        if (b.getType() != Material.AIR) {
                            if (b.getType() == Material.LAVA || b.getType() == Material.STATIONARY_LAVA) {
                                if (e.getItem() != null) {
                                    if (e.getItem().getType() == Material.MINECART) {
                                        if (e.getItem().hasItemMeta()) {
                                            if (e.getItem().getItemMeta().hasDisplayName()) {
                                                if (e.getItem().getItemMeta().getDisplayName().contains(ChatColor.GRAY + "Iron Boat")) {
                                                    e.setUseItemInHand(Event.Result.DENY);
                                                    e.setCancelled(true);

                                                    Minecart cart = b.getWorld().spawn(b.getLocation().add(0.0D, 2.0D, 0.0D), Minecart.class);
                                                    cart.setMetadata("IronBoat", new FixedMetadataValue(main, true));

                                                    if (p.getGameMode() == GameMode.SURVIVAL) {
                                                        p.getInventory().removeItem(p.getItemInHand());
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (IllegalStateException ex) {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onCombust(EntityCombustEvent e) {
        if (e.getEntity() instanceof Minecart) {
            Minecart cart = (Minecart) e.getEntity();

            if (cart.hasMetadata("IronBoat")) {
                e.setCancelled(true);
                cart.setFireTicks(0);
                if (cart.getPassenger() != null) {
                    cart.getPassenger().setFireTicks(0);
                }
            }
        }
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();

            if (p.getVehicle() instanceof Minecart) {
                if (p.getVehicle().hasMetadata("IronBoat")) {
                    e.setCancelled(true);
                    p.setFireTicks(0);
                }
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();

            if (e.getCause() == EntityDamageEvent.DamageCause.LAVA || e.getCause() == EntityDamageEvent.DamageCause.FIRE || e.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK) {
                if (p.getVehicle() instanceof Minecart) {
                    if (p.getVehicle().hasMetadata("IronBoat")) {
                        p.setFireTicks(0);
                        p.getVehicle().setFireTicks(0);
                        e.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDamage2(VehicleDamageEvent e) {
        if (e.getVehicle() instanceof Minecart) {
            if (e.getVehicle().hasMetadata("IronBoat")) {
                if (!(e.getAttacker() instanceof Player) && !(e.getVehicle() instanceof Projectile)) {
                    e.setCancelled(true);
                    e.getVehicle().setFireTicks(0);
                    if (e.getVehicle().getPassenger() != null) {
                        e.getVehicle().getPassenger().setFireTicks(0);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onMove(VehicleMoveEvent e) {
        if (e.getVehicle() instanceof Minecart) {
            Vector vect = e.getVehicle().getVelocity();
            Material mat = e.getVehicle().getLocation().getWorld().getBlockAt(e.getVehicle().getLocation()).getType();
            if (mat == Material.LAVA || mat == Material.STATIONARY_LAVA) {
                vect.setY(0.25D);
            }
            e.getVehicle().setVelocity(vect);
            e.getVehicle().setFireTicks(0);
            if (e.getVehicle().getPassenger() != null) {
                e.getVehicle().getPassenger().setFireTicks(0);
            }
        }
    }

    @EventHandler
    public void onUpdate(VehicleUpdateEvent e) {
        if (e.getVehicle() instanceof Minecart) {
            if (e.getVehicle().getPassenger() instanceof Player) {
                if (e.getVehicle().getLocation().subtract(0.0D, 1.0D, 0.0D).getBlock().getType() == Material.LAVA || e.getVehicle().getLocation().subtract(0.0D, 1.0D, 0.0D).getBlock().getType() == Material.STATIONARY_LAVA) {
                    Minecart cart = (Minecart) e.getVehicle();
                    Player p = (Player) e.getVehicle().getPassenger();

                    Vector vect = new Vector(p.getLocation().getDirection().getX(), cart.getLocation().getDirection().getY(), p.getLocation().getDirection().getZ());
                    cart.setVelocity(vect);
                    cart.getLocation().setDirection(vect);
                    cart.setMaxSpeed(0.25D);
                    if (e.getVehicle().getPassenger() != null) {
                        e.getVehicle().getPassenger().setFireTicks(0);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDestroy(VehicleDestroyEvent e) {
        if (e.getVehicle().hasMetadata("IronBoat")) {
            e.setCancelled(true);
            e.getVehicle().getLocation().getWorld().dropItemNaturally(e.getVehicle().getLocation(), new ItemBuilder(Material.MINECART).name("&7Iron Boat").build());
            e.getVehicle().remove();
        }
    }
}