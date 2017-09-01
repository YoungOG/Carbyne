package com.medievallords.carbyne.missions.commands;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.missions.MissionsManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.entity.Player;

/**
 * Created by Dalton on 8/9/2017.
 */
public class MissionCommand extends BaseCommand {

    private MissionsManager missionsManager = Carbyne.getInstance().getMissionsManager();

    @Command(name = "mission", inGameOnly = true)
    public void onCommand(CommandArgs cmdargs) {
        Player player = cmdargs.getPlayer();
        if (missionsManager.getUuidMissions().containsKey(player.getUniqueId()))
            missionsManager.showPlayerMissionInventory(player);
    }

}
