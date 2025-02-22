package com.medievallords.carbyne.listeners;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.utils.*;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import org.apache.commons.lang.WordUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class OptimizationListeners implements Listener {

    //READ EVERYTHING

    private List<Integer> allowedBlockIds = new ArrayList<>();
    private ArrayList<Material> allowedMaterials = new ArrayList<>();

    private HashMap<Player, HashMap<String, ArrayList<Double>>> portalLocations = new HashMap<>();
    private HashMap<Player, Location> startLocations = new HashMap<>();

    public OptimizationListeners() {
        Bukkit.addRecipe(new FurnaceRecipe(new ItemStack(Material.GLASS, 4), Material.SANDSTONE));

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

        allowedBlockIds.addAll(Arrays.asList(0, 6, 8, 9, 10, 11, 30, 31, 32, 37, 38, 39, 40, 50, 51, 55, 59, 63, 65, 66, 68, 69, 70, 72, 75, 76, 77, 83, 90, 93, 94, 104, 105, 106, 115));
    }

    @EventHandler
    public void onSpawn(CreatureSpawnEvent event) {
        if (event.getEntity().getWorld().getName().equalsIgnoreCase("world"))
            if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.CUSTOM && event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.SPAWNER && event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.SPAWNER_EGG)
                if (!(event.getEntity() instanceof Villager) && !(event.getEntity() instanceof IronGolem) && !(event.getEntity() instanceof Chicken) && !(event.getEntity() instanceof ArmorStand))
                    event.getEntity().remove();


    }

    @EventHandler
    public void onMobSpawn(SpawnerSpawnEvent event) {
        if (!event.getLocation().getWorld().getName().equalsIgnoreCase("player_world")) {
            return;
        }

        event.setCancelled(true);

//        CreatureSpawner spawner = event.getSpawner();
//        if (spawner != null) {
//            spawner.getBlock().setType(Material.STONE);
//        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.COMMAND || event.getCause() == PlayerTeleportEvent.TeleportCause.PLUGIN) {
            Material material = event.getTo().getBlock().getType();

            if (material == Material.FENCE || material == Material.IRON_FENCE || material == Material.NETHER_FENCE) {
                event.setCancelled(true);
                MessageManager.sendMessage(event.getPlayer(), "&cYou cannot teleport while next to a fence.");
            }
        }
    }

    @EventHandler
    public void onPortal(PlayerTeleportEvent event) {
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.END_PORTAL)
            event.setCancelled(true);
    }


    @EventHandler
    public void onHungerLoss(FoodLevelChangeEvent event) {
        Player player = (Player) event.getEntity();

        if ((event.getFoodLevel() < player.getFoodLevel()) && (new Random().nextInt(100) > 8))
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        if (StaticClasses.staffManager.isVanished(event.getPlayer())) {
            event.setJoinMessage(null);
            return;
        }

        event.setJoinMessage("§a" + event.getPlayer().getName() + " §7has joined.");
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (StaticClasses.staffManager.isVanished(event.getPlayer())) {
            event.setQuitMessage(null);
            return;
        }

        event.setQuitMessage("§c" + event.getPlayer().getName() + " §7has left.");
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.hasBlock() && (event.getClickedBlock().getType() == Material.TRAP_DOOR
                    || event.getClickedBlock().getType() == Material.BEACON
                    || event.getClickedBlock().getType() == Material.NOTE_BLOCK
                    || event.getClickedBlock().getType() == Material.FENCE_GATE)) {

                TownBlock townBlock = TownyUniverse.getTownBlock(event.getClickedBlock().getLocation());

                if (townBlock != null)
                    try {
                        if (townBlock.getTown() != null)
                            if ((townBlock.getTown().getName().equalsIgnoreCase("Safezone") || townBlock.getTown().getName().equalsIgnoreCase("Warzone")) && !(event.getPlayer().isOp() || event.getPlayer().hasPermission("carbyne.bypass")))
                                event.setCancelled(true);
                    } catch (NotRegisteredException e) {
                        e.printStackTrace();
                    }
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        if (player.getLastDamageCause() != null && player.getLastDamageCause().getCause() != null) {
            if (!Cooldowns.tryCooldown(player.getUniqueId(), player.getUniqueId().toString() + ":killmessagecooldown", 30 * 1000))
                return;

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

                        if (killer.getInventory().getItemInMainHand().getType() == Material.BOW) {
                            boolean wasSniped = killer.getLocation().distance(player.getLocation()) > 50;

                            if (killer.getInventory().getItemInMainHand().hasItemMeta() && killer.getInventory().getItemInMainHand().getItemMeta() != null && killer.getInventory().getItemInMainHand().getItemMeta().hasDisplayName()) {
                                JSONMessage message = JSONMessage.create();
                                message.then(ChatColor.translateAlternateColorCodes('&', "&c" + player.getName() + "&e was " + (wasSniped ? "sniped" : "shot down") + " by &c" + killer.getName() + "&e using "));
                                StringBuilder toolTip;

                                toolTip = new StringBuilder(killer.getInventory().getItemInMainHand().getItemMeta().getDisplayName() + "\n");
                                for (Enchantment enchantment : killer.getInventory().getItemInMainHand().getEnchantments().keySet())
                                    toolTip.append("&7").append(MessageManager.getEnchantmentFriendlyName(enchantment)).append(" &7").append(MessageManager.getPotionAmplifierInRomanNumerals(killer.getInventory().getItemInMainHand().getEnchantments().get(enchantment))).append("\n");

                                for (String s : killer.getInventory().getItemInMainHand().getItemMeta().getLore())
                                    toolTip.append(s).append("\n");

                                String type = killer.getInventory().getItemInMainHand().getType().name().substring(0, 1).toUpperCase();
                                toolTip.append("\n" + "&7").append(type).append(killer.getInventory().getItemInMainHand().getType().name().substring(1).toLowerCase().replace("_", " "));

                                message.then(killer.getInventory().getItemInMainHand().getItemMeta().getDisplayName()).tooltip(ChatColor.translateAlternateColorCodes('&', toolTip.toString()));

                                PlayerUtility.getOnlinePlayers().forEach(message::send);
                                event.setDeathMessage("");
                            } else
                                event.setDeathMessage(ChatColor.translateAlternateColorCodes('&', "&c" + player.getName() + "&e was " + (wasSniped ? "sniped" : "shot down") + " by &c" + killer.getName()));
                        } else if (killer.getInventory().getItemInMainHand().hasItemMeta() && killer.getInventory().getItemInMainHand().getItemMeta() != null && killer.getInventory().getItemInMainHand().getItemMeta().hasDisplayName()) {
                            JSONMessage message = JSONMessage.create();
                            message.then(ChatColor.translateAlternateColorCodes('&', "&c" + player.getName() + "&e was killed by &c" + killer.getName() + "&e using "));
                            StringBuilder toolTip;

                            toolTip = new StringBuilder(killer.getInventory().getItemInMainHand().getItemMeta().getDisplayName() + "\n");
                            for (Enchantment enchantment : killer.getInventory().getItemInMainHand().getEnchantments().keySet())
                                toolTip.append("&7").append(MessageManager.getEnchantmentFriendlyName(enchantment)).append(" &7").append(MessageManager.getPotionAmplifierInRomanNumerals(killer.getInventory().getItemInMainHand().getEnchantments().get(enchantment))).append("\n");

                            for (String s : killer.getInventory().getItemInMainHand().getItemMeta().getLore())
                                toolTip.append(s).append("\n");

                            String type = killer.getInventory().getItemInMainHand().getType().name().substring(0, 1).toUpperCase();
                            toolTip.append("\n" + "&7").append(type).append(killer.getInventory().getItemInMainHand().getType().name().substring(1).toLowerCase().replace("_", " "));

                            message.then(killer.getInventory().getItemInMainHand().getItemMeta().getDisplayName()).tooltip(ChatColor.translateAlternateColorCodes('&', toolTip.toString()));

                            PlayerUtility.getOnlinePlayers().forEach(message::send);
                            event.setDeathMessage("");
                            //event.setDeathMessage(ChatColor.translateAlternateColorCodes('&', "&c" + player.getName() + "&e was killed by &c" + killer.getName() + "&e using &c" + killer.getInventory().getItemInMainHand().getItemMeta().getDisplayName()));
                        } else
                            event.setDeathMessage(ChatColor.translateAlternateColorCodes('&', "&c" + player.getName() + "&e was killed by &c" + killer.getName()));
                    }
                    break;
                case PROJECTILE:
                    if (player.getKiller() != null) {
                        Player killer = player.getKiller();

                        if (killer.getInventory().getItemInMainHand().getType() == Material.BOW) {
                            boolean wasSniped = killer.getLocation().distance(player.getLocation()) > 50;

                            if (killer.getInventory().getItemInMainHand().hasItemMeta() && killer.getInventory().getItemInMainHand().getItemMeta() != null && killer.getInventory().getItemInMainHand().getItemMeta().hasDisplayName()) {
                                JSONMessage message = JSONMessage.create();
                                message.then(ChatColor.translateAlternateColorCodes('&', "&c" + player.getName() + "&e was " + (wasSniped ? "sniped" : "shot down") + " by &c" + killer.getName() + "&e using "));
                                StringBuilder toolTip;

                                toolTip = new StringBuilder(killer.getInventory().getItemInMainHand().getItemMeta().getDisplayName() + "\n");
                                for (Enchantment enchantment : killer.getInventory().getItemInMainHand().getEnchantments().keySet())
                                    toolTip.append("&7").append(MessageManager.getEnchantmentFriendlyName(enchantment)).append(" &7").append(MessageManager.getPotionAmplifierInRomanNumerals(killer.getInventory().getItemInMainHand().getEnchantments().get(enchantment))).append("\n");

                                for (String s : killer.getInventory().getItemInMainHand().getItemMeta().getLore())
                                    toolTip.append(s).append("\n");

                                String type = killer.getInventory().getItemInMainHand().getType().name().substring(0, 1).toUpperCase();
                                toolTip.append("\n" + "&7").append(type).append(killer.getInventory().getItemInMainHand().getType().name().substring(1).toLowerCase().replace("_", " "));

                                message.then(killer.getInventory().getItemInMainHand().getItemMeta().getDisplayName()).tooltip(ChatColor.translateAlternateColorCodes('&', toolTip.toString()));

                                PlayerUtility.getOnlinePlayers().forEach(message::send);
                                event.setDeathMessage("");
                            } else
                                event.setDeathMessage(ChatColor.translateAlternateColorCodes('&', "&c" + player.getName() + "&e was " + (wasSniped ? "sniped" : "shot down") + " by &c" + killer.getName()));
                        } else
                            event.setDeathMessage(ChatColor.translateAlternateColorCodes('&', "&c" + player.getName() + "&e was shot down"));
                    }
                    break;
                default:
                    event.setDeathMessage(ChatColor.translateAlternateColorCodes('&', "&c" + player.getName() + "&e died"));
                    break;
            }
        } else
            event.setDeathMessage(ChatColor.translateAlternateColorCodes('&', "&c" + player.getName() + "&e died"));
    }

//    @EventHandler
//    public void onConsume(PlayerItemConsumeEvent event) {
//        if (event.getItem().getType() == Material.GOLDEN_APPLE || event.getItem().getType() == Material.POTION) {
//            new BukkitRunnable() {
//                @Override
//                public void run() {
//                    event.getPlayer().removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
//                }
//            }.runTaskLater(Carbyne.getInstance(), 3L);
//        }
//    }

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
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE && !event.getPlayer().isOp())
            event.setCancelled(true);
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

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerTeleportEvent(PlayerTeleportEvent event) {
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {

            Location location = event.getTo();
            location.setX(location.getBlockX() + 0.5);
            location.setY(location.getBlockY());
            location.setZ(location.getBlockZ() + 0.5);

            if (!allowedBlockIds.contains(location.getBlock().getTypeId()) && !allowedBlockIds.contains(location.getBlock().getRelative(BlockFace.UP).getTypeId()))
                event.setTo(event.getFrom());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPortalEvent(PlayerPortalEvent event) {
        Player player = event.getPlayer();

        new BukkitRunnable() {
            @Override
            public void run() {
                Location l = player.getLocation();
                Location nblock = player.getLocation();
                nblock.setZ(player.getLocation().getZ() - 1.0);
                Location midnblock = player.getLocation();
                midnblock.setZ(player.getLocation().getZ() - 1.0);
                midnblock.setY(player.getLocation().getY() + 1.0);
                Location topnblock = player.getLocation();
                topnblock.setZ(player.getLocation().getZ() - 1.0);
                topnblock.setY(player.getLocation().getY() + 2.0);
                Location sblock = player.getLocation();
                sblock.setZ(player.getLocation().getZ() + 1.0);
                Location midsblock = player.getLocation();
                midsblock.setZ(player.getLocation().getZ() + 1.0);
                midsblock.setY(player.getLocation().getY() + 1.0);
                Location topsblock = player.getLocation();
                topsblock.setZ(player.getLocation().getZ() + 1.0);
                topsblock.setY(player.getLocation().getY() + 2.0);
                Location eblock = player.getLocation();
                eblock.setX(player.getLocation().getX() + 1.0);
                Location mideblock = player.getLocation();
                mideblock.setX(player.getLocation().getX() + 1.0);
                mideblock.setY(player.getLocation().getY() + 1.0);
                Location topeblock = player.getLocation();
                topeblock.setX(player.getLocation().getX() + 1.0);
                topeblock.setY(player.getLocation().getY() + 2.0);
                Location wblock = player.getLocation();
                wblock.setX(player.getLocation().getX() - 1.0);
                Location midwblock = player.getLocation();
                midwblock.setX(player.getLocation().getX() - 1.0);
                midwblock.setY(player.getLocation().getY() + 1.0);
                Location topwblock = player.getLocation();
                topwblock.setX(player.getLocation().getX() - 1.0);
                topwblock.setY(player.getLocation().getY() + 2.0);

                while (nblock.getBlock().getType() == Material.PORTAL) {
                    nblock.setZ(nblock.getZ() - 1.0);
                    midnblock.setZ(nblock.getZ());
                    topnblock.setZ(nblock.getZ());
                }

                while (sblock.getBlock().getType() == Material.PORTAL) {
                    sblock.setZ(sblock.getZ() + 1.0);
                    midsblock.setZ(sblock.getZ());
                    topsblock.setZ(sblock.getZ());
                }

                while (eblock.getBlock().getType() == Material.PORTAL) {
                    eblock.setX(eblock.getX() + 1.0);
                    mideblock.setX(eblock.getX());
                    topeblock.setX(eblock.getX());
                }

                while (wblock.getBlock().getType() == Material.PORTAL) {
                    wblock.setX(wblock.getX() - 1.0);
                    midwblock.setX(wblock.getX());
                    topwblock.setX(wblock.getX());
                }

                if ((!Carbyne.getInstance().getConfig().getIntegerList("allowed-blocks.bottom").contains(nblock.getBlock().getTypeId())
                        || !Carbyne.getInstance().getConfig().getIntegerList("allowed-block.top").contains(midnblock.getBlock().getTypeId()))
                        && (!Carbyne.getInstance().getConfig().getIntegerList("allowed-blocks.bottom").contains(sblock.getBlock().getTypeId())
                        || !Carbyne.getInstance().getConfig().getIntegerList("allowed-block.top").contains(midsblock.getBlock().getTypeId()))
                        && (!Carbyne.getInstance().getConfig().getIntegerList("allowed-blocks.bottom").contains(eblock.getBlock().getTypeId())
                        || !Carbyne.getInstance().getConfig().getIntegerList("allowed-block.top").contains(mideblock.getBlock().getTypeId()))
                        && (!Carbyne.getInstance().getConfig().getIntegerList("allowed-blocks.bottom").contains(wblock.getBlock().getTypeId())
                        || !Carbyne.getInstance().getConfig().getIntegerList("allowed-block.top").contains(midwblock.getBlock().getTypeId()))
                        && (!Carbyne.getInstance().getConfig().getIntegerList("allowed-blocks.bottom").contains(midnblock.getBlock().getTypeId())
                        || !Carbyne.getInstance().getConfig().getIntegerList("allowed-block.top").contains(topnblock.getBlock().getTypeId()))
                        && (!Carbyne.getInstance().getConfig().getIntegerList("allowed-blocks.bottom").contains(midsblock.getBlock().getTypeId())
                        || !Carbyne.getInstance().getConfig().getIntegerList("allowed-block.top").contains(topsblock.getBlock().getTypeId()))
                        && (!Carbyne.getInstance().getConfig().getIntegerList("allowed-blocks.bottom").contains(mideblock.getBlock().getTypeId())
                        || !Carbyne.getInstance().getConfig().getIntegerList("allowed-block.top").contains(topeblock.getBlock().getTypeId()))
                        && (!Carbyne.getInstance().getConfig().getIntegerList("allowed-blocks.bottom").contains(midwblock.getBlock().getTypeId())
                        || !Carbyne.getInstance().getConfig().getIntegerList("allowed-block.top").contains(topwblock.getBlock().getTypeId()))) {

                    HashMap<String, ArrayList<Double>> coordloc = new HashMap<>();
                    ArrayList<Double> xloc = new ArrayList<>();
                    ArrayList<Double> zloc = new ArrayList<>();
                    startLocations.put(player, l);
                    xloc.add(l.getX() - 2.0);
                    xloc.add(l.getX() - 1.0);
                    xloc.add(l.getX());
                    xloc.add(l.getX() + 1.0);
                    xloc.add(l.getX() + 2.0);
                    coordloc.put("x: ", xloc);
                    zloc.add(l.getZ() - 2.0);
                    zloc.add(l.getZ() - 1.0);
                    zloc.add(l.getZ());
                    zloc.add(l.getZ() + 1.0);
                    zloc.add(l.getZ() + 2.0);
                    coordloc.put("z: ", zloc);
                    portalLocations.put(player, coordloc);
                    Block block = player.getLocation().getWorld().getBlockAt(player.getLocation());
                    block.setType(Material.AIR);
                }
            }
        }.runTaskLater(Carbyne.getInstance(), 1L);
    }

    @EventHandler
    public void onPortalTpLeave(PlayerTeleportEvent event) {
        Player p = event.getPlayer();

        if (event.getCause() != PlayerTeleportEvent.TeleportCause.END_PORTAL || event.getCause() != PlayerTeleportEvent.TeleportCause.NETHER_PORTAL)
            return;

        if (!portalLocations.containsKey(p))
            return;

        HashMap<String, ArrayList<Double>> coordloc = portalLocations.get(p);
        ArrayList<Double> xloc = coordloc.get("x: ");
        ArrayList<Double> zloc = coordloc.get("z: ");

        if (!xloc.contains(event.getTo().getX()) && !zloc.contains(event.getTo().getZ())) {
            Location l = startLocations.get(p);
            Block block = l.getWorld().getBlockAt(l);
            block.setType(Material.FIRE);
            coordloc.clear();
            portalLocations.remove(p);
            startLocations.remove(p);
        }
    }

    @EventHandler
    public void onPortalLeave(PlayerMoveEvent event) {
        Player p = event.getPlayer();

        if (!portalLocations.containsKey(p))
            return;

        HashMap<String, ArrayList<Double>> coordloc = portalLocations.get(p);
        ArrayList<Double> xloc = coordloc.get("x: ");
        ArrayList<Double> zloc = coordloc.get("z: ");

        if (xloc.get(0) > event.getTo().getX() || xloc.get(4) < event.getTo().getX() || zloc.get(0) > event.getTo().getZ() || zloc.get(4) < event.getTo().getZ()) {
            Location l = startLocations.get(p);
            Block block = l.getWorld().getBlockAt(l);
            block.setType(Material.FIRE);
            coordloc.clear();
            portalLocations.remove(p);
            startLocations.remove(p);
        }
    }

    @EventHandler
    public void onDespawn(LeavesDecayEvent event) {
        if (!(event.getBlock().getWorld().getName().equalsIgnoreCase("player_world") || event.getBlock().getWorld().getName().equalsIgnoreCase("player_world_nether")))
            event.setCancelled(true);
    }


    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        if (event.getMessage().startsWith("//") && !player.hasPermission("carbyne.command.bypass"))
            event.setCancelled(true);

        if (event.getMessage().startsWith("/wor") && !player.hasPermission("carbyne.command.bypass"))
            event.setCancelled(true);
    }

    @EventHandler
    public void onConsoleCommand(PlayerCommandPreprocessEvent event) {
        if (event.getMessage().split(" ")[0].contains(":") && !event.getPlayer().isOp()) {
            event.setCancelled(true);
            MessageManager.sendMessage(event.getPlayer(), "&cSorry, that command syntax is not supported.");
        }
    }

    @EventHandler
    public void onEnterInTheMinecart(VehicleEnterEvent event) {
        if (!(event.getVehicle() instanceof Minecart) || !(event.getEntered() instanceof Player))
            return;

        Player player = (Player) event.getEntered();
        Location minecartLoc = event.getVehicle().getLocation();
        Location qualifierLoc = new Location(player.getWorld(), (double) minecartLoc.getBlockX(), (double) minecartLoc.getBlockY(), (double) minecartLoc.getBlockZ());
        Material material = qualifierLoc.getBlock().getType();

        if (material == Material.FENCE_GATE || material == Material.SIGN_POST) {
            event.setCancelled(true);

            if (player.isSneaking()) {
                player.teleport(getCoords(qualifierLoc, qualifierLoc.getBlockY(), 254));
                MessageManager.sendMessage(player, "&aSuccessfully teleported to the top.");
                player.playSound(player.getLocation(), Sound.ENTITY_ENDERMEN_TELEPORT, 10.0f, 1.0f);
            }
        }
    }

//    @EventHandler
//    public void onTeleport(PlayerTeleportEvent event) {
//        if (event.getPlayer().isFlying() && event.getPlayer().getFallDistance() > 0.0F)
//            event.getPlayer().setFallDistance(0.0F);
//    }

    @EventHandler
    void onProjectileLaunch(final ProjectileLaunchEvent event) {
        if (event.getEntityType() == EntityType.SPLASH_POTION) {
            Projectile projectile = event.getEntity();

            if (projectile.getShooter() instanceof Player && ((Player) projectile.getShooter()).isSprinting()) {
                Vector velocity = projectile.getVelocity();

                velocity.setY(velocity.getY() - 1.0);
                projectile.setVelocity(velocity);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onMine(BlockBreakEvent event) {
        if (event.getPlayer().getGameMode() != GameMode.SURVIVAL)
            return;

        if (event.getBlock().getType() == Material.COAL_ORE) {
            int r = ThreadLocalRandom.current().nextInt(100);

            if (r <= Carbyne.getInstance().getConfig().getInt("sulphur-drop-rate")) {
                event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.SULPHUR, 1));
                return;
            }
        }

        if (event.getBlock().getType() == Material.SAND) {
            int r = ThreadLocalRandom.current().nextInt(100);

            if (r <= Carbyne.getInstance().getConfig().getInt("glass-drop-rate"))
                event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.GLASS, 1));
        }

        if (event.getBlock().getType() == Material.GOLD_ORE) {
            if (!event.getPlayer().getInventory().getItemInMainHand().getEnchantments().keySet().contains(Enchantment.SILK_TOUCH)) {
                event.getBlock().setType(Material.AIR);
                int r = ThreadLocalRandom.current().nextInt(Carbyne.getInstance().getConfig().getInt("gold-ore-min"), Carbyne.getInstance().getConfig().getInt("gold-ore-max"));
                event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.GOLD_INGOT, r));
            }
        }

        if (event.getBlock().getType() == Material.STONE) {
            double random = Math.random();

            if (random < Carbyne.getInstance().getConfig().getDouble("gold-nugget-droprate"))
                event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.GOLD_NUGGET));
        }
    }

    @EventHandler
    public void CrystalInteraction(EntityExplodeEvent event) {
        Entity entity = event.getEntity();

        if (entity.getType() == EntityType.ENDER_CRYSTAL)
            event.setCancelled(true);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        Player p = e.getPlayer();

        if (p.hasPermission("carbyne.endercrystal.place")) {
            Block block = e.getBlock();

            if (block.getType() == Material.BEDROCK) {
                block.getWorld().spawnEntity(block.getLocation(), EntityType.ENDER_CRYSTAL);
                block.setType(Material.AIR);
                p.playSound(p.getLocation(), Sound.ENTITY_ENDERMEN_DEATH, 2.0f, 1.0f);
                p.playSound(p.getLocation(), Sound.ENTITY_ENDERDRAGON_DEATH, 2.0f, 1.0f);
            }
        }
    }

    @EventHandler
    public void onFish(PlayerFishEvent event) {
        Entity entity = event.getCaught();

        if (entity == null)
            return;

        if (Cooldowns.tryCooldown(event.getPlayer().getUniqueId(), "BigAssFish", 5000)) {
            if (entity instanceof Player) {
                Player player = (Player) entity;
                event.getPlayer().chat("Oh my god we caught a big ass " + player.getName() + ", reel that fat bitch in. Yea!");
            } else if (entity instanceof Item) {
                Item item = (Item) entity;
                event.getPlayer().chat("Oh my god we caught a big ass " + WordUtils.capitalizeFully(item.getItemStack().getType().name().replace("_", " ")) + ", reel that fat bitch in. Yea!");
            } else
                event.getPlayer().chat("Oh my god we caught a big ass " + WordUtils.capitalizeFully(entity.getType().name().replace("_", "")) + ", reel that fat bitch in. Yea!");
        }
    }

    public Location getCoords(Location loc, int min, int max) {
        for (int tp = min; tp < max; ++tp) {
            Material material1 = new Location(loc.getWorld(), (double) loc.getBlockX(), (double) tp, (double) loc.getBlockZ()).getBlock().getType();
            Material material2 = new Location(loc.getWorld(), (double) loc.getBlockX(), (double) (tp + 1), (double) loc.getBlockZ()).getBlock().getType();

            if (material1 == Material.AIR && material2 == Material.AIR)
                return new Location(loc.getWorld(), (double) loc.getBlockX(), (double) tp, (double) loc.getBlockZ());
        }

        return new Location(loc.getWorld(), (double) loc.getBlockX(), (double) loc.getWorld().getHighestBlockYAt(loc.getBlockX(), loc.getBlockZ()), (double) loc.getBlockZ());
    }
}
