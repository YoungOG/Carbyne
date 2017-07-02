package com.medievallords.carbyne.events.hohengroth;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.events.Event;
import com.medievallords.carbyne.events.EventManager;
import com.medievallords.carbyne.utils.LocationSerialization;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import io.lumine.xikage.mythicmobs.mobs.MobManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Williams on 2017-06-16
 * for the Carbyne project.
 */
public class HohengrothEvent extends Event {

    private static Carbyne main = Carbyne.getInstance();

    public ArrayList<UUID> participants = new ArrayList<>();
    public int hoardReclamation;
    public int requiredHoardReclamation;

    private HashMap<String, String> firstStageMobs = new HashMap<>();
    private HashMap<String, String> secondStageMobs = new HashMap<>();
    private HashMap<String, String> bossStageMobs = new HashMap<>();
    private HashMap<String, String> thirdStageMobs = new HashMap<>();
    private HashMap<String, String> hohengrothStageMobs = new HashMap<>();

    private int bossOneTimerId;

    @Override
    public void tick() {

    }

    public HohengrothEvent(EventManager eventManager, ArrayList<UUID> participants) {
        super(main.getEventManager());
        this.participants = participants;

    }

    public void startEvent() {
        for (String loc : main.getHohengrothSchedule().startWall) {
            Location location = LocationSerialization.deserializeLocation(loc);

            if (location.getBlock().getType() != main.getHohengrothSchedule().startWallMat) {
                return;
            }

            location.getBlock().setType(Material.AIR);
        }
    }

    public void stopEvent() {
        main.getHohengrothSchedule().canDonate = true;
    }

    public void firstStage() {

    }

    public void secondStage() {
        MobManager mobManager = MythicMobs.inst().getMobManager();
        for (String s : secondStageMobs.keySet()) {
            Location location = LocationSerialization.deserializeLocation(secondStageMobs.get(s));

            ActiveMob mob = mobManager.spawnMob(s, location);
            double maxHealth = mob.getEntity().getMaxHealth();
            mob.getEntity().setMaxHealth(getHealthAcord(maxHealth));
            mob.getEntity().setHealth(mob.getEntity().getMaxHealth());
        }
    }

    public void bossStage() {
        bossOneTimerId = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(main, new Runnable() {

            int t = 0;
            MobManager mobManager = MythicMobs.inst().getMobManager();

            @Override
            public void run() {
                t++;

                if (t >= 68) {
                    for (String s : bossStageMobs.keySet()) {
                        Location location = LocationSerialization.deserializeLocation(bossStageMobs.get(s));

                        ActiveMob mob = mobManager.spawnMob(s, location);
                        double maxHealth = mob.getEntity().getMaxHealth();
                        mob.getEntity().setMaxHealth(getHealthAcord(maxHealth));
                        mob.getEntity().setHealth(mob.getEntity().getMaxHealth());
                    }
                }
            }
        }, 0, 200);

    }

    public void thirdStage() {

    }

    public void hohengrothStage() {

    }

    public double getHealthAcord(double health) {
        double healthI = health;

        float perc = 100 / participants.size();
        float sub = 100;

        for (int i = 0; i < participants.size(); i++) {
            sub -= perc;
            healthI += health * sub;
        }

        return healthI;
    }

}