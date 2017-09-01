package com.medievallords.carbyne.events;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.events.hohengroth.HohengrothCoffer;
import com.medievallords.carbyne.events.implementations.object.LastAliveObject;
import com.medievallords.carbyne.events.implementations.object.RaceObject;
import com.medievallords.carbyne.utils.LocationSerialization;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Dalton on 6/24/2017.
 */
public class EventManager
{

    private Carbyne main = Carbyne.getInstance();

    @Getter
    private List<Event> activeEvents = Collections.synchronizedList(new CopyOnWriteArrayList<>());
    @Getter
    private List<Event> waitingEvents = Collections.synchronizedList(new CopyOnWriteArrayList<>());

    @Getter
    private HohengrothCoffer hohengrothCoffer;

    public EventManager()
    {
        loadEvents();

        new BukkitRunnable() {
            public void run() {
                synchronized (activeEvents) {
                    Iterator<Event> itr = activeEvents.iterator();
                    while (itr.hasNext())
                        itr.next().tick();
                }
                synchronized (waitingEvents) {
                    long currentTime = System.currentTimeMillis();
                    Iterator<Event> itr = waitingEvents.iterator();
                    while (itr.hasNext()) {
                        Event e = itr.next();
                        if (e.getTimeString() != null && e.getActivationTime() <= currentTime)
                            e.start();
                    }
                }
            }
        }.runTaskTimerAsynchronously(main, 0L, 20L);
    }

    public void loadEvents() {
        ConfigurationSection cs = main.getEventsFileConfiguration().getConfigurationSection("Races");
        if (cs == null) main.getEventsFileConfiguration().createSection("Races");

        for (String raceSection : cs.getKeys(false)) {
            boolean readyFlag = true;
            String name;
            if ((name = cs.getString(raceSection + ".Name")) == null) readyFlag = false;
            Location startingLocation = null, winningLocation = null, gateLocation = null;
            String startMessage;
            if ((startMessage = cs.getString(raceSection + ".StartMessage")) == null) {
                readyFlag = false;
            }
            String startLocation;
            if ((startLocation = cs.getString(raceSection + ".StartLocation")) == null) {
                readyFlag = false;
            } else
                startingLocation = LocationSerialization.deserializeLocation(startLocation);
            String winLocation;
            if ((winLocation = cs.getString(raceSection + ".WinningLocation")) == null) {
                readyFlag = false;
            } else
                winningLocation = LocationSerialization.deserializeLocation(winLocation);
            String gLocation;
            if ((gLocation = cs.getString(raceSection + ".GateLocation")) == null) {
                readyFlag = false;
            } else
                gateLocation = LocationSerialization.deserializeLocation(gLocation);

            RaceObject.raceObjects.add(new RaceObject(readyFlag, name, startMessage, startingLocation, winningLocation, gateLocation));
        }

        cs = main.getEventsFileConfiguration().getConfigurationSection("LastAlive");
        if (cs == null) main.getEventsFileConfiguration().createSection("LastAlive");

        if (cs != null && cs.getKeys(false) != null) {
            for (String section : cs.getKeys(false)) {
                boolean ready = true;
                String name;
                Location lobby = null;
                List<Location> spawnLocations = new ArrayList<>();

                if ((name = cs.getString(section + ".Name")) == null) ready = false;
                String lobbyString;
                if ((lobbyString = cs.getString(section + ".LobbyLocation")) != null)
                    lobby = LocationSerialization.deserializeLocation(lobbyString);
                else ready = false;
                List<String> unserlocs = cs.getStringList(section + ".SpawnLocations");
                if (unserlocs != null) {
                    for (int i = 0; i < unserlocs.size(); i++) {
                        spawnLocations.add(LocationSerialization.deserializeLocation(unserlocs.get(i)));
                    }
                }
                if (!(spawnLocations.size() >= 2)) ready = false;

                LastAliveObject.lastAliveObjects.add(new LastAliveObject(ready, name, lobby, spawnLocations));
            }
        }
    }

    public void saveEvents() {
        FileConfiguration fc = main.getEventsFileConfiguration();
        fc.set("Races", null);

        int num = 0;
        for (RaceObject data : RaceObject.raceObjects) {
            num++;
            fc.set("Races." + num + ".Name", data.getName());
            fc.set("Races." + num + ".StartMessage", data.getStartString());
            if (data.getStartingLocation() != null)
                fc.set("Races." + num + ".StartLocation", LocationSerialization.serializeLocation(data.getStartingLocation()));
            if (data.getWinningLocation() != null)
                fc.set("Races." + num + ".WinningLocation", LocationSerialization.serializeLocation(data.getWinningLocation()));
            if (data.getGateLocation() != null)
                fc.set("Races." + num + ".GateLocation", LocationSerialization.serializeLocation(data.getGateLocation()));
        }

        fc.set("LastAlive", null);
        num = 0;
        for (LastAliveObject data : LastAliveObject.lastAliveObjects) {
            num++;
            fc.set("LastAlive." + num + ".Name", data.getName());
            if (data.getLobby() != null)
                fc.set("LastAlive." + num + ".LobbyLocation", LocationSerialization.serializeLocation(data.getLobby()));
            List<String> deserLocs = new ArrayList<>();
            for (Location loc : data.getSpawnLocations()) {
                deserLocs.add(LocationSerialization.serializeLocation(loc));
            }
            fc.set("LastAlive." + num + ".SpawnLocations", deserLocs);
        }

        try {
            fc.save(main.getEventsFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
