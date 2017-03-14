package com.medievallords.carbyne.economy.sale;

import lombok.Getter;

@Getter
public class CompletedSale {

    private int amount;
    private double price;

    public void add(double price) {
        this.amount++;
        this.price += price;
    }
}