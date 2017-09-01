package com.medievallords.carbyne.economy.commands.administrator;

import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.command.CommandSender;

public class MarketSetTaxCommand extends BaseCommand {

    @Command(name = "settax", permission = "carbyne.market.tax")
    public void execute(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        CommandSender sender = commandArgs.getSender();

        if (args.length != 1) {
            MessageManager.sendMessage(sender, "&c/settaxx <amount>");
            return;
        }

        double amount;

        try {
            amount = Double.parseDouble(args[0]);

            if (amount < 0.0) {
                MessageManager.sendMessage(sender, "&7You must enter a positive amount.");
                return;
            } else if (amount == 0.0) {
                MessageManager.sendMessage(sender, "&7You must enter an amount greater than zero.");
                return;
            }
        } catch (Exception e) {
            MessageManager.sendMessage(sender, "&7You must enter a valid amount.");
            return;
        }

        getMarketManager().setSalesTax(amount);
        //SlackAPI.getInstance().sendMessage(SlackTeam.MAIN, "#economy-log",
        //new SlackMessage("MedievalRobot", ":robot_face:",
        //"" + sender.getName() + " has set the sale tax to " + MessageManager.format(amount) + "!"), false);
        MessageManager.sendMessage(sender, "&7You have set the Sales Tax to &c" + amount + "%&7.");
    }
}
