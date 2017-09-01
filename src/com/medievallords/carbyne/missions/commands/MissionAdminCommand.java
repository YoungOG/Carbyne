package com.medievallords.carbyne.missions.commands;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.missions.MissionsManager;
import com.medievallords.carbyne.missions.object.PlayerMissionData;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Created by Dalton on 8/13/2017.
 */
public class MissionAdminCommand extends BaseCommand {

    private MissionsManager missionsManager = Carbyne.getInstance().getMissionsManager();

    @Command(name = "mission.admin", permission = "carbyne.mission.admin", inGameOnly = true)
    public void onCommand(CommandArgs cmds) {
        Player sender = cmds.getPlayer();
        String[] args = cmds.getArgs();

        if (args.length >= 1) {
            if (args[0].equalsIgnoreCase("complete")) {
                if (args.length >= 3) {
                    Player player;
                    if ((player = Bukkit.getPlayer(args[1])) == null) {
                        MessageManager.sendMessage(player, "&cPlayer not found!");
                    } else {
                        UUID uuid = player.getUniqueId();
                        PlayerMissionData playerMissionData = missionsManager.getUuidMissions().get(player.getUniqueId());
                        switch (args[2].toLowerCase()) {
                            case "1": {
                                playerMissionData.getCurrentMissions()[0].adminPassMission(uuid);
                                return;
                            }
                            case "2": {
                                playerMissionData.getCurrentMissions()[1].adminPassMission(uuid);
                                return;
                            }
                            case "3": {
                                playerMissionData.getCurrentMissions()[2].adminPassMission(uuid);
                                return;
                            }
                            case "daily": {
                                playerMissionData.getDailyChallenge().adminPassMission(uuid);
                                return;
                            }
                            default: {
                                sendHelp(sender);
                                return;
                            }
                        }
                    }
                } else sendHelp(sender);
            } else if (args[0].equalsIgnoreCase("reset")) {
                if (args.length >= 3) {
                    Player player;
                    if ((player = Bukkit.getPlayer(args[1])) == null) {
                        MessageManager.sendMessage(player, "&cPlayer not found!");
                    } else {
                        PlayerMissionData playerMissionData = missionsManager.getUuidMissions().get(player.getUniqueId());
                        switch (args[2].toLowerCase()) {
                            case "1": {
                                playerMissionData.getCurrentMissions()[0] = missionsManager.chooseRandomMission();
                                return;
                            }
                            case "2": {
                                playerMissionData.getCurrentMissions()[1] = missionsManager.chooseRandomMission();
                                return;
                            }
                            case "3": {
                                playerMissionData.getCurrentMissions()[2] = missionsManager.chooseRandomMission();
                                return;
                            }
                            case "daily": {
                                playerMissionData.setRandomDailyChallenge();
                                return;
                            }
                            case "all": {
                                missionsManager.assignRandomMissions(player.getUniqueId());
                                return;
                            }
                            default: {
                                sendHelp(sender);
                                return;
                            }
                        }
                    }
                }
            } else sendHelp(sender);
        } else sendHelp(sender);
    }

    private void sendHelp(Player player) {
        MessageManager.sendMessage(player, new String[]
                {
                        "&c/mission admin complete <player> [1/2/3/daily]",
                        "&c/mission admin reset <player> [1/2/3/daily/all]"
                });
    }

}
