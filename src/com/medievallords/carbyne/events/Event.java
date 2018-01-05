package com.medievallords.carbyne.events;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.utils.DateUtil;
import com.medievallords.carbyne.utils.command.BaseCommand;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Williams on 2017-06-16
 * for the Carbyne project.
 * <p>
 * Dalton here. Get ready for a great explanation for how this class works.
 * After reading this you should look at the EventManager class.
 */

public abstract class Event {

    protected static Carbyne main = Carbyne.getInstance();
    @Getter
    protected static Location spawn = new Location(Bukkit.getWorld("world"), -729, 110, 295);
    @Getter
    private final String eventName;
    protected EventManager eventManager; //EventManager runs events and is closely tied to this class.
    @Getter
    protected boolean active; //Is the event active i.e. not in the waiting list.
    @Getter
    protected boolean commandWhitelistActive;
    @Getter
    protected List<EventProperties> properties = new ArrayList<>();
    @Getter
    protected List<EventComponent> components = new ArrayList<>();
    protected List<BaseCommand> commands = new ArrayList<>();
    @Getter
    protected List<Player> participants = new ArrayList<>();
    @Getter
    protected List<String> whitelistedCommands = new ArrayList<>();
    @Getter
    protected Map<Player, BukkitRunnable> waitingTasks = new HashMap<>();
    @Getter
    private String timeString; // String used to find the time to run the event next.
    @Getter
    private long activationTime; // The actual time the event will activate. Calculated by using timeString and DateUtil.

    /**
     * This constructor is designed for events that are meant to be started by command.
     * The arguments that are not set via constructor will be used as flags to not auto schedule the event again.
     *
     * @param eventManager All powerful event manager.
     */
    public Event(EventManager eventManager, String name) {
        this.eventManager = eventManager;
        this.eventName = name;
        this.active = false;
        this.timeString = null;
        this.activationTime = -1;
        eventManager.getWaitingEvents().add(this);
    }

    /**
     * This constructor is used for creating events designed to run without a command.
     * timeString is used to calculate the next run time for the event.
     * <p>
     * Notice that when the event is constructed, it is noy active by default.
     * It is added to the event waiting list.
     *
     * @param eventManager All powerful event manager.
     * @param timeString   The amount of time an event should wait before running again.
     */
    public Event(EventManager eventManager, String timeString, String eventName) {
        this.eventManager = eventManager;
        this.eventName = eventName;
        this.active = false;
        this.timeString = timeString;
        try {
            this.activationTime = DateUtil.parseDateDiff(timeString, true);
        } catch (Exception howDidYouMessThisUp) {
        }
        eventManager.getWaitingEvents().add(this);
    }

    public abstract void tick();// Method used to tick event logic. Is ticked with all other active events in the EventManager. IT IS TICKED ASYNCHRONOUSLY EVERY SECOND REMEMBER THAT!

    /**
     * Method to check if it is time to run the event
     *
     * @return
     */
    public boolean isItTimeToActivate() {
        return System.currentTimeMillis() > activationTime;
    }

    /**
     * Universal start method. This method can be overridden and called again in the subclass. I recommend this.
     * Start is special because it is called automatically if the event repeats alone.
     * Notice that start registers and stop unregisters the event class which is a listeners of itself (Not sure if this will work or not, but if it did, it would be cool, if not, we can use method overriding to do this).
     */
    public synchronized void start() {
        active = true;

        components.forEach(EventComponent::start);

        for (BaseCommand command : commands)
            Carbyne.getInstance().getCommandFramework().registerCommands(command);
        //Bukkit.getServer().getPluginManager().registerEvents(this, main);
        eventManager.getWaitingEvents().remove(this);
        eventManager.getActiveEvents().add(this);
    }

    /**
     * Same as start. You should probably override this in the subclass but call this method from the overridden method.
     * You will notice that I use timeString as a flag often and try to abstract it from you, so don't mess with it!
     */
    public synchronized void stop() {
        active = false;

        components.forEach(EventComponent::stop);

        if (timeString != null)
            try {
                this.activationTime = DateUtil.parseDateDiff(timeString, true);
            } catch (Exception howDidYouMessThisUp) {
            }

        for (BaseCommand command : commands)
            Carbyne.getInstance().getCommandFramework().unregisterCommands(command);
        //HandlerList.unregisterAll(this);
        eventManager.getActiveEvents().remove(this);
        eventManager.getWaitingEvents().add(this);
        teleportPlayersToLocationAndLeaveEvent(participants);
    }

    public boolean isPlayerInEvent(Object player) {
        return participants.contains(player);
    }

    public void addPlayerToEvent(Player player) {
        main.getProfileManager().getProfile(player.getUniqueId()).setActiveEvent(this);
        participants.add(player);
    }

    public void removePlayerFromEvent(Player player) {
        main.getProfileManager().getProfile(player.getUniqueId()).setActiveEvent(null);
        participants.remove(player);
    }

    protected void teleportPlayersToLocationAndLeaveEvent(List<Player> players) {
        new BukkitRunnable() {
            public void run() {
                for (Player player : players)
                    player.teleport(spawn);

                for (int i = 0; i < players.size(); i++) {
                    removePlayerFromEvent(players.get(i));
                    i--;
                }
            }
        }.runTask(Carbyne.getInstance());
    }


    protected void syncChangeBlockLocation(Location location, Material type) {
        new BukkitRunnable() {
            public void run() {
                location.getChunk().load();
                location.getBlock().setType(type);
                location.getChunk().unload();
            }
        }.runTask(Carbyne.getInstance());

    }

    protected void syncTeleportAllPlayers(Location location) {
        new BukkitRunnable() {
            public void run() {
                for (int i = 0; i < participants.size(); i++) {
                    participants.get(i).teleport(location);
                }
            }
        }.runTask(Carbyne.getInstance());
    }

    public EventComponent getEventComponent(Class<?> component) {
        for (EventComponent comp : components) {
            if (comp.getClass() == component) {
                return comp;
            }
        }
        return null;
    }

}
