package com.medievallords.carbyne.war.objects;

import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Williams on 2017-08-21
 * for the Carbyne project.
 */
@Getter
@Setter
public class WarPlot {

    private Town holder;

    private WarNation defender, attacker;

    private boolean started = false;

    private TownBlock plot;

    private List<UUID> attackers = new ArrayList<>();
    private List<UUID> defenders = new ArrayList<>();

    public WarPlot(TownBlock plot, WarNation defender, WarNation attacker, Town holder) {
        this.holder = holder;
        this.attacker = attacker;
        this.defender = defender;
        this.plot = plot;
    }

    public void startWar() {

    }

    public void stopWar() {

    }

}
