package com.medievallords.carbyne.events.implementations;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.events.Event;
import com.medievallords.carbyne.events.EventManager;
import com.medievallords.carbyne.events.EventProperties;
import com.medievallords.carbyne.events.SingleWinnerEvent;
import com.medievallords.carbyne.events.implementations.commands.CliffClimbCommands;
import com.medievallords.carbyne.events.implementations.listeners.CliffClimbListeners;
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
import java.util.logging.Level;

/**
 * Created by Dalton on 7/5/2017.
 */
@Getter
public class CliffClimb extends Event implements SingleWinnerEvent
{

    private Location cliffClimbSpawn = new Location(Bukkit.getWorld("world"), -365, 18, -1077);
    private Location winningLocation = new Location(Bukkit.getWorld("world"), -366, 105, -1072);
    private Gate eventGate;
    @Setter
    private Player winner = null;

    @Getter
    private boolean afterCountdown = false;
    private String countDownString = "5m1s";
    private long startTime = -1;
    private int ticks = 0;

    private Map<Player, BukkitRunnable> waitingTasks = new HashMap<>();

    private CliffClimbListeners cliffClimbListeners;

    public CliffClimb(EventManager eventManager)
    {
        super(eventManager, CliffClimb.class.getSimpleName());

        properties.add(EventProperties.PVP_DISABLED);
        properties.add(EventProperties.SPELLS_DISABLED);
        properties.add(EventProperties.PLUGIN_TELEPORT_DISABLED);
        properties.add(EventProperties.ENDERPEARL_TELEPORT_DISABLED);
        properties.add(EventProperties.REMOVE_PLAYER_ON_DEATH);
        properties.add(EventProperties.REMOVE_PLAYER_ON_QUIT);
        properties.add(EventProperties.PREVENT_POTION_DRINKING);

        commandWhitelistActive = true;
        whitelistedCommands.add("/cliffclimb");

        commands.add(new CliffClimbCommands(this));
        for(BaseCommand command : commands)
            Carbyne.getInstance().getCommandFramework().unregisterCommands(command);
        cliffClimbListeners = new CliffClimbListeners(this);
        Location gateBlock = new Location(Bukkit.getWorld("world"), -353, 20, -1077);
        eventGate = main.getGateManager().getGate(gateBlock);
    }

    @Override
    public void tick()
    {
        if(!active)
        {
            if(isItTimeToActivate()) start();
        }
        else
        {
            if(!afterCountdown)
            {
                long time = System.currentTimeMillis();
                if(time > startTime)
                {
                    afterCountdown = true;
                    eventGate.setKeepClosed(false);
                    eventGate.setKeepOpen(true);
                    eventGate.openGate();
                    for(Player p : participants)
                        MessageManager.sendMessage(p, "&bThe walls are down, climb the mountain!");
                }
                else
                {
                    ticks++;
                    if(ticks == 10)
                    {
                        int secondsUntilStart = (int)Math.floor(((startTime - System.currentTimeMillis()) / 1000));
                        ticks = 0;
                        for(Player p : participants)
                            MessageManager.sendMessage(p, "&bCliff Climb will begin in " + secondsUntilStart + " seconds!");
                    }
                }
            }
            else {
                if (winner != null)
                {
                    MessageManager.broadcastMessage("The winner of Cliff Climb is " + winner.getName() + "!");
                    for (Player player : participants)
                        player.sendTitle(new Title.Builder().title(winner.getDisplayName()).subtitle(ChatColor.translateAlternateColorCodes('&', "&fis the victor of Cliff Climb!")).stay(55).build());
                    this.stop();
                }
            }
        }
    }

    @Override
    public synchronized void start()
    {
        super.start();
        Carbyne.getInstance().getLogger().log(Level.INFO, "The Cliff Climb event is now active!");
        Bukkit.getPluginManager().registerEvents(cliffClimbListeners, main);
        Title title = new Title.Builder().title(ChatColor.translateAlternateColorCodes('&', "&bCliff &fClimb")).subtitle(ChatColor.translateAlternateColorCodes('&', "&rCliff Climb &bis starting! /cliffclimb join!")).stay(55).build();
        for(Player player : Bukkit.getOnlinePlayers()) {
            player.sendTitle(title);
            MessageManager.sendMessage(player, "The Cliff Climb event is starting. /cliffclimb join");
        }
        try { startTime = DateUtil.parseDateDiff(countDownString, true); } catch(Exception e) {}
    }

    @Override
    public synchronized void stop()
    {
        HandlerList.unregisterAll(cliffClimbListeners);
        afterCountdown = false;
        winner = null;
        eventGate.setKeepClosed(true);
        eventGate.setKeepOpen(false);
        eventGate.closeGate();
        super.stop();
    }

}
