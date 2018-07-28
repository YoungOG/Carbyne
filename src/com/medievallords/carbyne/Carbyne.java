package com.medievallords.carbyne;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.medievallords.carbyne.commands.*;
import com.medievallords.carbyne.crates.CrateManager;
import com.medievallords.carbyne.crates.commands.*;
import com.medievallords.carbyne.crates.listeners.CrateListeners;
import com.medievallords.carbyne.dailybonus.DailyBonusManager;
import com.medievallords.carbyne.dailybonus.commands.DailyBonusCommand;
import com.medievallords.carbyne.dailybonus.listeners.DailyBonusListeners;
import com.medievallords.carbyne.donator.GamemodeManager;
import com.medievallords.carbyne.donator.TrailManager;
import com.medievallords.carbyne.donator.commands.FlyCommand;
import com.medievallords.carbyne.donator.commands.GamemodeCommand;
import com.medievallords.carbyne.donator.commands.GiveRankRewardsCommand;
import com.medievallords.carbyne.donator.commands.TrailCommand;
import com.medievallords.carbyne.donator.listeners.GameModeListener;
import com.medievallords.carbyne.donator.listeners.TrailListener;
import com.medievallords.carbyne.duels.arena.commands.*;
import com.medievallords.carbyne.duels.arena.listeners.ArenaListeners;
import com.medievallords.carbyne.duels.duel.DuelListeners;
import com.medievallords.carbyne.duels.duel.DuelManager;
import com.medievallords.carbyne.duels.duel.commands.*;
import com.medievallords.carbyne.economy.EconomyManager;
import com.medievallords.carbyne.economy.commands.DepositCommand;
import com.medievallords.carbyne.economy.commands.WithdrawCommand;
import com.medievallords.carbyne.economy.objects.Account;
import com.medievallords.carbyne.events.EventManager;
import com.medievallords.carbyne.events.UniversalEventCommand;
import com.medievallords.carbyne.events.UniversalEventListeners;
import com.medievallords.carbyne.events.component.commands.EventDonationCommands;
import com.medievallords.carbyne.events.implementations.LastAlive;
import com.medievallords.carbyne.events.implementations.Race;
import com.medievallords.carbyne.gates.GateManager;
import com.medievallords.carbyne.gates.commands.*;
import com.medievallords.carbyne.gates.listeners.GateListeners;
import com.medievallords.carbyne.gates.listeners.GateMobListeners;
import com.medievallords.carbyne.gear.GearManager;
import com.medievallords.carbyne.gear.commands.GearCommands;
import com.medievallords.carbyne.gear.commands.GearGiveCommand;
import com.medievallords.carbyne.gear.commands.GearSetChargeCommand;
import com.medievallords.carbyne.gear.listeners.GearListeners;
import com.medievallords.carbyne.heartbeat.HeartbeatRunnable;
import com.medievallords.carbyne.leaderboards.LeaderboardListeners;
import com.medievallords.carbyne.leaderboards.LeaderboardManager;
import com.medievallords.carbyne.leaderboards.commands.*;
import com.medievallords.carbyne.listeners.*;
import com.medievallords.carbyne.lootchests.LootChestListeners;
import com.medievallords.carbyne.lootchests.LootChestManager;
import com.medievallords.carbyne.lootchests.commands.LootChestCommand;
import com.medievallords.carbyne.mechanics.MechanicListener;
import com.medievallords.carbyne.missions.listeners.MissionListeners;
import com.medievallords.carbyne.packages.listeners.PackageListener;
import com.medievallords.carbyne.profiles.ProfileListeners;
import com.medievallords.carbyne.profiles.ProfileManager;
import com.medievallords.carbyne.regeneration.RegenerationHandler;
import com.medievallords.carbyne.regeneration.RegenerationListeners;
import com.medievallords.carbyne.regeneration.commands.RegenerationBypassCommand;
import com.medievallords.carbyne.region.Region;
import com.medievallords.carbyne.spawners.commands.SpawnerCommand;
import com.medievallords.carbyne.spawners.commands.SpawnerCreateCommand;
import com.medievallords.carbyne.spawners.listeners.SpawnerListeners;
import com.medievallords.carbyne.spellmenu.SpellMenuCommand;
import com.medievallords.carbyne.spellmenu.SpellMenuListeners;
import com.medievallords.carbyne.spellmenu.SpellMenuManager;
import com.medievallords.carbyne.squads.SquadManager;
import com.medievallords.carbyne.squads.commands.*;
import com.medievallords.carbyne.staff.StaffManager;
import com.medievallords.carbyne.staff.commands.*;
import com.medievallords.carbyne.staff.listeners.*;
import com.medievallords.carbyne.utils.*;
import com.medievallords.carbyne.utils.combatindicators.PacketManager;
import com.medievallords.carbyne.utils.combatindicators.PacketManagerImpl;
import com.medievallords.carbyne.utils.command.CommandFramework;
import com.medievallords.carbyne.utils.nametag.NametagManager;
import com.medievallords.carbyne.utils.scoreboard.CarbyneScoreboard;
import com.medievallords.carbyne.utils.signgui.SignGUI;
import com.medievallords.carbyne.utils.tabbed.Tabbed;
import com.medievallords.carbyne.zones.ZoneListeners;
import com.medievallords.carbyne.zones.ZoneManager;
import com.medievallords.carbyne.zones.commands.ZoneCommands;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.client.MongoDatabase;
import com.palmergames.bukkit.towny.Towny;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import de.slikey.effectlib.EffectManager;
import lombok.Getter;
import lombok.Setter;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

@Getter
@Setter
public class Carbyne extends JavaPlugin {

    public static Carbyne instance;

    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;

    private Towny towny;
    private boolean townyEnabled = false;

    private WorldGuardPlugin worldGuardPlugin;
    private boolean worldGuardEnabled = false;

    private boolean mythicMobsEnabled = false;

    private File gearFile;
    private FileConfiguration gearFileConfiguration;
    private File gatesFile;
    private FileConfiguration gatesFileConfiguration;
    private File cratesFile;
    private FileConfiguration cratesFileConfiguration;
    private File arenasFile;
    private FileConfiguration arenasFileConfiguration;
    private File leaderboardFile;
    private FileConfiguration leaderboardFileConfiguration;
    private File lootChestsFile;
    private FileConfiguration lootChestsFileConfiguration;
    private File dropPointsFile;
    private FileConfiguration dropPointsFileConfiguration;
    private File donatorTownsFile;
    private FileConfiguration donatorTownsConfiguration;
    private File eventsFile;
    private FileConfiguration eventsFileConfiguration;
    private File rulesFile;
    private FileConfiguration rulesFileCongfiguration;
    private File zonesFile;
    private FileConfiguration zonesFileConfiguration;

    private Permission permissions = null;

    private CommandFramework commandFramework;

    private HeartbeatRunnable heartbeatRunnable;

    private CarbyneBoardAdapter carbyneBoardAdapter;
    private CarbyneScoreboard carbyneScoreboard;
    private SignGUI signGUI;

    private EntityHider entityHider;
    private ProfileManager profileManager;
    private StaffManager staffManager;
    private RegenerationHandler regenerationHandler;
    private EconomyManager economyManager;
    private GearManager gearManager;
    private EffectManager effectManager;
    private GateManager gateManager;
    private SquadManager squadManager;
    private CrateManager crateManager;
    private DuelManager duelManager;
    private LeaderboardManager leaderboardManager;
    private LootChestManager lootChestManager;
    private GamemodeManager gamemodeManager;
    private ItemDb itemDb;
    private PacketManager packetManager;
    private TrailManager trailManager;
    private EventManager eventManager;
    private SpellMenuManager spellMenuManager;
    private DailyBonusManager dailyBonusManager;
    private Tabbed tabbed;
    private GearListeners gearListeners;
    private ZoneManager zoneManager;

    public static Carbyne getInstance() {
        return instance;
    }

    public void onEnable() {
        instance = this;

        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        registerConfigurations();

        registerMongoConnection();

        PluginManager pm = Bukkit.getServer().getPluginManager();

        setupPermissions();

        if (pm.isPluginEnabled("Towny")) {
            towny = (Towny) pm.getPlugin("Towny");
            townyEnabled = true;
        }

        if (pm.isPluginEnabled("WorldGuard")) {
            worldGuardPlugin = (WorldGuardPlugin) pm.getPlugin("WorldGuard");
            worldGuardEnabled = true;
        }

        if (pm.isPluginEnabled("MythicMobs"))
            mythicMobsEnabled = true;

        heartbeatRunnable = new HeartbeatRunnable();
        heartbeatRunnable.runTaskTimer(Carbyne.getInstance(), 0L, 2L);

        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new LagTask(), 100L, 1L);

        itemDb = new ItemDb();

        for (Player all : PlayerUtility.getOnlinePlayers())
            all.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());

        entityHider = new EntityHider(this, EntityHider.Policy.BLACKLIST);
        profileManager = new ProfileManager();
        staffManager = new StaffManager();
        economyManager = new EconomyManager();
        gearManager = new GearManager();
        effectManager = new EffectManager(this);
        gateManager = new GateManager();
        squadManager = new SquadManager();
        crateManager = new CrateManager();
        duelManager = new DuelManager();
        leaderboardManager = new LeaderboardManager();
        regenerationHandler = new RegenerationHandler();
        lootChestManager = new LootChestManager();
        gamemodeManager = new GamemodeManager();
        trailManager = new TrailManager();
        packetManager = new PacketManagerImpl(this);
        spellMenuManager = new SpellMenuManager();
        dailyBonusManager = new DailyBonusManager();
        tabbed = new Tabbed(this);
        zoneManager = new ZoneManager();
        gearListeners = new GearListeners();

        carbyneBoardAdapter = new CarbyneBoardAdapter(this);
        carbyneScoreboard = new CarbyneScoreboard(this, carbyneBoardAdapter);
        signGUI = new SignGUI();

        commandFramework = new CommandFramework(this);

        eventManager = new EventManager();

        registerCommands();
        registerEvents(pm);
        registerPackets();

        CombatTagListeners.ForceFieldTask.run(this);
//        GearListeners.ForceFieldTask.run(this);

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : PlayerUtility.getOnlinePlayers())
                    NametagManager.updateNametag(player);
            }
        }.runTaskTimerAsynchronously(this, 0L, 10L);

        clearVillagers();

        Region.loadAll();
    }

    public void onDisable() {
        profileManager.saveProfiles(false);
        Account.saveAccounts(false);
        staffManager.shutdown();
        gateManager.saveGates();
        effectManager.dispose();
        crateManager.save(cratesFileConfiguration);
        leaderboardManager.stopAllLeaderboardTasks();
        mongoClient.close();
        staffManager.shutdown();
        gearManager.getRepairItems().forEach(Entity::remove);
        eventManager.saveEvents();
        commandFramework.unregisterAll();

        clearVillagers();
        duelManager.cancelAll();
    }

    public void registerMongoConnection() {
        MongoClientOptions options = MongoClientOptions.builder().connectionsPerHost(200000).build();
        mongoClient = new MongoClient(getConfig().getString("database.host"), options);
        mongoDatabase = mongoClient.getDatabase(getConfig().getString("database.database-name"));
    }

    private void registerEvents(PluginManager pm) {
        pm.registerEvents(new ProfileListeners(), this);
        pm.registerEvents(new CombatTagListeners(), this);
        pm.registerEvents(gearListeners, this);
        pm.registerEvents(new CooldownListeners(), this);
        pm.registerEvents(new OptimizationListeners(), this);
        pm.registerEvents(new ChatListener(), this);
        pm.registerEvents(new GateListeners(), this);
        pm.registerEvents(new SpawnerListeners(), this);
        pm.registerEvents(new RegenerationListeners(), this);
        pm.registerEvents(new CrateListeners(), this);
        pm.registerEvents(new ArenaListeners(), this);
        pm.registerEvents(new DuelListeners(), this);
        pm.registerEvents(new LeaderboardListeners(), this);
        pm.registerEvents(new LootChestListeners(), this);
        pm.registerEvents(new SetSlotsCommand(), this);
        pm.registerEvents(new SpellMenuListeners(), this);
        pm.registerEvents(new GameModeListener(gamemodeManager), this);
        pm.registerEvents(new PlayerListeners(), this);
        pm.registerEvents(new FreezeListeners(), this);
        pm.registerEvents(new SpambotListener(), this);
        pm.registerEvents(new StaffModeListeners(), this);
        pm.registerEvents(new PinListeners(), this);
        pm.registerEvents(new VanishListeners(), this);
        pm.registerEvents(new TrailListener(), this);
        pm.registerEvents(new UniversalEventListeners(eventManager), this);
        pm.registerEvents(new SetDamageCommand(), this);
        pm.registerEvents(new FollowCommand(), this);
        pm.registerEvents(new PackageListener(), this);
        pm.registerEvents(new MissionListeners(), this);
        pm.registerEvents(new MechanicListener(), this);
        pm.registerEvents(new IgnoreCommand(), this);
        pm.registerEvents(new StaffLogging(), this);
        pm.registerEvents(new IronBoatListener(), this);
        pm.registerEvents(new StaffListeners(), this);
        pm.registerEvents(new DailyBonusListeners(), this);
        pm.registerEvents(new ZoneListeners(), this);
        pm.registerEvents(new ArrowListeners(), this);
        pm.registerEvents(new GiveRankRewardsCommand(), this);

        if (mythicMobsEnabled)
            pm.registerEvents(new GateMobListeners(), this);

        if (townyEnabled)
            pm.registerEvents(new DamageListener(), this);
    }

    private void registerCommands() {
        //General Commands
        new WebsiteCommand();
        new StatsCommand();
        new ChatCommand();
        new ToggleCommand();
        new RegenerationBypassCommand();
        new LogoutCommand();
        new PvpTimerCommand();
        new SetDurabilityCommand();
        new VoteCommand();
        new DiscordCommand();
        new LocalChatCommand();
        new TownChatCommand();
        new NationChatCommand();
        new SetMotdCommand();
        new SetHitDelayCommand();
        new SpellMenuCommand();
        new WithdrawCommand();
        new DepositCommand();
        new DailyBonusCommand();
        new LagCommand();
        new GiveRankRewardsCommand();

        //Gate Commands
        new GearCommands();
        new GearGiveCommand();
        new GearSetChargeCommand();
        new GateCommand();
        new GateAddBCommand();
        new GateAddPPCommand();
        new GateAddRSBCommand();
        new GateAddSpawnerCommand();
        new GateDelBCommand();
        new GateDelPPCommand();
        new GateDelRSBCommand();
        new GateDelSpawnerCommand();
        new GateCreateCommand();
        new GateActiveCommand();
        new GateRemoveCommand();
        new GateRenameCommand();
        new GateStatusCommand();
        new GateListCommand();

        //Economy Commands
        new BalanceCommand();

        //Squad Commands
        new SquadCommand();
        new SquadJoinCommand();
        new SquadCreateCommand();
        new SquadInviteCommand();
        new SquadLeaveCommand();
        new SquadDisbandCommand();
        new SquadFriendlyFireCommand();
        new SquadSetCommand();
        new SquadKickCommand();
        new SquadChatCommand();
        new SquadListCommand();
        new FocusCommand();

        //Spawner Commands
        new SpawnerCreateCommand();
        new SpawnerCommand();

        //Crate Commands
        new CrateCommand();
        new CrateCreateCommand();
        new CrateEditCommand();
        new CrateKeyCommand();
        new CrateListCommand();
        new CrateReloadCommand();
        new CrateRemoveCommand();
        new CrateRenameCommand();
        new CrateSetLocationCommand();

        //Arena Commands
        new ArenaCommand();
        new ArenaCreateCommand();
        new ArenaRemoveCommand();
        new ArenaListCommand();
        new ArenaSetLobbyCommand();
        new ArenaAddSpawnCommand();
        new ArenaAddPedastoolCommand();
        new ArenaRemoveSpawnCommand();
        new ArenaRemovePedastoolCommand();
        new ArenaReloadCommand();

        //Duel Commands
        new DuelAcceptCommand();
        new DuelSetSquadFightCommand();
        new DuelCommand();
        new DuelBetCommand();
        new DuelDeclineCommand();

        //Leaderboard Commands
        new LeaderboardCommand();
        new LeaderboardCreateCommand();
        new LeaderboardRemoveCommand();
        new LeaderboardSetPrimarySignCommand();
        new LeaderboardDelPrimarySignCommand();
        new LeaderboardAddSignCommand();
        new LeaderboardDelSignCommand();
        new LeaderboardAddHeadCommand();
        new LeaderboardDelHeadCommand();
        new LeaderboardListCommand();

        //Loot Chest Commands
        new LootChestCommand();

        //Staff Commands
        new ClearChatCommand();
        new HelpopCommand();
        new MuteChatCommand();
        new ReportCommand();
        new SetSlotsCommand();
        new SlowChatCommand();
        new VanishCommand();
        new StaffCommand();
        new FreezeCommand();
        new SetPinCommand();
        new ResetPinCommand();
        new ReviveCommand();
        new StaffModeWhitelist();
        new StaffChatCommand();
        new TestMessageCommand();

        //new DropPointCommand();

        //Spell Mods Commands
        new SpellMenuCommand();

        //Gamemode Commands
        new FlyCommand();
        new GamemodeCommand();

        new TrailCommand();
        new RulesCommand();

        new EventDonationCommands();
        new UniversalEventCommand(new Race(eventManager), new LastAlive(eventManager));

        new ZoneCommands();

        //TEMPRARY
        // new TempCommandNeat();
    }

    private void registerPackets() {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this, PacketType.Play.Client.CLIENT_COMMAND) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                if (event.getPacket().getClientCommands().read(0) == EnumWrappers.ClientCommand.OPEN_INVENTORY_ACHIEVEMENT) {
                    Player player = event.getPlayer();

                    PlayerUtility.checkForIllegalItems(player, player.getInventory());

                    if (player.getGameMode() == GameMode.CREATIVE) {
                        player.getInventory().setArmorContents(new ItemStack[]{null, null, null, null});
                        player.getInventory().setHelmet(null);
                        player.getInventory().setChestplate(null);
                        player.getInventory().setLeggings(null);
                        player.getInventory().setBoots(null);
                        player.updateInventory();
                    }
                }
            }
        });
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        permissions = rsp.getProvider();
        return permissions != null;
    }

    public void registerConfigurations() {
        https:
//www.youtube.com/watch?v=vwnAeIsg3as        saveResource("gates.yml", false);
        saveResource("item.csv", false);
        saveResource("crates.yml", false);
        saveResource("arenas.yml", false);
        saveResource("leaderboards.yml", false);
        saveResource("lang.yml", false);
        saveResource("lootchests.yml", false);
        saveResource("droppoints.yml", false);
        saveResource("donatortowns.yml", false);
        saveResource("events.yml", false);
        saveResource("rules.yml", false);
        saveResource("packages.yml", false);
        saveResource("missions.yml", false);
        saveResource("zones.yml", false);

        gearFile = new File(getDataFolder(), "gear.yml");
        gearFileConfiguration = YamlConfiguration.loadConfiguration(gearFile);

        gatesFile = new File(getDataFolder(), "gates.yml");
        gatesFileConfiguration = YamlConfiguration.loadConfiguration(gatesFile);

        cratesFile = new File(getDataFolder(), "crates.yml");
        cratesFileConfiguration = YamlConfiguration.loadConfiguration(cratesFile);

        arenasFile = new File(getDataFolder(), "arenas.yml");
        arenasFileConfiguration = YamlConfiguration.loadConfiguration(arenasFile);

        leaderboardFile = new File(getDataFolder(), "leaderboards.yml");
        leaderboardFileConfiguration = YamlConfiguration.loadConfiguration(leaderboardFile);

        lootChestsFile = new File(getDataFolder(), "lootchests.yml");
        lootChestsFileConfiguration = YamlConfiguration.loadConfiguration(lootChestsFile);

        dropPointsFile = new File(getDataFolder(), "droppoints.yml");
        dropPointsFileConfiguration = YamlConfiguration.loadConfiguration(dropPointsFile);

        donatorTownsFile = new File(getDataFolder(), "donatortowns.yml");
        donatorTownsConfiguration = YamlConfiguration.loadConfiguration(donatorTownsFile);

        eventsFile = new File(getDataFolder(), "events.yml");
        eventsFileConfiguration = YamlConfiguration.loadConfiguration(eventsFile);

        rulesFile = new File(getDataFolder(), "rules.yml");
        rulesFileCongfiguration = YamlConfiguration.loadConfiguration(rulesFile);

        zonesFile = new File(getDataFolder(), "zones.yml");
        zonesFileConfiguration = YamlConfiguration.loadConfiguration(zonesFile);

        File langFile = new File(getDataFolder(), "lang.yml");
        FileConfiguration langFileConfiguration = YamlConfiguration.loadConfiguration(langFile);

        for (Lang item : Lang.values())
            if (langFileConfiguration.getString(item.getPath()) == null)
                if (item.getAllMessages().length > 1)
                    langFileConfiguration.set(item.getPath(), item.getAllMessages());
                else
                    langFileConfiguration.set(item.getPath(), item.getFirstMessage());

        Lang.setFile(langFileConfiguration);

        try {
            langFileConfiguration.save(langFile);
        } catch (IOException e) {
            e.printStackTrace();
            getLogger().log(Level.WARNING, "Failed to save lang.yml!");
            getLogger().log(Level.WARNING, "Please report this stacktrace to Young.");
        }
    }

    public void clearVillagers() {
        for (World w : Bukkit.getWorlds())
            w.getEntities().stream().filter(ent -> ent instanceof Villager).forEach(ent -> {
                Villager villager = (Villager) ent;

                if (villager.getMetadata("logger") != null && villager.hasMetadata("logger"))
                    villager.remove();
            });
    }

    private WorldGuardPlugin getWorldGuard() {
        Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");

        // WorldGuard may not be loaded
        if (plugin == null || !(plugin instanceof WorldGuardPlugin))
            return null; // Maybe you want throw an exception instead

        return (WorldGuardPlugin) plugin;
    }
}
