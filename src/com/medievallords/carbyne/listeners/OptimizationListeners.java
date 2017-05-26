package com.medievallords.carbyne.listeners;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.utils.signgui.SignGUI;
import com.medievallords.carbyne.utils.signgui.SignGUIUpdateEvent;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Random;

public class OptimizationListeners implements Listener {

    private ArrayList<Material> allowedMaterials = new ArrayList<>();

    public OptimizationListeners() {
        allowedMaterials.add(Material.TRAPPED_CHEST);
        allowedMaterials.add(Material.CHEST);
        allowedMaterials.add(Material.FURNACE);
        allowedMaterials.add(Material.DROPPER);
        allowedMaterials.add(Material.DISPENSER);
        allowedMaterials.add(Material.HOPPER);
        allowedMaterials.add(Material.JUKEBOX);
        allowedMaterials.add(Material.BREWING_STAND_ITEM);
        allowedMaterials.add(Material.BEACON);
        allowedMaterials.add(Material.SIGN);
        allowedMaterials.add(Material.SKULL_ITEM);
        allowedMaterials.add(Material.MONSTER_EGG);
        allowedMaterials.add(Material.COMMAND);
        allowedMaterials.add(Material.MOB_SPAWNER);
    }

    @EventHandler
    public void onHungerLoss(FoodLevelChangeEvent event) {
        Player player = (Player) event.getEntity();

        if ((event.getFoodLevel() < player.getFoodLevel()) && (new Random().nextInt(100) > 4)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onRain(WeatherChangeEvent event) {
        World world = event.getWorld();

        if (!world.hasStorm()) {
            event.setCancelled(true);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if (world.hasStorm()) {
                    world.setStorm(false);
                }
            }
        }.runTaskLater(Carbyne.getInstance(), 5L);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
    }

    @EventHandler
    public void onSignEdit(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (!player.hasPermission("carbyne.caneditsigns")) {
            return;
        }

        if (player.getGameMode() != GameMode.CREATIVE) {
            return;
        }

        if (!player.isSneaking()) {
            return;
        }

        if (event.getClickedBlock() != null && event.getClickedBlock().getType() != Material.AIR) {
            if (event.getClickedBlock().getType() == Material.SIGN || event.getClickedBlock().getType() == Material.SIGN_POST || event.getClickedBlock().getType() == Material.WALL_SIGN) {
                Sign sign = (Sign) event.getClickedBlock().getState();

                SignGUI.openSignEditor(player, event.getClickedBlock(), sign.getLines());
            }
        }
    }

    @EventHandler
    public void onSignUpdate(SignGUIUpdateEvent event) {
        Block block = event.getBlock();

        if (block != null) {
            BlockState blockState = block.getState();

            if (blockState instanceof Sign) {
                Sign sign = (Sign) blockState;

                for (int i = 0; i <= 3; i++) {
                    sign.setLine(i, ChatColor.translateAlternateColorCodes('&', event.getSignText()[i]));
                }

                sign.update(true);
            }
        }
    }

//    @EventHandler
//    public void onInteract(PlayerInteractEvent event) {
//        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
//            if (event.hasBlock() && (event.getClickedBlock().getType() == Material.TRAP_DOOR
//                    || event.getClickedBlock().getType() == Material.BEACON
//                    || event.getClickedBlock().getType() == Material.NOTE_BLOCK
//                    || event.getClickedBlock().getType() == Material.FENCE_GATE)) {
//
//                TownBlock townBlock = TownyUniverse.getTownBlock(event.getClickedBlock().getLocation());
//
//                if (townBlock != null) {
//                    try {
//                        if (townBlock.getTown() != null) {
//                            if (townBlock.getTown().getName().equalsIgnoreCase("Safezone")) {
//                                event.setCancelled(true);
//                            }
//                        }
//                    } catch (NotRegisteredException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }
//    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        if (player.getLastDamageCause() != null && player.getLastDamageCause().getCause() != null) {
            switch (player.getLastDamageCause().getCause()) {
                case FALL:
                    event.setDeathMessage(ChatColor.translateAlternateColorCodes('&', "&c" + player.getName() + "&e broke their legs"));
                    break;
                case DROWNING:
                    event.setDeathMessage(ChatColor.translateAlternateColorCodes('&', "&c" + player.getName() + "&e forgot to hold their breath"));
                    break;
                case VOID:
                    event.setDeathMessage(ChatColor.translateAlternateColorCodes('&', "&c" + player.getName() + "&e fell into the void"));
                    break;
                case FIRE:
                    event.setDeathMessage(ChatColor.translateAlternateColorCodes('&', "&c" + player.getName() + "&e now looks like fried chicken"));
                case FIRE_TICK:
                    event.setDeathMessage(ChatColor.translateAlternateColorCodes('&', "&c" + player.getName() + "&e burnt into a crisp"));
                    break;
                case CUSTOM:
                    event.setDeathMessage(ChatColor.translateAlternateColorCodes('&', "&c" + player.getName() + "&e died"));
                    break;
                case BLOCK_EXPLOSION:
                    event.setDeathMessage(ChatColor.translateAlternateColorCodes('&', "&c" + player.getName() + "&e exploded into a million pieces"));
                    break;
                case MAGIC:
                    event.setDeathMessage(ChatColor.translateAlternateColorCodes('&', "&c" + player.getName() + "&e was killed by dark magic"));
                    break;
                case STARVATION:
                    event.setDeathMessage(ChatColor.translateAlternateColorCodes('&', "&c" + player.getName() + "&e starved to death"));
                    break;
                case LIGHTNING:
                    event.setDeathMessage(ChatColor.translateAlternateColorCodes('&', "&c" + player.getName() + "&e was raped by lightning"));
                    break;
                case LAVA:
                    event.setDeathMessage(ChatColor.translateAlternateColorCodes('&', "&c" + player.getName() + "&e tried to swim in lava"));
                    break;
                case ENTITY_ATTACK:
                    if (player.getKiller() != null) {
                        Player killer = player.getKiller();

                        if (killer.getItemInHand().hasItemMeta() && killer.getItemInHand().getItemMeta() != null && killer.getItemInHand().getItemMeta().hasDisplayName()) {
                            event.setDeathMessage(ChatColor.translateAlternateColorCodes('&', "&c" + player.getName() + "&e was killed by &c" + killer.getName() + "&e using &c" + killer.getItemInHand().getItemMeta().getDisplayName()));
                        } else {
                            event.setDeathMessage(ChatColor.translateAlternateColorCodes('&', "&c" + player.getName() + "&e was killed by &c" + killer.getName()));
                        }
                    }
                    break;
                case PROJECTILE:
                    if (player.getKiller() instanceof Arrow) {
                        Arrow arrow = (Arrow) player.getKiller();

                        if (arrow.getShooter() instanceof Player) {
                            Player shooter = (Player) arrow.getShooter();

                            if (shooter.getLocation().distance(player.getLocation()) > 50) {
                                event.setDeathMessage(ChatColor.translateAlternateColorCodes('&', "&c" + player.getName() + "&e was sniped by &c" + shooter.getName()));
                            } else {
                                event.setDeathMessage(ChatColor.translateAlternateColorCodes('&', "&c" + player.getName() + "&e was shot down by &c" + shooter.getName()));
                            }
                        } else {
                            event.setDeathMessage(ChatColor.translateAlternateColorCodes('&', "&c" + player.getName() + "&e was shot down"));
                        }
                    }
                    break;
                default:
                    event.setDeathMessage(ChatColor.translateAlternateColorCodes('&', "&c" + player.getName() + "&e died"));
                    break;
            }
        } else {
            event.setDeathMessage(ChatColor.translateAlternateColorCodes('&', "&c" + player.getName() + "&e died"));
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();

        if (player.getGameMode() == GameMode.CREATIVE && (event.getRightClicked().getType() == EntityType.HORSE || event.getRightClicked().getType() == EntityType.ARMOR_STAND)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();

        if (player.getGameMode() == GameMode.CREATIVE && event.getRightClicked().getType() == EntityType.HORSE) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onGamemodeChange(PlayerGameModeChangeEvent event) {
        if (event.getNewGameMode() == GameMode.SURVIVAL && event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            event.getPlayer().getInventory().clear();
        }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE && !event.getPlayer().isOp()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void inventoryCreativeEvent(InventoryCreativeEvent event) {
        if (event.getClick() == ClickType.CREATIVE) {
            Player player = (Player) event.getWhoClicked();

            if (!player.isOp()) {
                ItemStack item = event.getCursor();
                int amount = item.getAmount();
                short data = item.getData().getData();

                for (Material material : allowedMaterials) {
                    if (item.getType() == material) {
                        event.setCursor(new ItemStack(material, amount, data));
                        break;
                    }
                }

                if (!item.getEnchantments().isEmpty()) {
                    event.setCursor(new ItemStack(item.getType(), amount, data));
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();

            if (player.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
                for (PotionEffect effect : player.getActivePotionEffects()) {
                    if (effect.getType().equals(PotionEffectType.INCREASE_DAMAGE)) {
                        int level = effect.getAmplifier() + 1;

                        double newDamage = event.getDamage(EntityDamageEvent.DamageModifier.BASE) / (level * 1.3D + 1.0D) + 2 * level;
                        double damagePercent = newDamage / event.getDamage(EntityDamageEvent.DamageModifier.BASE);

                        try {
                            event.setDamage(EntityDamageEvent.DamageModifier.ARMOR, event.getDamage(EntityDamageEvent.DamageModifier.ARMOR) * damagePercent);
                        } catch (Exception ignored) {}

                        try {
                            event.setDamage(EntityDamageEvent.DamageModifier.MAGIC, event.getDamage(EntityDamageEvent.DamageModifier.MAGIC) * damagePercent);
                        } catch (Exception ignored) {}

                        try {
                            event.setDamage(EntityDamageEvent.DamageModifier.RESISTANCE, event.getDamage(EntityDamageEvent.DamageModifier.RESISTANCE) * damagePercent);
                        } catch (Exception ignored) {}

                        try {
                            event.setDamage(EntityDamageEvent.DamageModifier.BLOCKING, event.getDamage(EntityDamageEvent.DamageModifier.BLOCKING) * damagePercent);
                        } catch (Exception ignored) {}

                        event.setDamage(EntityDamageEvent.DamageModifier.BASE, newDamage);
                        break;
                    }
                }
            }
        }
    }




}
