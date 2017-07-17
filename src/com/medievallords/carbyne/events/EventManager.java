package com.medievallords.carbyne.events;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.events.hohengroth.HohengrothCoffer;
import lombok.Getter;
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

        //new CliffClimb(this, "3h"); removed for release

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

}
