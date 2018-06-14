package com.medievallords.carbyne.profiles;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.economy.objects.Account;
import com.medievallords.carbyne.events.Event;
import com.medievallords.carbyne.squads.Squad;
import com.medievallords.carbyne.squads.SquadManager;
import com.medievallords.carbyne.staff.StaffManager;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.scoreboard.Board;
import com.medievallords.carbyne.utils.scoreboard.BoardCooldown;
import com.medievallords.carbyne.utils.tabbed.item.TextTabItem;
import com.medievallords.carbyne.utils.tabbed.tablist.TableTabList;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Getter
@Setter
public class Profile {

    private UUID uniqueId;
    private String username, pin, previousInventoryContentString;
    private int kills, carbyneKills, deaths, carbyneDeaths, killStreak, professionLevel = 1, dailyRewardDay, stamina = 100, piledriveReady = 0;
    private double professionProgress = 0, requiredProfessionProgress = 100;
    private long pvpTime, timeLeft, professionResetCooldown, dailyRewardDayTime = -1, dailyRewardChallengeTime = -1, piledriveCombo = 0, sprintCombo = 0;
    private boolean pvpTimePaused, showTab, showEffects, playSounds, safelyLogged, hasClaimedDailyReward, hasCompletedDailyChallenge, dailyRewardsSetup,
            skillsToggled = true, piledriveBoolReady = false, sprintToggled = false, blocking = false;
    private Event activeEvent;
    private ProfileChatChannel profileChatChannel;
    private HashMap<Integer, Boolean> dailyRewards = new HashMap<>();
    private int[] dailyRewardsIndex = new int[8];
    private List<UUID> ignoredPlayers = new ArrayList<>();

    public Profile(UUID uniqueId) {
        this.uniqueId = uniqueId;

        new BukkitRunnable() {
            @Override
            public void run() {
                if (isANewDay())
                    if (dailyRewardsSetup)
                        prepareNewDay();
            }
        }.runTaskTimerAsynchronously(Carbyne.getInstance(), 0L, 20L);

//        new BukkitRunnable() {
//            @Override
//            public void run() {
//                if (dailyRewardsSetup) {
//                    if (Bukkit.getPlayer(uniqueId) != null && Bukkit.getPlayer(uniqueId).isOnline()) {
//                        Bukkit.broadcastMessage("DailyRewardDay: " + dailyRewardDay);
//                        Bukkit.broadcastMessage("DailyRewardDayTime: " + dailyRewardDayTime);
//                        Bukkit.broadcastMessage("DailyRewardDayTimeFormatted: " + DateUtil.readableTime(dailyRewardDayTime - System.currentTimeMillis(), true));
//                        Bukkit.broadcastMessage("RemainingDailyRewardDayTime: " + getRemainingDailyDayTime());
//                        Bukkit.broadcastMessage("RemainingDailyRewardDayTimeFormatted: " + DateUtil.readableTime(getRemainingDailyDayTime(), true));
//                        Bukkit.broadcastMessage("HasClaimedDailyReward: " + hasClaimedDailyReward);
//                        Bukkit.broadcastMessage("HasCompletedDailyChallenge: " + hasCompletedDailyChallenge);
//                        Bukkit.broadcastMessage("DailyRewards: ");
//
//                        for (int i = 0; i < dailyRewards.keySet().size(); i++) {
//                            Bukkit.broadcastMessage(" - " + i + ": " + dailyRewards.get(i));
//                            Bukkit.broadcastMessage(" - " + i + ": Can Claim Today: " + (i == dailyRewardDay && !dailyRewards.get(i)));
//
//                            if (!(i == dailyRewardDay && !dailyRewards.get(i)) && (i == (dailyRewardDay + 1)) && i + 1 < dailyRewards.keySet().size())
//                                Bukkit.broadcastMessage(" - " + i + ": Can Claim Tomorrow: " + (i == (dailyRewardDay + 1) && !dailyRewards.get(i + 1)));
//                        }
////
////                        Bukkit.broadcastMessage("DailyRewardIndex: ");
////
////                        for (int i = 0; i < dailyRewardsIndex.length; i++)
////                            Bukkit.broadcastMessage(" - " + i + ": " + dailyRewardsIndex[i]);
//                    }
//                }
//            }
//        }.runTaskTimer(Carbyne.getInstance(), 0L, 10 * 20L);
    }

    public void runTickGeneral() {
        Player player = Bukkit.getPlayer(uniqueId);
        if (sprintToggled) {
            stamina -= 7;

            if (stamina < 7) {
                sprintToggled = false;

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.setWalkSpeed(0.2f);
                    }
                }.runTask(Carbyne.getInstance());

                MessageManager.sendMessage(player, "&cSuper Sprint has been disabled!");
            }
        } else if (blocking) {
            stamina -= 15;

            if (stamina < 15) {
                blocking = false;
                MessageManager.sendMessage(player, "&cSuper Block has been disabled!");
            }
        } else if (stamina < 100)
            stamina++;

        if (piledriveReady > 0)
            piledriveReady--;
        else {
            if (piledriveBoolReady) {
                MessageManager.sendMessage(uniqueId, "&cYou are no longer able to piledrive!");
                piledriveBoolReady = false;
            }
        }

        if (player != null) {
            Board board = Board.getByPlayer(player);

            if (board != null) {
                BoardCooldown skillCooldown = board.getCooldown("skill");

                if (skillCooldown == null)
                    if (skillsToggled)
                        if (!player.getAllowFlight())
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    player.setAllowFlight(true);
                                }
                            }.runTask(Carbyne.getInstance());
            }
        }

    }

    public boolean hasClaimedDailyReward() {
        return hasClaimedDailyReward;
    }

    public boolean hasCompletedDailyChallenge() {
        return hasCompletedDailyChallenge;
    }

    public boolean hasEffectsToggled() {
        return showEffects;
    }

    public boolean hasSoundsToggled() {
        return playSounds;
    }

    public boolean hasPin() {
        return pin != null && !pin.isEmpty();
    }

    public double getKDR() {
        double kills = getKills();
        double deaths = getDeaths();
        double ratio;

        if (kills == 0.0D && deaths == 0.0D)
            ratio = 0.0D;
        else if (kills > 0.0D && deaths == 0.0D)
            ratio = kills;
        else if (deaths > 0.0D && kills == 0.0D)
            ratio = -deaths;
        else
            ratio = kills / deaths;

        return Math.round(ratio * 100.0D) / 100.0D;
    }

    public double getCarbyneKDR() {
        double kills = getCarbyneKills();
        double deaths = getCarbyneDeaths();
        double ratio;

        if (kills == 0.0D && deaths == 0.0D)
            ratio = 0.0D;
        else if (kills > 0.0D && deaths == 0.0D)
            ratio = kills;
        else if (deaths > 0.0D && kills == 0.0D)
            ratio = -deaths;
        else
            ratio = kills / deaths;

        return Math.round(ratio * 100.0D) / 100.0D;
    }

    public long getRemainingPvPTime() {
        if (pvpTimePaused)
            return timeLeft;
        else {
            timeLeft = pvpTime - System.currentTimeMillis();
            return timeLeft;
        }
    }

    public void setPvpTimePaused(boolean paused) {
        if (this.pvpTimePaused != paused) {
            if (paused)
                //System.currentTimeMillis() - (System.currentTimeMillis() -
                timeLeft = getRemainingPvPTime();

            this.pvpTimePaused = paused;
        }
    }

    public long getRemainingDailyDayTime() {
        return dailyRewardDayTime - System.currentTimeMillis();
    }


    public void assignNewWeeklyRewards() {
        dailyRewardDay = 0;

        try {
            dailyRewardDayTime = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        hasClaimedDailyReward = false;
        hasCompletedDailyChallenge = false;

        //TODO: Randomize with available rewards.
        dailyRewardsIndex[0] = 0;
        dailyRewardsIndex[1] = 1;
        dailyRewardsIndex[2] = 2;
        dailyRewardsIndex[3] = 3;
        dailyRewardsIndex[4] = 4;
        dailyRewardsIndex[5] = 5;
        dailyRewardsIndex[6] = 6;
        dailyRewardsIndex[7] = 7;

        dailyRewards.clear();
        for (int i = 0; i < 8; i++)
            dailyRewards.put(i, false);

        Player player = Bukkit.getPlayer(uniqueId);
        if (player != null)
            if (ChatColor.stripColor(player.getOpenInventory().getTopInventory().getTitle()).contains("Daily Bonus")) {
                player.closeInventory();
                Carbyne.getInstance().getDailyBonusManager().openDailyBonusGui(player);
            }
    }

    public void prepareNewDay() {
        try {
            dailyRewardDayTime = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1);

            hasClaimedDailyReward = false;
        } catch (Exception e) {
            e.printStackTrace();
        }

        hasCompletedDailyChallenge = false;

        dailyRewardDay++;

        if (dailyRewardDay >= 7) {
            boolean hasClaimedRewards = false;

            for (int i = 0; i < dailyRewards.keySet().size(); i++)
                if (dailyRewards.get(i))
                    hasClaimedRewards = true;

            if (!hasClaimedRewards)
                dailyRewardsSetup = false;
            else
                assignNewWeeklyRewards();
        }
    }

    public boolean isANewDay() {
        return System.currentTimeMillis() >= dailyRewardDayTime;
    }

    public enum ProfileChatChannel {
        GLOBAL, LOCAL, TOWN, NATION
    }

    public static class PlayerTabRunnable extends BukkitRunnable {

        private StaffManager staffManager = Carbyne.getInstance().getStaffManager();
        private SquadManager squadManager = Carbyne.getInstance().getSquadManager();
        //private DropPointManager dropPointManager = Carbyne.getInstance().getDropPointManager();
        private Profile profile;
        private Player player;
        private EntityPlayer entityPlayer;
        private Account account;
        private TableTabList tab;

        public PlayerTabRunnable(Player player, Profile profile, Account account, TableTabList tab) {
            this.player = player;
            this.account = account;
            this.profile = profile;
            this.tab = tab;
            this.entityPlayer = ((CraftPlayer) player).getHandle();
        }

        @Override
        public void run() {
            tab.set(0, 1, new TextTabItem("§b§lPlayer Info:", 1));
            tab.set(0, 2, new TextTabItem(" §d§lBalance§7: " + account.getBalance(), 1));
            tab.set(0, 3, new TextTabItem(" §d§lPing§7: " + entityPlayer.ping, 1));

            tab.set(0, 7, new TextTabItem("§b§lServer Info:", 1));
            tab.set(0, 8, new TextTabItem(" §d§lPlayers Online§7: " + (Bukkit.getOnlinePlayers().size() - staffManager.getVanish().size()), 1));
            tab.set(0, 9, new TextTabItem(" §d§lStaff Online§7: " + staffManager.getStaff().size(), 1));

            tab.set(0, 14, new TextTabItem("§b§lFriends:", 1));
            Resident resident = null;
            try {
                resident = TownyUniverse.getDataSource().getResident(player.getName());
            } catch (NotRegisteredException e) {
                e.printStackTrace();
            }

            // 14 to 19

            if (resident != null) {
                int c = 15;
                int r = 0;
                for (Resident friend : resident.getFriends()) {
                    Player friendPlayer = Bukkit.getPlayer(friend.getName());
                    if (friendPlayer != null) {
                        tab.set(r, c++, new TextTabItem(" §7§l- §a" + friendPlayer.getName(), ((CraftPlayer) friendPlayer).getHandle().ping));
                    }

                    if (c > 19) {
                        c = 14;
                        r++;
                    }

                    if (r > 3) {
                        break;
                    }
                }
            }

            tab.set(1, 1, new TextTabItem("§b§lStats:", 1));
            tab.set(1, 2, new TextTabItem(" §d§lKills§7: " + profile.getKills(), 1));
            tab.set(1, 3, new TextTabItem(" §d§lDeaths§7: " + profile.getDeaths(), 1));
            tab.set(1, 4, new TextTabItem(" §d§lStreak§7: " + profile.getKillStreak(), 1));

            tab.set(1, 7, new TextTabItem("§b§lChallenge:", 1));

            Squad squad = squadManager.getSquad(player.getUniqueId());
            tab.set(2, 1, new TextTabItem("§b§lSquad Info:", 1));
            if (squad != null) {
                tab.set(2, 2, new TextTabItem(" §d§lLeader§7:", 1));
                Player leader = Bukkit.getPlayer(squad.getLeader());
                tab.set(2, 3, new TextTabItem(" §7§l- §a" + leader.getName(), ((CraftPlayer) leader).getHandle().ping));
                tab.set(2, 4, new TextTabItem(" §d§lMembers§7:", 1));
                for (int i = 5; i < 11; i++)
                    tab.set(2, i, new TextTabItem(" §7§l- ", 1));

                int x = 5;
                for (int i = 0; i < squad.getMembers().size(); i++) {
                    Player other = Bukkit.getPlayer(squad.getMembers().get(i));
                    tab.set(2, x, new TextTabItem(" §7§l- §a" + other.getName(), ((CraftPlayer) other).getHandle().ping));
                    x++;
                }
            }

            tab.set(3, 7, new TextTabItem("§b§lDaily Bonus:", 1));
            tab.set(3, 8, new TextTabItem(" §d§lStatus§7: " + (profile.hasClaimedDailyReward ? "Claimed" : "Claimable"), 1));


            tab.set(3, 1, new TextTabItem("§b§lDrop Points:", 1));
            /*int index = 2;
            for (int i = 1; i < dropPointManager.getDropPoints().size() - 1; i++) {
                DropPoint dropPoint = dropPointManager.getDropPoints().get(i);
                tab.set(3, index++, new TextTabItem(" §d'" + i + "'", 1));
                if (dropPoint.isStarted())  {
                    tab.set(3, index++, new TextTabItem("  §dTime§7: " + dropPoint.getTimeLeft(), 1));
                    tab.set(3, index++, new TextTabItem("  §dBreaks§7: " + dropPoint.blocksLeft(), 1));
                } else {
                    tab.set(3, index++, new TextTabItem("  §dCountdown§7: " + dropPoint.getTimeLeft(), 1));
                }

                tab.set(3, index++, new TextTabItem("  §dLocation§7:", 1));
                tab.set(3, index++, new TextTabItem("   §aX§7: " + dropPoint.getMainLocation().getX(), 1));
                tab.set(3, index++, new TextTabItem("   §aY§7: " + dropPoint.getMainLocation().getY(), 1));
                tab.set(3, index++, new TextTabItem("   §aZ§7: " + dropPoint.getMainLocation().getZ(), 1));
            }*/
        }
    }
}
