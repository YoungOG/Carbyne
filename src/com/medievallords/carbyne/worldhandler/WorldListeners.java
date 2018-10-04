package com.medievallords.carbyne.worldhandler;

import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.StaticClasses;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerTeleportEvent;


public class WorldListeners implements Listener {

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        CGWorld CGWorld = StaticClasses.worldHandler.getWorld(event.getPlayer().getWorld().getName());
        if (CGWorld == null) {
            return;
        }

        String command = event.getMessage().replace("/", "");
        if (CGWorld.getDisabledCommands().contains(command)) {
            event.setCancelled(true);
            MessageManager.sendMessage(event.getPlayer(), "&cYou may not use that command here.");
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event) {
        if (event.getFrom().getWorld().equals(event.getTo().getWorld())) {
            return;
        }

        CGWorld toCGWorld = StaticClasses.worldHandler.getWorld(event.getTo().getWorld().getName());
        if (toCGWorld == null) {
            return;
        }

        if (!toCGWorld.isWhitelisted()) {
            return;
        }
        if (!toCGWorld.getWhitelistedPlayers().contains(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            MessageManager.sendMessage(event.getPlayer(), "&cYou may not teleport to this world.");
        }
    }
}
