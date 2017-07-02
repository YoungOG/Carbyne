package com.medievallords.carbyne.events;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.utils.DateUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

/**
 * Created by Williams on 2017-06-16
 * for the Carbyne project.
 *
 * Dalton here. Get ready for a great explanation for how this class works.
 * After reading this you should look at the EventManager class.
 */
public abstract class Event implements Listener // Event super class will be used to represent all common elements most events share. Yes it is a listener, read more on that below.
{

    protected static Carbyne main = Carbyne.getInstance();

    protected EventManager eventManager; //EventManager runs events and is closely tied to this class.
    protected boolean active; //Is the event active i.e. not in the waiting list.

    @Getter
    private String timeString; // String used to find the time to run the event next.
    @Getter
    private long activationTime; // The actual time the event will activate. Calculated by using timeString and DateUtil.

    /**
     * This constructor is designed for events that are meant to be started by command.
     * The arguments that are not set via constructor will be used as flags to not auto schedule the event again.
     * @param eventManager All powerful event manager.
     */
    public Event(EventManager eventManager)
    {
        this.eventManager = eventManager;
        this.active = false;
        this.timeString = null;
        this.activationTime = -1;
        eventManager.getWaitingEvents().add(this);
    }

    /**
     * This constructor is used for creating events designed to run without a command.
     * timeString is used to calculate the next run time for the event.
     *
     * Notice that when the event is constructed, it is noy active by default.
     * It is added to the event waiting list.
     * @param eventManager All powerful event manager.
     * @param timeString The amount of time an event should wait before running again.
     */
    public Event(EventManager eventManager, String timeString)
    {
        this.eventManager = eventManager;
        this.active = false;
        this.timeString = timeString;
        try { this.activationTime = DateUtil.parseDateDiff(timeString, true); } catch(Exception howDidYouMessThisUp) { }
        eventManager.getWaitingEvents().add(this);
    }

    public abstract void tick(); // Method used to tick event logic. Is ticked with all other active events in the EventManager. IT IS TICKED ASYNCHRONOUSLY EVERY SECOND REMEMBER THAT!

    /**
     * Universal start method. This method can be overridden and called again in the subclass. I recommend this.
     * Start is special because it is called automatically if the event repeats alone.
     * Notice that start registers and stop unregisters the event class which is a listener of itself (Not sure if this will work or not, but if it did, it would be cool, if not, we can use method overriding to do this).
     */
    public void start()
    {
        active = true;

        Bukkit.getServer().getPluginManager().registerEvents(this, main);
        eventManager.getWaitingEvents().remove(this);
        eventManager.getActiveEvents().add(this);
    }

    /**
     * Same as start. You should probably override this in the subclass but call this method from the overridden method.
     * You will notice that I use timeString as a flag often and try to abstract it from you, so don't mess with it!
     */
    public void stop()
    {
        active = false;

        if(timeString != null)
            try { this.activationTime = DateUtil.parseDateDiff(timeString, true); } catch(Exception howDidYouMessThisUp) { }

        HandlerList.unregisterAll(this);
        eventManager.getActiveEvents().remove(this);
        eventManager.getWaitingEvents().add(this);
    }

}
