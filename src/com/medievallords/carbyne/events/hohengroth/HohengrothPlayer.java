package com.medievallords.carbyne.events.hohengroth;

import java.util.UUID;

/**
 * Created by William on 7/10/2017.
 */
public class HohengrothPlayer {

    private UUID player;
    private double damageDealt = 0, damageTaken = 0;

    private double healthHealed = 0;

    private int kills = 0, deaths = 0;

    public HohengrothPlayer(UUID player) {
        this.player = player;
    }
}
