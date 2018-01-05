package com.medievallords.carbyne.economy;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.economy.objects.Account;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class EconomyManager {

    private Carbyne main = Carbyne.getInstance();

    private boolean economyHalted = false;
    private int goldWorth = 0;

    public EconomyManager() {
        goldWorth = main.getConfig().getInt("economy.gold-worth");

        Account.loadAccounts();

        new BukkitRunnable() {
            @Override
            public void run() {
                Account.saveAccounts(true);
            }
        }.runTaskTimer(main, 0L, 300 * 20L);
    }

    public void deposit(UUID uuid, double amount) {
        Account.getAccount(uuid).setBalance(Account.getAccount(uuid).getBalance() + amount);
    }

    public boolean withdraw(UUID uuid, double amount) {
        if (amount > Account.getAccount(uuid).getBalance())
            return false;
        else {
            Account.getAccount(uuid).setBalance(Account.getAccount(uuid).getBalance() - amount);
            return true;
        }
    }

    public boolean isEconomyHalted() {
        return economyHalted;
    }

    public int getGoldWorth() {
        return goldWorth;
    }
}
