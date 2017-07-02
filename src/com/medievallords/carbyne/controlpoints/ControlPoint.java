package com.medievallords.carbyne.controlpoints;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.utils.MessageManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Williams on 2017-06-11
 * for the Carbyne project.
 */

@Getter
@Setter
public class ControlPoint {

    private String name;
    private String displayName;
    private Location location;

    private int timerId;
    private int countdownTimer;
    private int timer;
    private int maxTime;

    public UUID capper;
    public List<String> rewards = new ArrayList<>();
    public UUID countdownCapper;
    private boolean countingDown = false;

    public ControlPoint(String name, Location location, int maxTime) {
        this.name = name;
        this.location = location;
        this.maxTime = maxTime;
    }

    public ControlPoint(String name, Location location, int maxTime, String displayName) {
        this.name = name;
        this.location = location;
        this.maxTime = maxTime;
        this.displayName = displayName;
    }

    public void startTimer(UUID capper) {
        timer = maxTime;

        MessageManager.sendMessage(this.capper, "&b" + Bukkit.getPlayer(capper).getName() + " &chas captured the control point!");
        MessageManager.sendMessage(capper, "&aYou captured the point!");

        stopTimer();
        countingDown = false;
        this.capper = capper;


        timerId = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Carbyne.getInstance(), new Runnable() {


            @Override
            public void run() {
                timer--;

                if (timer == (maxTime / 2) || timer == (maxTime / 4) || timer == (maxTime / 4) * 3) {
                    broadcastTimer();
                }

                if (capper == null || Bukkit.getPlayer(capper) == null || !Bukkit.getPlayer(capper).isOnline()) {
                    stopTimer();
                }

                if (timer <= 0) {
                    win(capper);
                    stopTimer();
                }
            }
        }, 0, 20);
    }

    public void startCountdown(UUID newCapper) {

        if (countingDown) {
            if (countdownCapper == newCapper) {
                return;
            }
            stopCountdown();
        }

        if (capper != null && capper == newCapper) {
            int seconds = (timer % 60);
            int minutes = (timer - seconds) / 60;
            MessageManager.sendMessage(capper, "&aYou have &b" + minutes + " minutes " + seconds + " seconds &aleft");
            return;
        }

        countdownCapper = newCapper;
        countingDown = true;

        if (this.capper != null) {
            MessageManager.sendMessage(this.capper, "&b" + Bukkit.getPlayer(newCapper).getName() + " &cis trying to take your control point!");
        }

        MessageManager.sendMessage(newCapper, "&aCapture has started");

        int tempTime = timer;

        countdownTimer = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Carbyne.getInstance(), new Runnable() {

            int t = 10;

            @Override
            public void run() {
                t--;
                timer = tempTime;

                Firework firework = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
                firework.setFireworkMeta(getFireworkMeta(firework));

                if (countdownCapper == null || Bukkit.getPlayer(countdownCapper) == null || !Bukkit.getPlayer(countdownCapper).isOnline()) {
                    stopCountdown();
                }

                if (t <= 0) {
                    countdownCapper = null;
                    if (countdownTimer >= 1) {
                        Bukkit.getServer().getScheduler().cancelTask(countdownTimer);
                    }
                    startTimer(newCapper);

                }
            }
        }, 0, 20);
    }

    public void stopCountdown() {
        if (countdownTimer >= 1) {
            Bukkit.getServer().getScheduler().cancelTask(countdownTimer);
            if (countdownCapper != null) {
                MessageManager.sendMessage(countdownCapper, "&cCapture has been cancelled");
            }

            if (this.capper != null) {
                MessageManager.sendMessage(this.capper, "&aCapture has maintained");
            }

            countdownCapper = null;
            countingDown = false;
        }
    }

    public void stopTimer() {
        if (timerId >= 1) {
            Bukkit.getServer().getScheduler().cancelTask(timerId);
            capper = null;
        }
    }

    public void win(UUID winnerUUID) {
        Player winner = Bukkit.getServer().getPlayer(winnerUUID);
        for (String reward : rewards) {
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), reward.replace("%player%", winner.getName()));
        }

        MessageManager.broadcastMessage( "&b" + winner.getName() + "&a has captured &b" + (displayName != null ? displayName : "&aa control point"));
    }

    public FireworkMeta getFireworkMeta(Firework firework){
        FireworkMeta fireworkMeta = firework.getFireworkMeta();
        FireworkEffect effect = FireworkEffect.builder().flicker(false).trail(false).withColor(Color.RED).withFade(Color.YELLOW).with(FireworkEffect.Type.BURST).build();
        FireworkEffect effect1 = FireworkEffect.builder().trail(false).flicker(false).withColor(Color.BLUE).withFade(Color.PURPLE).with(FireworkEffect.Type.BURST).build();
        FireworkEffect effect2 = FireworkEffect.builder().trail(false).flicker(false).withColor(Color.GREEN).withFade(Color.WHITE).with(FireworkEffect.Type.BURST).build();
        fireworkMeta.clearEffects();
        fireworkMeta.addEffect(effect);
        fireworkMeta.addEffect(effect1);
        fireworkMeta.addEffect(effect2);
        return fireworkMeta;
    }

    public void broadcastTimer() {
        Player player = Bukkit.getServer().getPlayer(capper);
        if (player != null) {
            int seconds = (timer % 60);
            int minutes = (timer - seconds) / 60;

            if (seconds <= 0 && minutes >= 1) {
                MessageManager.broadcastMessage("&b" + player.getName() + " &ahas &b" + minutes + " minutes &aleft to capture their control point");
            } else if (seconds >= 1 && minutes <= 0) {
                MessageManager.broadcastMessage("&b" + player.getName() + " &ahas &b" + seconds + " seconds &aleft to capture their control point");
            } else {
                MessageManager.broadcastMessage("&b" + player.getName() + " &ahas &b" + minutes + " minutes " + seconds + " seconds &aleft to capture their control point");
            }

        }
    }
}
