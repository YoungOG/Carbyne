package com.medievallords.carbyne.commands;

import com.medievallords.carbyne.utils.LagTask;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.command.CommandSender;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class LagCommand extends BaseCommand {

    @Command(name = "lag")
    public void onCommand(CommandArgs commandArgs) {
        CommandSender sender = commandArgs.getSender();
        MessageManager.sendMessage(sender, "&cServer TPS: " + new DecimalFormat("##.09").format(BigDecimal.valueOf(LagTask.getTPS())));
        MessageManager.sendMessage(sender, "&cServer Lag: " + Math.round((1.0D - LagTask.getTPS() / 20.0D) * 100.0D) + "%");
    }
}
