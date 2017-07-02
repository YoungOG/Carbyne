package com.medievallords.carbyne.staff.tickets.commands;

import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.entity.Player;

/**
 * Created by Dalton on 6/2/2017.
 */
public class TicketCommand extends BaseCommand
{

    @Command(name = "ticket", aliases = {"modreq"}, inGameOnly = true)
    public void onCommand(CommandArgs commandArgs)
    {
        Player sender = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();

        if(sender.hasPermission("carbyne.commands.ticket"))
        {
            if(args.length == 0)
            {
                getTicketManager().showTickets(sender);
                return;
            }

            if(args[0].equalsIgnoreCase("claim"))
            {
                if (args.length > 1)
                {
                    try {
                        int id = Integer.parseInt(args[1]);
                        if(getTicketManager().claimTicket(id, sender)) return;
                    } catch (Exception ex) {}
                    MessageManager.sendMessage(sender, "&cFailed to claim ticket!");
                    return;
                }
            }
            else if(args[0].equalsIgnoreCase("close"))
            {
                if (args.length > 1)
                    try {
                        int id = Integer.parseInt(args[1]);
                        if(getTicketManager().closeTicket(id, sender)) return;
                    } catch (Exception ex) {}
                MessageManager.sendMessage(sender, "&cFailed to close ticket!");
                return;
            }
            else if(args[0].equalsIgnoreCase("check"))
            {
                if(args.length > 1)
                    try {
                        int id = Integer.parseInt(args[1]);
                        getTicketManager().showTicket(sender, id);
                        return;
                    } catch (Exception ex) {}
                MessageManager.sendMessage(sender, "&cFailed to read ticket!");
                return;
            }
        }

        if(args.length <= 1) {
            MessageManager.sendMessage(sender, "&cPlease provide a question.");
            return;
        }

        if(getTicketManager().doesPlayerHaveActiveTicket(sender))
        {
            MessageManager.sendMessage(sender, "&cYou have an active ticket still pending!");
            return;
        }

        StringBuilder sb = new StringBuilder();
        for(int i = 1; i < args.length; i++)
        {
            if(i + 1 < args.length) sb.append(args[i] + " ");
            else sb.append(args[i]);
        }

        String playerMessage = sb.toString();
        getTicketManager().createTicket(sender, playerMessage);
    }

}
