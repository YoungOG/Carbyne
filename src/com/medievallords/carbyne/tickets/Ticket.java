package com.medievallords.carbyne.tickets;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

/**
 * Created by WE on 2017-07-08.
 */
@Getter
@Setter
public class Ticket {

    private UUID player;
    private UUID staff;

    private String question;
    private String date;
    private TicketStatus status;
    private String response;

    public Ticket (UUID player, String question) {
        this.player = player;
        this.question = question;
        this.status = TicketStatus.OPEN;
        response = "";
        this.date = new Date().toString();
    }

    public Ticket (UUID player, String question, String response, TicketStatus status, String date, UUID staff) {
        this.player = player;
        this.question = question;
        this.response = response;
        this.status = status;
        this.date = date;
        this.staff = staff;
    }
}
