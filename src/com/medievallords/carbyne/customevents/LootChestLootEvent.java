package com.medievallords.carbyne.customevents;

import com.medievallords.carbyne.lootchests.Loot;
import lombok.Getter;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;

public class LootChestLootEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    @Getter
    private final Player player;
    private final Chest chest;
    private final List<Loot> loot;
    private boolean isCancelled;

    public LootChestLootEvent(Player player, Chest chest, List<Loot> loot) {
        this.player = player;
        this.chest = chest;
        this.loot = loot;
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

    public Chest getChest() {
        return chest;
    }

    public List<Loot> getLoot() {
        return loot;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
