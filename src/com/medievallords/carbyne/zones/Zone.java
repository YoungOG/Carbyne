package com.medievallords.carbyne.zones;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.region.Selection;
import com.medievallords.carbyne.utils.Maths;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.mobs.MythicMob;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;


@Getter
@Setter
public class Zone {

    private String name, displayName;
    private Selection selection;
    private Set<UUID> playersInZone = new HashSet<>();
    private Map<MythicMob, Integer> mobs = new HashMap<>();
    private int maxMobs, amountOfMobs;
    private double minDistance;
    private boolean run = true;
    private int cooldown = 0;
    private List<Location> locations = new ArrayList<>();


    public Zone(String name, Selection selection) {
        this.name = name;
        this.selection = selection;

        initialize();
    }

    public void initialize() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (run) {
                    if (mobs.isEmpty())
                        return;

                    tick();
                } else
                    cancel();
            }
        }.runTaskTimerAsynchronously(Carbyne.getInstance(), 100, 5);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (run) {
                    if (mobs.isEmpty())
                        return;

                    handleLocations();

                    if (amountOfMobs >= (maxMobs * playersInZone.size()))
                        return;

                    if (cooldown > 0) {
                        cooldown--;
                        return;
                    }

                    if (locations.isEmpty())
                        return;

                    int sum = 0;
                    for (Integer value : mobs.values())
                        sum += value;

                    int randomMax = Maths.randomNumberBetween(playersInZone.size(), 1);

                    for (int i = 0; i < randomMax; i++) {
                        if (locations.isEmpty()) {
                            return;
                        }

                        int randomNumber = Maths.randomNumberBetween(locations.size(), 0);
                        Location randomLocation = locations.get(randomNumber).add(0.5, 0, 0.5);
                        locations.remove(randomNumber);

                        int random = Maths.randomNumberBetween(sum, 0);
                        int last = 0;

                        for (MythicMob mob : mobs.keySet()) {
                            int value = mobs.get(mob);

                            if (random >= last && random < value + last) {
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        mob.spawn(BukkitAdapter.adapt(randomLocation), 1);
                                    }
                                }.runTask(Carbyne.getInstance());

                                amountOfMobs++;
                                cooldown += (int) (Math.pow(amountOfMobs / playersInZone.size(), 2) * 0.25);
//                                MessageManager.broadcastMessage("Z: " + displayName + " A: " + amountOfMobs + " C: " + cooldown, "carbyne.debug");
                                break;
                            } else
                                last = value + last;
                        }
                    }
                } else
                    cancel();
            }
        }.runTaskTimerAsynchronously(Carbyne.getInstance(), 0, 20);
    }

    public void tick() {
        List<Player> players = findPlayers();
        if (!players.isEmpty()) {
            Location randomLocation = findRandomLocation(players.get(Maths.randomNumberBetween(players.size(), 0)).getLocation());
            if (randomLocation != null)
                locations.add(randomLocation);

            randomLocation = findRandomLocation(players.get(Maths.randomNumberBetween(players.size(), 0)).getLocation());
            if (randomLocation != null)
                locations.add(randomLocation);

            randomLocation = findRandomLocation(players.get(Maths.randomNumberBetween(players.size(), 0)).getLocation());
            if (randomLocation != null)
                locations.add(randomLocation);
        }
    }

    private void handleLocations() {
        if (locations.size() > 50) {
            int middle = (int) Math.floor((double) locations.size() / 2d);
            locations = locations.subList(middle - 25, middle + 25);
        }
    }

    public void reduceMob() {
        amountOfMobs--;
        cooldown += (int) ((0.5 * (double) cooldown) / ((double) amountOfMobs / (double) cooldown));
    }

    public boolean isInZone(Location location) {
        int minX = Math.min(selection.getLocation1().getBlockX(), selection.getLocation2().getBlockX());
        int minY = Math.min(selection.getLocation1().getBlockY(), selection.getLocation2().getBlockY());
        int minZ = Math.min(selection.getLocation1().getBlockZ(), selection.getLocation2().getBlockZ());
        int maxX = Math.max(selection.getLocation1().getBlockX(), selection.getLocation2().getBlockX());
        int maxY = Math.max(selection.getLocation1().getBlockY(), selection.getLocation2().getBlockY());
        int maxZ = Math.max(selection.getLocation1().getBlockZ(), selection.getLocation2().getBlockZ());

        return (minX <= location.getBlockX() && location.getBlockX() <= maxX && minY <= location.getBlockY() && location.getBlockY() <= maxY && minZ <= location.getBlockZ() && location.getBlockZ() <= maxZ);
    }


    private Location findRandomLocation(Location location) {
        double x = Math.sin(Maths.randomNumberBetween(10, 0)) * (minDistance + (double) Maths.randomNumberBetween(30, 0));
        double y = Maths.randomNumberBetween(10, -10);
        double z = Math.cos(Maths.randomNumberBetween(10, 0)) * (minDistance + (double) Maths.randomNumberBetween(30, 0));
        Location newLocation = location.clone().add(x, y, z);

        if (hasMetCondition(newLocation))
            return newLocation;
        else
            return null;
    }

    private boolean hasMetCondition(Location location) {
        Location clone = location.clone();

        if (!clone.getBlock().getType().isSolid()) {
            clone.add(0, 1, 0);

            if (!clone.getBlock().getType().isSolid()) {
                clone.subtract(0, 2, 0);

                if (clone.getBlock().getType().isSolid())
                    return (int) location.getBlock().getLightLevel() < 10 || (location.getWorld().getTime() > 12000 && location.getWorld().getTime() < 24000);

            }
        }

        return false;
    }

    private List<Player> findPlayers() {
        List<Player> players = new ArrayList<>();
        Location last = new Location(selection.getLocation1().getWorld(), 0, 0, 0);

        for (UUID uuid : playersInZone) {
            Player player = Bukkit.getPlayer(uuid);

            if (player != null) {
                if (last.getWorld().equals(player.getWorld()))
                    if (last.distance(player.getLocation()) > 30)
                        players.add(player);

                last = player.getLocation();
            }
        }

        return players;
    }
}
