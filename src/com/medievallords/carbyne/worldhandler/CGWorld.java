package com.medievallords.carbyne.worldhandler;

import com.medievallords.carbyne.Carbyne;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.World;

import java.util.*;
import java.util.logging.Level;

@Getter
@Setter
public class CGWorld {

    private String name;
    private boolean pvp, loaded, whitelisted, keepSpawnInMemory;
    private List<String> disabledCommands = new ArrayList<>();
    private Set<UUID> whitelistedPlayers = new HashSet<>();

    public CGWorld(String name) {
        this.name = name;
    }

    public void updateSettings() {
        World world = Carbyne.getInstance().getServer().getWorld(name);
        if (world == null) {
            Carbyne.getInstance().getServer().getLogger().log(Level.WARNING, "Could not update settings for world " + name);
            return;
        }

        world.setPVP(pvp);
        world.setKeepSpawnInMemory(keepSpawnInMemory);
    }
}
