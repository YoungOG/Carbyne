package com.medievallords.carbyne.zones;

import com.medievallords.carbyne.conquerpoints.objects.ConquerPoint;
import com.medievallords.carbyne.customevents.LootChestLootEvent;
import com.medievallords.carbyne.lootchests.Loot;
import com.medievallords.carbyne.region.Selection;
import com.medievallords.carbyne.utils.Maths;
import com.medievallords.carbyne.utils.StaticClasses;
import io.lumine.xikage.mythicmobs.mobs.MythicMob;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Chest;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;


@Getter
@Setter
public class Zone {

    private String name, displayName;
    private Selection selection;
    private Set<UUID> playersInZone = new HashSet<>();
    private Map<EntityType, List<MobData>> mobs = new HashMap<>();
    private int maxMobs, amountOfMobs, cooldown = 0;
    private double minDistance;
    private boolean run = true, nerfedZone = false;
    private List<Location> locations = new ArrayList<>();
    private HashMap<String, Integer> lootTables = new HashMap<>();
    private long cooldownForChests;

    public Zone(String name, Selection selection) {
        this.name = name;
        this.selection = selection;

        //initialize();
    }

    //    public void initialize() {
//        new BukkitRunnable() {
//            @Override
//            public void run() {
//                if (run) {
//                    if (mobs.isEmpty())
//                        return;
//
//                    tick();
//                } else
//                    cancel();
//            }
//        }.runTaskTimerAsynchronously(Carbyne.getInstance(), 100, 5);
//
//        new BukkitRunnable() {
//            @Override
//            public void run() {
//                if (run) {
//                    if (mobs.isEmpty())
//                        return;
//
//                    handleLocations();
//
//                    if (amountOfMobs >= (maxMobs * playersInZone.size()))
//                        return;
//
//                    if (cooldown > 0) {
//                        cooldown--;
//                        return;
//                    }
//
//                    if (locations.isEmpty())
//                        return;
//
//                    int sum = 0;
//                    for (Integer value : mobs.values())
//                        sum += value;
//
//                    int randomMax = Maths.randomNumberBetween(playersInZone.size(), 1);
//
//                    for (int i = 0; i < randomMax; i++) {
//                        if (locations.isEmpty())
//                            return;
//
//                        int randomNumber = Maths.randomNumberBetween(locations.size(), 0);
//                        Location randomLocation = locations.get(randomNumber).add(0.5, 0, 0.5);
//                        locations.remove(randomNumber);
//
//                        int random = Maths.randomNumberBetween(sum, 0);
//                        int last = 0;
//
//                        for (MythicMob mob : mobs.keySet()) {
//                            int value = mobs.get(mob);
//
//                            if (random >= last && random < value + last) {
//                                new BukkitRunnable() {
//                                    @Override
//                                    public void run() {
//                                        mob.spawn(BukkitAdapter.adapt(randomLocation), 1);
//                                    }
//                                }.runTask(Carbyne.getInstance());
//
//                                amountOfMobs++;
//                                cooldown += (int) (Math.pow(amountOfMobs / playersInZone.size(), 2) * 0.25);
//                                MessageManager.broadcastMessage("Z: " + displayName + " A: " + amountOfMobs + " C: " + cooldown, "carbyne.debug");
//                                break;
//                            } else
//                                last = value + last;
//                        }
//                    }
//                } else
//                    cancel();
//            }
//        }.runTaskTimerAsynchronously(Carbyne.getInstance(), 0, 20);
//    }
//
//    public void tick() {
//        List<Player> players = findPlayers();
//
//        if (!players.isEmpty()) {
//            Location randomLocation = findRandomLocation(players.get(Maths.randomNumberBetween(players.size(), 0)).getLocation());
//            if (randomLocation != null)
//                locations.add(randomLocation);
//
//            randomLocation = findRandomLocation(players.get(Maths.randomNumberBetween(players.size(), 0)).getLocation());
//            if (randomLocation != null)
//                locations.add(randomLocation);
//
//            randomLocation = findRandomLocation(players.get(Maths.randomNumberBetween(players.size(), 0)).getLocation());
//            if (randomLocation != null)
//                locations.add(randomLocation);
//        }
//    }
//
//    private void handleLocations() {
//        if (locations.size() > 50) {
//            int middle = (int) Math.floor((double) locations.size() / 2d);
//            locations = locations.subList(middle - 25, middle + 25);
//        }
//    }
//
//    public void reduceMob() {
//        amountOfMobs--;
//        cooldown += (int) ((0.5 * (double) cooldown) / ((double) amountOfMobs / (double) cooldown));
//    }
//
    public boolean isInZone(Location location) {
        if (!location.getWorld().equals(selection.getLocation1().getWorld())) {
            return false;
        }

        int minX = Math.min(selection.getLocation1().getBlockX(), selection.getLocation2().getBlockX());
        int minY = Math.min(selection.getLocation1().getBlockY(), selection.getLocation2().getBlockY());
        int minZ = Math.min(selection.getLocation1().getBlockZ(), selection.getLocation2().getBlockZ());
        int maxX = Math.max(selection.getLocation1().getBlockX(), selection.getLocation2().getBlockX());
        int maxY = Math.max(selection.getLocation1().getBlockY(), selection.getLocation2().getBlockY());
        int maxZ = Math.max(selection.getLocation1().getBlockZ(), selection.getLocation2().getBlockZ());
        return (minX <= location.getBlockX() && location.getBlockX() <= maxX && minY <= location.getBlockY() && location.getBlockY() <= maxY && minZ <= location.getBlockZ() && location.getBlockZ() <= maxZ);
    }
//
//
//    private Location findRandomLocation(Location location) {
//        double x = Math.sin(Maths.randomNumberBetween(10, 0)) * (minDistance + (double) Maths.randomNumberBetween(30, 0));
//        double y = Maths.randomNumberBetween(10, -10);
//        double z = Math.cos(Maths.randomNumberBetween(10, 0)) * (minDistance + (double) Maths.randomNumberBetween(30, 0));
//        Location newLocation = location.clone().add(x, y, z);
//
//        if (hasMetCondition(newLocation))
//            return newLocation;
//        else
//            return null;
//    }
//
//    private boolean hasMetCondition(Location location) {
//        try {
//            if (location.getChunk().isLoaded()) {
//                if (!location.getBlock().getType().isSolid()) {
//                    location.add(0, 1, 0);
//
//                    if (!location.getBlock().getType().isSolid()) {
//                        location.subtract(0, 2, 0);
//
//                        return location.add(0, 1, 0).getBlock().getType().isSolid();
//
//                    }
//                }
//            }
//
//            return false;
//        } catch (IllegalStateException e) {
//            Bukkit.getLogger().warning("Zone error: " + name + ", location: " + location.getWorld().getName());
//            Bukkit.getLogger().warning("X: " + location.getX());
//            Bukkit.getLogger().warning("Y: " + location.getY());
//            Bukkit.getLogger().warning("Z: "+ location.getZ());
//            return false;
//        }
//    }
//
//    private List<Player> findPlayers() {
//        List<Player> players = new ArrayList<>();
//        Location last = new Location(selection.getLocation1().getWorld(), 0, 0, 0);
//
//        for (UUID uuid : playersInZone) {
//            Player player = Bukkit.getPlayer(uuid);
//
//            if (player != null) {
//                if (last.getWorld().equals(player.getWorld()))
//                    if (last.distance(player.getLocation()) > 30)
//                        players.add(player);
//
//                last = player.getLocation();
//            }
//        }
//
//        return players;
//    }

    public MythicMob getRandomMob() {
        List<MobData> mobList = new ArrayList<>();
        int sum = 0;
        for (List<MobData> mobDataList : mobs.values()) {
            for (MobData mobData : mobDataList) {
                sum += mobData.getChance();
                mobList.add(mobData);
            }
        }

        int random = Maths.randomNumberBetween(sum, 0);
        int last = 0;

        for (MobData mobData : mobList) {
            int value = mobData.getChance();

            if (random >= last && random < value + last) {
                return mobData.getMob();

            } else {
                last = value + last;
            }
        }

        return null;
    }

    public MythicMob getRandomMob(List<MobData> mobDataList) {
        List<MobData> mobList = new ArrayList<>();
        int sum = 0;
        for (MobData mobData : mobDataList) {
            sum += mobData.getChance();
            mobList.add(mobData);
        }

        int random = Maths.randomNumberBetween(sum, 0);
        int last = 0;

        for (MobData mobData : mobList) {
            int value = mobData.getChance();

            if (random >= last && random < value + last) {
                return mobData.getMob();

            } else {
                last = value + last;
            }
        }

        return null;
    }

    private List<Loot> getRandomLoot() {
        List<Loot> loots = new ArrayList<>();

        int sum = 0;
        for (Integer value : lootTables.values())
            sum += value;


        int random = Maths.randomNumberBetween(sum, 0);
        int last = 0;

        for (String lootTable : lootTables.keySet()) {
            int value = lootTables.get(lootTable);

            if (random >= last && random < value + last) {
                loots.addAll(getLoot(lootTable));

                break;
            } else
                last = value + last;
        }

        return loots;
    }

    public List<Loot> getLoot(String table) {
        List<Loot> loots = new ArrayList<>();
        StaticClasses.lootChestManager.getLootTables().get(table).forEach(l -> {
            if (l.shouldSpawnItem())
                loots.add(l);
        });
        return loots;
    }

    public void giveLoot(final Player player, final Chest chest) {
        for (int i = 0; i < chest.getBlockInventory().getSize(); i++) {
            ItemStack item = chest.getBlockInventory().getItem(i);
            if (item == null)
                continue;

            for (String lootTable : lootTables.keySet()) {
                List<Loot> loots = getLoot(lootTable);
                for (Loot loot : loots) {
                    ItemStack lootItem = loot.getItem();
                    if (lootItem.getType() == item.getType()) {
                        if (lootItem.hasItemMeta() && item.hasItemMeta()) {
                            if (lootItem.getItemMeta().getLore() == item.getItemMeta().getLore())
                                chest.getBlockInventory().setItem(i, null);
                        } else
                            chest.getBlockInventory().setItem(i, null);
                    }
                }
            }
        }

        List<Loot> loots = getRandomLoot();

        LootChestLootEvent event = new LootChestLootEvent(player, chest, loots);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }

        for (Loot loot : loots)
            chest.getBlockInventory().addItem(loot.getItem());
    }
}
