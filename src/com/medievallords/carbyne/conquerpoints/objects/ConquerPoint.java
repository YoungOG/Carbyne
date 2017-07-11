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
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;

/**
 * Created by Calvin on 7/3/2017
 * for the Carbyne project.
 */
@Setter
@Getter
public class ConquerPoint {

    private Carbyne main = Carbyne.getInstance();

    private String id;
    private Location pos1, pos2;
    private ConquerPointState state;
    private UUID holder;
    private Nation nation;
    private BukkitTask captureTask, cooldownTask;
    private int captureTime, cooldownTime;

    public ConquerPoint(String id, Location pos1, Location pos2) {
        this.id = id;
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.state = ConquerPointState.OPEN;
        this.captureTime = 121;
        this.cooldownTime = 0;
    }

    public ConquerPoint(String id, Location pos1, Location pos2, Nation nation) {
        this.id = id;
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.state = ConquerPointState.OPEN;
        this.nation = nation;
        this.captureTime = 121;
        this.cooldownTime = 0;
    }

    public void startCapture(Player player) {
        try {
            Resident resident = TownyUniverse.getDataSource().getResident(player.getName());
            Nation nation = resident.getTown().getNation();
            Bukkit.getScheduler().cancelTask(this.hashCode());
            setState(ConquerPointState.CAPTURING);
            holder = player.getUniqueId();

            captureTask = new BukkitRunnable() {
                @Override
                public void run() {
                    if (captureTime > 0) {
                        captureTime--;
                    }

                    if (captureTime <= 121 && captureTime > 16) {
                        if (captureTime % 30 == 0) {
                            try {
                                MessageManager.broadcastMessage("&c[&4&lConquer&c]: &5" + resident.getTown().getNation().getName() + " &cis conquering &d" + getId() + "&c! [&4" + MessageManager.convertSecondsToMinutes(captureTime) + "&c]");
                            } catch (NotRegisteredException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    if (captureTime <= 10 && captureTime >= 1) {
                        try {
                            MessageManager.broadcastMessage("&c[&4&lConquer&c]: &5" + resident.getTown().getNation().getName() + " &cis conquering &d" + getId() + "&c! [&4" + MessageManager.convertSecondsToMinutes(captureTime) + "&c]");
                        } catch (NotRegisteredException e) {
                            e.printStackTrace();
                        }
                    }

                    if (captureTime == 0) {
                        if (captureTask != null)
                            captureTask.cancel();

                        setState(ConquerPointState.CAPTURED);

                        try {
                            if (getNation() == null) {
                                setNation(nation);
                                MessageManager.broadcastMessage("&c[&4&lConquer&c]: &5" + resident.getTown().getNation().getName() + " &chas conquered &d" + getId() + "&c! Congratulations!");
                            } else {
                                Nation n1 = getNation();
                                setNation(resident.getTown().getNation());
                                MessageManager.broadcastMessage("&c[&4&lConquer&c]: &5" + resident.getTown().getNation().getName() + " &chas conquered &5" + n1.getName() + "&c's &d" + getId() + "&c! Congratulations!");
                            }
                        } catch (NotRegisteredException e) {
                            e.printStackTrace();
                        }

                        for (Player all : TownyUniverse.getOnlinePlayers(nation)) {
                            MessageManager.sendMessage(all, "&c[&4&lConquer&c]: Your nation has conquered &d" + getId() + "&c!\nDefend it all costs!");
                        }

                        ItemStack reward = main.getGearManager().getTokenItem().clone();
                        reward.setAmount(3);

                        player.getInventory().addItem(reward);

                        Firework fw = player.getWorld().spawn(player.getEyeLocation(), Firework.class);
                        FireworkMeta meta = fw.getFireworkMeta();
                        FireworkEffect effect = FireworkEffect.builder().flicker(true).withColor(Color.RED).withFade(Color.BLACK).with(FireworkEffect.Type.BALL_LARGE).trail(true).build();
                        meta.setPower(1);
                        meta.addEffect(effect);
                        fw.setFireworkMeta(meta);

                        if (captureTask != null)
                            captureTask.cancel();

                        holder = null;
                        captureTime = 121;
                        cooldownTime = 1801;

                        setOnCooldown();
                    }
                }
            }.runTaskTimer(main, 0L, 20L);
        } catch (NotRegisteredException e) {
            e.printStackTrace();
        }
    }

    public void stopCapturing() {
        setState(ConquerPointState.OPEN);

        if (captureTask != null)
            captureTask.cancel();

        holder = null;
        captureTime = 121;
    }

    public void setOnCooldown() {
        if (cooldownTask != null)
            cooldownTask.cancel();

        cooldownTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (cooldownTime > 0) {
                    cooldownTime--;
                } else {
                    this.cancel();
                }
            }
        }.runTaskTimer(main, 0L, 20L);
    }

    public boolean isOnCooldown() {
        return cooldownTime > 0;
    }
}
