package com.medievallords.carbyne.donator.commands;

import com.medievallords.carbyne.utils.StaticClasses;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;

/**
 * Created by Dalton on 6/27/2017.
 */
public class TrailCommand extends BaseCommand
{

    @Command(name="effects", aliases = { "trail" }, inGameOnly = true)
    public void onCommand(CommandArgs cmdArgs)
    {
        StaticClasses.trailManager.showAllEffectsGui(cmdArgs.getPlayer());
    }

}
