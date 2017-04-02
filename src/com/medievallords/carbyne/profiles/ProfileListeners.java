package com.medievallords.carbyne.profiles;

import com.medievallords.carbyne.Carbyne;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Created by Calvin on 3/22/2017
 * for the Carbyne project.
 */
public class ProfileListeners implements Listener {

    private Carbyne main = Carbyne.getInstance();

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
        Player player = event.getPlayer();

        if (Profile.getProfile(player.getUniqueId()) == null) {
            Profile profile = new Profile(player.getUniqueId());

            try {
                profile.load();
            } catch (Exception e) {

            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        Player player = event.getPlayer();


    }
}
