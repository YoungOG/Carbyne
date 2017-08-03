package com.medievallords.carbyne.tickets;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.utils.ItemBuilder;
import com.medievallords.carbyne.utils.JSONMessage;
import com.medievallords.carbyne.utils.MessageManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * Created by WE on 2017-07-08.
 */
public class TicketListener implements Listener {

    private TicketManager ticketManager = Carbyne.getInstance().getTicketManager();

    @EventHandler
    public void onChat (AsyncPlayerChatEvent event) {
        UUID player = event.getPlayer().getUniqueId();
        if (ticketManager.getRespondingTickets().containsKey(player)) {
            event.setCancelled(true);
            String msg = event.getMessage();
            Ticket ticket = ticketManager.getRespondingTickets().get(player);
            ticket.setResponse(ticket.getResponse() + msg + " ");

            JSONMessage send = JSONMessage.create();
            send.then(ChatColor.translateAlternateColorCodes('&', "&aCLICK ME TO SUBMIT YOUR RESPONSE")).runCommand("/respondticket").tooltip(ChatColor.translateAlternateColorCodes('&', "&aClick to send respond to ticket")).send(event.getPlayer());
            return;
        }

        if (ticketManager.getNewTickets().containsKey(player)) {
            event.setCancelled(true);
            String question = ticketManager.getNewTickets().get(player);
            String msg = event.getMessage();

            question = question + msg + " ";
            ticketManager.getNewTickets().put(player, question);
            JSONMessage send = JSONMessage.create();
            send.then(ChatColor.translateAlternateColorCodes('&', "&aCLICK ME TO SUBMIT YOUR TICKET")).runCommand("/submitticket").tooltip(ChatColor.translateAlternateColorCodes('&', "&aClick to send ticket")).send(event.getPlayer());
            return;
        }
    }

    @EventHandler
    public void onInvClick (InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        if (event.getInventory().getName().equalsIgnoreCase("Cancel ticket")) {
            event.setCancelled(true);
            if (event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.ANVIL) {
                ticketManager.getNewTickets().remove(player.getUniqueId());
                player.closeInventory();
                MessageManager.sendMessage(player, "&cTicket has been cancelled");
                return;
            }
        }

        if (event.getInventory().getName().equalsIgnoreCase(TicketManager.GUI_NAME)) {
            event.setCancelled(true);
            ItemStack item = event.getCurrentItem();
            if (item != null) {
                if (item.getType() == Material.GOLD_INGOT) {
                    event.getWhoClicked().closeInventory();
                    ticketManager.getNewTickets().put(player.getUniqueId(), "");
                    MessageManager.sendMessage(player, "&aPlease enter your question");
                } else if (item.getType() == Material.IRON_INGOT) {

                } else if (item.getType() == Material.NAME_TAG) {
                    ticketManager.openTicketList(player.getUniqueId());
                } else if (item.getType() == Material.PAPER) {
                    ticketManager.openPlayerTickets(player.getUniqueId());
                } else if (item.getType() == Material.WRITTEN_BOOK) {
                    ticketManager.openClosedTickcets(player.getUniqueId());
                }
            }

            return;
        }

        if (event.getInventory().getName().equalsIgnoreCase(TicketManager.LIST_GUI_NAME)) {
            event.setCancelled(true);
            ItemStack item = event.getCurrentItem();
            if (item != null) {
                if (item.getType() == Material.BARRIER) {
                    ticketManager.openTicketGUI(player.getUniqueId(), player.hasPermission("carbyne.staff.tickets"));
                    return;
                }

                if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                    ticketManager.openTicket(player, item.getItemMeta().getDisplayName(), player.hasPermission("carbyne.staff.tickets"));
                }
            }

            return;
        }

        if (event.getInventory().getName().equalsIgnoreCase(player.getName() + "'s Tickets")) {
            event.setCancelled(true);
            ItemStack item = event.getCurrentItem();
            if (item != null) {
                if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                    ticketManager.openTicket(player, item.getItemMeta().getDisplayName(), player.hasPermission("carbyne.staff.tickets"));
                }
            }

            return;
        }

        if (event.getInventory().getName().equals(TicketManager.CLOSED_TICKET_LIST_GUI_NAME)) {
            event.setCancelled(true);
            if (event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.BARRIER) {
                ticketManager.openTicketGUI(player.getUniqueId(), player.hasPermission("carbyne.staff.tickets"));
                return;
            }
        }

        Ticket ticket = ticketManager.getTicket(event.getInventory().getName());
        if (ticket != null) {
            event.setCancelled(true);
            ItemStack item = event.getCurrentItem();
            if (item != null) {
                switch (item.getType()) {
                    case BED:
                        ticketManager.claimTicket(player.getUniqueId(), event.getInventory().getName());
                        player.closeInventory();
                        break;
                    case BAKED_POTATO:
                        ticketManager.closeTicket(player.getUniqueId(), event.getInventory().getName());
                        event.setCurrentItem(null);
                        event.getClickedInventory().setItem(5, new ItemBuilder(ticketManager.getMaterial(ticket)).name(ticketManager.getColor(ticket) + ticket.getStatus().name()).build());
                        break;
                    case REDSTONE:
                        if (ticketManager.getTickets().contains(ticket)) {
                            ticketManager.getTickets().remove(ticket);
                        }

                        if (ticketManager.getClosedTickets().contains(ticket)) {
                            ticketManager.getClosedTickets().remove(ticket);
                        }

                        MessageManager.sendMessage(player, "&cThe ticket has been removed");
                        break;
                    case BARRIER:
                        ticketManager.openTicketList(player.getUniqueId());
                        break;
                }
            }
        }
    }
}
