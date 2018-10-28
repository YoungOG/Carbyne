package com.medievallords.carbyne.customevents;

import com.medievallords.carbyne.dungeons.dungeons.instance.DungeonInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DungeonEnterEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private Player player;
    private DungeonInstance instance;

    public DungeonEnterEvent(Player player, DungeonInstance instance) {
        this.player = player;
        this.instance = instance;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public DungeonInstance getInstance() {
        return instance;
    }

    public Player getPlayer() {
        return player;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
