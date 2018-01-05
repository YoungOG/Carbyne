package com.medievallords.carbyne.events.component;

import com.medievallords.carbyne.economy.objects.Account;
import com.medievallords.carbyne.events.Event;
import com.medievallords.carbyne.events.EventComponent;
import com.medievallords.carbyne.events.SingleWinnerEvent;
import com.medievallords.carbyne.utils.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class DonationComponent implements EventComponent {

    private static final double MINIMUM_DONATION_AMOUNT = 50;

    public boolean active = false;

    private Event event;
    private Map<Account, Double> donations;
    private double donationSummation;

    /**
     * PRECONDITION: Event event is an interface of SingleWinnerEvent!
     *
     * @param event The event to pass for donations.
     */
    public DonationComponent(Event event) {
        this.event = event;
        donations = new HashMap<>();
        donationSummation = 0;
    }

    @Override
    public void start() {
        if (active) {
            MessageManager.broadcastMessage("&2Donations are enabled for this event! /event donate " + event.getEventName() + "&2 <amount>");
        }
    }

    @Override
    public void tick() { /* Do nothing */ }

    @Override
    public void stop() {
        if (active) {
            decideToDistributeDonations();
            active = false;
        }
    }

    /**
     * Simply adds a donation to an event and stores the individual donation a map in case of refunding and stores the total as a double.
     * MINIMUM_DONATION_AMOUNT is used here as a check to prevent small donations and donations under or equal to zero.
     * It is intended that only one donation message is broadcast per player.
     *
     * @param player Player who is trying to make a donation.
     * @param amount Amount trying to be donated.
     */
    public void donate(Player player, Double amount) {
        Account account = Account.getAccount(player.getUniqueId());
        if (account.getBalance() >= amount) {
            if (amount >= MINIMUM_DONATION_AMOUNT) {
                if (donations.containsKey(account))
                    donations.put(account, donations.get(account) + amount);
                else {
                    donations.put(account, amount);
                    MessageManager.broadcastMessage(player.getDisplayName() + "&2 has donated " + amount + " for the " + event.getEventName() + "&2!");
                }
                donationSummation += amount;
                account.setBalance(account.getBalance() - amount);
                MessageManager.sendMessage(player, "&2You have donated " + amount + "!");
            } else
                MessageManager.sendMessage(player, "&2The minimum donation amount is " + MINIMUM_DONATION_AMOUNT + "!");
        } else
            MessageManager.sendMessage(player, "&cYou do not have that much credits!");
    }

    /**
     * Simlpe method to refund all donations. Does not clear donation map and summation.
     */
    private void refundAllDonations() {
        for (Account a : donations.keySet()) {
            double donation = donations.get(a);
            a.setBalance(donations.get(a) + a.getBalance());
            Player donator;
            if ((donator = Bukkit.getPlayer(a.getAccountHolderId())) != null)
                MessageManager.sendMessage(donator, "&2Your donation of " + donations + " has been refunded!");
        }
    }

    /**
     * Gives donations to the winner. Uses donation summation as the reward balance.
     *
     * @param winner The winner of the event.
     */
    private void distributeDonationsToWinner(Player winner) {
        Account awin = Account.getAccount(winner.getUniqueId());
        awin.setBalance(awin.getBalance() + donationSummation);
        MessageManager.sendMessage(winner, "&2You have been awarded " + donationSummation + " for being the winner!");
    }

    /**
     * Run this method to decide to distribute the donations. It only distributes to a winner. If there is no winner, it refunds all donations.
     */
    public void decideToDistributeDonations() {
        Player winner = ((SingleWinnerEvent) event).getWinner();
        if (winner == null)
            refundAllDonations();
        else
            distributeDonationsToWinner(winner);
        donations.clear();
        donationSummation = 0;
    }


}
