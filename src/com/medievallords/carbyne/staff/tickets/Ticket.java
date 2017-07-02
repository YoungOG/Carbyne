package com.medievallords.carbyne.staff.tickets;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.Date;

/**
 * Created by Dalton on 5/30/2017.
 */
@Getter
@Setter
public class Ticket
{

    private static int idCounter = 0;

    private int id;
    private Player sentBy, claimedBy;
    private String playerMessage, date;
    private TicketStatus status;

    public Ticket(Player sentBy, String playerMessage)
    {
        idCounter++;
        this.id = idCounter;
        this.status = TicketStatus.OPEN;
        this.date = new Date(System.currentTimeMillis()).toString();
        this.sentBy = sentBy;
        this.claimedBy = null;
        this.playerMessage = playerMessage;
    }

}
