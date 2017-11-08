package com.medievallords.carbyne.gates.listeners;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.gates.Gate;
import com.medievallords.carbyne.gates.GateManager;
import net.elseland.xikage.MythicMobs.API.Bukkit.Events.MythicMobDeathEvent;
import net.elseland.xikage.MythicMobs.API.Bukkit.Events.MythicMobSpawnEvent;
import net.elseland.xikage.MythicMobs.Mobs.ActiveMob;
import net.elseland.xikage.MythicMobs.MythicMobs;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Created by Calvin on 2/4/2017
 * for the Carbyne-Gear project.
 */
public class GateMobListeners implements Listener {

    private Carbyne carbyne = Carbyne.getInstance();
    private GateManager gateManager = carbyne.getGateManager();

    @EventHandler
    public void onEntitySpawn(MythicMobSpawnEvent event) {
        ActiveMob mob = MythicMobs.inst().getAPI().getMobAPI().getMythicMobInstance(event.getEntity());

        for (Gate gate: gateManager.getGates()) {
            if (gate.getEntityUUIDs().contains(mob.getUniqueId())) {
                gate.killMob();
                Bukkit.broadcastMessage("MOB KILLED: " + mob.getType().getInternalName() + " ON GATE: " + gate.getGateId());
            }
        }
    }

    @EventHandler
    public void onEntityDeath(MythicMobDeathEvent event) {
        ActiveMob mob = MythicMobs.inst().getAPI().getMobAPI().getMythicMobInstance(event.getEntity());

        for (Gate gate : gateManager.getGates()) {
            if (gate.getEntityUUIDs().contains(mob.getUniqueId())) {
                gate.addMob();
                Bukkit.broadcastMessage("MOB SPAWNED: " + mob.getType().getInternalName() + " ON GATE: " + gate.getGateId());
            }
        }
    }
}
