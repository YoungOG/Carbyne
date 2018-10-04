package com.medievallords.carbyne.worldhandler;

import com.medievallords.carbyne.Carbyne;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

public class WorldHandler {

    private final Set<CGWorld> worlds = new HashSet<>();

    public WorldHandler() {
        load();
    }

    public void load() {
        worlds.clear();

        ConfigurationSection cs = Carbyne.getInstance().getWorldsFileConfiguration();

        for (String key : cs.getKeys(false)) {
            boolean pvp = false, loaded = true, whitelisted = false;
            Set<UUID> whitelistedPlayers = new HashSet<>();
            List<String> disabledCommands = new ArrayList<>();

            if (cs.contains(key + ".Whitelisted")) {
                whitelisted = cs.getBoolean(key + ".Whitelisted");
            }

            if (cs.contains(key + ".PVP")) {
                pvp = cs.getBoolean(key + ".PVP");
            }

            if (cs.contains(key + ".Loaded")) {
                loaded = cs.getBoolean(key + ".Loaded");
            }

            if (cs.contains(key + ".WhitelistedPlayers")) {
                for (String uuidString : cs.getStringList(key + ".WhitelistedPlayers")) {
                    whitelistedPlayers.add(UUID.fromString(uuidString));
                }
            }

            if (cs.contains(key + ".DisabledCommands")) {
                disabledCommands = cs.getStringList(key + ".DisabledCommands");
            }

            CGWorld world = new CGWorld(key);
            world.setLoaded(loaded);
            world.setPvp(pvp);
            world.setWhitelisted(whitelisted);
            world.setDisabledCommands(disabledCommands);
            world.setWhitelistedPlayers(whitelistedPlayers);

            world.updateSettings();

            worlds.add(world);
        }
    }

    public CGWorld getWorld(String name) {
        for (CGWorld world : worlds) {
            if (world.getName().equalsIgnoreCase(name)) {
                return world;
            }
        }

        return null;
    }
}
