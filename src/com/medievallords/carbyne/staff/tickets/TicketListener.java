package com.medievallords.carbyne.staff.tickets;

import com.medievallords.carbyne.Carbyne;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Created by Dalton on 5/31/2017.
 */
public class TicketListener implements Listener
{

    private Carbyne main = Carbyne.getInstance();
    private TicketManager ticketManager = main.getTicketManager();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e)
    {
        if(e.getPlayer().hasPermission("carbyne.commands.ticket"))
        {
            ticketManager.getOnlineStaff().add(e.getPlayer());
            if(!ticketManager.getStaffStats().containsKey(e.getPlayer().getUniqueId()))
                ticketManager.getStaffStats().put(e.getPlayer().getUniqueId(), new Statistics(0, 0));
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e)
    {
        if(e.getPlayer().hasPermission("carbyne.commands.ticket"))
            ticketManager.getOnlineStaff().remove(e.getPlayer());
    }

}
