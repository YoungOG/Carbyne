package com.medievallords.carbyne.utils.command;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.crates.CrateManager;
import com.medievallords.carbyne.duels.duel.DuelManager;
import com.medievallords.carbyne.economy.MarketManager;
import com.medievallords.carbyne.gates.GateManager;
import com.medievallords.carbyne.gear.GearManager;
import com.medievallords.carbyne.leaderboards.LeaderboardManager;
import com.medievallords.carbyne.regeneration.RegenerationHandler;
import com.medievallords.carbyne.squads.SquadManager;
import lombok.Getter;

public class BaseCommand {

    @Getter private Carbyne carbyne = Carbyne.getInstance();
    @Getter private GearManager gearManager = carbyne.getGearManager();
    @Getter private GateManager gateManager = carbyne.getGateManager();
    @Getter private LeaderboardManager leaderboardManager = carbyne.getLeaderboardManager();
    @Getter private MarketManager marketManager = carbyne.getMarketManager();
    @Getter private SquadManager squadManager = carbyne.getSquadManager();
    @Getter private RegenerationHandler regenerationHandler = carbyne.getRegenerationHandler();
    @Getter private CrateManager crateManager = carbyne.getCrateManager();
    @Getter private DuelManager duelManager = carbyne.getDuelManager();

    public BaseCommand() {
        carbyne.getCommandFramework().registerCommands(this);
    }
}
