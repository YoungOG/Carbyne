package com.medievallords.carbyne.economy.sale;

import lombok.Getter;
import org.bson.Document;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

@Getter
public class Sale {

    private UUID transactionID, seller;
    private ItemStack itemStack;
    private int amount;
    private String dateCreated;
    private double price;

    public Sale(UUID transactionID, UUID seller, ItemStack itemStack, int amount, String dateCreated, double price) {
        this.transactionID = transactionID;
        this.seller = seller;
        this.itemStack = itemStack;
        this.amount = amount;
        this.dateCreated = dateCreated;
        this.price = price;
    }

    public Document toDocument() {
        Document document = new Document("transactionID", transactionID.toString());

        document.append("seller", seller.toString());
        document.append("itemStack", new Document("material", itemStack.getType().toString()).append("durability", itemStack.getDurability()));
        document.append("amount", amount);
        document.append("dateCreated", dateCreated);
        document.append("price", price);

        return document;
    }

    @Override
    public String toString() {
        return "Sale{" +
                "transId=" + transactionID +
                ", seller=" + seller +
                ", stack=" + itemStack.toString() +
                ", amount=" + amount +
                ", price=" + price +
                ", date=" + dateCreated +
                '}';
    }
}