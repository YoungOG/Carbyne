package com.medievallords.carbyne.listeners;

import com.medievallords.carbyne.Carbyne;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class OptimizationListeners implements Listener {

    @EventHandler
    public void onHungerLoss(FoodLevelChangeEvent e) {
        Player p = (Player) e.getEntity();

        if ((e.getFoodLevel() < p.getFoodLevel()) && (new Random().nextInt(100) > 4)) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onRain(WeatherChangeEvent e) {
        World w = e.getWorld();

        if (!w.hasStorm()) {
            e.setCancelled(true);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if (w.hasStorm()) {
                    w.setStorm(false);
                }
            }
        }.runTaskLater(Carbyne.getInstance(), 5L);
    }
}
