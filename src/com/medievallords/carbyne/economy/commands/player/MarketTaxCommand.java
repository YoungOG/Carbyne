package com.medievallords.carbyne.economy.commands.player;

import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.command.CommandSender;

public class MarketTaxCommand extends BaseCommand {

    @Command(name = "tax")
    public void execute(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        CommandSender sender = commandArgs.getSender();

        if (args.length != 0) {
            MessageManager.sendMessage(sender, "&c/tax");
            return;
        }

        MessageManager.sendMessage(sender, "&7The sales tax is currently at &c" + getMarketManager().getSalesTax() + "%&7.");
    }
}
