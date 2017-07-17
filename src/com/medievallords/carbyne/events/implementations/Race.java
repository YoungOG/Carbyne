package com.medievallords.carbyne.events.implementations;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.events.Event;
import com.medievallords.carbyne.events.EventManager;
import com.medievallords.carbyne.events.EventProperties;
import com.medievallords.carbyne.events.implementations.commands.RaceCommands;
import com.medievallords.carbyne.events.implementations.enumerations.RaceType;
import com.medievallords.carbyne.events.implementations.listeners.RaceListeners;
import com.medievallords.carbyne.gates.Gate;
import com.medievallords.carbyne.utils.DateUtil;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.scheduler.BukkitRunnable;
import org.github.paperspigot.Title;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Dalton on 7/10/2017.
 */
public class Race extends Event {

    @Getter
    @Setter
    private RaceType currentRace;

    @Getter
    @Setter
    private Player winner = null;

    @Getter
    private boolean afterCountdown;

    @Getter
    private Location winningLocation = new Location(Bukkit.getWorld("world"), -186, 99, 1389);
    @Getter
    private Location eventSpawn = new Location(Bukkit.getWorld("world"), -208, 42, 1360);

    private Gate eventGate;

    private RaceListeners raceListener;

    @Getter
    private Map<Player, BukkitRunnable> waitingTasks = new HashMap<>();

    private String countDownString = "300s";
    private long startTime = -1;
    private int ticks = 0;

    public Race(EventManager eventManager) {
        super(eventManager);

        properties.add(EventProperties.PVP_DISABLED);
        properties.add(EventProperties.SPELLS_DISABLED);
        properties.add(EventProperties.REMOVE_PLAYER_ON_DEATH);
        properties.add(EventProperties.REMOVE_PLAYER_ON_QUIT);
        properties.add(EventProperties.PREVENT_POTION_DRINKING);
        properties.add(EventProperties.ENDERPEARL_TELEPORT_DISABLED);
        properties.add(EventProperties.PLUGIN_TELEPORT_DISABLED);

        raceListener = new RaceListeners(this);

        commandWhitelistActive = true;
        whitelistedCommands.add("/race");

        commands.add(new RaceCommands(this));
        for (BaseCommand command : commands)
            Carbyne.getInstance().getCommandFramework().unregisterCommands(command);

        currentRace = RaceType.FIESTA_BOWL;
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
                    eventGate.setKeepClosed(false);
                    eventGate.setKeepOpen(true);
                    eventGate.openGate();
                    for (Player p : participants)
                        MessageManager.sendMessage(p, currentRace.getStartString());
                } else {
                    ticks++;
                    if (ticks == 10) {
                        int secondsUntilStart = (int) Math.floor(((startTime - System.currentTimeMillis()) / 1000));
                        ticks = 0;
                        for (Player p : participants)
                            MessageManager.sendMessage(p, "&2The %race% will begin in " + secondsUntilStart + " seconds!".replace("%race%", currentRace.getRaceName()));
                    }
                }
            } else {
                if (winner != null) {
                    MessageManager.broadcastMessage("The winner of %race% is " + winner.getName() + "!".replace("%race%", ""));
                    for (Player player : participants)
                        player.sendTitle(new Title.Builder().title(winner.getDisplayName()).subtitle(ChatColor.translateAlternateColorCodes('&', "&fis the victor of %race%!".replace("%race%", currentRace.getRaceName()))).stay(55).build());
                    this.stop();
                }
            }
        }
    }

    @Override
    public synchronized void start() {
        Title title = new Title.Builder().title(ChatColor.translateAlternateColorCodes('&', "&2%race%".replace("%race%", currentRace.getRaceName()))).subtitle(ChatColor.translateAlternateColorCodes('&', "&f%race% is starting! /cliffclimb join!".replace("%race%", currentRace.getRaceName()))).stay(55).build();
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendTitle(title);
            MessageManager.sendMessage(player, "The %race% race is starting. /race join".replace("%race%", currentRace.getRaceName()));
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
        eventGate.setKeepClosed(true);
        eventGate.setKeepOpen(false);
        eventGate.closeGate();
        HandlerList.unregisterAll(raceListener);
        winner = null;
        super.stop();
    }

}
