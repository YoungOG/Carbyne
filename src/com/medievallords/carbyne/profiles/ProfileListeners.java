package com.medievallords.carbyne.profiles;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.customevents.ProfileCreatedEvent;
import com.medievallords.carbyne.economy.objects.Account;
import com.medievallords.carbyne.utils.Cooldowns;
import com.medievallords.carbyne.utils.PlayerHealth;
import com.medievallords.carbyne.utils.StaticClasses;
import com.medievallords.carbyne.utils.nametag.NametagManager;
import com.medievallords.carbyne.utils.serialization.InventorySerialization;
import com.medievallords.carbyne.utils.tabbed.item.TextTabItem;
import com.medievallords.carbyne.utils.tabbed.tablist.TableTabList;
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
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.MerchantInventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Created by Calvin on 3/22/2017
 * for the Carbyne project.
 */
public class ProfileListeners implements Listener {

    private HashMap<UUID, Profile.PlayerTabRunnable> playerTabs = new HashMap<>();
    private static final Location holoLocation = new Location(Bukkit.getWorld("world"), -716.5, 108, 307.5);
    String tablistHeader, tablistFooter;

    public ProfileListeners() {
        if (Carbyne.getInstance().getConfig().getString("TablistHeader") != null)
            tablistHeader = ChatColor.translateAlternateColorCodes('&', Carbyne.getInstance().getConfig().getString("TablistHeader"));
        if (Carbyne.getInstance().getConfig().getString("TablistFooter") != null)
            tablistFooter = ChatColor.translateAlternateColorCodes('&', Carbyne.getInstance().getConfig().getString("TablistFooter"));

        handleSkills();
    }

    private void handleSkills() {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        PlayerHealth playerHealth = PlayerHealth.getPlayerHealth(player.getUniqueId());
                        playerHealth.runTickGeneral(player);
                    }
                } catch (Exception e) {
                    System.out.println("Error player health warzone-------------");
                    e.printStackTrace();
                }
            }
        }.runTaskTimerAsynchronously(Carbyne.getInstance(), 20, 20);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (player == null)
            return;

        player.setAllowFlight(true);
        player.setFlying(false);

        Account account = Account.getAccount(player.getUniqueId());
        if (account != null)
            account.setAccountHolder(player.getName());
        else
            Account.createAccount(player.getUniqueId(), player.getName());

        if (!StaticClasses.profileManager.hasProfile(player.getUniqueId())) {
            StaticClasses.profileManager.createProfile(player);
            ProfileCreatedEvent profileCreatedEvent = new ProfileCreatedEvent(player, StaticClasses.profileManager.getProfile(player.getUniqueId()));
            Bukkit.getPluginManager().callEvent(profileCreatedEvent);
        }

        Profile profile = StaticClasses.profileManager.getProfile(player.getUniqueId());

        double maxHealth = StaticClasses.gearManager.calculateMaxHealth(event.getPlayer(), false);
        PlayerHealth playerHealth = new PlayerHealth(maxHealth, maxHealth, profile.isSkillsToggled());
        playerHealth.setMaxHealth(maxHealth);
        playerHealth.setHealth(maxHealth, player);
        PlayerHealth.players.put(event.getPlayer().getUniqueId(), playerHealth);

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
                Carbyne.getInstance().getLogger().log(Level.SEVERE, "EXCEPTION OCCURRED IN TOWNY NAME UPDATER: ");
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

        if (profile.isShowTab()) {
            TableTabList tab = Carbyne.getInstance().getTabbed().newTableTabList(event.getPlayer());
            tab.setHeader(tablistHeader.replace("{newline}", "\n"));
            tab.setFooter(tablistFooter.replace("{newline}", "\n"));

            for (int i = 0; i < 4; i++)
                for (int l = 0; l < 20; l++)
                    tab.set(i, l, new TextTabItem("", 1));

            Profile.PlayerTabRunnable runnable = new Profile.PlayerTabRunnable(event.getPlayer(), profile, Account.getAccount(event.getPlayer().getUniqueId()), tab);
            runnable.runTaskTimerAsynchronously(Carbyne.getInstance(), 5L, 20);

            playerTabs.put(event.getPlayer().getUniqueId(), runnable);
        }

        Hologram holo = HologramsAPI.createHologram(Carbyne.getInstance(), holoLocation);
        holo.getVisibilityManager().showTo(player);
        holo.getVisibilityManager().setVisibleByDefault(false);
        StaticClasses.dailyBonusManager.getPlayerHolograms().put(player.getUniqueId(), holo);
        StaticClasses.dailyBonusManager.getToUpdate().add(profile);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        PlayerHealth.players.remove(event.getPlayer().getUniqueId());

        if (playerTabs.containsKey(event.getPlayer().getUniqueId()) && playerTabs.get(event.getPlayer().getUniqueId()) != null) {
            playerTabs.get(event.getPlayer().getUniqueId()).cancel();
            playerTabs.remove(event.getPlayer().getUniqueId());
        }

        StaticClasses.dailyBonusManager.getPlayerHolograms().get(event.getPlayer().getUniqueId()).delete();
        StaticClasses.dailyBonusManager.getPlayerHolograms().remove(event.getPlayer().getUniqueId());
        StaticClasses.dailyBonusManager.getToUpdate().remove(StaticClasses.profileManager.getProfile(event.getPlayer().getUniqueId()));

        NametagManager.remove(event.getPlayer());
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Profile profile = StaticClasses.profileManager.getProfile(player.getUniqueId());

        profile.setPreviousInventoryContentString(InventorySerialization.serializePlayerInventoryAsString(player.getInventory()));

        profile.setKillStreak(0);

        if (StaticClasses.gearManager.isInFullCarbyne(player)) {
            if (player.getKiller() != null) {
                Player killer = player.getKiller();
                Profile killerProfile = StaticClasses.profileManager.getProfile(killer.getUniqueId());

                if (Cooldowns.tryCooldown(player.getUniqueId(), killer.getUniqueId().toString() + ":carbynedeath", 300000))
                    profile.setCarbyneDeaths(profile.getCarbyneDeaths() + 1);

                if (Cooldowns.tryCooldown(killer.getUniqueId(), player.getUniqueId().toString() + ":carbynekill", 300000))
                    killerProfile.setCarbyneKills(killerProfile.getCarbyneKills() + 1);
            } else
                profile.setCarbyneDeaths(profile.getCarbyneDeaths() + 1);
        } else {
            if (player.getKiller() != null) {
                Player killer = player.getKiller();
                Profile killerProfile = StaticClasses.profileManager.getProfile(killer.getUniqueId());

                if (Cooldowns.tryCooldown(player.getUniqueId(), killer.getUniqueId().toString() + ":death", 300000))
                    profile.setDeaths(profile.getDeaths() + 1);

                if (Cooldowns.tryCooldown(killer.getUniqueId(), player.getUniqueId().toString() + ":kill", 300000))
                    killerProfile.setKills(killerProfile.getKills() + 1);
            } else
                profile.setDeaths(profile.getDeaths() + 1);
        }

        if (player.getKiller() != null) {
            Profile killerProfile = StaticClasses.profileManager.getProfile(player.getKiller().getUniqueId());

            if (Cooldowns.tryCooldown(player.getKiller().getUniqueId(), player.getUniqueId().toString() + ":killstreak", 300000))
                killerProfile.setKillStreak(killerProfile.getKillStreak() + 1);
        }
    }

    @EventHandler
    public void plotChange(PlayerChangePlotEvent event) {
        Player player = event.getPlayer();
        Profile profile = StaticClasses.profileManager.getProfile(player.getUniqueId());

        if (player.getLocation().getWorld().equals(holoLocation.getWorld()) && player.getLocation().distance(holoLocation) < 50)
            StaticClasses.dailyBonusManager.getToUpdate().add(profile);
        else
            StaticClasses.dailyBonusManager.getToUpdate().remove(profile);

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
