package com.medievallords.carbyne.economy;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.economy.account.Account;
import com.medievallords.carbyne.economy.sale.CompletedSale;
import com.medievallords.carbyne.economy.sale.Sale;
import com.medievallords.carbyne.utils.DateUtil;
import com.medievallords.carbyne.utils.MessageManager;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class MarketManager {

    private Carbyne main = Carbyne.getInstance();

    private MongoCollection<Document> salesCollection = main.getMongoDatabase().getCollection("sales");

    private HashMap<UUID, HashSet<Sale>> playerSales = new HashMap<>();

    private boolean economyHalted = false;
    private double salesTax = 0.0;
    private int goldWorth = 0;

    public MarketManager() {
//        salesCollection.createIndex(new Document("transactionId", 1));
//        salesCollection.createIndex(new Document("uniqueId", 1));

        salesTax = main.getConfig().getDouble("economy.sales-tax");
        goldWorth = main.getConfig().getInt("economy.gold-worth");

        Account.loadAccounts();
//        loadSales();

        new BukkitRunnable() {
            @Override
            public void run() {
                Account.saveAccounts(true);
//                saveSales(true);
            }
        }.runTaskTimer(main, 0L, 300 * 20L);
    }

    @SuppressWarnings("unchecked")
    public void loadSales() {
        if (salesCollection.count() > 0) {
            main.getLogger().log(Level.INFO, "Preparing to load " + salesCollection.count() + " sales.");

            long startTime = System.currentTimeMillis();
            int salesCount = 0;

            for (Document document : salesCollection.find()) {
                UUID id = UUID.fromString(document.getString("uniqueId"));
                ArrayList<Document> saleDocs = (ArrayList<Document>) document.get("sales");

                if (!playerSales.containsKey(id)) {
                    if (saleDocs.size() > 0) {
                        HashSet<Sale> sales = new HashSet<>();

                        for (Document saleDoc : saleDocs) {
                            UUID transactionID = UUID.fromString(saleDoc.getString("transactionID"));
                            UUID seller = UUID.fromString(saleDoc.getString("seller"));

                            Document itemData = (Document) saleDoc.get("itemStack");
                            Material material = Material.matchMaterial(itemData.getString("material"));
                            int durability = itemData.getInteger("durability");

                            ItemStack newItem = new ItemStack(material, 1, (short) durability);

                            int amount = saleDoc.getInteger("amount");
                            String dateCreated = saleDoc.getString("dateCreated");
                            double price = saleDoc.getDouble("price");

                            sales.add(new Sale(transactionID, seller, newItem, amount, dateCreated, price));

                            salesCount++;
                        }

                        if (sales.size() > 0) {
                            playerSales.put(id, sales);
                        }
                    }
                }
            }

            main.getLogger().log(Level.INFO, "Successfully loaded " + playerSales.keySet().size() + " ID's and (" + salesCount + ") sales. Took (" + (System.currentTimeMillis() - startTime) + "ms).");
        }
    }

    public void saveSales(boolean async) {
        if (playerSales.keySet().size() > 0) {
            main.getLogger().log(Level.INFO, "Preparing to save " + playerSales.keySet().size() + " ID's with (" + playerSales.values().size() + ") sales.");

            long startTime = System.currentTimeMillis();
            final int[] salesCount = {0};

            if (async) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        for (UUID id : playerSales.keySet()) {
                            HashSet<Document> saleDocs = playerSales.get(id).stream().map(Sale::toDocument).collect(Collectors.toCollection(HashSet::new));
                            Document document = new Document("uniqueId", id.toString()).append("sales", saleDocs);

                            salesCollection.replaceOne(Filters.eq("uniqueId", id.toString()), document, new UpdateOptions().upsert(true));

                            salesCount[0] += playerSales.get(id).size();
                        }

                        main.getLogger().log(Level.INFO, "Successfully saved " + playerSales.keySet().size() + " ID's and (" + salesCount[0] + ") sales. Took (" + (System.currentTimeMillis() - startTime) + "ms).");
                    }
                }.runTaskAsynchronously(main);
            } else {
                for (UUID id : playerSales.keySet()) {
                    HashSet<Document> saleDocs = playerSales.get(id).stream().map(Sale::toDocument).collect(Collectors.toCollection(HashSet::new));
                    Document document = new Document("uniqueId", id.toString()).append("sales", saleDocs);

                    salesCollection.replaceOne(Filters.eq("uniqueId", id.toString()), document, new UpdateOptions().upsert(true));

                    salesCount[0] += playerSales.get(id).size();
                }

                main.getLogger().log(Level.INFO, "Successfully saved " + playerSales.keySet().size() + " ID's and (" + salesCount[0] + ") sales. Took (" + (System.currentTimeMillis() - startTime) + "ms).");
            }
        }
    }

    public void sell(Player seller, ItemStack itemStack, double totalPrice) {
        if (itemStack.getAmount() > 64) {
            MessageManager.sendMessage(seller, "&7You can only sell 64 items at a time.");
            return;
        }

        if (totalPrice < 0.01) {
            MessageManager.sendMessage(seller, "&7You can only sell at a minimum of 0.01 credits.");
            return;
        }

        if (!isFullyRepaired(itemStack)) {
            MessageManager.sendMessage(seller, "&7You cannot sell armor/weapons that have not been fully repaired.");
            return;
        }

        if (!seller.getInventory().containsAtLeast(itemStack, itemStack.getAmount())) {
            MessageManager.sendMessage(seller, "&7You do not have &c" + itemStack.getAmount() + " &7of &c" + getItemName(itemStack) + " &7in your inventory.");
            MessageManager.sendMessage(seller, "&cNote&7: You cannot sell items that are not fully repaired, have enchantments, or with custom names.");
            return;
        }

        double perUnitPrice = totalPrice / itemStack.getAmount();

        HashSet<Sale> sales = new HashSet<>();
        UUID transactionId = UUID.randomUUID();

        for (int i = 0; i < itemStack.getAmount(); i++) {
            sales.add(new Sale(transactionId, seller.getUniqueId(), new ItemStack(itemStack.getType(), 1, itemStack.getDurability()), itemStack.getAmount(), DateUtil.getProperDate(new Date()), perUnitPrice));
        }

        if (sales.size() > 0) {
            if (playerSales.containsKey(seller.getUniqueId())) {
                playerSales.get(seller.getUniqueId()).addAll(sales);
            } else {
                playerSales.put(seller.getUniqueId(), sales);
            }

            seller.getInventory().removeItem(itemStack);

            MessageManager.sendMessage(seller, "&7You have put &c" + itemStack.getAmount() + " &7of &c" + getItemName(itemStack) + " &7on the market for &c\u00A9" + totalPrice + " &7(Tax: &c\u00A9" + totalPrice * salesTax + "&7).");
        } else {
            MessageManager.sendMessage(seller, "&cTransaction failed. Your items have been returned.");
        }
    }

    public void buy(Player buyer, ItemStack itemStack, double priceLimit) {
        if (itemStack.getAmount() > 64) {
            MessageManager.sendMessage(buyer, "&7You can only buy 64 items at a time.");
            return;
        }

        if (priceLimit < 0.01) {
            MessageManager.sendMessage(buyer, "&7The minimum price limit is 0.01 credits.");
        }

        ArrayList<Sale> sales = getSpecificSales(itemStack, priceLimit);

        if (sales.size() < itemStack.getAmount()) {
            MessageManager.sendMessage(buyer, "&7There is not enough " + getItemName(itemStack) + " on the market.");
            return;
        }

        double totalPrice = sales.stream().mapToDouble(Sale::getPrice).sum();

        if (totalPrice > priceLimit) {
            MessageManager.sendMessage(buyer, "&c" + itemStack.getAmount() + " " + getItemName(itemStack) + " costs \u00A9" + totalPrice + " which is more than your limit.");
            return;
        }

        int actualAmountBought = 0;
        double actualPrice = 0;
        Map<UUID, CompletedSale> completedSales = new HashMap<>();

        for (Sale sale : sales) {
            HashMap<Integer, ItemStack> leftover = buyer.getInventory().addItem(sale.getItemStack());

            if (leftover.isEmpty()) {
                actualAmountBought++;
                actualPrice += sale.getPrice();

                CompletedSale completedSale = completedSales.getOrDefault(sale.getSeller(), new CompletedSale());
                completedSale.add(sale.getPrice());
                completedSales.put(sale.getSeller(), completedSale);

                if (playerSales.containsKey(sale.getSeller())) {
                    playerSales.get(sale.getSeller()).remove(sale);

                    if (playerSales.get(sale.getSeller()).size() <= 0) {
                        playerSales.remove(sale.getSeller());

                        long startTime = System.currentTimeMillis();

                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                salesCollection.deleteOne(Filters.eq("uniqueId", sale.getSeller().toString()));

                                main.getLogger().log(Level.INFO, "Successfully removed &c" + sale.getSeller().toString() + "&7's document. Took (&c" + (System.currentTimeMillis() - startTime) + " ms&7).");
                            }
                        }.runTaskAsynchronously(main);
                    }
                }
            } else {
                break;
            }
        }

        for (Entry<UUID, CompletedSale> data : completedSales.entrySet()) {
            UUID uuid = data.getKey();
            CompletedSale completedSale = data.getValue();

            deposit(uuid, (completedSale.getPrice() * salesTax));

            Player seller = Bukkit.getPlayer(uuid);

            if (seller != null) {
                MessageManager.sendMessage(seller, "&6A player has bought " + completedSale.getAmount() + " of " + getItemName(itemStack) + " for \u00A9" + completedSale.getPrice() + " (Tax: \u00A9" + completedSale.getPrice() * salesTax + "). This has been deposited into your account.");
            }
        }

        withdraw(buyer.getUniqueId(), actualPrice);
        MessageManager.sendMessage(buyer, "&7You bought " + actualAmountBought + " " + getItemName(itemStack) + " for \u00A9" + actualPrice + " credits.");
    }

    public void showPrice(Player player, ItemStack itemStack) {
        if (itemStack.getAmount() > 64) {
            MessageManager.sendMessage(player, "&7You can only check the price for 64 items at a time.");
            return;
        }

        ArrayList<Sale> sales = getSpecificSales(itemStack);

        if (sales.size() < itemStack.getAmount()) {
            MessageManager.sendMessage(player, "&7There is not enough " + getItemName(itemStack) + " on the market.");
            return;
        }

        double totalPrice = 0.0;

        for (Sale sale : sales) {
            totalPrice += sale.getPrice();
        }

        MessageManager.sendMessage(player, "&c" + itemStack.getAmount() + " &7of &c" + getItemName(itemStack) + " &7costs &c\u00A9" + totalPrice + " &7credits.");
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

    public String getItemName(ItemStack itemStack) {
        return main.getItemDb().name(itemStack);
    }

    public ItemStack getItemStack(String partial, int amount) throws Exception {
        return main.getItemDb().get(partial, amount);
    }

    private boolean isFullyRepaired(ItemStack itemStack) {
        String checkMaterial = itemStack.getType().name();

        return !(checkMaterial.contains("HELMET") || checkMaterial.contains("CHESTPLATE") || checkMaterial.contains("LEGGINGS") || checkMaterial.contains("BOOTS") || checkMaterial.contains("SWORD") || itemStack.getType() == Material.BOW) || itemStack.getDurability() == 0;
    }

    public ArrayList<Sale> getSpecificSales(ItemStack itemStack) {
        getSales().sort(Comparator.comparing(Sale::getPrice));

        ArrayList<Sale> results = new ArrayList<>();

        for (Sale sale : getSales()) {
            if (sale.getItemStack().getType() == itemStack.getType() && sale.getItemStack().getDurability() == itemStack.getDurability()) {
                if (results.size() < itemStack.getAmount())
                    results.add(sale);
            }
        }

        results.sort(Comparator.comparing(Sale::getPrice));

        return results;
    }

    public ArrayList<Sale> getSpecificSales(ItemStack itemStack, double price) {
        getSales().sort(Comparator.comparing(Sale::getPrice));

        ArrayList<Sale> results = new ArrayList<>();

        for (Sale sale : getSales()) {
            if (sale.getItemStack().getType() == itemStack.getType() && sale.getItemStack().getDurability() == itemStack.getDurability()) {
                if (results.size() < itemStack.getAmount()) {
                    if (sale.getPrice() <= price) {
                        results.add(sale);
                    }
                }
            }
        }

        results.sort(Comparator.comparing(Sale::getPrice));

        return results;
    }

    public ArrayList<Sale> getSpecificSales(UUID uniqueId) {
        ArrayList<Sale> sales = new ArrayList<>();

        if (playerSales.containsKey(uniqueId)) {
            for (Sale sale : playerSales.get(uniqueId)) {
                sales.add(sale);
            }
        }

        return sales;
    }

    public ArrayList<Sale> getSales() {
        ArrayList<Sale> sales = new ArrayList<>();

        for (UUID id : playerSales.keySet()) {
            sales.addAll(playerSales.get(id).stream().collect(Collectors.toList()));
        }

        return sales;
    }

    public boolean isEconomyHalted() {
        return economyHalted;
    }

    public void setEconomyHalted(boolean economyHalted) {
        this.economyHalted = economyHalted;
    }

    public double getSalesTax() {
        return salesTax;
    }

    public void setSalesTax(double salesTax) {
        this.salesTax = salesTax;
    }

    public int getGoldWorth() {
        return goldWorth;
    }

    public void setGoldWorth(int goldWorth) {
        this.goldWorth = goldWorth;
    }
}
