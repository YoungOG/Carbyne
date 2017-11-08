package com.medievallords.carbyne.customevents;

import com.medievallords.carbyne.profiles.Profile;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by Dalton on 6/23/2017.
 *
 * NOTES ABOUT THIS CLASS:
 * Most of the stuff in here is included because Bukkit requires it so don't touch anything.
 * This class is used to get a player when they join the server for the first time.
 * The profile is already created after this event is called, so setCancelled does nothing.
 * Implementation class: ProfileListeners.java
 */
@Getter
public class ProfileCreatedEvent extends Event implements Cancellable
{

    private static final HandlerList handlers = new HandlerList();

    private Player player;
    private Profile playerProfile;
    private boolean isCancelled;

    public ProfileCreatedEvent(Player player, Profile playerProfile)
    {
        this.player = player;
        this.playerProfile = playerProfile;
        this.isCancelled = false;
    }

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override
    public void setCancelled(boolean arg0) {
        this.isCancelled = arg0;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
