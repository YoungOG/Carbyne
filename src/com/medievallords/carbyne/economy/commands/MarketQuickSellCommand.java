package com.medievallords.carbyne.economy.commands;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.economy.MarketManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import org.bukkit.command.CommandSender;

public class MarketQuickSellCommand extends BaseCommand {

    private Carbyne main = Carbyne.getInstance();
    private MarketManager marketManager = main.getMarketManager();

    @Command(name = "market.quicksell", aliases = {"qsell"}, inGameOnly = true)
    public void execute(CommandSender sender, String[] args) {
    }
}
