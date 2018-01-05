package com.medievallords.carbyne.events.implementations.commands;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.events.implementations.Race;
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

/**
 * Created by Dalton on 7/10/2017.
 */
public class RaceCommands extends BaseCommand {

    private Race race;

    public RaceCommands(Race race) {
        this.race = race;
    }

    @Command(name = "race")
    public void onCommand(CommandArgs cmd) {
        Player player = cmd.getPlayer();
        String[] args = cmd.getArgs();

        if (args.length >= 1) {
            if (args[0].equalsIgnoreCase("join") && race.isActive()) {
                Profile profile = Carbyne.getInstance().getProfileManager().getProfile(player.getUniqueId());
                if (profile.getActiveEvent() != null) {
                    MessageManager.sendMessage(player, "&cYou are already in an event!");
                    return;
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
                        race.getWaitingTasks().remove(player);
                        player.getInventory().clear();
                        for (PotionEffect e : player.getActivePotionEffects()) player.removePotionEffect(e.getType());
                        player.teleport(race.getCurrentRace().getStartingLocation());
                        player.setGameMode(GameMode.SURVIVAL);
                        player.setFlying(false);
                        player.setAllowFlight(false);
                        player.setFoodLevel(20);
                        race.addPlayerToEvent(player);
                    }
                };
                MessageManager.sendMessage(player, "&cYou will be teleported to the event in 10 seconds!");
                race.getWaitingTasks().put(player, telTask);
                telTask.runTaskLater(Carbyne.getInstance(), 200L);
            } else if (args[0].equalsIgnoreCase("leave") && race.isActive()) {
                if (race.getParticipants().contains(player)) {
                    player.getInventory().clear();
                    race.removePlayerFromEvent(player);
                    player.teleport(race.getSpawn());
                }
            } else if (args[0].equalsIgnoreCase("admin")) {
                if (player.hasPermission("carbyne.event.admin")) {
                    if (args.length >= 2) {
                        switch (args[1].toLowerCase()) {
                            case "create": {
                                if (args.length >= 3) {
                                    if (RaceObject.getRaceObject(args[2]) == null) {
                                        RaceObject.raceObjects.add(new RaceObject(false, args[2], null, null, null, null));
                                        MessageManager.sendMessage(player, "&bA race has been created with the name " + args[2] + "&b but still has empty properties!");
                                        return;
                                    } else {
                                        MessageManager.sendMessage(player, "&cA race with this name already exists!");
                                        return;
                                    }
                                }
                                break;
                            }
                            case "list": {
                                for (RaceObject raceObject : RaceObject.raceObjects) {
                                    MessageManager.sendMessage(player, raceObject.toString());
                                }
                                break;
                            }
                            default: {
                                String raceName = args[1];
                                RaceObject raceData = RaceObject.getRaceObject(args[1]);
                                if (raceData == null) {
                                    sendRaceAdminHelp(player);
                                    return;
                                } else {
                                    if (args.length >= 3) {
                                        switch (args[2].toLowerCase()) {
                                            case "name": {
                                                String newName = args[3];
                                                RaceObject race = null;
                                                if ((race = RaceObject.getRaceObject(newName)) == null) {
                                                    race.setName(newName);
                                                    MessageManager.sendMessage(player, "&bRace name change successful!");
                                                } else
                                                    MessageManager.sendMessage(player, "&cA race with the name " + newName + "&c is not found!");
                                                break;
                                            }
                                            case "startingmessage": {
                                                StringBuilder sb = new StringBuilder();
                                                for (int i = 3; i < args.length; i++) sb.append(args[i] + " ");
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
                                    } else sendRaceAdminHelp(player);
                                }
                            }
                            break;
                        }
                    } else sendRaceAdminHelp(player);
                } else {
                    MessageManager.sendMessage(player, "&cYou do not have permission to use this command!");
                    return;
                }
            }
        }
    }

    private void sendRaceAdminHelp(Player player) {
        MessageManager.sendMessage(player, new String[]{
                "&b/race admin create <name>",
                "&b/race admin list <opt:raceName>",
                "&b/race admin <name> name <newName>",
                "&b/race admin <name> startingMessage <newStartingMessage>",
                "&b/race admin <name> startingLocation",
                "&b/race admin <name> winningLocation",
                "&b/race admin <name> gateLocation"
        });
    }

}

