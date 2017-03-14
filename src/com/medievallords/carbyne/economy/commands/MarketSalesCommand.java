package com.medievallords.carbyne.economy.commands;

import com.medievallords.carbyne.economy.sale.Sale;
import com.medievallords.carbyne.utils.JSONMessage;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

public class MarketSalesCommand extends BaseCommand {

    @Command(name = "sales", inGameOnly = true)
    public void execute(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        Player player = commandArgs.getPlayer();

        if (args.length == 0) {
            if (getSaleStacks(player).size() <= 0) {
                MessageManager.sendMessage(player, "&cYou do not have any sales listed.");
                return;
            }

            MessageManager.sendMessage(player, "&eNOTE: Click on any sale to cancel the sale.");
            MessageManager.sendMessage(player, "&7Sales:");

            for (SaleStack saleStack : getSaleStacks(player)) {
                JSONMessage.create(ChatColor.translateAlternateColorCodes('&', "&bx" + saleStack.getAmount() + " " + saleStack.getType().name() + ":" + saleStack.getData() + " &7- $" + (saleStack.getPerUnitPrice() * saleStack.getAmount())))
                        .tooltip(ChatColor.translateAlternateColorCodes('&', "&aTransactionId: &b" + saleStack.getTransactionId() + "\n"
                                + "&aSeller: &b" + Bukkit.getOfflinePlayer(saleStack.getSeller()).getName() + "\n"
                                + "&aDate Listed: &b" + saleStack.getDateCreated() + "\n"
                                + "&aMaterial: &b" + saleStack.getType().name() + ":" + saleStack.getData() + "\n"
                                + "&aAmount: &b" + saleStack.getAmount() + "\n"
                                + "&aTotal Price: &b$" + (saleStack.getPerUnitPrice() * saleStack.getAmount()) + " &7(Tax: &b$" + ((saleStack.getPerUnitPrice() * saleStack.getAmount()) * getMarketManager().getSalesTax()) + "&7)" + "\n"
                                + "&aPrice Per-Unit: &b$" + saleStack.getPerUnitPrice()))
                        .send(player);
            }
        } else if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                MessageManager.sendMessage(player, "&cThat player could not be found.");
                return;
            }

            if (getSaleStacks(target).size() <= 0) {
                MessageManager.sendMessage(player, "&c" + target.getName() + " does not have any sales listed.");
                return;
            }

            MessageManager.sendMessage(player, "&7" + target.getName() + "'s Sales:");

            for (SaleStack saleStack : getSaleStacks(target)) {
                JSONMessage.create(ChatColor.translateAlternateColorCodes('&', "&bx" + saleStack.getAmount() + " " + saleStack.getType().name() + ":" + saleStack.getData() + " &7- $" + (saleStack.getPerUnitPrice() * saleStack.getAmount())))
                        .tooltip(ChatColor.translateAlternateColorCodes('&', "&aTransactionId: &b" + saleStack.getTransactionId() + "\n"
                                + "&aSeller: &b" + Bukkit.getOfflinePlayer(saleStack.getSeller()).getName() + "\n"
                                + "&aDate Listed: &b" + saleStack.getDateCreated() + "\n"
                                + "&aMaterial: &b" + saleStack.getType().name() + ":" + saleStack.getData() + "\n"
                                + "&aAmount: &b" + saleStack.getAmount() + "\n"
                                + "&aTotal Price: &b$" + (saleStack.getPerUnitPrice() * saleStack.getAmount()) + " &7(Tax: &b$" + ((saleStack.getPerUnitPrice() * saleStack.getAmount()) * getMarketManager().getSalesTax()) + "&7)" + "\n"
                                + "&aPrice Per-Unit: &b$" + saleStack.getPerUnitPrice()))
                        .send(player);
            }
        }
    }

    public ArrayList<SaleStack> getSaleStacks(Player player) {
        ArrayList<Sale> sales = getMarketManager().getSpecificSales(player.getUniqueId());
        ArrayList<UUID> uuids = new ArrayList<>();
        ArrayList<SaleStack> saleStacks = new ArrayList<>();

        for (Sale sale : sales) {
            if (!uuids.contains(sale.getTransactionID())) {
                uuids.add(sale.getTransactionID());
                saleStacks.add(new SaleStack(sale));
            }
        }

        return saleStacks;
    }

    @Getter
    @Setter
    public class SaleStack {

        private UUID transactionId;
        private UUID seller;
        private Material type;
        private int data;
        private int amount;
        private String dateCreated;
        private double perUnitPrice;

        public SaleStack(Sale sale) {
            this.transactionId = sale.getTransactionID();
            this.seller = sale.getSeller();
            this.type = sale.getItemStack().getType();
            this.data = sale.getItemStack().getDurability();
            this.amount = sale.getAmount();
            this.dateCreated = sale.getDateCreated();
            this.perUnitPrice = sale.getPrice();
        }
    }
}
