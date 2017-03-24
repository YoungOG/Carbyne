package com.medievallords.carbyne;

import com.bizarrealex.aether.Aether;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.medievallords.carbyne.commands.BlackholeCommand;
import com.medievallords.carbyne.economy.MarketManager;
import com.medievallords.carbyne.economy.commands.*;
import com.medievallords.carbyne.gates.GateManager;
import com.medievallords.carbyne.gates.commands.*;
import com.medievallords.carbyne.gates.listeners.GateListeners;
import com.medievallords.carbyne.gates.listeners.GateMobListeners;
import com.medievallords.carbyne.gear.GearManager;
import com.medievallords.carbyne.gear.commands.GearCommands;
import com.medievallords.carbyne.gear.commands.GearGiveCommand;
import com.medievallords.carbyne.gear.listeners.GearListeners;
import com.medievallords.carbyne.heartbeat.HeartbeatRunnable;
import com.medievallords.carbyne.leaderboards.LeaderboardManager;
import com.medievallords.carbyne.listeners.*;
import com.medievallords.carbyne.profiles.ProfileListeners;
import com.medievallords.carbyne.regeneration.RegenerationHandler;
import com.medievallords.carbyne.regeneration.RegenerationListeners;
import com.medievallords.carbyne.spawners.commands.SpawnerCommand;
import com.medievallords.carbyne.spawners.commands.SpawnerCreateCommand;
import com.medievallords.carbyne.spawners.listeners.SpawnerListeners;
import com.medievallords.carbyne.squads.SquadManager;
import com.medievallords.carbyne.squads.commands.*;
import com.medievallords.carbyne.utils.CarbyneBoardAdapter;
import com.medievallords.carbyne.utils.ItemDb;
import com.medievallords.carbyne.utils.PlayerUtility;
import com.medievallords.carbyne.utils.command.CommandFramework;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.client.MongoDatabase;
import com.palmergames.bukkit.towny.Towny;
import de.slikey.effectlib.EffectManager;
import lombok.Getter;
import lombok.Setter;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import net.minelink.ctplus.CombatTagPlus;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

@Getter
@Setter
public class Carbyne extends JavaPlugin {

    public static Carbyne instance;

    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;

    private File gearFile;
    private FileConfiguration gearFileConfiguration;

    private File dummyFile;
    private FileConfiguration dummyFileConfiguration;

    private File controlPointFile;
    private FileConfiguration controlPointConfiguration;

    private File duelFile;
    private FileConfiguration duelFileConfiguration;

    private File gateFile;
    private FileConfiguration gateFileConfiguration;

    private CommandFramework commandFramework;

    private Permission permissions = null;
    private Economy economy = null;

    private Towny towny;
    private boolean townyEnabled = false;

    private CombatTagPlus combatTagPlus;
    private boolean combatTagPlusEnabled = false;

    private boolean mythicMobsEnabled = false;

    private HeartbeatRunnable heartbeatRunnable;

    private Aether aether;

    private RegenerationHandler regenerationHandler;
    private MarketManager marketManager;
    private GearManager gearManager;
    private EffectManager effectManager;
    private GateManager gateManager;
    private LeaderboardManager leaderboardManager;
    private SquadManager squadManager;
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
        setupEconomy();

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

        regenerationHandler = new RegenerationHandler();
        marketManager = new MarketManager();
        gearManager = new GearManager();
        effectManager = new EffectManager(this);
        gateManager = new GateManager();
        leaderboardManager = new LeaderboardManager();
        squadManager = new SquadManager();

        aether = new Aether(this, new CarbyneBoardAdapter(this));

        registerCommands();
        registerEvents(pm);
        registerPackets();
    }

    public void onDisable() {
        marketManager.saveSales(false);
        gateManager.saveGates();
        effectManager.dispose();
        mongoClient.close();
    }

    public void registerMongoConnection() {
        MongoClientOptions options = MongoClientOptions.builder().connectionsPerHost(200000).build();
        mongoClient = new MongoClient(getConfig().getString("database.host"), options);
        mongoDatabase = mongoClient.getDatabase(getConfig().getString("database.database-name"));
    }

    private void registerEvents(PluginManager pm) {
        pm.registerEvents(new GearListeners(), this);
        pm.registerEvents(new CooldownListeners(), this);
        pm.registerEvents(new OptimizationListeners(), this);
//        pm.registerEvents(new TimingsFixListener(this), this);
        pm.registerEvents(new ChatListener(), this);
        pm.registerEvents(new GateListeners(), this);
        pm.registerEvents(new LootListener(), this);
        pm.registerEvents(new SpawnerListeners(), this);
        pm.registerEvents(new ProfileListeners(), this);
        pm.registerEvents(new RegenerationListeners(), this);

        if (mythicMobsEnabled)
            pm.registerEvents(new GateMobListeners(), this);

        if (townyEnabled)
            pm.registerEvents(new DamageListener(), this);
    }

    private void registerCommands() {
        //General Commands
        commandFramework.registerCommands(new BlackholeCommand());

        //Gate Commands
        commandFramework.registerCommands(new GearCommands());
        commandFramework.registerCommands(new GearGiveCommand());
        commandFramework.registerCommands(new GateCommand());
        commandFramework.registerCommands(new GateAddBCommand());
        commandFramework.registerCommands(new GateAddPPCommand());
        commandFramework.registerCommands(new GateAddRSBCommand());
        commandFramework.registerCommands(new GateAddSpawnerCommand());
        commandFramework.registerCommands(new GateDelBCommand());
        commandFramework.registerCommands(new GateDelPPCommand());
        commandFramework.registerCommands(new GateDelRSBCommand());
        commandFramework.registerCommands(new GateDelSpawnerCommand());
        commandFramework.registerCommands(new GateCreateCommand());
        commandFramework.registerCommands(new GateActiveCommand());
        commandFramework.registerCommands(new GateRemoveCommand());
        commandFramework.registerCommands(new GateRenameCommand());
        commandFramework.registerCommands(new GateStatusCommand());
        commandFramework.registerCommands(new GateListCommand());

        //Market Commands
        commandFramework.registerCommands(new MarketBuyCommand());
        commandFramework.registerCommands(new MarketSellCommand());
        commandFramework.registerCommands(new MarketPriceCommand());
        commandFramework.registerCommands(new MarketSalesCommand());
        commandFramework.registerCommands(new MarketSetTaxCommand());
        commandFramework.registerCommands(new MarketTaxCommand());

        //Squad Commands
        commandFramework.registerCommands(new SquadCommand());
        commandFramework.registerCommands(new SquadJoinCommand());
        commandFramework.registerCommands(new SquadCreateCommand());
        commandFramework.registerCommands(new SquadInviteCommand());
        commandFramework.registerCommands(new SquadLeaveCommand());
        commandFramework.registerCommands(new SquadDisbandCommand());
        commandFramework.registerCommands(new SquadFriendlyFireCommand());
        commandFramework.registerCommands(new SquadSetCommand());
        commandFramework.registerCommands(new SquadKickCommand());
        commandFramework.registerCommands(new SquadChatCommand());
        commandFramework.registerCommands(new SquadListCommand());

        //ControlPoint Commands
//        commandFramework.registerCommands(new ControlListCommand());
//        commandFramework.registerCommands(new ControlCommand());
//        commandFramework.registerCommands(new ControlReloadCommand());
//        commandFramework.registerCommands(new ControlCreateCommand());
//        commandFramework.registerCommands(new ControlRemoveCommand());
//        commandFramework.registerCommands(new ControlTimerCommand());
//        commandFramework.registerCommands(new ControlTeleportCommand());

        //Spawner Commands
        commandFramework.registerCommands(new SpawnerCreateCommand());
        commandFramework.registerCommands(new SpawnerCommand());
    }

    private void registerPackets() {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this, PacketType.Play.Client.CLIENT_COMMAND) {
            @Override
            public void onPacketReceiving(PacketEvent event){
                if (event.getPacket().getClientCommands().read(0) == EnumWrappers.ClientCommand.OPEN_INVENTORY_ACHIEVEMENT) {
                    Player player = event.getPlayer();

                    PlayerUtility.checkForIllegalItems(player, player.getInventory());
                }
            }
        });
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        permissions = rsp.getProvider();
        return permissions != null;
    }

    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        economy = rsp.getProvider();
        return economy != null;
    }

    public void registerConfigurations() {
        saveResource("gear.yml", false);
        saveResource("duel.yml", false);
        saveResource("gates.yml", false);
        saveResource("item.csv", false);
        saveResource("controlpoints.yml", false);
        saveResource("dummies.yml", false);

        gearFile = new File(getDataFolder(), "gear.yml");
        gearFileConfiguration = YamlConfiguration.loadConfiguration(gearFile);

        duelFile = new File(getDataFolder(), "duel.yml");
        duelFileConfiguration = YamlConfiguration.loadConfiguration(duelFile);

        gateFile = new File(getDataFolder(), "gates.yml");
        gateFileConfiguration = YamlConfiguration.loadConfiguration(gateFile);

        controlPointFile = new File(getDataFolder(), "controlpoints.yml");
        controlPointConfiguration = YamlConfiguration.loadConfiguration(controlPointFile);

        dummyFile = new File(getDataFolder(), "dummies.yml");
        dummyFileConfiguration = YamlConfiguration.loadConfiguration(dummyFile);
    }

    public static Carbyne getInstance() {
        return instance;
    }
}
