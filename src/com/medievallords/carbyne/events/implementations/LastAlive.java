package com.medievallords.carbyne.events.implementations;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.events.Event;
import com.medievallords.carbyne.events.EventManager;
import com.medievallords.carbyne.events.EventProperties;
import com.medievallords.carbyne.events.SingleWinnerEvent;
import com.medievallords.carbyne.events.component.DonationComponent;
import com.medievallords.carbyne.events.component.InventoryComponent;
import com.medievallords.carbyne.events.implementations.listeners.LastAliveListeners;
import com.medievallords.carbyne.events.implementations.object.LastAliveObject;
import com.medievallords.carbyne.utils.DateUtil;
import com.medievallords.carbyne.utils.JSONMessage;
import com.medievallords.carbyne.utils.MessageManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.scheduler.BukkitRunnable;
import org.github.paperspigot.Title;

/**
 * Created by Dalton on 8/19/2017.
 */
public class LastAlive extends Event implements SingleWinnerEvent {


    @Getter
    private boolean isAfterCountdown;

    private LastAliveListeners lastAliveListeners;

    private String timeoutString = "3m";
    private long timeoutActual = -1;
    private int lastMinute = -1;

    @Getter
    @Setter
    private LastAliveObject currentLastAliveObject;

    @Setter
    private Player winner = null;

    public LastAlive(EventManager eventManager) {
        super(eventManager, LastAlive.class.getSimpleName());
        properties.add(EventProperties.SPELLS_DISABLED);
        properties.add(EventProperties.REMOVE_PLAYER_ON_QUIT);
        properties.add(EventProperties.REMOVE_PLAYER_ON_DEATH);

        lastAliveListeners = new LastAliveListeners(this);

        commandWhitelistActive = true;
        whitelistedCommands.add("/event.donate");
        whitelistedCommands.add("/event donate");
        whitelistedCommands.add("/event");

        components.add(new DonationComponent(this));
        components.add(new InventoryComponent(this));

    }

    @Override
    public void tick() {
        if (!active) {
            if (isItTimeToActivate()) start();
        } else {
            if (!isAfterCountdown) {
                if (maxPlayers() == participants.size()) {
                    begin();
                } else if (timeoutActual <= System.currentTimeMillis()) {
                    begin();
                } else {
                    int minutesUntilTimeout = (int) Math.floor(((timeoutActual - System.currentTimeMillis()) / (1000 * 60)) % 60) + 1;

                    if (minutesUntilTimeout != lastMinute) {
                        lastMinute = minutesUntilTimeout;
                        String message = ("&bThe %name%&b is going to begin in " + minutesUntilTimeout + ((minutesUntilTimeout == 1) ? " minute" : " minutes") + " &bor when " + getCurrentLastAliveObject().getSpawnLocations().size() + " &bplayers join!").replace("%name%", currentLastAliveObject.getName());
                        JSONMessage json = JSONMessage.create(ChatColor.translateAlternateColorCodes('&', message + " &bClick here to join!"));
                        json.runCommand("/event lastalive join");
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            if (!participants.contains(player)) json.send(player);
                            else MessageManager.sendMessage(player, message);
                        }
                    }

                    if (lastMinute == -1)
                        lastMinute = minutesUntilTimeout;
                }
            } else {
                if (winner != null) {
                    String winMsg = ("&bThe winner of %name%&b is " + winner.getName() + "&b!").replace("%name%", currentLastAliveObject.getName());
                    MessageManager.broadcastMessage(winMsg);
                    this.stop();
                } else if (participants.size() == 1) {
                    winner = participants.get(0);
                    String winMsg = ("&bThe winner of %name%&b is " + winner.getName() + "&b!").replace("%name%", currentLastAliveObject.getName());
                    MessageManager.broadcastMessage(winMsg);
                    this.stop();
                } else if (participants.size() == 0) {
                    String winMsg = "&bThe participants of %name%&b are all dead!".replace("%name%", currentLastAliveObject.getName());
                    MessageManager.broadcastMessage(winMsg);
                    this.stop();
                }
            }
        }
    }

    @Override
    public Player getWinner() {
        return this.winner;
    }

    @Override
    public synchronized void start() {
        Title title = new Title.Builder().title(ChatColor.translateAlternateColorCodes('&', "&b%name%".replace("%name%", currentLastAliveObject.getName()))).subtitle(ChatColor.translateAlternateColorCodes('&', "&bThe %name% &bis starting! /event lastalive join!".replace("%name%", currentLastAliveObject.getName()))).stay(55).build();

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendTitle(title);
        }

        Bukkit.getServer().getPluginManager().registerEvents(lastAliveListeners, main);

        try {
            timeoutActual = DateUtil.parseDateDiff(timeoutString, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.start();
    }

    @Override
    public synchronized void stop() {
        if (participants.size() >= 1)
            participants.get(0).getInventory().clear();

        isAfterCountdown = false;
        HandlerList.unregisterAll(lastAliveListeners);
        super.stop();
        winner = null;
        timeoutActual = -1;
        lastMinute = -1;
    }

    private void teleportPlayersToStartLocations() {
        new BukkitRunnable() {
            public void run() {
                for (int i = 0; i < participants.size(); i++) {
                    try {
                        participants.get(i).teleport(currentLastAliveObject.getSpawnLocations().get(i));
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        break;
                    }
                }
            }
        }.runTask(Carbyne.getInstance());
    }

    private void begin() {
        isAfterCountdown = true;
        getEventComponent(InventoryComponent.class).tick();
        teleportPlayersToStartLocations();
    }

    public int maxPlayers() {
        return currentLastAliveObject.getSpawnLocations().size();
    }

}
