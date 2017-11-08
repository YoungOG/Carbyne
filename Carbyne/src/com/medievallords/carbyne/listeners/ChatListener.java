package com.medievallords.carbyne.listeners;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.utils.JSONMessage;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import ru.tehkode.permissions.bukkit.PermissionsEx;

/**
 * Created by Calvin on 1/9/2017
 * for the Carbyne-Gear project.
 */
public class ChatListener implements Listener {

    private Carbyne carbyne = Carbyne.getInstance();

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        String message = e.getMessage();

        String prefix = PermissionsEx.getUser(e.getPlayer()).getGroups()[0].getPrefix();

        JSONMessage.create(ChatColor.translateAlternateColorCodes('&', prefix + player.getName() + "&f: " + message)).tooltip("Primary Group: " + carbyne.getPermissions().getPrimaryGroup(e.getPlayer())).send(e.getPlayer());
    }
}
