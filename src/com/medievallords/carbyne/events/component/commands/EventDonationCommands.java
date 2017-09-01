package com.medievallords.carbyne.events.component.commands;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.events.Event;
import com.medievallords.carbyne.events.EventComponent;
import com.medievallords.carbyne.events.EventManager;
import com.medievallords.carbyne.events.component.DonationComponent;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.entity.Player;

import java.util.List;

public class EventDonationCommands extends BaseCommand {

    private EventManager eventManager = Carbyne.getInstance().getEventManager();

    @Command(name = "event.donate")
    public void onCommand(CommandArgs cmdargs) {
        Player player = cmdargs.getPlayer();
        String[] args = cmdargs.getArgs();

        if (args.length >= 1) {
            if (args.length >= 2) {
                double amount;
                try {
                    amount = new Double(args[1]);
                } catch (Exception ex) {
                    sendHelp(player);
                    return;
                }

                if (amount == -1) {
                    MessageManager.sendMessage(player, "&cThe donation amount entered is invalid!");
                    return;
                }

                List<Event> activeEvents = eventManager.getActiveEvents();
                Event event = null;
                for (Event e : activeEvents) {
                    if (e.isActive() && e.getEventName().equalsIgnoreCase(args[0])) {
                        event = e;
                        break;
                    }
                }

                if (event == null) {
                    MessageManager.sendMessage(player, "&cAn event with that name is not found!");
                    return;
                }

                DonationComponent component = null;
                for (EventComponent ec : event.getComponents()) {
                    if (ec instanceof DonationComponent) {
                        component = (DonationComponent) ec;
                        break;
                    }
                }

                if (component == null || !(component.active)) {
                    MessageManager.sendMessage(player, "&cDonations are not enabled for this event!");
                    return;
                }

                component.donate(player, amount);
            }
        } else sendHelp(player);
    }

    private void sendHelp(Player player) {
        MessageManager.sendMessage(player, "&2Usage: /event donate <eventName> <amount>");
    }

}
