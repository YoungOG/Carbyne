package com.medievallords.carbyne.profiles;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.economy.account.Account;
import com.medievallords.carbyne.utils.Cooldowns;
import com.medievallords.carbyne.utils.nametag.NametagManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
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
        Player player = event.getPlayer();

        if (player == null) {
            return;
        }

        if (!main.getProfileManager().hasProfile(player.getUniqueId())) {
            main.getProfileManager().createProfile(player);
        } else {
            Profile profile = main.getProfileManager().getProfile(player.getUniqueId());
            profile.setUsername(player.getName());
        }

        if (!Account.hasAccount(player.getUniqueId())) {
            if (Account.hasAccount(player.getName())) {
                Account.getAccount(player.getName()).setAccountHolderId(player.getUniqueId());
            } else {
                Account.createAccount(player.getUniqueId(), player.getName());
            }
        }

        NametagManager.setup(player);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        NametagManager.remove(player);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Profile profile = main.getProfileManager().getProfile(player.getUniqueId());

        if (profile != null) {
            if (main.getGearManager().isInFullCarbyne(player)) {
                profile.setCarbyneDeaths(profile.getCarbyneDeaths() + 1);
            } else {
                profile.setDeaths(profile.getDeaths() + 1);
            }

            profile.setKillStreak(0);
        }

        if (player.getKiller() != null) {
            Player killer = player.getKiller();
            Profile killerProfile = main.getProfileManager().getProfile(killer.getUniqueId());

            if (killerProfile != null) {
                if (main.getGearManager().isInFullCarbyne(player)) {
                    killerProfile.setCarbyneKills(killerProfile.getCarbyneKills() + 1);
                } else {
                    killerProfile.setKills(killerProfile.getKills() + 1);
                }

                if (!Cooldowns.tryCooldown(killerProfile.getUniqueId(), player.getUniqueId().toString() + ":killstreak", 300000))
                     killerProfile.setKillStreak(killerProfile.getKillStreak() + 1);
            }
        }
    }
}
