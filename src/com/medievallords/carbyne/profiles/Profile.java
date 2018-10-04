package com.medievallords.carbyne.profiles;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.economy.objects.Account;
import com.medievallords.carbyne.listeners.PlayerListeners;
import com.medievallords.carbyne.quests.Quest;
import com.medievallords.carbyne.quests.Task;
import com.medievallords.carbyne.squads.Squad;
import com.medievallords.carbyne.utils.*;
import com.medievallords.carbyne.utils.tabbed.item.TextTabItem;
import com.medievallords.carbyne.utils.tabbed.tablist.TableTabList;
import com.medievallords.carbyne.zones.Zone;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import lombok.Getter;
import lombok.Setter;
import me.lucko.luckperms.LuckPerms;
import me.lucko.luckperms.api.Group;
import me.lucko.luckperms.api.User;
import me.lucko.luckperms.api.manager.GroupManager;
import net.minecraft.server.v1_12_R1.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Getter
@Setter
public class Profile {

    private UUID uniqueId;
    private String username,
            pin,
            previousInventoryContentString,
            carbyneLootInventory;
    private int kills,
            carbyneKills,
            deaths, carbyneDeaths,
            killStreak,
            professionLevel = 1,
            dailyRewardDay,
            questSkipsLeft = 2, questsLeft = 10,
            kitPoints = 0;
    private double professionProgress = 0,
            requiredProfessionProgress = 100;
    private long pvpTime,
            timeLeft,
            professionResetCooldown,
            dailyRewardDayTime = -1,
            dailyRewardChallengeTime = -1,
            skipTime = 0,
            questNext = 0;
    private boolean pvpTimePaused,
            showTab,
            showEffects,
            playSounds,
            safelyLogged,
            hasClaimedDailyReward,
            dailyRewardsSetup,
            skillsToggled = true,
            hasClaimedRankRewards = false,
            vanishEffect = true,
            chatEnabled = true,
            announcementsEnabled = true;

    /*


    List:
    - questName:[task1,5:task2,0]
    - questName2:[task3,5:task4,0]
     */

    private ProfileChatChannel profileChatChannel;
    private HashMap<Integer, Boolean> dailyRewards = new HashMap<>();
    private HashMap<String, Long> usedKits = new HashMap<>();
    private List<UUID> ignoredPlayers = new ArrayList<>();
    private List<String> quests = new ArrayList<>();
    private List<String> dormantQuests = new ArrayList<>();
    private List<String> forcedQuests = new ArrayList<>();


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
    }

    public boolean hasClaimedDailyReward() {
        return hasClaimedDailyReward;
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

    public void checkQuestNext() {
        if (questNext <= 0) {
            questsLeft = 10;
            if (Bukkit.getPlayer(uniqueId).hasPermission("carbyne.donator")) {
                questSkipsLeft += 5;
            }

            questNext = System.currentTimeMillis();
        } else {
            if (System.currentTimeMillis() >= questNext + 1728000) {
                questsLeft = 10;
                if (Bukkit.getPlayer(uniqueId).hasPermission("carbyne.donator")) {
                    questSkipsLeft += 5;
                }

                questNext = System.currentTimeMillis();
            }
        }
    }

    public String getQuestNextString() {
        if (questNext <= 0) {
            return "now";
        }

        long timeLeft =  - (questNext + 1728000) - System.currentTimeMillis();
        if (timeLeft <= 0) {
            return "now";
        } else {
            return DateUtil.readableTime(timeLeft, true);
        }
    }

    public void checkQuestSkip() {
        if (skipTime <= 0) {
            questSkipsLeft = 3;
            if (Bukkit.getPlayer(uniqueId).hasPermission("carbyne.donator")) {
                questSkipsLeft += 2;
            }

            skipTime = System.currentTimeMillis();
        } else {
            if (System.currentTimeMillis() >= skipTime + 1728000) {
                questSkipsLeft = 3;
                if (Bukkit.getPlayer(uniqueId).hasPermission("carbyne.donator")) {
                    questSkipsLeft += 2;
                }

                skipTime = System.currentTimeMillis();
            }
        }
    }

    public String getSkipTimeString() {
        if (skipTime <= 0) {
            return "now";
        }

        long timeLeft = (skipTime + 1728000) - System.currentTimeMillis();
        if (timeLeft <= 0) {
            return "now";
        } else {
            return DateUtil.readableTime(timeLeft, true);
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

        dailyRewards.clear();
        for (int i = 0; i < 7; i++)
            dailyRewards.put(i, false);

        Player player = Bukkit.getPlayer(uniqueId);
        if (player != null)
            if (ChatColor.stripColor(player.getOpenInventory().getTopInventory().getTitle()).contains("Daily Bonus")) {
                player.closeInventory();
                StaticClasses.dailyBonusManager.openDailyBonusGui(player);
            }
    }

    public void prepareNewDay() {
        try {
            dailyRewardDayTime = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1);

            hasClaimedDailyReward = false;
        } catch (Exception e) {
            e.printStackTrace();
        }

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

    //TODO: Add default tab, stylized by tabbed, (removes the npc name confliction)

    public static class PlayerTabRunnable extends BukkitRunnable {

        private static final GroupManager api = LuckPerms.getApi().getGroupManager();

        //private DropPointManager dropPointManager = Carbyne.getInstance().getDropPointManager();
        private final Profile profile;
        private final Player player;
        private final EntityPlayer entityPlayer;
        private final Account account;
        private final TableTabList tab;
        private final User user;

        public PlayerTabRunnable(Player player, Profile profile, Account account, TableTabList tab) {
            this.player = player;
            this.account = account;
            this.profile = profile;
            this.tab = tab;
            this.entityPlayer = ((CraftPlayer) player).getHandle();
            this.user = LuckPerms.getApi().getUser(player.getUniqueId());
        }

        public void run() {
            tab.set(0, 1, new TextTabItem("§b§lPlayer Info:", 1));

            Group group = api.getGroup(user.getPrimaryGroup());
            String display = "Default";
            if (group != null) {
                display = group.getDisplayName();
            }
            tab.set(0, 2, new TextTabItem(" §d§lRank§7: " + ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', display)), 1));
            tab.set(0, 3, new TextTabItem(" §d§lBalance§7: \u00A9" + (int) account.getBalance(), 1));
            tab.set(0, 4, new TextTabItem(" §d§lKit Points§7: " + profile.getKitPoints(), 1));
            tab.set(0, 5, new TextTabItem(" §d§lPing§7: " + entityPlayer.ping, 1));

            tab.set(0, 7, new TextTabItem("§b§lServer Info:", 1));
            tab.set(0, 8, new TextTabItem(" §d§lTPS§7: " + new DecimalFormat("##.00").format(BigDecimal.valueOf(LagTask.getTPS())) + " §7\u2758 §d§lLag§7: " + Math.round((1.0D - LagTask.getTPS() / 20.0D) * 100.0D) + "%", 1));
            tab.set(0, 9, new TextTabItem(" §d§lPlayers Online§7: " + (Bukkit.getOnlinePlayers().size() - StaticClasses.staffManager.getVanish().size()), 1));
            tab.set(0, 10, new TextTabItem(" §d§lStaff Online§7: " + StaticClasses.staffManager.getStaff().size(), 1));
            tab.set(0, 11, new TextTabItem(" §d§lConsecutive Votes§7: " + PlayerListeners.getVoteCount(), 1));


            tab.set(0, 13, new TextTabItem("§b§lFriends:", 1));
            Resident resident = null;
            try {
                resident = TownyUniverse.getDataSource().getResident(player.getName());
            } catch (NotRegisteredException e) {
                e.printStackTrace();
            }


            // 14 to 19

            if (resident != null) {
                int c = 14;
                int r = 0;
                for (Resident friend : resident.getFriends()) {
                    Player friendPlayer = Bukkit.getPlayer(friend.getName());

                    if (friendPlayer == null)
                        continue;

                    int health = (int) friendPlayer.getHealth() / 5;

                    tab.set(r, c++, new TextTabItem(" §7§l- §a" + friendPlayer.getName() + ChatColor.translateAlternateColorCodes('&', " " + StringUtils.formatHealthBar(health)), ((CraftPlayer) friendPlayer).getHandle().ping));

                    if (c > 19) {
                        c = 14;
                        r++;
                    }

                    if (r > 3)
                        break;
                }
            }

            tab.set(1, 1, new TextTabItem("§b§lStats:", 1));
            tab.set(1, 2, new TextTabItem(" §d§lKills§7: " + profile.getKills(), 1));
            tab.set(1, 3, new TextTabItem(" §d§lDeaths§7: " + profile.getDeaths(), 1));
            tab.set(1, 4, new TextTabItem(" §d§lCarbyne Kills§7: " + profile.getCarbyneKills(), 1));
            tab.set(1, 5, new TextTabItem(" §d§lCarbyne Deaths§7: " + profile.getCarbyneDeaths(), 1));
            tab.set(1, 6, new TextTabItem(" §d§lKDR§7: " + profile.getKDR(), 1));
            tab.set(1, 7, new TextTabItem(" §d§lCarbyne KDR§7: " + profile.getCarbyneKDR(), 1));
            tab.set(1, 8, new TextTabItem(" §d§lStreak§7: " + profile.getKillStreak(), 1));

            Squad squad = StaticClasses.squadManager.getSquad(player.getUniqueId());
            tab.set(1, 11, new TextTabItem("§b§lSquad Info:" + (squad != null ? " §7(" + squad.getAllPlayers().size() + "/5)" : " §7(none)"), 1));
            if (squad != null) {
                tab.set(1, 12, new TextTabItem(" §d§lLeader§7:", 1));
                Player leader = Bukkit.getPlayer(squad.getLeader());
                int leaderHealth = (int) leader.getHealth();
                tab.set(1, 13, new TextTabItem(" §7§l- §a" + leader.getName() + ChatColor.translateAlternateColorCodes('&', " " + StringUtils.formatHealthBar(leaderHealth)), ((CraftPlayer) leader).getHandle().ping));
                tab.set(1, 14, new TextTabItem(" §d§lMembers§7:", 1));

                for (int i = 15; i < 19; i++)
                    tab.set(1, i, new TextTabItem(" §7§l- ", 1));

                int x = 15;
                for (int i = 0; i < squad.getMembers().size(); i++) {
                    Player other = Bukkit.getPlayer(squad.getMembers().get(i));
                    int otherHealth = (int) other.getHealth();
                    tab.set(1, x, new TextTabItem(" §7§l- §a" + other.getName() + ChatColor.translateAlternateColorCodes('&', " " + StringUtils.formatHealthBar(otherHealth)), ((CraftPlayer) other).getHandle().ping));
                    x++;
                }
            } else {
                for (int i = 12; i < 19; i++) {
                    tab.set(1, i, new TextTabItem("", 1));
                }
            }

            tab.set(3, 13, new TextTabItem("§b§lLocation:", 1));

            if (player.getWorld().getName().equalsIgnoreCase("player_world"))
                tab.set(3, 14, new TextTabItem(" §d§lX§7: " + player.getLocation().getBlockX() + " §d§lY§7: " + player.getLocation().getBlockY() + " §d§lZ§7: " + player.getLocation().getBlockZ(), 1));
            else
                tab.set(3, 14, new TextTabItem(" §d§lArea§7: " + getZone(player), 1));

            tab.set(3, 7, new TextTabItem("§b§lDaily Bonus:", 1));
            String text;
            if (profile.isDailyRewardsSetup())
                if (!profile.hasClaimedDailyReward())
                    if (Cooldowns.getCooldown(profile.getUniqueId(), "DailyRewardWarmUp") > 0)
                        text = ChatColor.translateAlternateColorCodes('&', "&d&lClaimable in&7: " + DateUtil.readableTime(Cooldowns.getCooldown(profile.getUniqueId(), "DailyRewardWarmUp"), true));
                    else
                        text = ChatColor.translateAlternateColorCodes('&', "&d&lClaim Now!");
                else
                    text = ChatColor.translateAlternateColorCodes('&', "&d&lNext reward&7: " + DateUtil.readableTime(profile.getRemainingDailyDayTime(), true));
            else
                text = ChatColor.translateAlternateColorCodes('&', "&d&lBegin at Spawn!");
            tab.set(3, 8, new TextTabItem(text, 1));

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

            tab.set(2, 1, new TextTabItem("§b§lQuests:", 1));
            for (int i = 2; i < 20; i++) {
                tab.set(2, i, new TextTabItem("", 1));
            }

            List<Quest> quests = StaticClasses.questHandler.getQuests(player.getUniqueId());
            if (!quests.isEmpty()) {
                int i = 2;
                for (Quest quest : quests) {
                    if (i >= 19)
                        break;

                    if (quest.isComplete(player)) {
                        tab.set(2, i++, new TextTabItem("  §d§l" + quest.getDisplayName() + ": §a§l\u2713", 1));
                    } else {
                        tab.set(2, i++, new TextTabItem("  §d§l" + quest.getDisplayName() + ":", 1));
                        List<Task> tasks = quest.getTasks();
                        for (Task task : tasks) {
                            if (!task.isCompleted(player.getUniqueId())) {
                                if (i >= 19)
                                    break;

                                tab.set(2, i++, new TextTabItem(ChatColor.translateAlternateColorCodes('&', "    " + task.getProgress(player)), 1));
                            }
                        }
                    }
                }
            }
        }

        private String getZone(Player player) {
            for (Zone zone : StaticClasses.zoneManager.getZones())
                if (zone.getPlayersInZone().contains(player.getUniqueId()))
                    return zone.getDisplayName();

            return "Safezone";
        }
    }
}
