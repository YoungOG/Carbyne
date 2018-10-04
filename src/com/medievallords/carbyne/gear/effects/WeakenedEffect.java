package com.medievallords.carbyne.gear.effects;

import com.medievallords.carbyne.utils.JSONMessage;
import com.medievallords.carbyne.utils.PlayerHealth;
import com.medievallords.carbyne.utils.StaticClasses;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class WeakenedEffect extends BukkitRunnable {

    private final Player player;
    private final PlayerHealth playerHealth;

    public WeakenedEffect(Player player) {
        this.player = player;
        this.playerHealth = PlayerHealth.getPlayerHealth(player.getUniqueId());
    }

    private int beat = 0;
    private boolean beatNext = false;
    private int beatNextI = 0;

    @Override
    public void run() {
        if (playerHealth.getHealth() > playerHealth.getMaxHealth() * 0.1 || player.isDead()) {
            cancel();
            StaticClasses.gearListeners.removeFromExhaust(player);
            return;
        }

        if (beat >= 18) {
            beat();
            beat = 0;
            beatNext = true;
        } else if (beatNext) {
            beatNextI++;

            if (beatNextI >= 4) {
                beatNextI = 0;
                beat();
                beatNext = false;
            }
        } else {
            String s = ChatColor.translateAlternateColorCodes('&', String.format("&c&l%s \u2665", ""));
            JSONMessage json = JSONMessage.create(s);
            json.actionbar(player);
        }
        beat++;
    }

    private void beat() {
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BASEDRUM, .55f, .63f);
        String s = ChatColor.translateAlternateColorCodes('&', String.format("&4&l%s \u2764", ""));
        JSONMessage json = JSONMessage.create(s);
        json.actionbar(player);
    }
}
