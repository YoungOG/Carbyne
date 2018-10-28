package com.medievallords.carbyne.dailybonus.listeners;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.profiles.Profile;
import com.medievallords.carbyne.quests.Task;
import com.medievallords.carbyne.quests.types.DailyBonusStreakTask;
import com.medievallords.carbyne.utils.Cooldowns;
import com.medievallords.carbyne.utils.StaticClasses;
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

import java.util.List;

/**
 * Created by Calvin on 11/18/2017
 * for the Carbyne project.
 */
public class DailyBonusListeners implements Listener {

    //private HashSet<UUID> crateOpeners = new HashSet<>();

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (event.getInventory().getTitle().contains("Daily Bonus")) {
            Profile profile = StaticClasses.profileManager.getProfile(player.getUniqueId());
            ItemStack item = event.getCurrentItem();
            event.setCancelled(true);

            if (item != null) {
                if (event.getSlot() >= 10 && event.getSlot() <= 16) {
                    int index = (event.getSlot() - 10);
                    boolean hasClaimed = profile.getDailyRewards().get(index);

                    if ((index == profile.getDailyRewardDay()) && !hasClaimed) {
                        if (Cooldowns.getCooldown(player.getUniqueId(), "DailyRewardWarmUp") > 0) {
                            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1.0F, 1.0F);
                            return;
                        }

                        profile.getDailyRewards().put(index, true);
                        profile.setHasClaimedDailyReward(true);

                        player.closeInventory();

                        List<Task> tasks = StaticClasses.questHandler.getTasks(player.getUniqueId());

                        for (Task task : tasks) {
                            if (task instanceof DailyBonusStreakTask) {
                                task.incrementProgress(player.getUniqueId(), 1);
                            }
                        }

                        if (!StaticClasses.dailyBonusManager.hasClaimedAllDays(profile)) {
                            if (StaticClasses.crateManager.getCrates().get(0) != null)
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        StaticClasses.crateManager.getCrates().get(0).generateRewards(player, true);
                                        //crateOpeners.add(player.getUniqueId());
                                    }
                                }.runTaskLater(Carbyne.getInstance(), 2L);
                        } else if (StaticClasses.crateManager.getCrates().get(1) != null)
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    StaticClasses.crateManager.getCrates().get(1).generateRewards(player, true);

                                    //crateOpeners.add(player.getUniqueId());
                                }
                            }.runTaskLater(Carbyne.getInstance(), 2L);
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
                    Profile profile = StaticClasses.profileManager.getProfile(player.getUniqueId());

                    event.setCancelled(true);

                    if (!profile.isDailyRewardsSetup()) {
                        profile.setDailyRewardsSetup(true);
                        profile.assignNewWeeklyRewards();
                    }

                    StaticClasses.dailyBonusManager.openDailyBonusGui(event.getPlayer());
                }
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        new BukkitRunnable() {
            @Override
            public void run() {
                Profile profile = StaticClasses.profileManager.getProfile(player.getUniqueId());

                if (!profile.hasClaimedDailyReward())
                    Cooldowns.setCooldown(player.getUniqueId(), "DailyRewardWarmUp", 300000);
            }
        }.runTaskLaterAsynchronously(Carbyne.getInstance(), 10L);
    }
}
