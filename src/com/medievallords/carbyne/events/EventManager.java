package com.medievallords.carbyne.events;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.events.hohengroth.HohengrothCoffer;
import com.medievallords.carbyne.events.implementations.CliffClimb;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;

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
        new CliffClimb(this);

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
        for (String raceSection : cs.getKeys(false)) {
            boolean readyFlag = true;
            String name;
            if ((name = cs.getString(raceSection + ".Name")) == null) readyFlag = false;
            Location startingLocation, winningLocation;
            String startMessage;
            if ((startMessage = cs.getString(raceSection + ".StartMessage")) == null) {
                readyFlag = false;
            } else
                String startLocation;
            if ((startLocation = cs.getString(raceSection + ".StartingLocation")) == null) {
                readyFlag = false;

            }

        }
    }

}
