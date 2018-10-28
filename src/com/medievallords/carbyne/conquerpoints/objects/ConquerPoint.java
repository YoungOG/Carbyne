package com.medievallords.carbyne.conquerpoints.objects;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.utils.MessageManager;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

@Getter
@Setter
public class ConquerPoint {

    private String name;
    private Location pos1, pos2;
    private ConquerPointState state;
    private Player holder;
    private Nation nation;
    private int captureTID, captureTime, cooldownTID, cooldownTime, maxCaptureTime;


    public ConquerPoint(String name, Location pos1, Location pos2) {
        this.name = name;
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.state = ConquerPointState.OPEN;
        this.holder = null;
        this.captureTID = new Random().nextInt(1000000);
        this.maxCaptureTime = 601;
        this.captureTime = 601;
        this.cooldownTID = captureTID + 1;
        this.cooldownTime = 0;

        final double dX = (pos1.getX() - pos2.getX());
        final double dY = (pos1.getY() - pos2.getY());
        final double dZ = (pos1.getZ() - pos2.getZ());

        update(new Location(pos1.getWorld(), pos1.getX() + (dX / 2),
                pos1.getY() + (dY / 2), pos1.getZ() + (dZ / 2)), Math.sqrt(dX * dX + dY * dY + dZ * dZ));
    }

    public boolean isOnCooldown() {
        return cooldownTime > 0;
    }

    //&c[&4&lConquer&c]: &5Vollandore &chas begun capturing &dHaven&c! [&415:00&c]

    private void update(final Location center, final double radius) {
        captureTID = Bukkit.getScheduler().scheduleSyncRepeatingTask(Carbyne.getInstance(), new Runnable() {
            @Override
            public void run() {
                if (holder != null) {
                    //Someone is capturing. Check if they are in the zone.
                    if (!isInArea(holder.getLocation())) {
                        if (captureTime < maxCaptureTime) {
                            captureTime++;
                        } else {
                            holder = null;
                            captureTime = maxCaptureTime;
                            selectFirstPlayer(center, radius);
                        }
                    } else {
                        if (--captureTime <= 0) {
                            handleCapture(holder);
                        }
                    }
                } else {
                    selectFirstPlayer(center, radius);
                }
            }
        }, 20, 20);

    }

    private void selectFirstPlayer(final Location center, final double radius) {
        for (Entity entity : center.getWorld().getNearbyEntities(center, radius, radius, radius)) {
            if (!(entity instanceof Player)) {
                continue;
            }

            Player player = (Player) entity;
            if (isInArea(player.getLocation())) {
                //Check for towns and such.
                try {
                    Resident res = TownyUniverse.getDataSource().getResident(player.getName());

                    if (!res.hasTown())
                        return;

                    if (!res.hasNation())
                        return;

                    if (!res.getTown().hasNation())
                        return;

                    if (res.getTown().getNation().equals(nation)) {
                        return;
                    }

                } catch (NotRegisteredException e) {
                    continue;
                }

                //Player is now capturing.
                holder = player;
                captureTime = maxCaptureTime;
                MessageManager.sendMessage(player, "&cYou are now capturing " + name); //Edit message.
                break;
            }
        }
    }

    private void handleCapture(Player player) {
        Bukkit.getScheduler().cancelTask(captureTID);
        setState(ConquerPointState.CAPTURED);
        Resident res = null;
        Nation n = null;

        try {
            res = TownyUniverse.getDataSource().getResident(player.getName());
            n = res.getTown().getNation();
        } catch (NotRegisteredException e) {
            return;
        }

        try {
            if (getNation() == null) {
                setNation(n);
                MessageManager.broadcastMessage("�c[�4�lConquer�c]: �5" + res.getTown().getNation().getName() + " �chas conquered �d" + getName() + "�c! Congratulations!");
            } else {
                Nation n1 = getNation();
                setNation(res.getTown().getNation());
                MessageManager.broadcastMessage("�c[�4�lConquer�c]: �5" + res.getTown().getNation().getName() + " �chas conquered �5" + n1.getName() + "�c's �d" + getName() + "�c! Congratulations!");
            }
        } catch (NotRegisteredException e) {
            e.printStackTrace();
        }

        for (Player all : TownyUniverse.getOnlinePlayers(n))
            MessageManager.sendMessage(all, "�c[�4�lConquer�c]: �cYour nation has conquered �d" + getName() + "�c!\nDefend it at all costs!");

        Firework fw = player.getWorld().spawn(player.getEyeLocation(), Firework.class);
        FireworkMeta meta = fw.getFireworkMeta();
        FireworkEffect effect = FireworkEffect.builder().flicker(true).withColor(Color.RED).withFade(Color.BLACK).with(FireworkEffect.Type.BALL_LARGE).trail(true).build();
        meta.setPower(1);
        meta.addEffect(effect);
        fw.setFireworkMeta(meta);

        holder = null;
        captureTime = maxCaptureTime;

        new BukkitRunnable() {
            @Override
            public void run() {
                final double dX = (pos1.getX() - pos2.getX());
                final double dY = (pos1.getY() - pos2.getY());
                final double dZ = (pos1.getZ() - pos2.getZ());

                update(new Location(pos1.getWorld(), pos1.getX() + (dX / 2),
                        pos1.getY() + (dY / 2), pos1.getZ() + (dZ / 2)), Math.sqrt(dX * dX + dY * dY + dZ * dZ));
            }
        }.runTaskLater(Carbyne.getInstance(), cooldownTime);
    }

    /*public void startCapture(final Player p) {
        try {
            final Resident res = TownyUniverse.getDataSource().getResident(p.getName());
            final Nation n = res.getTown().getNation();
            Bukkit.getScheduler().cancelTask(this.hashCode());
            setState(ConquerPointState.CAPTURING);
            holder = p;
            captureTID = Bukkit.getScheduler().scheduleSyncRepeatingTask(main, () -> {
                if (captureTime > 0)
                    captureTime--;

                if (captureTime <= 601 && captureTime > 16)
                    if (captureTime % 30 == 0)
                        try {
                            MessageManager.broadcastMessage("�c[�4�lConquer�c]: �5" + res.getTown().getNation().getName() + " �cis conquering �d" + getName() + "�c! [�4" + convertSecondsToMinutes(captureTime) + "�c] + �e /rules control");
                        } catch (NotRegisteredException e) {
                            e.printStackTrace();
                        }

                if (captureTime <= 10 && captureTime >= 1)
                    try {
                        MessageManager.broadcastMessage("�c[�4�lConquer�c]: �5" + res.getTown().getNation().getName() + " �cis conquering �d" + getName() + "�c! [�4" + convertSecondsToMinutes(captureTime) + "�c]");
                    } catch (NotRegisteredException e) {
                        e.printStackTrace();
                    }

                if (captureTime == 0) {
                    Bukkit.getScheduler().cancelTask(captureTID);
                    setState(ConquerPointState.CAPTURED);

                    try {
                        if (getNation() == null) {
                            setNation(n);
                            MessageManager.broadcastMessage("�c[�4�lConquer�c]: �5" + res.getTown().getNation().getName() + " �chas conquered �d" + getName() + "�c! Congratulations!");
                        } else {
                            Nation n1 = getNation();
                            setNation(res.getTown().getNation());
                            MessageManager.broadcastMessage("�c[�4�lConquer�c]: �5" + res.getTown().getNation().getName() + " �chas conquered �5" + n1.getName() + "�c's �d" + getName() + "�c! Congratulations!");
                        }
                    } catch (NotRegisteredException e) {
                        e.printStackTrace();
                    }

                    for (Player all : TownyUniverse.getOnlinePlayers(n))
                        MessageManager.sendMessage(all, "�c[�4�lConquer�c]: �cYour nation has conquered �d" + getName() + "�c!\nDefend it at all costs!");

                    Firework fw = p.getWorld().spawn(p.getEyeLocation(), Firework.class);
                    FireworkMeta meta = fw.getFireworkMeta();
                    FireworkEffect effect = FireworkEffect.builder().flicker(true).withColor(Color.RED).withFade(Color.BLACK).with(FireworkEffect.Type.BALL_LARGE).trail(true).build();
                    meta.setPower(1);
                    meta.addEffect(effect);
                    fw.setFireworkMeta(meta);

                    Bukkit.getScheduler().cancelTask(captureTID);
                    holder = null;
                    captureTime = 601;
                    cooldownTime = 1801;
                    startCooldown();
                }
            }, 0, 20);
        } catch (NotRegisteredException e) {
            e.printStackTrace();
        }
    }

    public void startCooldown() {
        Bukkit.getScheduler().cancelTask(cooldownTID);
        cooldownTID = Bukkit.getScheduler().scheduleSyncRepeatingTask(main, () -> {
            if (cooldownTime > 0)
                cooldownTime--;
        }, 0, 20);
    }

    public void stopCapturing() {
        setState(ConquerPointState.OPEN);
        Bukkit.getScheduler().cancelTask(captureTID);
        holder = null;
        captureTime = 601;
    }*/

    public boolean isInArea(Location location) {
        int minX = Math.min(pos1.getBlockX(), pos2.getBlockX());
        int minY = Math.min(pos1.getBlockY(), pos2.getBlockY());
        int minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
        int maxX = Math.max(pos1.getBlockX(), pos2.getBlockX());
        int maxY = Math.max(pos1.getBlockY(), pos2.getBlockY());
        int maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());
        return (minX <= location.getBlockX() && location.getBlockX() <= maxX && minY <= location.getBlockY() && location.getBlockY() <= maxY && minZ <= location.getBlockZ() && location.getBlockZ() <= maxZ);
    }

    public static String convertSecondsToMinutes(int time) {
        int minutes = time / 60;
        int seconds = time % 60;
        String disMinu = "" + minutes;
        String disSec = (seconds < 10 ? "0" : "") + seconds;
        return disMinu + ":" + disSec;
    }
}
