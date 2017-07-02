package com.medievallords.carbyne.economy.commands.administrator;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.economy.MarketManager;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.PlayerUtility;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MarketHaltCommand extends BaseCommand {

    private Carbyne main = Carbyne.getInstance();
    private MarketManager marketManager = main.getMarketManager();

    @Command(name = "market.halt", permission = "carbyne.market.halt")
    public void execute(CommandSender sender, String[] args) {
        if (marketManager.isEconomyHalted()) {
            marketManager.setEconomyHalted(false);

            for (Player all : PlayerUtility.getOnlinePlayers()) {
                if (all.hasPermission("kmain.halteconomy")) {
                    MessageManager.sendMessage(all, "&7The economy market has been resumed by &c" + sender.getName() + "&7.");
                }
            }
        } else {
            marketManager.setEconomyHalted(true);

            for (Player all : PlayerUtility.getOnlinePlayers()) {
                if (all.hasPermission("kmain.halteconomy")) {
                    MessageManager.sendMessage(all, "&7The economy market has been halted by &c" + sender.getName() + "&7.");
                }
            }
        }
    }
}
