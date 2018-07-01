package com.medievallords.carbyne.utils.command;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.crates.CrateManager;
import com.medievallords.carbyne.dailybonus.DailyBonusManager;
import com.medievallords.carbyne.donator.GamemodeManager;
import com.medievallords.carbyne.duels.duel.DuelManager;
import com.medievallords.carbyne.economy.EconomyManager;
import com.medievallords.carbyne.gates.GateManager;
import com.medievallords.carbyne.gear.GearManager;
import com.medievallords.carbyne.leaderboards.LeaderboardManager;
import com.medievallords.carbyne.lootchests.LootChestManager;
import com.medievallords.carbyne.profiles.ProfileManager;
import com.medievallords.carbyne.regeneration.RegenerationHandler;
import com.medievallords.carbyne.spellmenu.SpellMenuManager;
import com.medievallords.carbyne.squads.SquadManager;
import com.medievallords.carbyne.staff.StaffManager;
import com.medievallords.carbyne.zones.ZoneManager;
import lombok.Getter;

@Getter
public class BaseCommand {

    private final Carbyne carbyne = Carbyne.getInstance();
    private final StaffManager staffManager = carbyne.getStaffManager();
    private final GearManager gearManager = carbyne.getGearManager();
    private final GateManager gateManager = carbyne.getGateManager();
    private final LeaderboardManager leaderboardManager = carbyne.getLeaderboardManager();
    private final EconomyManager economyManager = carbyne.getEconomyManager();
    private final SquadManager squadManager = carbyne.getSquadManager();
    private final RegenerationHandler regenerationHandler = carbyne.getRegenerationHandler();
    private final CrateManager crateManager = carbyne.getCrateManager();
    private final DuelManager duelManager = carbyne.getDuelManager();
    private final ProfileManager profileManager = carbyne.getProfileManager();
    private final LootChestManager lootChestManager = carbyne.getLootChestManager();
    private final GamemodeManager gamemodeManager = carbyne.getGamemodeManager();
    //private final DropPointManager conquerPointManager = carbyne.getConquerPointManager();
//    private final PackageManager packageManager = carbyne.getPackageManager();
    private final SpellMenuManager spellMenuManager = carbyne.getSpellMenuManager();
    private final DailyBonusManager dailyBonusManager = carbyne.getDailyBonusManager();
    private final ZoneManager zoneManager = carbyne.getZoneManager();

    public BaseCommand() {
        carbyne.getCommandFramework().registerCommands(this);
    }
}
