package com.medievallords.carbyne.utils.command;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.conquerpoints.ConquerPointManager;
import com.medievallords.carbyne.crates.CrateManager;
import com.medievallords.carbyne.donator.GamemodeManager;
import com.medievallords.carbyne.duels.duel.DuelManager;
import com.medievallords.carbyne.economy.MarketManager;
import com.medievallords.carbyne.gates.GateManager;
import com.medievallords.carbyne.gear.GearManager;
import com.medievallords.carbyne.leaderboards.LeaderboardManager;
import com.medievallords.carbyne.lootchests.LootChestManager;
import com.medievallords.carbyne.packages.PackageManager;
import com.medievallords.carbyne.profiles.ProfileManager;
import com.medievallords.carbyne.regeneration.RegenerationHandler;
import com.medievallords.carbyne.spellmenu.SpellMenuManager;
import com.medievallords.carbyne.squads.SquadManager;
import com.medievallords.carbyne.staff.StaffManager;
import lombok.Getter;

@Getter
public class BaseCommand {

    private Carbyne carbyne = Carbyne.getInstance();
    private StaffManager staffManager = carbyne.getStaffManager();
    private GearManager gearManager = carbyne.getGearManager();
    private GateManager gateManager = carbyne.getGateManager();
    private LeaderboardManager leaderboardManager = carbyne.getLeaderboardManager();
    private MarketManager marketManager = carbyne.getMarketManager();
    private SquadManager squadManager = carbyne.getSquadManager();
    private RegenerationHandler regenerationHandler = carbyne.getRegenerationHandler();
    private CrateManager crateManager = carbyne.getCrateManager();
    private DuelManager duelManager = carbyne.getDuelManager();
    private ProfileManager profileManager = carbyne.getProfileManager();
    private LootChestManager lootChestManager = carbyne.getLootChestManager();
    private GamemodeManager gamemodeManager = carbyne.getGamemodeManager();
    private ConquerPointManager conquerPointManager = carbyne.getConquerPointManager();
    private PackageManager packageManager = carbyne.getPackageManager();
    private SpellMenuManager spellMenuManager = carbyne.getSpellMenuManager();

    public BaseCommand() {
        carbyne.getCommandFramework().registerCommands(this);
    }
}
