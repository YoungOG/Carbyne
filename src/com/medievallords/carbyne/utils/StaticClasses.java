package com.medievallords.carbyne.utils;


import com.medievallords.carbyne.announcer.AnnouncerManager;
import com.medievallords.carbyne.conquerpoints.ConquerPointManager;
import com.medievallords.carbyne.crates.CrateManager;
import com.medievallords.carbyne.dailybonus.DailyBonusManager;
import com.medievallords.carbyne.donator.GamemodeManager;
import com.medievallords.carbyne.donator.TrailManager;
import com.medievallords.carbyne.dungeons.dungeons.DungeonHandler;
import com.medievallords.carbyne.dungeons.dungeons.DungeonQueuer;
import com.medievallords.carbyne.economy.EconomyManager;
import com.medievallords.carbyne.gates.GateManager;
import com.medievallords.carbyne.gear.GearManager;
import com.medievallords.carbyne.gear.listeners.GearListeners;
import com.medievallords.carbyne.kits.KitManager;
import com.medievallords.carbyne.leaderboards.LeaderboardManager;
import com.medievallords.carbyne.lootchests.LootChestManager;
import com.medievallords.carbyne.portals.PortalManager;
import com.medievallords.carbyne.prizeeggs.PrizeEggManager;
import com.medievallords.carbyne.profiles.ProfileManager;
import com.medievallords.carbyne.quests.QuestHandler;
import com.medievallords.carbyne.regeneration.RegenerationHandler;
import com.medievallords.carbyne.spellmenu.SpellMenuManager;
import com.medievallords.carbyne.squads.SquadManager;
import com.medievallords.carbyne.staff.StaffManager;
import com.medievallords.carbyne.worldhandler.WorldHandler;
import com.medievallords.carbyne.zones.ZoneManager;
import de.slikey.effectlib.EffectManager;


public final class StaticClasses {

    public static ProfileManager profileManager;
    public static StaffManager staffManager;
    public static RegenerationHandler regenerationHandler;
    public static EconomyManager economyManager;
    public static GearManager gearManager;
    public static EffectManager effectManager;
    public static GateManager gateManager;
    public static SquadManager squadManager;
    public static CrateManager crateManager;
    public static ConquerPointManager conquerPointManager;
    public static LeaderboardManager leaderboardManager;
    public static LootChestManager lootChestManager;
    public static GamemodeManager gamemodeManager;
    //public static PacketManager packetManager;
    public static TrailManager trailManager;
    //public static EventManager eventManager;
    public static PortalManager portalManager;
    public static SpellMenuManager spellMenuManager;
    public static DailyBonusManager dailyBonusManager;
    public static GearListeners gearListeners;
    public static ZoneManager zoneManager;
    public static QuestHandler questHandler;
    public static DungeonHandler dungeonHandler;
    public static DungeonQueuer dungeonQueuer;
    public static WorldHandler worldHandler;
    public static KitManager kitManager;
    public static PrizeEggManager prizeEggManager;
    public static AnnouncerManager announcerManager;
}
