package com.medievallords.carbyne.war.objects.request;

import com.medievallords.carbyne.war.objects.WarNation;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;

/**
 * Created by Williams on 2017-08-21
 * for the Carbyne project.
 */
public class WarRequest {

    private TownBlock plot;
    private Town town;
    private WarNation attacker, defender;

    public WarRequest(TownBlock plot, Town town, WarNation attacker, WarNation defender) {
        this.plot = plot;
        this.town = town;
        this.attacker = attacker;
        this.defender = defender;
    }


}
