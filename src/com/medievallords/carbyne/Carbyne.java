package com.medievallords.carbyne;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.medievallords.carbyne.gates.GateManager;
import com.medievallords.carbyne.gates.commands.*;
import com.medievallords.carbyne.gates.listeners.GateListeners;
import com.medievallords.carbyne.gear.GearManager;
import com.medievallords.carbyne.gear.GuiManager;
import com.medievallords.carbyne.gear.commands.GearCommands;
import com.medievallords.carbyne.gear.listeners.CarbyneListener;
import com.medievallords.carbyne.gear.listeners.GuiListener;
import com.medievallords.carbyne.leaderboards.LeaderboardManager;
import com.medievallords.carbyne.listeners.*;
import com.medievallords.carbyne.scoreboard.ScoreboardCommands;
import com.medievallords.carbyne.scoreboard.ScoreboardHandler;
import com.medievallords.carbyne.utils.PlayerUtility;
import com.medievallords.carbyne.utils.command.CommandFramework;
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
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

@Getter
@Setter
public class Carbyne extends JavaPlugin {

    public static Carbyne instance;

    private File gearconfigFile;
    private File storeFile;
    private File duelFile;
    private File gateFile;

    private FileConfiguration gearData;
    private FileConfiguration storeData;
    private FileConfiguration gateData;

    private Permission permissions = null;
    private Economy economy = null;

    private Towny towny;
    private boolean townyEnabled = false;

    private CombatTagPlus combatTagPlus;
    private boolean combatTagPlusEnabled = false;

    private CommandFramework commandFramework;

    private GearManager gearManager;
    private EffectManager effectManager;
    private GuiManager guiManager;
    private ScoreboardHandler scoreboardHandler;
    private GateManager gateManager;
    private LeaderboardManager leaderboardManager;

    public void onEnable() {
        instance = this;
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

        gearconfigFile = new File(getDataFolder(), "gearconfig.yml");
        storeFile = new File(getDataFolder(), "store.yml");
        duelFile = new File(getDataFolder(), "duel.yml");
        gateFile = new File(getDataFolder(), "gate.yml");
        try {
            firstRun();
        } catch (Exception e) {
            e.printStackTrace();
        }
        gearData = YamlConfiguration.loadConfiguration(gearconfigFile);
        storeData = YamlConfiguration.loadConfiguration(storeFile);
        gateData = YamlConfiguration.loadConfiguration(gateFile);

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

        if (townyEnabled)
            pm.registerEvents(new DamageListener(), this);
    }

    private void registerCommands() {
        //Old Commands
        getCommand("cg").setExecutor(new GearCommands());
        getCommand("scoreboard").setExecutor(new ScoreboardCommands());

        //New Commands
        commandFramework.registerCommands(new GateCommand());
        commandFramework.registerCommands(new GateAddBCommand());
        commandFramework.registerCommands(new GateAddPPCommand());
        commandFramework.registerCommands(new GateAddBCommand());
        commandFramework.registerCommands(new GateCreateCommand());
        commandFramework.registerCommands(new GateDelayCommand());
        commandFramework.registerCommands(new GateRemoveCommand());
        commandFramework.registerCommands(new GateRenameCommand());
        commandFramework.registerCommands(new GateResetCommand());
        commandFramework.registerCommands(new GateStatusCommand());
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

    private void firstRun() throws Exception {
        if (!gearconfigFile.exists()) {
            gearconfigFile.getParentFile().mkdirs();
            copy(getResource("gearconfig.yml"), gearconfigFile);
        }
        if (!storeFile.exists()) {
            storeFile.getParentFile().mkdirs();
            copy(getResource("store.yml"), storeFile);
        }
        if (!duelFile.exists()) {
            duelFile.getParentFile().mkdirs();
            copy(getResource("duel.yml"), duelFile);
        }
        if (!gateFile.exists()) {
            gateFile.getParentFile().mkdirs();
            copy(getResource("gate.yml"), gateFile);
        }
    }

    private void copy(InputStream in, File file) {
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.flush();
            out.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Carbyne getInstance() {
        return instance;
    }
}
