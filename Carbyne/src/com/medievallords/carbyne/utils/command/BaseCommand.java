package com.medievallords.carbyne.utils.command;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.gates.GateManager;
import com.medievallords.carbyne.gear.GearManager;
import com.medievallords.carbyne.leaderboards.LeaderboardManager;
import lombok.Getter;
import net.elseland.xikage.MythicMobs.MythicMobs;

public class BaseCommand {

    @Getter Carbyne carbyne = Carbyne.getInstance();
    @Getter GearManager gearManager = carbyne.getGearManager();
    @Getter GateManager gateManager = carbyne.getGateManager();
    @Getter LeaderboardManager leaderboardManager = carbyne.getLeaderboardManager();

    public BaseCommand() {
        carbyne.getCommandFramework().registerCommands(this);
    }
}
