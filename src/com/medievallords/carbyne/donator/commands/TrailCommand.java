package com.medievallords.carbyne.donator.commands;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.donator.TrailManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;

/**
 * Created by Dalton on 6/27/2017.
 */
public class TrailCommand extends BaseCommand
{

    private TrailManager trailManager = Carbyne.getInstance().getTrailManager();

    @Command(name="effects", aliases = { "trail" }, inGameOnly = true)
    public void onCommand(CommandArgs cmdArgs)
    {
        trailManager.showAllEffectsGui(cmdArgs.getPlayer());
    }

}
