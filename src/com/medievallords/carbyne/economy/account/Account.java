package com.medievallords.carbyne.economy.account;

import com.medievallords.carbyne.Carbyne;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Created by Calvin on 4/11/2017
 * for the Carbyne project.
 */
@Getter
@Setter
public class Account {

    private static MongoCollection<Document> accountsCollection = Carbyne.getInstance().getMongoDatabase().getCollection("accounts");
    private static HashSet<Account> accounts = new HashSet<>();
    private static HashSet<Account> duplicateAccounts = new HashSet<>();

    private UUID accountHolderId;
    private String accountHolder;
    private double balance;

    public Account(UUID accountHolderId, String accountHolder, double balance) {
        this.accountHolderId = accountHolderId;
        this.accountHolder = accountHolder;
        this.balance = balance;
    }

    @SuppressWarnings("unchecked")
    public static void loadAccounts() {
        if (accountsCollection.count() > 0) {
            Carbyne.getInstance().getLogger().log(Level.INFO, "Preparing to load " + accountsCollection.count() + " accounts.");

            long startTime = System.currentTimeMillis();

            for (Document document : accountsCollection.find()) {
                UUID accountHolderId = null;

                if (document.getString("accountHolderId") != null) {
                    accountHolderId = UUID.fromString(document.getString("accountHolderId"));
                }

                String accountHolder = document.getString("accountHolder");
                double balance = document.getDouble("balance");

                Account account = new Account(accountHolderId, accountHolder, balance);

                if (!accounts.contains(account)) {
                    accounts.add(account);
                }
            }

            for (Account account : accounts) {
                if (account.getAccountHolderId() != null)
                    for (Account account1 : accounts) {
                        if (account1.getAccountHolderId() != null)
                            if (account.getAccountHolderId() == account1.getAccountHolderId()) {
                                Carbyne.getInstance().getLogger().log(Level.WARNING, "Duplicate Account found: " + account.getAccountHolderId() + " -> " + account1.getAccountHolderId() + ", " + account.getAccountHolder() + " -> " + account1.getAccountHolder() + ", " + account.getBalance() + " -> " + account1.getBalance());
                                //                        account.setBalance(account.getBalance() + account1.getBalance());
                                //                        duplicateAccounts.add(account1);
                            }
                    }
            }

            for (Account account : duplicateAccounts) {
                Document document = new Document();
                document.append("accountHolder", account.getAccountHolder());

                if (account.getAccountHolderId() != null) {
                    document.append("accountHolderId", account.getAccountHolderId().toString());
                }

                document.append("balance", account.getBalance());
                accountsCollection.deleteOne(document);

                accounts.remove(account);
            }

            duplicateAccounts.clear();

            Carbyne.getInstance().getLogger().log(Level.INFO, "Successfully loaded " + accounts.size() + " accounts. Took (" + (System.currentTimeMillis() - startTime) + "ms).");
        }
    }

    public static void saveAccounts(boolean async) {
        if (accounts.size() > 0) {
            Carbyne.getInstance().getLogger().log(Level.INFO, "Preparing to save " + accounts.size() + " accounts.");

            long startTime = System.currentTimeMillis();

            if (async) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        for (Account account : accounts) {
                            Document document = new Document();
                            document.append("accountHolder", account.getAccountHolder());

                            if (account.getAccountHolderId() != null) {
                                document.append("accountHolderId", account.getAccountHolderId().toString());
                            }

                            document.append("balance", account.getBalance());

                            accountsCollection.replaceOne(Filters.eq("accountHolder", account.getAccountHolder()), document, new UpdateOptions().upsert(true));
                        }

                        Carbyne.getInstance().getLogger().log(Level.INFO, "Successfully saved " + accounts.size() + " accounts. Took (" + (System.currentTimeMillis() - startTime) + "ms).");
                    }
                }.runTaskAsynchronously(Carbyne.getInstance());
            } else {
                for (Account account : accounts) {
                    Document document = new Document();
                    document.append("accountHolder", account.getAccountHolder());

                    if (account.getAccountHolderId() != null) {
                        document.append("accountHolderId", account.getAccountHolderId().toString());
                    }

                    document.append("balance", account.getBalance());

                    accountsCollection.replaceOne(Filters.eq("accountHolder", account.getAccountHolder()), document, new UpdateOptions().upsert(true));
                }

                Carbyne.getInstance().getLogger().log(Level.INFO, "Successfully saved " + accounts.size() + " accounts. Took (" + (System.currentTimeMillis() - startTime) + "ms).");
            }
        }
    }

    public static void createAccount(String accountHolder) {
        Account account = new Account(null, accountHolder, Carbyne.getInstance().getConfig().getInt("economy.starting-balance"));

        Document document = new Document();
        document.append("accountHolder", account.getAccountHolder());
        document.append("balance", account.getBalance());

        new BukkitRunnable() {
            @Override
            public void run() {
                accountsCollection.insertOne(document);
            }
        }.runTaskAsynchronously(Carbyne.getInstance());

        if (!accounts.contains(account)) {
            accounts.add(account);
        }

        //Carbyne.getInstance().getLogger().log(Level.INFO, "Account created for " + accountHolder);
    }

    public static void createAccount(UUID accountHolderId, String accountHolder) {
        Account account = new Account(accountHolderId, accountHolder, Carbyne.getInstance().getConfig().getInt("economy.starting-balance"));

        Document document = new Document();
        document.append("accountHolder", account.getAccountHolder());

        if (account.getAccountHolderId() != null) {
            document.append("accountHolderId", account.getAccountHolderId().toString());
        }

        document.append("balance", account.getBalance());

        new BukkitRunnable() {
            @Override
            public void run() {
                accountsCollection.insertOne(document);
            }
        }.runTaskAsynchronously(Carbyne.getInstance());

        if (!accounts.contains(account)) {
            accounts.add(account);
        }
    }

    public static Account getAccount(String accountHolder) {
        for (Account account : accounts) {
            if (account.getAccountHolder().equalsIgnoreCase(accountHolder)) {
                return account;
            }
        }

        return null;
    }

    public static Account getAccount(UUID accountHolderId) {
        for (Account account : accounts) {
            if (account.getAccountHolderId() != null && account.getAccountHolderId().equals(accountHolderId)) {
                return account;
            }
        }

        return null;
    }

    public static boolean hasAccount(String accountHolder) {
        for (Account account : accounts) {
            if (account.getAccountHolder().equalsIgnoreCase(accountHolder)) {
                return true;
            }
        }

        return false;
    }

    public static boolean hasAccount(UUID accountHolderId) {
        for (Account account : accounts) {
            if (account.getAccountHolderId() != null && account.getAccountHolderId().equals(accountHolderId)) {
                return true;
            }
        }

        return false;
    }

    public static HashSet<Account> getAccounts() {
        return accounts;
    }
}
