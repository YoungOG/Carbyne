package com.medievallords.carbyne.prizeeggs.commands;

import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.StaticClasses;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.entity.Player;

public class PrizeEggReloadCommand extends BaseCommand {

    @Command(name = "prizeeggs.reload", inGameOnly = true, permission = "carbyne.administrator")
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();

        StaticClasses.prizeEggManager.reload();
        MessageManager.sendMessage(player, "&aThe prize egg configuration has been reloaded.");
    }
}
