package com.medievallords.carbyne.events;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.events.component.DonationComponent;
import com.medievallords.carbyne.events.component.InventoryComponent;
import com.medievallords.carbyne.events.implementations.LastAlive;
import com.medievallords.carbyne.events.implementations.Race;
import com.medievallords.carbyne.events.implementations.object.LastAliveObject;
import com.medievallords.carbyne.events.implementations.object.RaceObject;
import com.medievallords.carbyne.profiles.Profile;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.PlayerUtility;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.List;

public class UniversalEventCommand extends BaseCommand {

    private EventManager eventManager = Carbyne.getInstance().getEventManager();
    private Race race;
    private LastAlive lastAlive;

    public UniversalEventCommand(Race race, LastAlive lastAlive) {
        this.race = race;
        this.lastAlive = lastAlive;
    }

    @Command(name = "event")
    public void onCommand(CommandArgs cmdargs) {
        Player player = cmdargs.getPlayer();
        String[] args = cmdargs.getArgs();

        if (args.length >= 1) {
            if (args[0].equalsIgnoreCase("start") && player.hasPermission("carbyne.event.admin")) {
                if (args.length >= 2) {
                    String eventName = args[1];
                    List<Event> events = eventManager.getWaitingEvents();
                    Event event = null;
                    for (Event e : events)
                        if (e.getEventName().equalsIgnoreCase(args[1])) {
                            event = e;
                            break;
                        }

                    if (event == null) {
                        MessageManager.sendMessage(player, "&cEvent is not found or is currently active!");
                        return;
                    }

                    if (event.isActive()) {
                        MessageManager.sendMessage(player, "&cThis event is already active!");
                        return;
                    }

                    if (event.getEventName().equalsIgnoreCase("race") && event instanceof Race) {
                        if (args.length >= 3) {
                            String raceName = args[2];
                            RaceObject raceType = RaceObject.getRaceObject(raceName);
                            if (raceType == null) {
                                MessageManager.sendMessage(player, "&cA race with the name " + raceName + "&c was not found!");
                                return;
                            }
                            if (!raceType.isReady()) {
                                MessageManager.sendMessage(player, "&cThis race has empty properties and cannot be started!");
                                return;
                            }
                            ((Race) event).setCurrentRace(raceType);
                            processEventFlags(3, player, args, event);
                            event.start();
                            return;
                        } else sendHelp(player);
                    } else if (event.getEventName().equalsIgnoreCase("lastalive") && event instanceof LastAlive) {
                        String lastAliveName = args[2];
                        LastAliveObject lastAliveObject = LastAliveObject.getLastAliveObject(lastAliveName);
                        if (lastAliveObject == null) {
                            MessageManager.sendMessage(player, "&cA lastAlive with the name of " + lastAliveName + "&c was not found!");
                            return;
                        }
                        if (!lastAliveObject.isReady()) {
                            MessageManager.sendMessage(player, "&cThis LastAlive has missing properties and cannot be started!");
                            return;
                        }
                        ((LastAlive) event).setCurrentLastAliveObject(lastAliveObject);
                        processEventFlags(3, player, args, event);
                        event.start();
                        return;
                    }
                } else sendHelp(player);
            } else if (args[0].equalsIgnoreCase("race")) {
                if (args.length >= 2) {
                    if (args[1].equalsIgnoreCase("admin") && player.hasPermission("carbyne.event.admin")) {
                        if (args.length >= 3) {
                            switch (args[2].toLowerCase()) {
                                case "create": {
                                    if (args.length >= 4) {
                                        if (RaceObject.getRaceObject(args[3]) == null) {
                                            RaceObject.raceObjects.add(new RaceObject(false, args[3], null, null, null, null));
                                            MessageManager.sendMessage(player, "&bA race has been created with the name " + args[3] + "&b but still has empty properties!");
                                            return;
                                        } else {
                                            MessageManager.sendMessage(player, "&cA race with this name already exists!");
                                            return;
                                        }
                                    }
                                    break;
                                }
                                case "list": {
                                    for (RaceObject raceObject : RaceObject.raceObjects)
                                        MessageManager.sendMessage(player, raceObject.toString());
                                    break;
                                }
                                default: {
                                    String raceName = args[2];
                                    RaceObject raceData = RaceObject.getRaceObject(args[2]);
                                    if (raceData == null) {
                                        sendRaceAdminHelp(player);
                                        return;
                                    } else {
                                        if (args.length >= 4) {
                                            switch (args[3].toLowerCase()) {
                                                case "name": {
                                                    if (args.length >= 5) {
                                                        String newName = args[4];
                                                        RaceObject race = null;
                                                        if ((race = RaceObject.getRaceObject(newName)) == null) {
                                                            race.setName(newName);
                                                            MessageManager.sendMessage(player, "&bRace name change successful!");
                                                        } else
                                                            MessageManager.sendMessage(player, "&cA race with the name " + newName + "&c is not found!");
                                                    } else sendRaceAdminHelp(player);
                                                    break;
                                                }
                                                case "startingmessage": {
                                                    StringBuilder sb = new StringBuilder();
                                                    for (int i = 4; i < args.length; i++) sb.append(args[i] + " ");
                                                    raceData.setStartString(sb.toString());
                                                    raceData.setStartString(sb.toString());
                                                    MessageManager.sendMessage(player, "&bStarting message set!");
                                                    break;
                                                }
                                                case "startinglocation": {
                                                    raceData.setStartingLocation(player.getLocation());
                                                    MessageManager.sendMessage(player, "&bStarting Location set to your location!");
                                                    break;
                                                }
                                                case "winninglocation": {
                                                    Block target = player.getTargetBlock((HashSet<Byte>) null, 50);
                                                    if (target == null || !target.getType().toString().contains("PLATE")) {
                                                        MessageManager.sendMessage(player, "&cYou must be looking at a pressure plate!");
                                                        return;
                                                    }
                                                    raceData.setWinningLocation(target.getLocation());
                                                    MessageManager.sendMessage(player, "&bWinning Location set to the plate at your eye location!");
                                                    break;
                                                }
                                                case "gatelocation": {
                                                    Block target = player.getTargetBlock((HashSet<Byte>) null, 50);
                                                    if (target == null) {
                                                        MessageManager.sendMessage(player, "&cBlock not found!");
                                                        return;
                                                    }
                                                    raceData.setGateLocation(target.getLocation());
                                                    MessageManager.sendMessage(player, "&bGate Location set to the block at your eye location!");
                                                    break;
                                                }
                                                default: {
                                                    sendRaceAdminHelp(player);
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } else sendRaceAdminHelp(player);
                    } else if (args[1].equalsIgnoreCase("join")) {
                        joinEvent(player, race);
                    } else if (args[1].equalsIgnoreCase("leave")) {
                        leaveEvent(player, race);
                    } else MessageManager.sendMessage(player, "&2You do not have permission to use this command!");
                } else sendRacePlayerHelp(player);
            } else if (args[0].equalsIgnoreCase("lastalive")) {
                if (args.length >= 2) {
                    if (args[1].equalsIgnoreCase("admin") && player.hasPermission("carbyne.event.admin")) {
                        if (args.length >= 3) {
                            switch (args[2].toLowerCase()) {
                                case "create": {
                                    if (args.length >= 4) {
                                        if (LastAliveObject.getLastAliveObject(args[3]) != null) {
                                            MessageManager.sendMessage(player, "&cA last alive with the name " + args[3] + "&r&c already exists!");
                                            return;
                                        } else {
                                            LastAliveObject.lastAliveObjects.add(new LastAliveObject(false, args[3], null, null));
                                            MessageManager.sendMessage(player, "&bA last alive with the name of " + args[3] + " &r&bhas been created but still has empty properties!");
                                            return;
                                        }
                                    }
                                }
                                case "list": {
                                    for (LastAliveObject data : LastAliveObject.lastAliveObjects) {
                                        MessageManager.sendMessage(player, data.toString());
                                    }
                                    return;
                                }
                                default: {
                                    String lastAliveName = args[2];
                                    LastAliveObject lastAliveObject;
                                    if ((lastAliveObject = LastAliveObject.getLastAliveObject(lastAliveName)) == null) {
                                        MessageManager.sendMessage(player, "&cA lastAlive with that name was not found!");
                                        return;
                                    }
                                    if (args.length >= 4) {
                                        switch (args[3].toLowerCase()) {
                                            case "name": {
                                                if (args.length >= 5) {
                                                    if (LastAliveObject.getLastAliveObject(args[4]) == null) {
                                                        lastAliveObject.setName(args[4]);
                                                        MessageManager.sendMessage(player, "bName of LastAlive changed to " + args[4] + "&r&b!");
                                                    } else {
                                                        MessageManager.sendMessage(player, "&cA LastAlive already has that name!");
                                                    }
                                                    return;
                                                }
                                            }
                                            case "lobbylocation": {
                                                lastAliveObject.setLobby(player.getLocation());
                                                MessageManager.sendMessage(player, "&bLobby location set to your current location!");
                                                return;
                                            }
                                            case "spawnlocation": {
                                                if (args.length >= 5) {
                                                    if (args[4].equalsIgnoreCase("clear"))
                                                        lastAliveObject.getSpawnLocations().clear();
                                                    else sendLastAliveAdminHelp(player);
                                                } else {
                                                    lastAliveObject.getSpawnLocations().add(player.getLocation());
                                                    MessageManager.sendMessage(player, "&bYour current location has been added as a spawn location!");
                                                }
                                                return;
                                            }
                                        }
                                    } else sendLastAliveAdminHelp(player);
                                }
                            }
                        } else sendLastAliveAdminHelp(player);
                    } else if (args[1].equalsIgnoreCase("join") && lastAlive.isActive()) {
                        if (lastAlive.isAfterCountdown()) {
                            MessageManager.sendMessage(player, "&2This event has already started!");
                        } else if (lastAlive.maxPlayers() < lastAlive.participants.size() + 1) {
                            MessageManager.sendMessage(player, "&cThis event is full :(");
                        } else {
                            joinEvent(player, lastAlive);
                        }
                    } else if (args[1].equalsIgnoreCase("leave") && lastAlive.properties.contains(player)) {
                        leaveEvent(player, lastAlive);
                    } else sendLastAliveHelp(player);
                }
            }
        } else sendHelp(player);
    }

    /**
     * Mega method for processing event command arguments to customize event game play.
     *
     * @param index  The index to start looking for arguments from in the String[] args from the command.
     * @param player The player who sent the command and args.
     * @param args   Flag args. Current flags: donate, default.
     * @param event  The event to process flags for.
     */
    private void processEventFlags(int index, Player player, String[] args, Event event) {
        for (; index < args.length; index++) {
            String flag = args[index];
            switch (flag.toLowerCase()) {
                case "donate": {
                    if (event instanceof SingleWinnerEvent) {
                        DonationComponent comp = (DonationComponent) event.getEventComponent(DonationComponent.class);
                        if (comp == null) {
                            sendFlagDisabled(player, flag);
                            break;
                        }
                        comp.active = true;
                        break;
                    } else sendFlagDisabled(player, flag);
                    break;
                }
                case "inventory": {
                    InventoryComponent comp = (InventoryComponent) event.getEventComponent(InventoryComponent.class);
                    if (comp == null) {
                        sendFlagDisabled(player, flag);
                        break;
                    }
                    comp.setContents(player.getInventory().getContents());
                    comp.setArmorContents(player.getInventory().getArmorContents());
                    break;
                }
                default: {
                    sendFlagDisabled(player, flag);
                    break;
                }
            }
        }
    }

    private void sendHelp(Player player) {
        MessageManager.sendMessage(player, new String[]{
                "&c/event start <eventName> [-flags]",
                "&c/event start race <raceName> [-flags]",
                "&cFlags: donate"
        });
    }

    private void sendRacePlayerHelp(Player player) {
        MessageManager.sendMessage(player, new String[]{
                "&b/event race join",
                "&b/event race leave"
        });
    }

    private void sendLastAliveHelp(Player player) {
        MessageManager.sendMessage(player, new String[]{
                "&b/event lastAlive join",
                "&b/event lastAlive leave"
        });
    }

    private void sendLastAliveAdminHelp(Player player) {
        MessageManager.sendMessage(player, new String[]
                {
                        "&b/event lastalive admin create <name>",
                        "&b/event lastalive admin list",
                        "&b/event lastalive admin name <newName>",
                        "&b/event lastalive admin lobbyLocation",
                        "&b/event lastalive admin spawnLocation <opt:clear>"
                });
    }

    private void sendRaceAdminHelp(Player player) {
        MessageManager.sendMessage(player, new String[]{
                "&b/event race admin create <name>",
                "&b/event race admin list <opt:raceName>",
                "&b/event race admin <name> name <newName>",
                "&b/event race admin <name> startingMessage <newStartingMessage>",
                "&b/event race admin <name> startingLocation",
                "&b/event race admin <name> winningLocation",
                "&b/event race admin <name> gateLocation"
        });
    }

    private void sendFlagDisabled(Player player, String flagName) {
        MessageManager.sendMessage(player, "&cThe flag " + flagName + " cannot be enabled for this event!");
    }

    private void joinEvent(Player player, Event event) {
        if (event.isActive()) {
            Profile profile = Carbyne.getInstance().getProfileManager().getProfile(player.getUniqueId());
            if (profile.getActiveEvent() != null) {
                MessageManager.sendMessage(player, "&cYou are already in an event!");
                return;
            }

            if (event instanceof LastAlive) {
                if (((LastAlive) event).isAfterCountdown()) {
                    MessageManager.sendMessage(player, "&cThis event has already started!");
                    return;
                }
            }

            if (!PlayerUtility.isInventoryEmpty(player.getInventory())) {
                MessageManager.sendMessage(player, "&cYou need an empty inventory to join!");
                return;
            }

            ItemStack[] armor = player.getInventory().getArmorContents();
            for (int i = 0; i < armor.length; i++)
                if (armor[i].getType() != Material.AIR) {
                    MessageManager.sendMessage(player, "&cYou need an empty inventory to join!");
                    return;
                }

            BukkitRunnable telTask = new BukkitRunnable() {
                @Override
                public void run() {
                    event.getWaitingTasks().remove(player);
                    if (event instanceof Race)
                        player.teleport(((Race) event).getCurrentRace().getStartingLocation());
                    else if (event instanceof LastAlive) {
                        if (((LastAlive) event).getCurrentLastAliveObject().getSpawnLocations().size() == event.participants.size()) {
                            MessageManager.sendMessage(player, "&cThe event is full! Sorry!");
                            return;
                        }
                        player.teleport(((LastAlive) event).getCurrentLastAliveObject().getLobby());
                    }
                    Profile profile = getProfileManager().getProfile(player.getUniqueId());

                    player.setGameMode(GameMode.SURVIVAL);
                    player.getInventory().clear();
                    for (PotionEffect e : player.getActivePotionEffects()) player.removePotionEffect(e.getType());
                    player.setFlying(false);
                    player.setAllowFlight(false);
                    player.setFoodLevel(20);
                    event.addPlayerToEvent(player);
                }
            };
            MessageManager.sendMessage(player, "&cYou will be teleported to the event in 10 seconds!");
            event.getWaitingTasks().put(player, telTask);
            telTask.runTaskLater(Carbyne.getInstance(), 200L);
        }
    }

    private void leaveEvent(Player player, Event event) {
        if (event.getParticipants().contains(player)) {
            player.getInventory().clear();
            race.removePlayerFromEvent(player);
            player.teleport(race.getSpawn());
        }
    }

}
