package com.medievallords.carbyne.dailybonus.listeners;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.crates.CrateManager;
import com.medievallords.carbyne.dailybonus.DailyBonusManager;
import com.medievallords.carbyne.profiles.Profile;
import com.medievallords.carbyne.profiles.ProfileManager;
import com.medievallords.carbyne.utils.Cooldowns;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.UUID;

/**
 * Created by Calvin on 11/18/2017
 * for the Carbyne project.
 */
public class DailyBonusListeners implements Listener {

    private Carbyne main = Carbyne.getInstance();
    private ProfileManager profileManager = main.getProfileManager();
    private DailyBonusManager dailyBonusManager = main.getDailyBonusManager();
    private CrateManager crateManager = main.getCrateManager();

    private HashSet<UUID> crateOpeners = new HashSet<>();

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (event.getInventory().getTitle().contains("Daily Bonus")) {
            Profile profile = profileManager.getProfile(player.getUniqueId());
            ItemStack item = event.getCurrentItem();
            event.setCancelled(true);

            if (item != null) {
                if (event.getSlot() >= 10 && event.getSlot() <= 16) {
                    int index = (event.getSlot() - 10);
                    boolean hasClaimed = profile.getDailyRewards().get(index);

                    if ((index == profile.getDailyRewardDay()) && !hasClaimed) {
                        if (Cooldowns.getCooldown(player.getUniqueId(), "DailyRewardWarmUp") > 0) {
                            player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
                            return;
                        }

                        profile.getDailyRewards().put(index, true);
                        profile.setHasClaimedDailyReward(true);

                        if (crateManager.getCrates().get(0) != null) {
                            player.closeInventory();

                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    crateManager.getCrates().get(0).generateRewards(player);
                                    crateOpeners.add(player.getUniqueId());
                                }
                            }.runTaskLaterAsynchronously(main, 2L);
                        }
                    }
                }
            }
        }
    }

//    @EventHandler
//    public void onCrateOpened(CrateOpenedEvent event) {
//        if (crateOpeners.contains(event.getPlayer().getUniqueId())) {
//            crateOpeners.remove(event.getPlayer().getUniqueId());
//
//            new BukkitRunnable() {
//                @Override
//                public void run() {
//                    MessageManager.sendMessage(event.getPlayer(), "&aYou have claimed today's reward!");
//
//                    event.getPlayer().closeInventory();
//                    dailyBonusManager.openDailyBonusGui(event.getPlayer());
//                }
//            }.runTaskLater(main, 3 * 20L);
//        }
//    }

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();

        if (event.getRightClicked() != null && event.getRightClicked().getType() == EntityType.PLAYER) {
            if (event.getRightClicked().getCustomName() == null) {
                if (CitizensAPI.getNPCRegistry().isNPC(event.getRightClicked())) {
                    Profile profile = profileManager.getProfile(player.getUniqueId());

                    event.setCancelled(true);

                    if (!profile.isDailyRewardsSetup()) {
                        profile.setDailyRewardsSetup(true);
                        profile.assignNewWeeklyRewards();
                    }

                    dailyBonusManager.openDailyBonusGui(event.getPlayer());
                }
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Profile profile = profileManager.getProfile(player.getUniqueId());

        if (!profile.hasClaimedDailyReward()) {
            Cooldowns.setCooldown(player.getUniqueId(), "DailyRewardWarmUp", 300000);
        }
    }
}
