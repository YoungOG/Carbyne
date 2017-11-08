package com.medievallords.carbyne.gates.commands;

import com.medievallords.carbyne.gates.Gate;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import net.elseland.xikage.MythicMobs.Mobs.ActiveMob;
import net.elseland.xikage.MythicMobs.MythicMobs;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import java.util.UUID;

/**
 * Created by Calvin on 1/31/2017
 * for the Carbyne-Gear project.
 */
public class GateStatusCommand extends BaseCommand {

    @Command(name = "gate.status", permission = "carbyne.gate.status")
    public void onCommand(CommandArgs commandArgs) {
        CommandSender sender = commandArgs.getSender();
        String[] args = commandArgs.getArgs();

        if (args.length != 1) {
            MessageManager.sendMessage(sender, "&c/gate status [name]");
            return;
        }

        String gateId = args[0];

        if (getGateManager().getGate(gateId) == null) {
            MessageManager.sendMessage(sender, "&cCould not find a gate with the ID \"" + gateId + "\".");
            return;
        }

        Gate gate = getGateManager().getGate(gateId);

        MessageManager.sendMessage(sender, "&aGate Id: &b" + gate.getGateId());
        MessageManager.sendMessage(sender, " &aActive Length: &b" + gate.getActiveLength());
        MessageManager.sendMessage(sender, " &aCurrent Length: &b" + gate.getCurrentLength());
        MessageManager.sendMessage(sender, " &aPressure Plates(&b" + gate.getPressurePlateMap().keySet().size() + "&a):");

        int id = 0;

        for (Location location : gate.getPressurePlateMap().keySet()) {
            id++;
            MessageManager.sendMessage(sender, "   &b" + id + "&7. &aActive: &b" + gate.getPressurePlateMap().get(location) + "&a, World: &b" + location.getWorld().getName() + "&a, X: &b" + location.getBlockX() + "&a, Y: &b" + location.getBlockY() + "&a, Z: &b" + location.getBlockZ());
        }

        MessageManager.sendMessage(sender, " &aRedstone Blocks(&b" + gate.getRedstoneBlockLocations().size() + "&a):");

        id = 0;
        for (Location location : gate.getRedstoneBlockLocations()) {
            id++;
            MessageManager.sendMessage(sender, "   &b" + id + "&7. &aType: &b" + (location.getBlock() != null ? location.getBlock().getType() : "Null") + "&a, World: &b" + location.getWorld().getName() + "&a, X: &b" + location.getBlockX() + "&a, Y: &b" + location.getBlockY() + "&a, Z: &b" + location.getBlockZ());
        }

        MessageManager.sendMessage(sender, " &aButton(&b" + gate.getButtonLocations().size() + "&a):");

        id = 0;
        for (Location location : gate.getButtonLocations()) {
            id++;
            MessageManager.sendMessage(sender, "   &b" + id + "&7. &aType: &b" + (location.getBlock() != null ? location.getBlock().getType() : "Null") + "&a, World: &b" + location.getWorld().getName() + "&a, X: &b" + location.getBlockX() + "&a, Y: &b" + location.getBlockY() + "&a, Z: &b" + location.getBlockZ());
        }

        MessageManager.sendMessage(sender, " &aEntities(&b" + gate.getEntityUUIDs().size() + "&a):");

        if (getCarbyne().isMythicMobsEnabled()) {
            for (UUID uuid : gate.getEntityUUIDs()) {
                ActiveMob mob = MythicMobs.inst().activeMobs.get(uuid);
                MessageManager.sendMessage(sender, "    &b" + id + "&7 - &aName: &b" + mob.getType().getInternalName() + "&a, Alive: &b" + !mob.isDead() + "&a, Total Count: &b" + mob.getActiveMobsInWorld(mob.getLivingEntity().getWorld()).size());
            }
        } else {
            MessageManager.sendMessage(sender, "    &cMythicMobs support is disabled.");
        }
    }
}
