package com.medievallords.carbyne.dailybonus;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.profiles.Profile;
import com.medievallords.carbyne.utils.*;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Date;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by Calvin on 11/18/2017
 * for the Carbyne project.
 */
public class DailyBonusManager {

    //Don't touch any of my code you little bastard.

    private Carbyne main = Carbyne.getInstance();

    @Getter
    @Setter
    private HashMap<Integer, String> dailyBonusRewards = new HashMap<>();
    @Getter
    private HashMap<UUID, Hologram> playerHolograms = new HashMap<>();

    public DailyBonusManager() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (UUID id : playerHolograms.keySet()) {
                    Profile profile = main.getProfileManager().getProfile(id);
                    String text;

                    if (profile.isDailyRewardsSetup()) {
                        if (!profile.hasClaimedDailyReward()) {
                            if (Cooldowns.getCooldown(profile.getUniqueId(), "DailyRewardWarmUp") > 0) {
                                text = ChatColor.translateAlternateColorCodes('&', "&dDaily Bonus &7[&c" + DateUtil.readableTime(Cooldowns.getCooldown(profile.getUniqueId(), "DailyRewardWarmUp"), true) + "&7]");
                            } else {
                                text = ChatColor.translateAlternateColorCodes('&', "&dDaily Bonus &7[&aClaim Now&7]");
                            }
                        } else {
                            text = ChatColor.translateAlternateColorCodes('&', "&dDaily Bonus &7[&6" + DateUtil.readableTime(profile.getRemainingDailyDayTime(), true) + "&7]");
                        }
                    } else {
                        text = ChatColor.translateAlternateColorCodes('&', "&dDaily Bonus &7[&aStart Here&7]");
                    }

                    playerHolograms.get(id).clearLines();
                    playerHolograms.get(id).insertTextLine(0, text);
                }
            }
        }.runTaskTimer(main, 20L, 10L);

        new BukkitRunnable() {
            private boolean reverse = false;
            private double theta = Math.PI / 8;
            private double x, y, z;
            private Location location = new Location(Bukkit.getWorld("world"), -716.5, 105, 307.5);
            private ParticleEffect.OrdinaryColor black = new ParticleEffect.OrdinaryColor(0, 0, 0);
            private ParticleEffect.OrdinaryColor purple = new ParticleEffect.OrdinaryColor(119, 3, 158);

            @Override
            public void run() {
                theta += Math.PI / 16;

                if (reverse) {
                    x = Math.cos(theta);
                    y -= 0.05;
                    z = Math.sin(theta);
                } else {
                    x = Math.cos(theta);
                    y += 0.05;
                    z = Math.sin(theta);
                }

                location.add(x, y, z);
                ParticleEffect.REDSTONE.display(black, location, 40, true);
                location.subtract(x, 0, z);
                location.subtract(x, 0, z);
                ParticleEffect.REDSTONE.display(purple, location, 40, true);
                location.add(x, -y, z);

                if (y >= 2)
                    reverse = true;

                else if (y <= 0)
                    reverse = false;
            }
        }.runTaskTimerAsynchronously(main, 0L, 1L);
    }

    public void openDailyBonusGui(Player player) {
        Inventory inventory = Bukkit.createInventory(player, 27, ChatColor.translateAlternateColorCodes('&', "&bDaily Bonus&7:"));
        Profile profile = main.getProfileManager().getProfile(player.getUniqueId());

        for (int i = 0; i < inventory.getSize(); i++)
            inventory.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE).durability(15).name("&bDaily Bonus")
                    .addLore("&7Come back every day to receive")
                    .addLore("&7your daily bonus.")
                    .addLore("")
                    .addLore("&aThe more days in a row you")
                    .addLore("&ajoin the better the reward!")
                    .addLore("")
                    .addLore((profile.hasClaimedDailyReward() ? "&cYou must wait: &7" + DateUtil.readableTime(profile.getRemainingDailyDayTime(), true) : "&dClaim today's reward"))
                    .addLore((profile.hasClaimedDailyReward() ? "&cbefore claiming your next bonus." : "&dwithin: &7" + DateUtil.readableTime(profile.getRemainingDailyDayTime(), true))).build());

        new BukkitRunnable() {
            public void run() {
                if (inventory.getViewers().size() == 0)
                    cancel();
                else {
                    PlayerUtility.updateChestInventoryTitle(player, ChatColor.translateAlternateColorCodes('&', "&bDaily Bonus&7: &c" + DateUtil.readableTime((getRemainingWeekFromMillis(profile.getRemainingDailyDayTime())), true)));

                    for (int i = 0; i < inventory.getSize(); i++)
                        switch (i) {
                            case 0:
                                new ItemBuilder(inventory.getItem(i))
                                        .setLore(6, (profile.hasClaimedDailyReward() ? "&cYou must wait: &7" + DateUtil.readableTime(profile.getRemainingDailyDayTime(), true) : "&dClaim today's reward"))
                                        .setLore(7, (profile.hasClaimedDailyReward() ? "&cbefore claiming your next bonus." : "&dwithin: &7" + DateUtil.readableTime(profile.getRemainingDailyDayTime(), true)));
                                break;
                            case 8:
                                new ItemBuilder(inventory.getItem(i))
                                        .setLore(6, (profile.hasClaimedDailyReward() ? "&cYou must wait: &7" + DateUtil.readableTime(profile.getRemainingDailyDayTime(), true) : "&dClaim today's reward"))
                                        .setLore(7, (profile.hasClaimedDailyReward() ? "&cbefore claiming your next bonus." : "&dwithin: &7" + DateUtil.readableTime(profile.getRemainingDailyDayTime(), true)));
                                break;
                            case 9:
                                new ItemBuilder(inventory.getItem(i))
                                        .setLore(6, (profile.hasClaimedDailyReward() ? "&cYou must wait: &7" + DateUtil.readableTime(profile.getRemainingDailyDayTime(), true) : "&dClaim today's reward"))
                                        .setLore(7, (profile.hasClaimedDailyReward() ? "&cbefore claiming your next bonus." : "&dwithin: &7" + DateUtil.readableTime(profile.getRemainingDailyDayTime(), true)));
                                break;
                            case 17:
                                new ItemBuilder(inventory.getItem(i))
                                        .setLore(6, (profile.hasClaimedDailyReward() ? "&cYou must wait: &7" + DateUtil.readableTime(profile.getRemainingDailyDayTime(), true) : "&dClaim today's reward"))
                                        .setLore(7, (profile.hasClaimedDailyReward() ? "&cbefore claiming your next bonus." : "&dwithin: &7" + DateUtil.readableTime(profile.getRemainingDailyDayTime(), true)));
                                break;
                            case 18:
                                new ItemBuilder(inventory.getItem(i))
                                        .setLore(6, (profile.hasClaimedDailyReward() ? "&cYou must wait: &7" + DateUtil.readableTime(profile.getRemainingDailyDayTime(), true) : "&dClaim today's reward"))
                                        .setLore(7, (profile.hasClaimedDailyReward() ? "&cbefore claiming your next bonus." : "&dwithin: &7" + DateUtil.readableTime(profile.getRemainingDailyDayTime(), true)));
                                break;
                            case 26:
                                new ItemBuilder(inventory.getItem(i))
                                        .setLore(6, (profile.hasClaimedDailyReward() ? "&cYou must wait: &7" + DateUtil.readableTime(profile.getRemainingDailyDayTime(), true) : "&dClaim today's reward"))
                                        .setLore(7, (profile.hasClaimedDailyReward() ? "&cbefore claiming your next bonus." : "&dwithin: &7" + DateUtil.readableTime(profile.getRemainingDailyDayTime(), true)));
                                break;
                        }

                    for (int i = 10; i < 17; i++) {
                        int index = i - 10;
                        boolean hasClaimed = profile.getDailyRewards().get(index);

                        if ((index == profile.getDailyRewardDay()) && !hasClaimed) {
                            ItemBuilder itemBuilder = new ItemBuilder(Material.STAINED_GLASS).durability(4).name("&eDay " + (index + 1))
                                    .addLore("&dClick here to claim")
                                    .addLore("&dtoday's reward!")
                                    .addLore("")
                                    .addLore("&cClaim within: &7" + DateUtil.readableTime(profile.getRemainingDailyDayTime(), true));

                            if (Cooldowns.getCooldown(profile.getUniqueId(), "DailyRewardWarmUp") > 0)
                                itemBuilder.addLore("")
                                        .addLore("&7You can claim this reward in &c" + DateUtil.readableTime(Cooldowns.getCooldown(profile.getUniqueId(), "DailyRewardWarmUp"), false) + "&7.")
                                        .addLore("&7Completing a challenge within this time limit will boost the reward.");

                            inventory.setItem(i, itemBuilder.build());
                            inventory.setItem((i - 9), itemBuilder.type(Material.STAINED_GLASS_PANE).build());
                            inventory.setItem((i + 9), itemBuilder.type(Material.STAINED_GLASS_PANE).build());

                            //CURRENT DAY: YELLOW = NOT CLAIMED
                        } else if ((index <= profile.getDailyRewardDay())) {
                            ItemBuilder itemBuilder = new ItemBuilder(Material.STAINED_GLASS).durability((hasClaimed ? 5 : 8)).name((hasClaimed ? "&a" : "&7") + "Day " + (index + 1))
                                    .addLore((hasClaimed ? "&7Keep up the streak to" : "&7Oh no! Looks like"))
                                    .addLore((hasClaimed ? "&7Earn even better rewards!" : "&7you missed this day."))
                                    .addLore("")
                                    .addLore((hasClaimed ? "&aClaimed!" : "&cMissed!"));

                            inventory.setItem(i, itemBuilder.build());
                            inventory.setItem((i - 9), itemBuilder.type(Material.STAINED_GLASS_PANE).build());
                            inventory.setItem((i + 9), itemBuilder.type(Material.STAINED_GLASS_PANE).build());

                            //PREVIOUS: GRAY = NOT CLAIMED, GREEN = CLAIMED
                        } else if ((index >= (profile.getDailyRewardDay() + 1)) && (profile.getDailyRewardDay() + 1) <= 7) {
                            ItemBuilder itemBuilder = new ItemBuilder(Material.STAINED_GLASS).durability((index == (profile.getDailyRewardDay() + 1)) ? 1 : 14).name((hasClaimed ? "&6" : "&c") + "Day " + (index + 1))
                                    .addLore(((index == (profile.getDailyRewardDay() + 1)) ? "&7Come back tomorrow" : "&7Come back everyday"))
                                    .addLore("&7to earn more rewards!");

                            if ((index == (profile.getDailyRewardDay() + 1)))
                                itemBuilder.addLore("")
                                        .addLore("&cClaimable in: &7" + DateUtil.readableTime(profile.getRemainingDailyDayTime(), true))
                                        .addLore("");
                            else if (((index > (profile.getDailyRewardDay() + 1))))
                                itemBuilder.addLore("")
                                        .addLore("&7Claiming all days will")
                                        .addLore("&7result in an exciting bonus!");

                            inventory.setItem(i, itemBuilder.build());
                            inventory.setItem((i - 9), itemBuilder.type(Material.STAINED_GLASS_PANE).build());
                            inventory.setItem((i + 9), itemBuilder.type(Material.STAINED_GLASS_PANE).build());

                            //REST OF THE DAY(S): ORANGE = NEXT DAY,  = REST OF DAYS
                        } else
                            Bukkit.broadcastMessage("Index: " + index + " : " + (profile.getDailyRewardDay()));
                    }
                }
            }
        }.runTaskTimerAsynchronously(Carbyne.getInstance(), 0L, 20L);

        player.openInventory(inventory);
        player.playSound(player.getLocation(), Sound.CHEST_OPEN, 1, .8f);
    }

    public long getRemainingWeekFromMillis(long time) {
        Date date = new Date();
        int d = 7 - date.getDay();

        if (d < 7) {
            return TimeUnit.DAYS.toMillis(d) + time;
        } else {
            return time;
        }
    }
}
