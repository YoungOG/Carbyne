package com.medievallords.carbyne.dungeons.triggers.listeners;

import com.medievallords.carbyne.dungeons.dungeons.instance.DungeonInstance;
import com.medievallords.carbyne.dungeons.triggers.InteractTrigger;
import com.medievallords.carbyne.dungeons.triggers.MobTrigger;
import com.medievallords.carbyne.dungeons.triggers.Trigger;
import com.medievallords.carbyne.utils.StaticClasses;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

public class TriggerInteractListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }

        DungeonInstance instance = StaticClasses.dungeonHandler.getInstance(block.getWorld());
        if (instance == null) {
            return;
        }

        List<Trigger> interactTriggers = instance.getTriggers(InteractTrigger.class);

        for (Trigger trigger : interactTriggers) {
            //Location cloned = new Location(instance.getWorld(), trigger.getLocation().getX(), trigger.getLocation().getY(), trigger.getLocation().getZ());
            if (trigger.getLocation().equals(block.getLocation())) {
                ((InteractTrigger) trigger).trigger(event.getPlayer());
            }
        }
    }

    @EventHandler
    public void onBossDeath(MythicMobDeathEvent event) {
        Entity entity = event.getEntity();
        Location location = entity.getLocation();
        //List<Trigger> mobTriggers = Trigger.getTriggers(MobTrigger.class);

        DungeonInstance instance = StaticClasses.dungeonHandler.getInstance(location.getWorld());
        if (instance == null) {
            return;
        }

        List<Trigger> mobTriggers = instance.getTriggers(MobTrigger.class);

        for (Trigger trigger : mobTriggers) {
            MobTrigger mobTrigger = (MobTrigger) trigger;
            if (mobTrigger.getMobType().equalsIgnoreCase(event.getMob().getType().getInternalName())) {
                mobTrigger.trigger(event.getKiller(), location);
            }
        }
    }
}
