package com.medievallords.carbyne.profiles;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.keenant.tabbed.item.TextTabItem;
import com.keenant.tabbed.tablist.TableTabList;
import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.customevents.ProfileCreatedEvent;
import com.medievallords.carbyne.economy.objects.Account;
import com.medievallords.carbyne.utils.Cooldowns;
import com.medievallords.carbyne.utils.nametag.NametagManager;
import com.medievallords.carbyne.utils.serialization.InventorySerialization;
import com.palmergames.bukkit.towny.event.PlayerChangePlotEvent;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Created by Calvin on 3/22/2017
 * for the Carbyne project.
 */
public class ProfileListeners implements Listener {

    private Carbyne main = Carbyne.getInstance();
    private HashMap<UUID, Profile.PlayerTabRunnable> playerTabs = new HashMap<>();
    String tablistHeader, tablistFooter;

    public ProfileListeners() {
        if (main.getConfig().getString("TablistHeader") != null)
            tablistHeader = ChatColor.translateAlternateColorCodes('&', Carbyne.getInstance().getConfig().getString("TablistHeader"));
        if (main.getConfig().getString("TablistFooter") != null)
            tablistFooter = ChatColor.translateAlternateColorCodes('&', Carbyne.getInstance().getConfig().getString("TablistFooter"));
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (player == null)
            return;

        Account account = Account.getAccount(player.getUniqueId());
        if (account != null)
            account.setAccountHolder(player.getName());
        else
            Account.createAccount(player.getUniqueId(), player.getName());

        if (!main.getProfileManager().hasProfile(player.getUniqueId())) {
            main.getProfileManager().createProfile(player);
            ProfileCreatedEvent profileCreatedEvent = new ProfileCreatedEvent(player, main.getProfileManager().getProfile(player.getUniqueId()));
            Bukkit.getPluginManager().callEvent(profileCreatedEvent);
        }

        Profile profile = main.getProfileManager().getProfile(player.getUniqueId());

        if (!profile.getUsername().equalsIgnoreCase(player.getName())) {
            try {
                //Bukkit.broadcastMessage("Resident Found: " + TownyUniverse.getDataSource().getResident(profile.getUsername()).getName());
                Resident resident = TownyUniverse.getDataSource().getResident(profile.getUsername());
                TownyUniverse.getDataSource().renamePlayer(resident, player.getName());
                if (resident.hasTown()) {
                    Town town = resident.getTown();
                    TownyUniverse.getDataSource().saveTown(town);
                }

                //Bukkit.broadcastMessage("Resident Replacement: " + TownyUniverse.getDataSource().getResident(player.getName()).getName());
            } catch (NotRegisteredException ignored) {
            } catch (Exception shouldNeverHappen) {
                main.getLogger().log(Level.SEVERE, "EXCEPTION OCCURRED IN TOWNY NAME UPDATER: ");
                shouldNeverHappen.printStackTrace();

            }

            profile.setUsername(player.getName());
        }

        NametagManager.setup(player);

        TownBlock townBlock = TownyUniverse.getTownBlock(player.getLocation());

        if (townBlock == null)
            profile.setPvpTimePaused(false);
        else if (!townBlock.getPermissions().pvp)
            profile.setPvpTimePaused(true);
        else
            profile.setPvpTimePaused(false);

        TableTabList tab = main.getTabbed().newTableTabList(event.getPlayer());
        tab.setHeader(tablistHeader);
        tab.setFooter(tablistFooter);
        for (int i = 0; i < 4; i++) {
            for (int l = 0; l < 20; l++) {
                tab.set(i, l, new TextTabItem("", 1));
            }
        }
        tab.set(0, 1, new TextTabItem("§b§lPlayer Info:", 1));
        tab.set(0, 2, new TextTabItem(" §d§lBalance§7: 0", 1));
        tab.set(0, 3, new TextTabItem(" §d§lPing§7: 0", 1));
        tab.set(0, 7, new TextTabItem("§b§lServer Info:", 1));
        tab.set(0, 8, new TextTabItem(" §d§lPlayers Online§7: 0", 1));
        tab.set(0, 9, new TextTabItem(" §d§lStaff Online§7: 0", 1));

        Profile.PlayerTabRunnable runnable = new Profile.PlayerTabRunnable(event.getPlayer(), profile, Account.getAccount(event.getPlayer().getUniqueId()), tab);
        runnable.runTaskTimerAsynchronously(main, 5L, 20);

        playerTabs.put(event.getPlayer().getUniqueId(), runnable);
        Hologram holo = HologramsAPI.createHologram(main, new Location(Bukkit.getWorld("world"), -716.5, 108, 307.5));
        holo.getVisibilityManager().showTo(player);
        holo.getVisibilityManager().setVisibleByDefault(false);
        main.getDailyBonusManager().getPlayerHolograms().put(player.getUniqueId(), holo);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (playerTabs.containsKey(event.getPlayer().getUniqueId()) && playerTabs.get(event.getPlayer().getUniqueId()) != null) {
            playerTabs.get(event.getPlayer().getUniqueId()).cancel();
            playerTabs.remove(event.getPlayer().getUniqueId());
        }

        main.getDailyBonusManager().getPlayerHolograms().get(event.getPlayer().getUniqueId()).delete();
        main.getDailyBonusManager().getPlayerHolograms().remove(event.getPlayer().getUniqueId());

        NametagManager.remove(event.getPlayer());
    }

    @EventHandler
    public void onKick(PlayerKickEvent event) {
        if (playerTabs.containsKey(event.getPlayer().getUniqueId()) && playerTabs.get(event.getPlayer().getUniqueId()) != null) {
            playerTabs.get(event.getPlayer().getUniqueId()).cancel();
            playerTabs.remove(event.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Profile profile = main.getProfileManager().getProfile(player.getUniqueId());

        profile.setPreviousInventoryContentString(InventorySerialization.serializePlayerInventoryAsString(player.getInventory()));

        if (main.getGearManager().isInFullCarbyne(player))
            if (player.getKiller() != null)
                if (Cooldowns.tryCooldown(player.getUniqueId(), player.getKiller().getUniqueId().toString() + ":carbynedeath", 300000))
                    profile.setCarbyneDeaths(profile.getCarbyneDeaths() + 1);
                else
                    profile.setCarbyneDeaths(profile.getCarbyneDeaths() + 1);
            else {
                if (player.getKiller() != null)
                    if (Cooldowns.tryCooldown(player.getUniqueId(), player.getKiller().getUniqueId().toString() + ":death", 300000))
                        profile.setDeaths(profile.getDeaths() + 1);
                    else
                        profile.setDeaths(profile.getDeaths() + 1);
            }

        profile.setKillStreak(0);

        if (player.getKiller() != null) {
            Player killer = player.getKiller();
            Profile killerProfile = main.getProfileManager().getProfile(killer.getUniqueId());

            if (Cooldowns.tryCooldown(killerProfile.getUniqueId(), player.getUniqueId().toString() + ":kill", 300000))
                if (main.getGearManager().isInFullCarbyne(player))
                    killerProfile.setCarbyneKills(killerProfile.getCarbyneKills() + 1);
                else
                    killerProfile.setKills(killerProfile.getKills() + 1);

            if (Cooldowns.tryCooldown(killerProfile.getUniqueId(), player.getUniqueId().toString() + ":killstreak", 300000))
                killerProfile.setKillStreak(killerProfile.getKillStreak() + 1);
        }
    }

    @EventHandler
    public void plotChange(PlayerChangePlotEvent event) {
        Player player = event.getPlayer();
        Profile profile = main.getProfileManager().getProfile(player.getUniqueId());

        try {
            if (!event.getTo().getTownBlock().getPermissions().pvp)
                profile.setPvpTimePaused(true);
            else
                profile.setPvpTimePaused(false);
        } catch (NotRegisteredException e) {
            profile.setPvpTimePaused(false);
        }
    }
}
