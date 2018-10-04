package com.medievallords.carbyne.utils.command;

import com.medievallords.carbyne.Carbyne;
import lombok.Getter;

@Getter
public class BaseCommand {

//    private final Carbyne carbyne = Carbyne.getInstance();
//    private final StaffManager staffManager = carbyne.getStaffManager();
//    private final GearManager gearManager = carbyne.getGearManager();
//    private final GateManager gateManager = carbyne.getGateManager();
//    private final LeaderboardManager leaderboardManager = carbyne.getLeaderboardManager();
//    private final EconomyManager economyManager = carbyne.getEconomyManager();
//    private final SquadManager squadManager = carbyne.getSquadManager();
//    private final RegenerationHandler regenerationHandler = carbyne.getRegenerationHandler();
//    private final CrateManager crateManager = carbyne.getCrateManager();
//    private final DuelManager duelManager = carbyne.getDuelManager();
//    private final ProfileManager profileManager = carbyne.getProfileManager();
//    private final LootChestManager lootChestManager = carbyne.getLootChestManager();
//    private final GamemodeManager gamemodeManager = carbyne.getGamemodeManager();
//    //private final DropPointManager conquerPointManager = carbyne.getConquerPointManager();
////    private final PackageManager packageManager = carbyne.getPackageManager();
//    private final SpellMenuManager spellMenuManager = carbyne.getSpellMenuManager();
//    private final DailyBonusManager dailyBonusManager = carbyne.getDailyBonusManager();
//    private final ZoneManager zoneManager = carbyne.getZoneManager();
//    private final QuestHandler questHandler = carbyne.getQuestHandler();

    public BaseCommand() {
        Carbyne.getInstance().getCommandFramework().registerCommands(this);
    }
}
