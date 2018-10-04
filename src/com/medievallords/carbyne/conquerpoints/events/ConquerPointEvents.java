package com.medievallords.carbyne.conquerpoints.events;

import com.medievallords.carbyne.conquerpoints.ConquerPointManager;
import com.medievallords.carbyne.conquerpoints.objects.ConquerPoint;
import com.medievallords.carbyne.utils.Cooldowns;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.StaticClasses;
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

import java.util.HashMap;
import java.util.UUID;

public class ConquerPointEvents implements Listener {

    private ConquerPointManager conquerPointManager = StaticClasses.conquerPointManager;
    private HashMap<UUID, ConquerPoint> inArea = new HashMap<>();

    /*@EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.getPlayer().getWorld().toString().contains("world")) {
            Player player = event.getPlayer();

            try {
                Resident res = TownyUniverse.getDataSource().getResident(player.getName());

                if (!res.hasTown())
                    return;

                if (!res.hasNation())
                    return;

                if (!res.getTown().hasNation())
                    return;

                Nation n = res.getTown().getNation();

                for (ConquerPoint cps : conquerPointManager.getConquerPoints())
                    if (cps.getPos1().distance(player.getLocation()) <= 150)
                        if (cps.getNation() != null)
                            if (!inArea.containsKey(player.getUniqueId())) {
                                inArea.put(player.getUniqueId(), cps);

                                if (cps.getNation() != null)

                                    MessageManager.sendMessage(player, "&c[&4&lConquer&c]: &cThis area is currently conquered by &d" + cps.getNation().getName());
                                else
                                    MessageManager.sendMessage(player, "&c[&4&lConquer&c]: &cThis area is currently not conquered by a nation.");
                            } else
                                inArea.remove(player.getUniqueId());

                if (Cooldowns.getCooldown(player.getUniqueId(), "CaptureCooldown") > 0)
                    return;

                if (conquerPointManager.getConquerPoint(player.getLocation()) != null) {
                    ConquerPoint cp = conquerPointManager.getConquerPoint(player.getLocation());

                    if (cp.isOnCooldown()) {
                        if (Cooldowns.tryCooldown(player.getUniqueId(), "MessageC", 3000))
                            MessageManager.sendMessage(player, "&c[&4&lConquer&c]: &d" + cp.getName() + " &cwas recently conquered, and can be reconquered in " + convertSecondsToMinutes(cp.getCooldownTime()));

                        return;
                    }

                    if (cp.getHolder() == null) {
                        if (n.equals(cp.getNation()))
                            return;

                        if (cp.getNation() == null)
                            MessageManager.broadcastMessage("&c[&4&lConquer&c]: &5" + res.getTown().getNation().getName() + " &cis attempting to conquer &d" + cp.getName() + "&c!");
                        else {
                            MessageManager.broadcastMessage("&c[&4&lConquer&c]: &5" + res.getTown().getNation().getName() + " &cis attempting to conquer &5" + cp.getNation().getName() + "&c's &d" + cp.getName() + "&c!");

                            for (Player all : TownyUniverse.getOnlinePlayers(cp.getNation()))
                                all.sendMessage("&c[&4&lConquer&c]: &5" + res.getTown().getNation().getName() + " &cis trying to conquer your territory in &5" + cp.getName() + "&c!\nDefend your territory!");
                        }

                        cp.startCapture(player);
                    }
                } else {
                    for (ConquerPoint conquerPoint : conquerPointManager.getConquerPoints())
                        if (conquerPoint.getHolder() != null)
                            if (conquerPoint.getHolder().equals(player.getUniqueId())) {
                                conquerPoint.stopCapturing();
                                MessageManager.broadcastMessage("&c[&4&lConquer&c]: &5" + res.getTown().getNation().getName() + " &chas stopped trying to conquer &d" + conquerPoint.getName() + "&c!");
                            }

                    Cooldowns.setCooldown(player.getUniqueId(), "CaptureCooldown", 30000);
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
                Resident res = TownyUniverse.getDataSource().getResident(player.getName());

                if (!res.hasTown())
                    return;

                if (!res.hasNation())
                    return;

                if (!res.getTown().hasNation())
                    return;

                Nation n = res.getTown().getNation();

                if (Cooldowns.getCooldown(player.getUniqueId(), "CaptureCooldown") > 0)
                    return;

                if (conquerPointManager.getConquerPoint(player.getLocation()) != null) {
                    ConquerPoint conquerPoint = conquerPointManager.getConquerPoint(player.getLocation());
                    if (conquerPoint.isOnCooldown()) {
                        if (Cooldowns.tryCooldown(player.getUniqueId(), "MessageC", 3000))
                            MessageManager.sendMessage(player, "&c[&4&lConquer&c]: &d" + conquerPoint.getName() + " &cwas recently conquered, and can be reconquered in " + convertSecondsToMinutes(conquerPoint.getCooldownTime()));

                        return;
                    }

                    if (conquerPoint.getHolder() == null) {
                        if (n.equals(conquerPoint.getNation()))
                            return;

                        if (conquerPoint.getNation() == null)
                            MessageManager.broadcastMessage("&c[&4&lConquer&c]: &5" + res.getTown().getNation().getName() + " &cis attempting to conquer &d" + conquerPoint.getName() + "&c!");
                        else {
                            MessageManager.broadcastMessage("&c[&4&lConquer&c]: &5" + res.getTown().getNation().getName() + " &cis attempting to conquer &5" + conquerPoint.getNation().getName() + "&c's &d" + conquerPoint.getName() + "&c!");

                            for (Player all : TownyUniverse.getOnlinePlayers(conquerPoint.getNation()))
                                MessageManager.sendMessage(all, "&c[&4&lConquer&c]: &5" + res.getTown().getNation().getName() + " &cis trying to conquer your territory in &5" + conquerPoint.getName() + "&c!\nDefend your territory!");
                        }

                        conquerPoint.startCapture(player);
                    }
                } else
                    for (ConquerPoint conquerPoint : conquerPointManager.getConquerPoints()) {
                        if (conquerPoint.getHolder() != null)
                            if (conquerPoint.getHolder().equals(player.getUniqueId())) {
                                conquerPoint.stopCapturing();

                                MessageManager.sendMessage(player, "&c[&4&lConquer&c]: &5" + res.getTown().getNation().getName() + " &chas stopped trying to conquer &d" + conquerPoint.getName() + "&c!");
                            }

                        Cooldowns.setCooldown(player.getUniqueId(), "CaptureCooldown", 30000);
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
                Resident res = TownyUniverse.getDataSource().getResident(player.getName());

                if (!res.hasTown())
                    return;

                if (!res.hasNation())
                    return;

                if (!res.getTown().hasNation())
                    return;

                Nation n = res.getTown().getNation();
                if (Cooldowns.getCooldown(player.getUniqueId(), "CaptureCooldown") > 0)
                    return;

                if (conquerPointManager.getConquerPoint(player.getLocation()) != null) {
                    ConquerPoint conquerPoint = conquerPointManager.getConquerPoint(player.getLocation());

                    if (conquerPoint.isOnCooldown())
                        if (Cooldowns.tryCooldown(player.getUniqueId(), "MessageC", 3000)) {
                            MessageManager.sendMessage(player, "&c[&4&lConquer&c]: &d" + conquerPoint.getName() + " &cwas recently conquered, and can be reconquered in " + convertSecondsToMinutes(conquerPoint.getCooldownTime()));

                            return;
                        }

                    if (conquerPoint.getHolder() == null) {
                        if (n.equals(conquerPoint.getNation()))
                            return;

                        if (conquerPoint.getNation() == null)
                            MessageManager.broadcastMessage("&c[&4&lConquer&c]: &5" + res.getTown().getNation().getName() + " &cis attempting to conquer &d" + conquerPoint.getName() + "&c!");
                        else {
                            MessageManager.broadcastMessage("&c[&4&lConquer&c]: &5" + res.getTown().getNation().getName() + " &cis attempting to conquer &5" + conquerPoint.getNation().getName() + "&c's &d" + conquerPoint.getName() + "&c!");

                            for (Player all : TownyUniverse.getOnlinePlayers(conquerPoint.getNation()))
                                MessageManager.sendMessage(all, "&c[&4&lConquer&c]: &5" + res.getTown().getNation().getName() + " &cis trying to conquer your territory in &5" + conquerPoint.getName() + "&c!\nDefend your territory!");
                        }

                        conquerPoint.startCapture(player);
                    }
                } else {
                    for (ConquerPoint conquerPoint : conquerPointManager.getConquerPoints()) {
                        if (conquerPoint.getHolder() != null)
                            if (conquerPoint.getHolder().equals(player.getUniqueId())) {
                                conquerPoint.stopCapturing();
                                MessageManager.broadcastMessage("&c[&4&lConquer&c]: &5" + res.getTown().getNation().getName() + " &chas stopped trying to conquer &d" + conquerPoint.getName() + "&c!");
                            }

                        Cooldowns.setCooldown(player.getUniqueId(), "CaptureCooldown", 30000);
                    }
                }
            } catch (NotRegisteredException e1) {
                e1.printStackTrace();
            }
        }
    }*/

    public static String convertSecondsToMinutes(int time) {
        int minutes = time / 60;
        int seconds = time % 60;
        String disMinu = "" + minutes;
        String disSec = (seconds < 10 ? "0" : "") + seconds;
        return disMinu + ":" + disSec;
    }
}
