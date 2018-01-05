package com.medievallords.carbyne.gear.effects;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.utils.JSONMessage;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ExhaustEffect extends BukkitRunnable {

    private Player player;

    public ExhaustEffect(Player player) {
        this.player = player;
    }

    private int beat = 0;
    private boolean beatNext = false;
    private int beatNextI = 0;

    @Override
    public void run() {
        if (player.getHealth() >= 30) {
            cancel();
            Carbyne.getInstance().getGearListeners().removeFromExhaust(player);
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
        player.playSound(player.getLocation(), Sound.NOTE_BASS_DRUM, .55f, .63f);
        String s = ChatColor.translateAlternateColorCodes('&', String.format("&4&l%s \u2764", ""));
        JSONMessage json = JSONMessage.create(s);
        json.actionbar(player);
    }
}
