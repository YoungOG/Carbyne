package com.medievallords.carbyne.staff.tickets;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Dalton on 5/30/2017.
 */
@Getter
@Setter
public class Statistics
{

    private int claimed, closed;

    public Statistics()
    {
        claimed = 0;
        closed = 0;
    }

    public Statistics(int claimed, int closed)
    {
        this.claimed = claimed;
        this.closed = closed;
    }

}
