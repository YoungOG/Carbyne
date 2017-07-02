package com.medievallords.carbyne.staff.tickets;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.utils.MessageManager;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Dalton on 5/31/2017.
 */
public class TicketTask extends BukkitRunnable
{

    private Carbyne main = Carbyne.getInstance();
    private TicketManager ticketManager;

    public TicketTask(TicketManager ticketManager)
    {
        this.ticketManager = ticketManager;
    }


    /**
     * Sends messages to moderators that tickets are open.
     */
    @Override
    public void run()
    {
        int openedTickets = ticketManager.ticketsOpened();
        if(openedTickets == 0) return;
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                for(Player player : ticketManager.getOnlineStaff())
                    MessageManager.sendMessage(player, "&3There are currently &e" + openedTickets + "&3 open tickets!");
            }
        }.runTask(main);
    }

}
