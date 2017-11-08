package com.medievallords.carbyne;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.medievallords.carbyne.gates.GateManager;
import com.medievallords.carbyne.gates.commands.*;
import com.medievallords.carbyne.gates.listeners.GateListeners;
import com.medievallords.carbyne.gates.listeners.GateMobListeners;
import com.medievallords.carbyne.gear.GearManager;
import com.medievallords.carbyne.gear.GuiManager;
import com.medievallords.carbyne.gear.commands.GearCommands;
import com.medievallords.carbyne.gear.listeners.CarbyneListener;
import com.medievallords.carbyne.gear.listeners.GuiListener;
import com.medievallords.carbyne.heartbeat.HeartbeatRunnable;
import com.medievallords.carbyne.leaderboards.LeaderboardManager;
import com.medievallords.carbyne.listeners.*;
import com.medievallords.carbyne.scoreboard.ScoreboardHandler;
import com.medievallords.carbyne.utils.PlayerUtility;
import com.medievallords.carbyne.utils.command.CommandFramework;
import com.palmergames.bukkit.towny.Towny;
import de.slikey.effectlib.EffectManager;
import lombok.Getter;
import lombok.Setter;
import net.elseland.xikage.MythicMobs.MythicMobs;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import net.minelink.ctplus.CombatTagPlus;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

@Getter
@Setter
public class Carbyne extends JavaPlugin {

    public static Carbyne instance;

    private File gearFile;
    private FileConfiguration gearFileConfiguration;

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

    private GearManager gearManager;
    private EffectManager effectManager;
    private GuiManager guiManager;
    private ScoreboardHandler scoreboardHandler;
    private GateManager gateManager;
    private LeaderboardManager leaderboardManager;

    public void onEnable() {
        instance = this;

//        getConfig().options().copyDefaults(true);
//        saveDefaultConfig();

        registerConfigurations();

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

        gearManager = new GearManager();
        guiManager = new GuiManager();
        effectManager = new EffectManager(this);
        scoreboardHandler = new ScoreboardHandler();
        gateManager = new GateManager();
        leaderboardManager = new LeaderboardManager();

        registerCommands();
        registerEvents(pm);
        registerPackets();
    }

    public void onDisable() {
        gateManager.saveGates();
        effectManager.dispose();
        HandlerList.unregisterAll(this);
    }

    private void registerEvents(PluginManager pm) {
        pm.registerEvents(new CarbyneListener(), this);
        pm.registerEvents(new CooldownListeners(), this);
        pm.registerEvents(new OptimizationListeners(), this);
        pm.registerEvents(new GuiListener(), this);
        pm.registerEvents(new TimingsFixListener(this), this);
        pm.registerEvents(new ChatListener(), this);
        pm.registerEvents(new GateListeners(), this);

        if (mythicMobsEnabled)
            pm.registerEvents(new GateMobListeners(), this);

        if (townyEnabled)
            pm.registerEvents(new DamageListener(), this);
    }

    private void registerCommands() {
        commandFramework.registerCommands(new GearCommands());
        commandFramework.registerCommands(new GateCommand());
        commandFramework.registerCommands(new GateAddBCommand());
        commandFramework.registerCommands(new GateAddPPCommand());
        commandFramework.registerCommands(new GateAddRSBCommand());
        commandFramework.registerCommands(new GateAddEntCommand());
        commandFramework.registerCommands(new GateDelBCommand());
        commandFramework.registerCommands(new GateDelPPCommand());
        commandFramework.registerCommands(new GateDelRSBCommand());
        commandFramework.registerCommands(new GateCreateCommand());
        commandFramework.registerCommands(new GateActiveCommand());
        commandFramework.registerCommands(new GateRemoveCommand());
        commandFramework.registerCommands(new GateRenameCommand());
        commandFramework.registerCommands(new GateStatusCommand());
        commandFramework.registerCommands(new GateListCommand());
    }

    private void registerPackets() {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this, PacketType.Play.Client.CLIENT_COMMAND) {
            @Override
            public void onPacketReceiving(PacketEvent event){
                if(event.getPacket().getClientCommands().read(0) == EnumWrappers.ClientCommand.OPEN_INVENTORY_ACHIEVEMENT) {
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

        gearFile = new File(getDataFolder(), "gear.yml");
        gearFileConfiguration = YamlConfiguration.loadConfiguration(gearFile);

        duelFile = new File(getDataFolder(), "duel.yml");
        duelFileConfiguration = YamlConfiguration.loadConfiguration(duelFile);

        gateFile = new File(getDataFolder(), "gates.yml");
        gateFileConfiguration = YamlConfiguration.loadConfiguration(gateFile);
    }

    public static Carbyne getInstance() {
        return instance;
    }
}
