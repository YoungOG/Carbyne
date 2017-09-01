package com.medievallords.carbyne.conquerpoints;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.conquerpoints.objects.ConquerPoint;
import com.medievallords.carbyne.utils.Cooldowns;
import com.medievallords.carbyne.utils.MessageManager;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import static com.medievallords.carbyne.utils.MessageManager.convertSecondsToMinutes;

public class ConquerPointListeners implements Listener {

    private Carbyne main = Carbyne.getInstance();
    private ConquerPointManager conquerPointManager = main.getConquerPointManager();

    @EventHandler
    public void onTeleportAway(PlayerTeleportEvent event) {
        ConquerPoint conquerPoint = conquerPointManager.getConquerPointFromLocation(event.getFrom());
        if (conquerPoint != null && (conquerPointManager.getConquerPointFromLocation(event.getTo()) == null || !event.getTo().getWorld().equals(event.getFrom()))) {
            if (conquerPoint.getHolder() != null && conquerPoint.getHolder().equals(event.getPlayer().getUniqueId())) {

                try {
                    Resident resident = TownyUniverse.getDataSource().getResident(event.getPlayer().getName());

                    if (!resident.hasTown()) {
                        return;
                    }

                    if (!resident.hasNation()) {
                        return;
                    }

                    if (!resident.getTown().hasNation()) {
                        return;
                    }

                    Nation nation = resident.getTown().getNation();
                    MessageManager.broadcastMessage("&c[&4&lConquer&c]: &5" + resident.getTown().getNation().getName() + " &chas stopped trying to conquer &d" + conquerPoint.getId() + "&c!");
                    conquerPoint.stopCapturing();
                    return;
                } catch (NotRegisteredException e1) {
                    e1.printStackTrace();
                }
            }
        }

        conquerPoint = conquerPointManager.getConquerPointFromLocation(event.getTo());

        if (conquerPoint != null) {
            event.setCancelled(true);
            MessageManager.sendMessage(event.getPlayer(), "&cYou cannot teleport into a conquerpoint");
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.getPlayer().getWorld().getName().equalsIgnoreCase("world")) {
            Player player = event.getPlayer();

            try {
                Resident resident = TownyUniverse.getDataSource().getResident(player.getName());

                if (!resident.hasTown()) {
                    return;
                }

                if (!resident.hasNation()) {
                    return;
                }

                if (!resident.getTown().hasNation()) {
                    return;
                }

                Nation nation = resident.getTown().getNation();

                for (ConquerPoint conquerPoint : conquerPointManager.getConquerPoints()) {
                    if (conquerPoint.getPos1().getWorld().equals(player.getWorld()) && conquerPoint.getPos1().distance(player.getLocation()) <= 200) {
                        if (conquerPoint.getNation() != null) {
                            if (!conquerPointManager.getInArea().containsKey(player.getUniqueId())) {
                                conquerPointManager.getInArea().put(player.getUniqueId(), conquerPoint);
                                MessageManager.sendMessage(player, "&c[&4&lConquer&c]: " + (conquerPoint.getNation() != null ? "This area is currently conquered by &d" + conquerPoint.getNation() : "This area is currently not conquered by a nation") + "&c.");
                            }
                        } else {
                            if (conquerPointManager.getInArea().containsKey(player.getUniqueId()) && conquerPointManager.getInArea().get(player.getUniqueId()).getId().equals(conquerPoint.getId())) {
                                conquerPointManager.getInArea().remove(player.getUniqueId());
                            }
                        }
                    } else {
                        if (conquerPointManager.getInArea().containsKey(player.getUniqueId()) && conquerPointManager.getInArea().get(player.getUniqueId()).getId().equals(conquerPoint.getId())) {
                            conquerPointManager.getInArea().remove(player.getUniqueId());
                        }
                    }
                }

                if (conquerPointManager.getConquerPointFromLocation(player.getLocation()) != null) {
                    if (Cooldowns.getCooldown(player.getUniqueId(), "CaptureCooldown") > 0) {
                        if (Cooldowns.tryCooldown(player.getUniqueId(), "MessageCooldown", 3000))
                            MessageManager.sendMessage(player, "&c[&4&lConquer&c]: &cYou are on cooldown, pleas try again in " + (Cooldowns.getCooldown(player.getUniqueId(), "CaptureCooldown") / 1000) + " seconds.");

                        return;
                    }

                    ConquerPoint conquerPoint = conquerPointManager.getConquerPointFromLocation(player.getLocation());

                    if (conquerPoint.isOnCooldown()) {
                        if (Cooldowns.tryCooldown(player.getUniqueId(), "MessageCooldown", 3000)) {
                            MessageManager.sendMessage(player, "&c[&4&lConquer&c]: &d" + conquerPoint.getId() + " &cwas recently conquered, and can be contested again in " + convertSecondsToMinutes(conquerPoint.getCooldownTime()) + ".");
                        }
                        return;
                    }

                    if (conquerPoint.getHolder() == null) {
                        if (nation.equals(conquerPoint.getNation())) {
                            return;
                        }

                        MessageManager.broadcastMessage("&c[&4&lConquer&c]: &5" + resident.getTown().getNation().getName() + " &cis attempting to conquer " + (conquerPoint.getNation() != null ? "&5" + conquerPoint.getNation().getName() + "&c's " : "") + "&d" + conquerPoint.getId() + "&c!");

                        if (conquerPoint.getNation() != null) {
                            for (Player all : TownyUniverse.getOnlinePlayers(conquerPoint.getNation())) {
                                MessageManager.sendMessage(all, "&c[&4&lConquer&c]: &5" + resident.getTown().getNation().getName() + " &cis trying to conquer your territory in &5" + conquerPoint.getId() + "&c!\nDefend your territory!");
                            }
                        }

                        conquerPoint.startCapture(player);
                    }
                } else {
                    for (ConquerPoint conquerPoint : conquerPointManager.getConquerPoints()) {
                        if (conquerPoint.getHolder() != null) {
                            if (conquerPoint.getHolder().equals(player.getUniqueId())) {
                                conquerPoint.stopCapturing();
                                MessageManager.broadcastMessage("&c[&4&lConquer&c]: &5" + resident.getTown().getNation().getName() + " &chas stopped trying to conquer &d" + conquerPoint.getId() + "&c!");
                                Cooldowns.setCooldown(player.getUniqueId(), "CaptureCooldown", 30000);
                            }
                        }
                    }
                }
            } catch (NotRegisteredException e1) {
                e1.printStackTrace();
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (event.getEntity().getWorld().toString().contains("world")) {
            Player player = event.getEntity();

            try {
                Resident resident = TownyUniverse.getDataSource().getResident(player.getName());

                if (!resident.hasTown()) {
                    return;
                }

                if (!resident.hasNation()) {
                    return;
                }

                if (!resident.getTown().hasNation()) {
                    return;
                }

                Nation nation = resident.getTown().getNation();

                if (Cooldowns.getCooldown(player.getUniqueId(), "CaptureCooldown") > 0) {
                    return;
                }

                if (conquerPointManager.getConquerPointFromLocation(player.getLocation()) != null) {
                    ConquerPoint conquerPoint = conquerPointManager.getConquerPointFromLocation(player.getLocation());
                    if (conquerPoint.isOnCooldown()) {
                        if (Cooldowns.tryCooldown(player.getUniqueId(), "MessageCooldown", 3000)) {
                            MessageManager.sendMessage(player, "&c[&4&lConquer&c]: &d" + conquerPoint.getId() + " &cwas recently conquered, and can be contested again in " + convertSecondsToMinutes(conquerPoint.getCaptureTime()) + ".");
                        }
                        return;
                    }
                    if (conquerPoint.getHolder() == null) {
                        if (nation.equals(conquerPoint.getNation())) {
                            return;
                        }

                        MessageManager.broadcastMessage("&c[&4&lConquer&c]: &5" + resident.getTown().getNation().getName() + " &cis attempting to conquer " + (conquerPoint.getNation() != null ? "&5" + conquerPoint.getNation().getName() + "&c's " : "") + "&d" + conquerPoint.getId() + "&c!");

                        if (conquerPoint.getNation() != null) {
                            for (Player all : TownyUniverse.getOnlinePlayers(conquerPoint.getNation())) {
                                MessageManager.sendMessage(all, "&c[&4&lConquer&c]: &5" + resident.getTown().getNation().getName() + " &cis trying to conquer your territory in &5" + conquerPoint.getId() + "&c!\nDefend your territory!");
                            }
                        }

                        conquerPoint.startCapture(player);
                    }
                } else {
                    for (ConquerPoint conquerPoint : conquerPointManager.getConquerPoints()) {
                        if (conquerPoint.getHolder() != null) {
                            if (conquerPoint.getHolder().equals(player.getUniqueId())) {
                                conquerPoint.stopCapturing();
                                MessageManager.broadcastMessage("&c[&4&lConquer&c]: &5" + resident.getTown().getNation().getName() + " &chas stopped trying to conquer &d" + conquerPoint.getId() + "&c!");
                            }
                        }

                        Cooldowns.setCooldown(player.getUniqueId(), "CaptureCooldown", 30000);
                    }
                }
            } catch (NotRegisteredException e1) {
                e1.printStackTrace();
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (player.getWorld().toString().contains("world")) {
            try {
                Resident resident = TownyUniverse.getDataSource().getResident(player.getName());

                if (!resident.hasTown()) {
                    return;
                }

                if (!resident.hasNation()) {
                    return;
                }

                if (!resident.getTown().hasNation()) {
                    return;
                }

                Nation nation = resident.getTown().getNation();

                if (Cooldowns.getCooldown(player.getUniqueId(), "CaptureCooldown") > 0) {
                    return;
                }

                if (conquerPointManager.getConquerPointFromLocation(player.getLocation()) != null) {
                    ConquerPoint conquerPoint = conquerPointManager.getConquerPointFromLocation(player.getLocation());
                    if (conquerPoint.isOnCooldown()) {
                        if (Cooldowns.tryCooldown(player.getUniqueId(), "MessageCooldown", 3000)) {
                            MessageManager.sendMessage(player, "&c[&4&lConquer&c]: &d" + conquerPoint.getId() + " &cwas recently conquered, and can be contested again in " + convertSecondsToMinutes(conquerPoint.getCaptureTime()) + ".");
                        }
                        return;
                    }
                    if (conquerPoint.getHolder() == null) {
                        if (nation.equals(conquerPoint.getNation())) {
                            return;
                        }

                        MessageManager.broadcastMessage("&c[&4&lConquer&c]: &5" + resident.getTown().getNation().getName() + " &cis attempting to conquer " + (conquerPoint.getNation() != null ? "&5" + conquerPoint.getNation().getName() + "&c's " : "") + "&d" + conquerPoint.getId() + "&c!");

                        if (conquerPoint.getNation() != null) {
                            for (Player all : TownyUniverse.getOnlinePlayers(conquerPoint.getNation())) {
                                MessageManager.sendMessage(all, "&c[&4&lConquer&c]: &5" + resident.getTown().getNation().getName() + " &cis trying to conquer your territory in &5" + conquerPoint.getId() + "&c!\nDefend your territory!");
                            }
                        }

                        conquerPoint.startCapture(player);
                    }
                } else {
                    for (ConquerPoint conquerPoint : conquerPointManager.getConquerPoints()) {
                        if (conquerPoint.getHolder() != null) {
                            if (conquerPoint.getHolder().equals(player.getUniqueId())) {
                                conquerPoint.stopCapturing();
                                MessageManager.broadcastMessage("&c[&4&lConquer&c]: &5" + resident.getTown().getNation().getName() + " &chas stopped trying to conquer &d" + conquerPoint.getId() + "&c!");
                            }
                        }

                        Cooldowns.setCooldown(player.getUniqueId(), "CaptureCooldown", 30000);
                    }
                }
            } catch (NotRegisteredException e1) {
                e1.printStackTrace();
            }
        }
    }
}
