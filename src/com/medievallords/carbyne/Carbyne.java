package com.medievallords.carbyne;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.medievallords.carbyne.announcer.AnnouncerManager;
import com.medievallords.carbyne.announcer.AnnouncerReloadCommand;
import com.medievallords.carbyne.commands.*;
import com.medievallords.carbyne.conquerpoints.ConquerPointManager;
import com.medievallords.carbyne.conquerpoints.commands.ConquerPointCommand;
import com.medievallords.carbyne.conquerpoints.events.ConquerPointEvents;
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
import com.medievallords.carbyne.dungeons.dungeons.DungeonHandler;
import com.medievallords.carbyne.dungeons.dungeons.DungeonQueuer;
import com.medievallords.carbyne.dungeons.dungeons.commands.*;
import com.medievallords.carbyne.dungeons.dungeons.listeners.DungeonInstanceListener;
import com.medievallords.carbyne.dungeons.dungeons.listeners.DungeonJoinListeners;
import com.medievallords.carbyne.dungeons.triggers.listeners.TriggerInteractListener;
import com.medievallords.carbyne.economy.EconomyManager;
import com.medievallords.carbyne.economy.commands.DepositCommand;
import com.medievallords.carbyne.economy.objects.Account;
import com.medievallords.carbyne.gates.GateManager;
import com.medievallords.carbyne.gates.commands.*;
import com.medievallords.carbyne.gates.listeners.GateListeners;
import com.medievallords.carbyne.gates.listeners.GateMobListeners;
import com.medievallords.carbyne.gear.GearManager;
import com.medievallords.carbyne.gear.commands.GearCommands;
import com.medievallords.carbyne.gear.commands.GearForgeCommand;
import com.medievallords.carbyne.gear.commands.GearGiveCommand;
import com.medievallords.carbyne.gear.commands.GearSetChargeCommand;
import com.medievallords.carbyne.gear.listeners.GearListeners;
import com.medievallords.carbyne.heartbeat.HeartbeatRunnable;
import com.medievallords.carbyne.kits.KitGuiListeners;
import com.medievallords.carbyne.kits.KitManager;
import com.medievallords.carbyne.leaderboards.LeaderboardListeners;
import com.medievallords.carbyne.leaderboards.LeaderboardManager;
import com.medievallords.carbyne.leaderboards.commands.*;
import com.medievallords.carbyne.listeners.*;
import com.medievallords.carbyne.lootchests.LootChestListeners;
import com.medievallords.carbyne.lootchests.LootChestManager;
import com.medievallords.carbyne.lootchests.commands.LootChestCommand;
import com.medievallords.carbyne.portals.PortalListeners;
import com.medievallords.carbyne.portals.PortalManager;
import com.medievallords.carbyne.portals.commands.PortalCreateCommand;
import com.medievallords.carbyne.portals.commands.PortalEditCommands;
import com.medievallords.carbyne.portals.commands.PortalInfoCommands;
import com.medievallords.carbyne.portals.commands.PortalRemoveCommand;
import com.medievallords.carbyne.prizeeggs.PrizeEggListeners;
import com.medievallords.carbyne.prizeeggs.PrizeEggManager;
import com.medievallords.carbyne.prizeeggs.commands.PrizeEggAddAltarCommand;
import com.medievallords.carbyne.prizeeggs.commands.PrizeEggReloadCommand;
import com.medievallords.carbyne.prizeeggs.commands.PrizeEggRemoveAltarCommand;
import com.medievallords.carbyne.profiles.ProfileListeners;
import com.medievallords.carbyne.profiles.ProfileManager;
import com.medievallords.carbyne.quests.QuestHandler;
import com.medievallords.carbyne.quests.commands.*;
import com.medievallords.carbyne.quests.listeners.QuestListener;
import com.medievallords.carbyne.recipes.RecipeListener;
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
import com.medievallords.carbyne.utils.command.CommandFramework;
import com.medievallords.carbyne.utils.nametag.NametagManager;
import com.medievallords.carbyne.utils.scoreboard.CarbyneScoreboard;
import com.medievallords.carbyne.utils.tabbed.Tabbed;
import com.medievallords.carbyne.worldhandler.WorldHandler;
import com.medievallords.carbyne.worldhandler.WorldListeners;
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

    private File gearFile,
            gatesFile,
            cratesFile,
            arenasFile,
            leaderboardFile,
            lootChestsFile,
            dropPointsFile,
            donatorTownsFile,
            eventsFile,
            rulesFile,
            zonesFile,
            questsFile,
            dungeonsFile,
            portalsFile,
            worldsFile,
            prizeEggFile,
            announcerFile,
            conquerPointFile;

    private FileConfiguration gearFileConfiguration,
            gatesFileConfiguration,
            cratesFileConfiguration,
            arenasFileConfiguration,
            leaderboardFileConfiguration,
            lootChestsFileConfiguration,
            dropPointsFileConfiguration,
            donatorTownsFileConfiguration,
            eventsFileConfiguration,
            rulesFileConfiguration,
            questsFileConfiguration,
            zonesFileConfiguration,
            dungeonsFileConfiguration,
            portalsFileConfiguration,
            worldsFileConfiguration,
            prizeEggFileConfiguration,
            announcerFileConfiguration,
            conquerPointFileConfiguration;

    private Permission permissions = null;

    private CommandFramework commandFramework;

    private HeartbeatRunnable heartbeatRunnable;

    private CarbyneBoardAdapter carbyneBoardAdapter;
    private CarbyneScoreboard carbyneScoreboard;

    private EntityHider entityHider;
    private ItemDb itemDb;
    private Tabbed tabbed;

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
        StaticClasses.profileManager = new ProfileManager();
        StaticClasses.staffManager = new StaffManager();
        StaticClasses.economyManager = new EconomyManager();
        StaticClasses.gearManager = new GearManager();
        StaticClasses.effectManager = new EffectManager(this);
        StaticClasses.gateManager = new GateManager();
        StaticClasses.squadManager = new SquadManager();
        StaticClasses.crateManager = new CrateManager();
        StaticClasses.leaderboardManager = new LeaderboardManager();
        StaticClasses.regenerationHandler = new RegenerationHandler();
        StaticClasses.lootChestManager = new LootChestManager();
        StaticClasses.gamemodeManager = new GamemodeManager();
        StaticClasses.trailManager = new TrailManager();
        StaticClasses.conquerPointManager = new ConquerPointManager();
        StaticClasses.spellMenuManager = new SpellMenuManager();
        StaticClasses.dailyBonusManager = new DailyBonusManager();
        this.tabbed = new Tabbed(this);
        StaticClasses.zoneManager = new ZoneManager();
        StaticClasses.gearListeners = new GearListeners();
        StaticClasses.questHandler = new QuestHandler();
        StaticClasses.dungeonHandler = new DungeonHandler();
        StaticClasses.dungeonQueuer = new DungeonQueuer();
        StaticClasses.portalManager = new PortalManager();
        StaticClasses.worldHandler = new WorldHandler();
        StaticClasses.kitManager = new KitManager();
        StaticClasses.prizeEggManager = new PrizeEggManager();
        StaticClasses.announcerManager = new AnnouncerManager(this);

        carbyneBoardAdapter = new CarbyneBoardAdapter();
        carbyneScoreboard = new CarbyneScoreboard(this, carbyneBoardAdapter);

        commandFramework = new CommandFramework(this);

        registerCommands();
        registerEvents(pm);
        registerPackets();

        CombatTagListeners.ForceFieldTask.run(this);

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
        StaticClasses.dungeonHandler.cancelAll();
        StaticClasses.profileManager.saveProfiles(false);
        Account.saveAccounts(false);
        StaticClasses.gateManager.saveGates();
        StaticClasses.effectManager.dispose();
        StaticClasses.crateManager.save(cratesFileConfiguration);
        StaticClasses.leaderboardManager.stopAllLeaderboardTasks();
        mongoClient.close();
        StaticClasses.staffManager.shutdown();
        StaticClasses.gearManager.getRepairItems().forEach(Entity::remove);
        commandFramework.unregisterAll();
        StaticClasses.kitManager.saveAllKits();
        StaticClasses.conquerPointManager.saveControlPoints();

        clearVillagers();
        StaticClasses.gearListeners.cleanupDamageIndicators();
    }

    public void registerMongoConnection() {
        MongoClientOptions options = MongoClientOptions.builder().connectionsPerHost(200000).build();
        mongoClient = new MongoClient(getConfig().getString("database.host"), options);
        mongoDatabase = mongoClient.getDatabase(getConfig().getString("database.database-name"));
    }

    private void registerEvents(PluginManager pm) {
        pm.registerEvents(new ProfileListeners(), this);
        pm.registerEvents(new CombatTagListeners(), this);
        pm.registerEvents(StaticClasses.gearListeners, this);
        pm.registerEvents(new CooldownListeners(), this);
        pm.registerEvents(new OptimizationListeners(), this);
        pm.registerEvents(new ChatListener(), this);
        pm.registerEvents(new GateListeners(), this);
        pm.registerEvents(new SpawnerListeners(), this);
        pm.registerEvents(new RegenerationListeners(), this);
        pm.registerEvents(new CrateListeners(), this);
        pm.registerEvents(new LeaderboardListeners(), this);
        pm.registerEvents(new LootChestListeners(), this);
        pm.registerEvents(new SetSlotsCommand(), this);
        pm.registerEvents(new SpellMenuListeners(), this);
        pm.registerEvents(new GameModeListener(StaticClasses.gamemodeManager), this);
        pm.registerEvents(new PlayerListeners(), this);
        pm.registerEvents(new FreezeListeners(), this);
        pm.registerEvents(new SpambotListener(), this);
        pm.registerEvents(new StaffModeListeners(), this);
        pm.registerEvents(new PinListeners(), this);
        pm.registerEvents(new VanishListeners(), this);
        pm.registerEvents(new TrailListener(), this);
        pm.registerEvents(new SetDamageCommand(), this);
        pm.registerEvents(new FollowCommand(), this);
        pm.registerEvents(new IgnoreCommand(), this);
        pm.registerEvents(new StaffLogging(), this);
        pm.registerEvents(new IronBoatListener(), this);
        pm.registerEvents(new StaffListeners(), this);
        pm.registerEvents(new DailyBonusListeners(), this);
        pm.registerEvents(new ZoneListeners(), this);
        pm.registerEvents(new QuestListener(), this);
        pm.registerEvents(new GiveRankRewardsCommand(), this);
        pm.registerEvents(new DungeonInstanceListener(), this);
        pm.registerEvents(new TriggerInteractListener(), this);
        pm.registerEvents(new DungeonJoinListeners(), this);
        pm.registerEvents(new PortalListeners(), this);
        pm.registerEvents(new WorldListeners(), this);
        pm.registerEvents(new PrizeEggListeners(), this);
        pm.registerEvents(new KitGuiListeners(), this);
        pm.registerEvents(new ConquerPointEvents(), this);
        pm.registerEvents(new RecipeListener(), this);

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
        new HealCommand();
        new LocalChatCommand();
        new TownChatCommand();
        new NationChatCommand();
        new SetMotdCommand();
        new SetHitDelayCommand();
        new SpellMenuCommand();
        new DepositCommand();
        new DailyBonusCommand();
        new LagCommand();
        new GiveRankRewardsCommand();
        new KitCommand();
        new SetKitCommand();
        new DeleteKitCommand();
        new KitPointsCommand();
        new BlockMaskCommand();
        new ConquerPointCommand();

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
//        new ArenaCommand();
//        new ArenaCreateCommand();
//        new ArenaRemoveCommand();
//        new ArenaListCommand();
//        new ArenaSetLobbyCommand();
//        new ArenaAddSpawnCommand();
//        new ArenaAddPedastoolCommand();
//        new ArenaRemoveSpawnCommand();
//        new ArenaRemovePedastoolCommand();
//        new ArenaReloadCommand();
//
//        //Duel Commands
//        new DuelAcceptCommand();
//        new DuelSetSquadFightCommand();
//        new DuelCommand();
//        new DuelBetCommand();
//        new DuelDeclineCommand();

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

        //Spell Mods Commands
        new SpellMenuCommand();

        //Gamemode Commands
        new FlyCommand();
        new GamemodeCommand();

        new TrailCommand();
        new RulesCommand();

        new ZoneCommands();
        new QuestClaimCommand();
        new QuestReloadCommand();
        new QuestCommand();
        new QuestInfoCommand();
        new QuestForceCommand();

        new DungeonCreateCommand();
        new DungeonSetCommand();
        new DungeonEditCommand();
        new DungeonLeaveCommand();
        new DungeonJoinCommand();
        new MechanicCreateCommand();
        new MechanicRemoveCommand();
        new TriggerCreateCommand();
        new TriggerRemoveCommand();
        new DungeonAddMobCommand();
        new DungeonRemoveMobCommand();
        new DungeonSetAmountMobCommand();

        new PortalCreateCommand();
        new PortalEditCommands();
        new PortalRemoveCommand();
        new PortalInfoCommands();

        new ReloadMainConfigCommand();
        new SellCarbyneCommand();
        new AnnouncerReloadCommand();

        new PrizeEggReloadCommand();
        new PrizeEggAddAltarCommand();
        new PrizeEggRemoveAltarCommand();

        new GearForgeCommand();
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

                    for (ItemStack item : player.getInventory().getContents()) {
                        StaticClasses.gearManager.updateDamage(item);
                        StaticClasses.gearManager.updateHealth(item);
                    }

                    for (ItemStack item : player.getInventory().getArmorContents())
                        StaticClasses.gearManager.updateHealth(item);
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
        saveResource("gates.yml", false);
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
        saveResource("quests.yml", false);
        saveResource("dungeons.yml", false);
        saveResource("portals.yml", false);
        saveResource("worlds.yml", false);
        saveResource("prizeEgg.yml", false);
        saveResource("announcements.yml", false);
        saveResource("conquerpoints.yml", false);

        this.dungeonsFile = new File(getDataFolder(), "dungeons.yml");
        this.dungeonsFileConfiguration = YamlConfiguration.loadConfiguration(dungeonsFile);

        this.portalsFile = new File(getDataFolder(), "portals.yml");
        this.portalsFileConfiguration = YamlConfiguration.loadConfiguration(portalsFile);

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
        donatorTownsFileConfiguration = YamlConfiguration.loadConfiguration(donatorTownsFile);

        eventsFile = new File(getDataFolder(), "events.yml");
        eventsFileConfiguration = YamlConfiguration.loadConfiguration(eventsFile);

        rulesFile = new File(getDataFolder(), "rules.yml");
        rulesFileConfiguration = YamlConfiguration.loadConfiguration(rulesFile);

        zonesFile = new File(getDataFolder(), "zones.yml");
        zonesFileConfiguration = YamlConfiguration.loadConfiguration(zonesFile);

        questsFile = new File(getDataFolder(), "quests.yml");
        questsFileConfiguration = YamlConfiguration.loadConfiguration(questsFile);

        worldsFile = new File(getDataFolder(), "worlds.yml");
        worldsFileConfiguration = YamlConfiguration.loadConfiguration(worldsFile);

        prizeEggFile = new File(getDataFolder(), "prizeEgg.yml");
        prizeEggFileConfiguration = YamlConfiguration.loadConfiguration(prizeEggFile);

        announcerFile = new File(getDataFolder(), "announcements.yml");
        announcerFileConfiguration = YamlConfiguration.loadConfiguration(announcerFile);

        conquerPointFile = new File(getDataFolder(), "conquerpoints.yml");
        conquerPointFileConfiguration = YamlConfiguration.loadConfiguration(conquerPointFile);

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
        if (!(plugin instanceof WorldGuardPlugin))
            return null; // Maybe you want throw an exception instead

        return (WorldGuardPlugin) plugin;
    }
}
