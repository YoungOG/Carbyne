package com.medievallords.carbyne;

import com.bizarrealex.aether.Aether;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.medievallords.carbyne.commands.*;
import com.medievallords.carbyne.crates.CrateManager;
import com.medievallords.carbyne.crates.commands.*;
import com.medievallords.carbyne.crates.listeners.CrateListeners;
import com.medievallords.carbyne.duels.arena.commands.*;
import com.medievallords.carbyne.duels.arena.listeners.ArenaListeners;
import com.medievallords.carbyne.duels.duel.DuelListeners;
import com.medievallords.carbyne.duels.duel.DuelManager;
import com.medievallords.carbyne.economy.MarketManager;
import com.medievallords.carbyne.economy.account.Account;
import com.medievallords.carbyne.economy.commands.administrator.MarketSetTaxCommand;
import com.medievallords.carbyne.economy.commands.player.*;
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
import com.medievallords.carbyne.profiles.ProfileListeners;
import com.medievallords.carbyne.profiles.ProfileManager;
import com.medievallords.carbyne.regeneration.RegenerationHandler;
import com.medievallords.carbyne.regeneration.RegenerationListeners;
import com.medievallords.carbyne.regeneration.commands.RegenerationBypassCommand;
import com.medievallords.carbyne.spawners.commands.SpawnerCommand;
import com.medievallords.carbyne.spawners.commands.SpawnerCreateCommand;
import com.medievallords.carbyne.spawners.listeners.SpawnerListeners;
import com.medievallords.carbyne.squads.SquadManager;
import com.medievallords.carbyne.squads.commands.*;
import com.medievallords.carbyne.utils.CarbyneBoardAdapter;
import com.medievallords.carbyne.utils.ItemDb;
import com.medievallords.carbyne.utils.Lang;
import com.medievallords.carbyne.utils.PlayerUtility;
import com.medievallords.carbyne.utils.command.CommandFramework;
import com.medievallords.carbyne.utils.nametag.NametagManager;
import com.medievallords.carbyne.utils.signgui.SignGUI;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.client.MongoDatabase;
import com.palmergames.bukkit.towny.Towny;
import de.slikey.effectlib.EffectManager;
import lombok.Getter;
import lombok.Setter;
import net.milkbowl.vault.permission.Permission;
import net.minelink.ctplus.CombatTagPlus;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
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

    private CombatTagPlus combatTagPlus;
    private boolean combatTagPlusEnabled = false;

    private boolean mythicMobsEnabled = false;

    private File gearFile;
    private FileConfiguration gearFileConfiguration;
    private File duelFile;
    private FileConfiguration duelFileConfiguration;
    private File gateFile;
    private FileConfiguration gateFileConfiguration;
    private File crateFile;
    private FileConfiguration crateFileConfiguration;
    private File arenaFile;
    private FileConfiguration arenaFileConfiguration;
    private File leaderboardFile;
    private FileConfiguration leaderboardFileConfiguration;

    private Permission permissions = null;

    private CommandFramework commandFramework;

    private HeartbeatRunnable heartbeatRunnable;

    private Aether aether;
    private SignGUI signGUI;

    private ProfileManager profileManager;
    private RegenerationHandler regenerationHandler;
    private MarketManager marketManager;
    private GearManager gearManager;
    private EffectManager effectManager;
    private GateManager gateManager;
    private SquadManager squadManager;
    private CrateManager crateManager;
    private DuelManager duelManager;
    private LeaderboardManager leaderboardManager;
    private ItemDb itemDb;

    public void onEnable() {
        instance = this;

        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        registerConfigurations();

        registerMongoConnection();

        PluginManager pm = Bukkit.getServer().getPluginManager();

        commandFramework = new CommandFramework(this);

        setupPermissions();

        if (pm.isPluginEnabled("Towny")) {
            towny = (Towny) pm.getPlugin("Towny");
            townyEnabled = true;
        }

        if (pm.isPluginEnabled("CombatTagPlus")) {
            combatTagPlus = (CombatTagPlus) pm.getPlugin("CombatTagPlus");
            combatTagPlusEnabled = true;
        }

        if (pm.isPluginEnabled("MythicMobs")) {
            mythicMobsEnabled = true;
        }

        heartbeatRunnable = new HeartbeatRunnable();
        heartbeatRunnable.runTaskTimer(Carbyne.getInstance(), 0L, 2L);

        itemDb = new ItemDb();

        for (Player all : PlayerUtility.getOnlinePlayers()) {
            all.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        }

        profileManager = new ProfileManager();
        regenerationHandler = new RegenerationHandler();
        marketManager = new MarketManager();
        gearManager = new GearManager();
        effectManager = new EffectManager(this);
        gateManager = new GateManager();
        squadManager = new SquadManager();
        crateManager = new CrateManager();
        duelManager = new DuelManager();
        leaderboardManager = new LeaderboardManager();

        aether = new Aether(this, new CarbyneBoardAdapter(this));
        signGUI = new SignGUI();

        registerCommands();
        registerEvents(pm);
        registerPackets();

        CombatTagListeners.ForceFieldTask.run(this);

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : PlayerUtility.getOnlinePlayers()) {
                    NametagManager.updateNametag(player);
                }
            }
        }.runTaskTimerAsynchronously(this, 0L, 10L);

        clearVillagers();
    }

    public void onDisable() {
        profileManager.saveProfiles(false);
        Account.saveAccounts(false);
        marketManager.saveSales(false);
        gateManager.saveGates();
        effectManager.dispose();
        regenerationHandler.saveTasks();
        crateManager.save(crateFileConfiguration);
        leaderboardManager.stopAllLeaderboardTasks();
        duelManager.cancelAll();
        mongoClient.close();

        clearVillagers();
    }

    public void registerMongoConnection() {
        MongoClientOptions options = MongoClientOptions.builder().connectionsPerHost(200000).build();
        mongoClient = new MongoClient(getConfig().getString("database.host"), options);
        mongoDatabase = mongoClient.getDatabase(getConfig().getString("database.database-name"));
    }

    private void registerEvents(PluginManager pm) {
        pm.registerEvents(new ProfileListeners(), this);
        pm.registerEvents(new CombatTagListeners(), this);
        pm.registerEvents(new GearListeners(), this);
        pm.registerEvents(new CooldownListeners(), this);
        pm.registerEvents(new OptimizationListeners(), this);
        pm.registerEvents(new ChatListener(), this);
        pm.registerEvents(new GateListeners(), this);
        pm.registerEvents(new LootListener(), this);
        pm.registerEvents(new SpawnerListeners(), this);
        pm.registerEvents(new RegenerationListeners(), this);
        pm.registerEvents(new CrateListeners(), this);
        pm.registerEvents(new ArenaListeners(), this);
        pm.registerEvents(new DuelListeners(), this);
        pm.registerEvents(new LeaderboardListeners(), this);

        if (mythicMobsEnabled)
            pm.registerEvents(new GateMobListeners(), this);

        if (townyEnabled)
            pm.registerEvents(new DamageListener(), this);
    }

    private void registerCommands() {
        //General Commands
        new BlackholeCommand();
        new StatsCommand();
        new ChatCommand();
        new ToggleCommand();
        new RegenerationBypassCommand();

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

        //Market Commands
        new BalanceCommand();
        new MarketBuyCommand();
        new MarketSellCommand();
        new MarketPriceCommand();
        new MarketSalesCommand();
        new MarketSetTaxCommand();
        new MarketTaxCommand();

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

        //Duel Commands
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
    }

    private void registerPackets() {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this, PacketType.Play.Client.CLIENT_COMMAND) {
            @Override
            public void onPacketReceiving(PacketEvent event){
                if (event.getPacket().getClientCommands().read(0) == EnumWrappers.ClientCommand.OPEN_INVENTORY_ACHIEVEMENT) {
                    Player player = event.getPlayer();

                    PlayerUtility.checkForIllegalItems(player, player.getInventory());

                    if (player.getGameMode() == GameMode.CREATIVE) {
                        player.getInventory().setArmorContents(new ItemStack[] {null, null, null, null});
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
        saveResource("gear.yml", false);
        saveResource("duel.yml", false);
        saveResource("gates.yml", false);
        saveResource("item.csv", false);
        saveResource("crates.yml", false);
        saveResource("arenas.yml", false);
        saveResource("leaderboards.yml", false);
        saveResource("lang.yml", false);

        gearFile = new File(getDataFolder(), "gear.yml");
        gearFileConfiguration = YamlConfiguration.loadConfiguration(gearFile);

        duelFile = new File(getDataFolder(), "duel.yml");
        duelFileConfiguration = YamlConfiguration.loadConfiguration(duelFile);

        gateFile = new File(getDataFolder(), "gates.yml");
        gateFileConfiguration = YamlConfiguration.loadConfiguration(gateFile);

        crateFile = new File(getDataFolder(), "crates.yml");
        crateFileConfiguration = YamlConfiguration.loadConfiguration(crateFile);

        arenaFile = new File(getDataFolder(), "arenas.yml");
        arenaFileConfiguration = YamlConfiguration.loadConfiguration(arenaFile);

        leaderboardFile = new File(getDataFolder(), "leaderboards.yml");
        leaderboardFileConfiguration = YamlConfiguration.loadConfiguration(leaderboardFile);

        File langFile = new File(getDataFolder(), "lang.yml");
        FileConfiguration langFileConfiguration = YamlConfiguration.loadConfiguration(langFile);

        for (Lang item : Lang.values()) {
            if (langFileConfiguration.getString(item.getPath()) == null) {
                if (item.getAllMessages().length > 1) {
                    langFileConfiguration.set(item.getPath(), item.getAllMessages());
                } else {
                    langFileConfiguration.set(item.getPath(), item.getFirstMessage());
                }
            }
        }

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
        for (World w : Bukkit.getWorlds()) {
            w.getEntities().stream().filter(ent -> ent instanceof Villager).forEach(ent -> {
                Villager villager = (Villager) ent;

                if (villager.getMetadata("logger") != null && villager.hasMetadata("logger")) {
                    villager.remove();
                }
            });
        }
    }

    public static Carbyne getInstance() {
        return instance;
    }
}
