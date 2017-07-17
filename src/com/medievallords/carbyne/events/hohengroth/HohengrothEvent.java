package com.medievallords.carbyne.events.hohengroth;

import com.medievallords.carbyne.events.Event;
import com.medievallords.carbyne.events.EventManager;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.github.paperspigot.Title;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Williams on 2017-06-16
 * for the Carbyne project.
 */
@Getter
@Setter
public class HohengrothEvent extends Event{

    private List<HohengrothPlayer> playerStats = new ArrayList<>();

    private List<Player> fightingBoss = new ArrayList<>();

    private Entity hohengrothEntity = null;
    private Entity bansheeEntity = null;
    private Entity librarianEntity = null;


    private boolean open = true, bansheeActive, hohengrothActive, librarianActive;

    private Location[] bossLocations = new Location[]{

    };

    private Location checkPoint;

    private Location enterEventLocation = new Location(Bukkit.getServer().getWorld(""), 0, 0, 0);
    private Location enterBansheeLocation = new Location(Bukkit.getServer().getWorld(""), 0, 0, 0);

    private Location[] walls = new Location[]{
            new Location(Bukkit.getServer().getWorld(""), 0, 0, 0),
            new Location(Bukkit.getServer().getWorld(""), 0, 0, 0),
            new Location(Bukkit.getServer().getWorld(""), 0, 0, 0),
            new Location(Bukkit.getServer().getWorld(""), 0, 0, 0),
            new Location(Bukkit.getServer().getWorld(""), 0, 0, 0),
            new Location(Bukkit.getServer().getWorld(""), 0, 0, 0),
    };

    private Location[] spawnPoints = new Location[]{

    };

    private double cofferChance;


    public HohengrothEvent(EventManager eventManager) {
        super(eventManager);
        cofferChance = getCofferChancePercent(eventManager.getHohengrothCoffer().getCofferPercent());
    }

    @Override
    public void tick() {
    }

    public void openWall(int wall) {
        int index = wall - 1;
        Location loc1 = walls[index];
        Location loc2 = walls[index + 2];

        double minX = Math.min(loc1.getX(), loc2.getX());
        double minY = Math.min(loc1.getY(), loc2.getY());
        double minZ = Math.min(loc1.getZ(), loc2.getZ());

        double maxX = Math.min(loc1.getX(), loc2.getX());
        double maxY = Math.min(loc1.getY(), loc2.getY());
        double maxZ = Math.min(loc1.getZ(), loc2.getZ());

        for (double x = minX; x < maxX; x++) {
            for (double y = minY; y < maxY; y++) {
                for (double z = minZ; z < maxZ; z++) {
                    Location location = new Location(loc1.getWorld(), x, y, z);
                    Block block = location.getBlock();
                    if (block != null && block.getType() != Material.AIR && block.getType() == Material.STAINED_GLASS && block.getData() == 10) {
                        block.setType(Material.AIR);
                    }
                }
            }
        }
    }

    public void spawnBanshee() {
        ActiveMob mob = MythicMobs.inst().getMobManager().spawnMob("Banshee", bossLocations[0]);
        bansheeEntity = mob.getEntity().getBukkitEntity();
        ((LivingEntity) bansheeEntity).setMaxHealth(getHealthScale(mob.getType().getHealth()));
        ((LivingEntity) bansheeEntity).setHealth(((LivingEntity) bansheeEntity).getMaxHealth());
    }

    public void spawnHohengroth() {
        ActiveMob mob = MythicMobs.inst().getMobManager().spawnMob("Hohengroth", bossLocations[2]);
        hohengrothEntity = mob.getEntity().getBukkitEntity();
        ((LivingEntity) hohengrothEntity).setMaxHealth(getHealthScale(mob.getType().getHealth()));
        ((LivingEntity) hohengrothEntity).setHealth(((LivingEntity) hohengrothEntity).getMaxHealth());
    }

    public void spawnLibrarian() {
        ActiveMob mob = MythicMobs.inst().getMobManager().spawnMob("Librarian", bossLocations[1]);
        librarianEntity = mob.getEntity().getBukkitEntity();
        ((LivingEntity) librarianEntity).setMaxHealth(getHealthScale(mob.getType().getHealth()));
        ((LivingEntity) librarianEntity).setHealth(((LivingEntity) librarianEntity).getMaxHealth());
    }

    @Override
    public synchronized void start() {
        super.start();
    }

    @Override
    public synchronized void stop() {
        super.stop();
    }

    public void closeWall(int wall) {
        int index = wall - 1;
        Location loc1 = walls[index];
        Location loc2 = walls[index + 2];

        double minX = Math.min(loc1.getX(), loc2.getX());
        double minY = Math.min(loc1.getY(), loc2.getY());
        double minZ = Math.min(loc1.getZ(), loc2.getZ());

        double maxX = Math.min(loc1.getX(), loc2.getX());
        double maxY = Math.min(loc1.getY(), loc2.getY());
        double maxZ = Math.min(loc1.getZ(), loc2.getZ());

        for (double x = minX; x < maxX; x++) {
            for (double y = minY; y < maxY; y++) {
                for (double z = minZ; z < maxZ; z++) {
                    Location location = new Location(loc1.getWorld(), x, y, z);
                    Block block = location.getBlock();
                    if (block.getType() == Material.AIR) {
                        block.setType(Material.STAINED_GLASS);
                        block.setData((byte) 10);
                    }
                }
            }
        }
    }

    public void enterLibraryPlayer(Player player) {
        if (!librarianActive) {
            librarianActive = true;
        }

        player.teleport(spawnPoints[3]);
    }

    public void teleportToPortal(Player player) {
        player.teleport(spawnPoints[2]);
    }

    public void leaveBansheePlayer(Player player) {

    }

    public void leaveLibraryPlayer(Player player) {

    }

    public void leaveHohengrothPlayer(Player player) {

    }

    public void enterBansheePlayer(Player player) {
        player.teleport(spawnPoints[1]);
    }

    public void enterPlayer(Player player) {
        participants.add(player);
        playerStats.add(new HohengrothPlayer(player.getUniqueId()));

        player.teleport(spawnPoints[0]);
        player.sendTitle(new Title.Builder().title(ChatColor.translateAlternateColorCodes('&', "&aHohengroth")).subtitle(ChatColor.translateAlternateColorCodes('6', "")).stay(33).build());

    }

    public double getHealthScale(double health) {
        return health + ((health * (double) participants.size()) * 0.65);
    }

    public double getCofferChancePercent(double cofferChance) {
        if (cofferChance >= 1) {
            return 0.2;
        } else if (cofferChance <= 0) {
            return 0;
        }

        return 0.2 * cofferChance;
    }
}