package com.medievallords.carbyne.gates.commands;

import com.medievallords.carbyne.gates.Gate;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import net.elseland.xikage.MythicMobs.API.MythicMobsAPI;
import net.elseland.xikage.MythicMobs.Mobs.ActiveMob;
import net.elseland.xikage.MythicMobs.Mobs.MythicMob;
import net.elseland.xikage.MythicMobs.MythicMobs;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

/**
 * Created by Calvin on 1/31/2017
 * for the Carbyne-Gear project.
 */
public class GateAddEntCommand extends BaseCommand {

    @Command(name = "gate.addent", permission = "carbyne.gate.addent", inGameOnly = true)
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();

        if (args.length != 1) {
            MessageManager.sendMessage(player, "&c/gate addent [name]");
            return;
        }

        if (!getCarbyne().isMythicMobsEnabled()) {
            MessageManager.sendMessage(player, "&cMythicMobs is not enabled.");
            return;
        }

        Gate gate = getGateManager().getGate(args[0]);

        if (gate == null) {
            MessageManager.sendMessage(player, "&cCould not find a gate with the ID \"" + args[0] + "\".");
            return;
        }

        Entity theEntity = null;
        MessageManager.sendMessage(player, "&cSearching nearby mobs..." + args[0] + "\".");
        for (Entity entity : player.getLocation().getWorld().getNearbyEntities(player.getLocation(), 20D, 20D, 20D)) {
            if (MythicMobs.inst().getAPI().getMobAPI().isMythicMob(entity)) {
                ActiveMob mob = MythicMobs.inst().getAPI().getMobAPI().getMythicMobInstance(entity);
                if (theEntity == null) {
                    if (!gate.getEntityUUIDs().contains(mob.getUniqueId())) {
                        theEntity = entity;
                    }
                } else {
                    if (entity.getLocation().distance(player.getLocation()) > theEntity.getLocation().distance(player.getLocation())) {
                        if (!gate.getEntityUUIDs().contains(mob.getUniqueId())) {
                            theEntity = entity;
                        }
                    }
                }
            }
        }

        if (theEntity == null) {
            MessageManager.sendMessage(player, "&cCould not find a Mythic Mob near you.");
            return;
        }

        gate.getEntityUUIDs().add(MythicMobs.inst().getAPI().getMobAPI().getMythicMobInstance(theEntity).getUniqueId());
        gate.saveGate();
        MessageManager.sendMessage(player, "&aYou have added a MythicMob to the gate &b" + gate.getGateId() + "&a.");
    }
}
