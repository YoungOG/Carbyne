package com.medievallords.carbyne.tickets.commands;

import com.medievallords.carbyne.profiles.Profile;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by WE on 2017-07-08.
 *
 */
public class TicketCommand extends BaseCommand {

    @Command(name = "ticket", aliases = {"modreq"}, inGameOnly = true)
    public void ticket(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        Player player = commandArgs.getPlayer();

        if (getTicketManager().getNewTickets().containsKey(player.getUniqueId())) {
            getTicketManager().openRemove(player.getUniqueId());
            return;
        }

        getTicketManager().openTicketGUI(player.getUniqueId(), player.hasPermission("carbyne.staff.tickets"));
    }

    @Command(name = "submitticket", aliases = {"subticket"}, inGameOnly = true)
    public void submit(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        Player player = commandArgs.getPlayer();

        if (getTicketManager().getNewTickets().containsKey(player.getUniqueId())) {
            getTicketManager().createTicket(player.getUniqueId(), getTicketManager().getNewTickets().get(player.getUniqueId()));
        }
        else {
            player.sendMessage("You don't have any tickets");
        }
    }

    @Command(name = "respondticket", aliases = {"resticket"}, inGameOnly = true, permission = "carbyne.staff.tickets")
    public void respond(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        Player player = commandArgs.getPlayer();

        if (getTicketManager().getRespondingTickets().containsKey(player.getUniqueId())) {
            getTicketManager().openTicket(player, ChatColor.translateAlternateColorCodes('&', "Ticket #" + getTicketManager().getTickets().indexOf(getTicketManager().getRespondingTickets().get(player.getUniqueId()))), true);
            getTicketManager().getRespondingTickets().remove(player.getUniqueId());
        }
        else {
            player.sendMessage("You don't have any tickets");
        }

    }

    @Command(name = "cancelticket", aliases = {"resticket"}, inGameOnly = true, permission = "carbyne.staff.tickets")
    public void cancel(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        Player player = commandArgs.getPlayer();

        getTicketManager().getNewTickets().remove(player.getUniqueId());
        MessageManager.sendMessage(player, "&cTicket has been cancelled");
    }

    @Command(name = "ticket.stats", aliases = {"modreq.stats"}, inGameOnly = true, permission = "carbyne.staff.tickets")
    public void ticketStats(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        Player player = commandArgs.getPlayer();
        Profile profile = getProfileManager().getProfile(player.getUniqueId());
        if (profile == null) {
            return;
        }

        MessageManager.sendMessage(player, "&aClosed Ticket: " + profile.getClosedTickets());
        MessageManager.sendMessage(player, "&aClaimed Ticket: " + profile.getClaimedTickets());
    }
}
