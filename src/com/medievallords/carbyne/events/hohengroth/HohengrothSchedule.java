package com.medievallords.carbyne.events.hohengroth;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.utils.LocationSerialization;
import com.medievallords.carbyne.utils.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Created by Williams on 2017-06-16
 * for the Carbyne project.
 */
public class HohengrothSchedule implements Listener {

    private Carbyne main = Carbyne.getInstance();

    public int timerId;

    public int hoardReclamation;
    public int requiredHoardReclamation;

    public Location startPosition;
    public Location portalPosition;

    public Location enterButtonLocation;
    public Location enterFirstPortalButtonLocation;

    public List<String> startWall = new ArrayList<>();
    public Material startWallMat = Material.STAINED_GLASS;

    public List<String> wallOne = new ArrayList<>();
    public Material wallOneMat = Material.STAINED_GLASS;

    public List<String> wallTwo = new ArrayList<>();
    public Material wallTwoMat = Material.STAINED_GLASS;

    public boolean canDonate = true;
    private boolean eventActive = false;
    private boolean eventRunning = false;

    private HohengrothEvent currentEvent;

    private ArrayList<UUID> participants = new ArrayList<>();

    public HohengrothSchedule() {
        //load();
        //init();
    }

    public void init() {
        int delay = 60 - new Date().getMinutes();

        new BukkitRunnable() {
            @Override
            public void run() {

                canDonate = false;

                scheduleEvent();

                startTimer();

            }
        }.runTaskLater(main, (20 * 60) * delay);
    }

    public void startTimer() {
        timerId = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Carbyne.getInstance(), new Runnable() {
            int hour = 0;

            @Override
            public void run() {

                canDonate = false;

                scheduleEvent();
            }
        }, 0, 216000);
    }

    public void scheduleEvent() {
        eventActive = true;

        /*for (Player player : PlayerUtility.getOnlinePlayers()) {
            player.sendTitle(new Title.Builder().title(ChatColor.GREEN + "Hohengroth event active").stay(55).build());
            player.playSound(player.getLocation(), Sound.ENDERDRAGON_GROWL, 10f, (float) Math.random() * 5);
        }*/

        setupWalls();

        /*for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            MessageManager.sendMessage(player, "" + 100);
        }*/

        new BukkitRunnable() {
            int time = 1;

            @Override
            public void run() {
                time++;

                /*float perc = time / 10; // 300
                float dis = (1 - perc);
                for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                     // 300
                    MessageManager.sendMessage(player, "" + dis);
                }*/

                if (time >= 10) { // 300
                    startEvent();
                    cancel();
                }
            }
        }.runTaskTimer(main, 0, 20);
    }

    public void setupWalls() {
        for (String loc : startWall) {
            Location location = LocationSerialization.deserializeLocation(loc);

            if (location.getBlock().getType() != Material.AIR) {
                continue;
            }

            location.getBlock().setType(startWallMat);
            location.getBlock().setData((byte) 10);
        }

        for (String loc : wallOne) {
            Location location = LocationSerialization.deserializeLocation(loc);

            if (location.getBlock().getType() != Material.AIR) {
                continue;
            }

            location.getBlock().setType(wallOneMat);
            location.getBlock().setData((byte) 10);
        }

        for (String loc : wallTwo) {
            Location location = LocationSerialization.deserializeLocation(loc);

            if (location.getBlock().getType() != Material.AIR) {
                continue;
            }

            location.getBlock().setType(main.getHohengrothSchedule().wallTwoMat);
            location.getBlock().setData((byte) 10);
        }

    }

    public void startEvent() {
        removeStartWall();

        currentEvent = new HohengrothEvent(main.getEventManager(), participants);
        eventActive = false;
        eventRunning = true;
        participants.clear();
    }

    public void stopEvent() {
        currentEvent = null;
        eventRunning = false;
    }

    public void save() {
        ConfigurationSection cs = main.getEventsFileConfiguration().getConfigurationSection("Hohengroth");

        if (cs == null) {
            main.getEventsFileConfiguration().createSection("Hohengroth");
        }

        cs.set("StartWall", startWall);
        cs.set("WallOne", wallOne);
        cs.set("WallTwo", wallTwo);
        cs.set("StartPosition", startPosition);
        cs.set("PortalButton", enterFirstPortalButtonLocation);
        cs.set("StartButton", enterButtonLocation);
        cs.set("PortalPosition", portalPosition);

        saveAndLoadConfig();
    }

    public void load() {
        ConfigurationSection cs = main.getEventsFileConfiguration().getConfigurationSection("Hohengroth");

        if (cs == null) {
            main.getEventsFileConfiguration().createSection("Hohengroth");
            return;
        }

        List<String> wallOneList = cs.getStringList("WallOne");
        List<String> wallTwoList = cs.getStringList("WallTwo");
        List<String> startWallList = cs.getStringList("StartWall");

        if (wallOneList != null) {
            wallOne.addAll(wallOneList);
        }

        if (wallTwoList != null) {
            wallTwo.addAll(wallTwoList);
        }
        if (startWallList != null) {
            startWall.addAll(startWallList);
        }

        enterButtonLocation = (Location) cs.get("StartButton");
        startPosition = (Location) cs.get("StartPosition");
        portalPosition = (Location) cs.get("PortalPosition");
        enterFirstPortalButtonLocation = (Location) cs.get("PortalButton");


    }

    public void saveAndLoadConfig() {
        try {

            main.getEventsFileConfiguration().save(main.getEventsFile());
            main.setEventsFileConfiguration(YamlConfiguration.loadConfiguration(main.getEventsFile()));

        } catch (IOException e) {
            Bukkit.getServer().getLogger().log(Level.WARNING, "Could not save and load events.yml");
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {

        if (event.getAction() == Action.PHYSICAL) {

            Block block = event.getClickedBlock();

            if (block.getLocation().equals(enterButtonLocation)) {
                if (eventActive) {
                    event.getPlayer().teleport(startPosition);
                    participants.add(event.getPlayer().getUniqueId());
                } else {
                    MessageManager.sendMessage(event.getPlayer(), "&cThe Hohengroth event is not active");
                }
            } else if (block.getLocation().equals(enterFirstPortalButtonLocation) && eventRunning) {
                event.getPlayer().teleport(portalPosition);
            }
        }
    }

    public void removeStartWall() {
        for (String loc : main.getHohengrothSchedule().startWall) {
            Location location = LocationSerialization.deserializeLocation(loc);

            if (location.getBlock().getType() != startWallMat) {
                return;
            }

            location.getBlock().setType(Material.AIR);
        }
    }

}
