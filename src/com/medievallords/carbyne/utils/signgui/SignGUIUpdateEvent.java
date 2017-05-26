package com.medievallords.carbyne.utils.signgui;


import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by Calvin on 4/6/2017
 * for the Carbyne project.
 */
public class SignGUIUpdateEvent extends Event {

    private static HandlerList handlers;
    private String[] signText;
    private Player player;
    private Block block;

    static {
        handlers = new HandlerList();
    }

    public SignGUIUpdateEvent(Player player, Block block, String[] signText) {
        this.player = player;
        this.block = block;
        this.signText = signText;
    }

    public HandlerList getHandlers() {
        return SignGUIUpdateEvent.handlers;
    }

    public static HandlerList getHandlerList() {
        return SignGUIUpdateEvent.handlers;
    }

    public Player getPlayer() {
        return this.player;
    }

    public Block getBlock() {
        return block;
    }

    public String[] getSignText() {
        return this.signText;
    }
}
