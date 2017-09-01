package com.medievallords.carbyne.events.implementations;

import com.medievallords.carbyne.events.Event;
import com.medievallords.carbyne.events.EventManager;
import com.medievallords.carbyne.events.EventProperties;
import com.medievallords.carbyne.events.SingleWinnerEvent;
import com.medievallords.carbyne.events.component.DonationComponent;
import com.medievallords.carbyne.events.implementations.listeners.RaceListeners;
import com.medievallords.carbyne.events.implementations.object.RaceObject;
import com.medievallords.carbyne.utils.DateUtil;
import com.medievallords.carbyne.utils.JSONMessage;
import com.medievallords.carbyne.utils.MessageManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.github.paperspigot.Title;

/**
 * Created by Dalton on 7/10/2017.
 */
public class Race extends Event implements SingleWinnerEvent {

    @Getter
    @Setter
    private RaceObject currentRace;

    @Getter
    @Setter
    private Player winner = null;

    @Getter
    private boolean afterCountdown;

    private RaceListeners raceListener;

    private String countDownString = "3m";
    private long startTime = -1;
    private int lastMinute = -1;

    public Race(EventManager eventManager) {
        super(eventManager, Race.class.getSimpleName());

        properties.add(EventProperties.PVP_DISABLED);
        properties.add(EventProperties.SPELLS_DISABLED);
        properties.add(EventProperties.REMOVE_PLAYER_ON_DEATH);
        properties.add(EventProperties.REMOVE_PLAYER_ON_QUIT);
        properties.add(EventProperties.PREVENT_POTION_DRINKING);
        properties.add(EventProperties.ENDERPEARL_TELEPORT_DISABLED);
        properties.add(EventProperties.PLUGIN_TELEPORT_DISABLED);
        properties.add(EventProperties.HUNGER_DISABLED);

        raceListener = new RaceListeners(this);

        commandWhitelistActive = true;
        whitelistedCommands.add("/event.donate");
        whitelistedCommands.add("/event donate");
        whitelistedCommands.add("/event");

        components.add(new DonationComponent(this));

        currentRace = null;
    }

    @Override
    public void tick() {
        if (!active) {
            if (isItTimeToActivate()) start();
        } else {
            if (!afterCountdown) {
                long time = System.currentTimeMillis();
                if (time > startTime) {
                    afterCountdown = true;
                    syncChangeBlockLocation(getCurrentRace().getGateLocation(), Material.REDSTONE_BLOCK);
                    for (Player p : participants)
                        MessageManager.sendMessage(p, currentRace.getStartString());
                } else {
                    int minutesUntilStart = (int) Math.floor(((startTime - System.currentTimeMillis()) / (1000 * 60)) % 60) + 1;
                    if (lastMinute == -1) lastMinute = minutesUntilStart;
                    if (minutesUntilStart != lastMinute) {
                        lastMinute = minutesUntilStart;
                        String message = new String("&bThe %race%&b will begin in " + minutesUntilStart + " minute" + ((minutesUntilStart <= 1) ? "" : "s") + "!").replace("%race%", currentRace.getName());
                        JSONMessage json = JSONMessage.create(ChatColor.translateAlternateColorCodes('&', message + " &bClick here to join!"));
                        json.runCommand("/event race join");
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            if (!participants.contains(player)) json.send(player);
                            else MessageManager.sendMessage(player, message);
                        }
                    }
                    if (minutesUntilStart == 1) {
                        int secondsUntilStart = (int) ((startTime - System.currentTimeMillis()) / 1000) % 60;
                        if (secondsUntilStart <= 5 && secondsUntilStart != 0) {
                            for (Player player : participants)
                                MessageManager.sendMessage(player, "&b" + secondsUntilStart);
                        }
                    }
                }
            } else {
                if (winner != null) {
                    String winMsg = new String("&bThe winner of %race%&b is " + winner.getName() + "&b!").replace("%race%", currentRace.getName());
                    MessageManager.broadcastMessage(winMsg);
                    String globalMessage = new String("&bis the victor of %race%!").replace("%race%", currentRace.getName());
                    for (Player player : participants)
                        player.sendTitle(new Title.Builder().title(winner.getDisplayName()).subtitle(ChatColor.translateAlternateColorCodes('&', globalMessage)).stay(55).build());
                    this.stop();
                }
            }
        }
    }

    @Override
    public synchronized void start() {
        Title title = new Title.Builder().title(ChatColor.translateAlternateColorCodes('&', "&b%race%".replace("%race%", currentRace.getName()))).subtitle(ChatColor.translateAlternateColorCodes('&', "&bThe %race% &bis starting! /event race join!".replace("%race%", currentRace.getName()))).stay(55).build();
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendTitle(title);
            String message = new String("&bThe %race%&b race is starting. Click here to join!".replace("%race%", currentRace.getName()));
            JSONMessage json = JSONMessage.create(ChatColor.translateAlternateColorCodes('&', message));
            json.runCommand("/event race join");
            json.send(Bukkit.getOnlinePlayers().toArray(new Player[0]));
        }
        try {
            startTime = DateUtil.parseDateDiff(countDownString, true);
        } catch (Exception e) {
        }
        Bukkit.getServer().getPluginManager().registerEvents(raceListener, main);
        super.start();
    }

    @Override
    public synchronized void stop() {
        afterCountdown = false;
        lastMinute = -1;
        syncChangeBlockLocation(currentRace.getGateLocation(), Material.AIR);
        HandlerList.unregisterAll(raceListener);
        super.stop();
        winner = null;
    }

}
