package com.medievallords.carbyne.profiles;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.quests.Quest;
import com.medievallords.carbyne.quests.QuestHandler;
import com.medievallords.carbyne.quests.Task;
import com.medievallords.carbyne.utils.StaticClasses;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.logging.Level;


public class ProfileManager {

    private Carbyne main = Carbyne.getInstance();
    @Getter
    private HashSet<Profile> loadedProfiles = new HashSet<>();
    private MongoCollection<Document> profileCollection = main.getMongoDatabase().getCollection("profiles");

    public ProfileManager() {
        loadProfiles();

        new BukkitRunnable() {
            @Override
            public void run() {
                saveProfiles(true);
            }
        }.runTaskTimerAsynchronously(main, 0L, 300 * 20L);
    }

    public void loadProfiles() {
        if (profileCollection.count() > 0) {
            main.getLogger().log(Level.INFO, "Preparing to load " + profileCollection.count() + " profiles.");

            long startTime = System.currentTimeMillis();

            for (Document document : profileCollection.find()) {
                UUID uniqueId = UUID.fromString(document.getString("uniqueId"));
                String username = document.getString("username");
                int kills = document.getInteger("kills"),
                        carbyneKills = document.getInteger("carbyneKills"),
                        deaths = document.getInteger("deaths"),
                        carbyneDeaths = document.getInteger("carbyneDeaths"),
                        killstreak = document.getInteger("killStreak"),
                        professionLevel = 1,
                        questsLeft = 10,
                        skipsLeft = 2;
                boolean showEffects = document.getBoolean("showEffects"),
                        playSounds = document.getBoolean("playSounds");
                boolean safelyLogged = document.getBoolean("safelyLogged");
                List<UUID> ignoredPlayers = new ArrayList<>();
                long professionResetCooldown = 0,
                        nextQuest = 1728000,
                        nextSkip = 1728000;
                double professionProgress = 0,
                        requiredProfessionProgress = 1;
                boolean showTab = true, vanishEffect = true, chatEnabled = true, announcementsEnabled = true;
                HashMap<String, Long> usedKits = new HashMap<>();
                List<String> dormantQuests = new ArrayList<>();
                List<String> quests = new ArrayList<>();
                List<String> forcedQuests = new ArrayList<>();

                int kitPoints = 0;

                if (document.containsKey("kitPoints")) {
                    kitPoints = document.getInteger("kitPoints");
                }

                if (document.containsKey("vanishEffect")) {
                    vanishEffect = document.getBoolean("vanishEffect");
                }

                if (document.containsKey("chatEnabled")) {
                    chatEnabled = document.getBoolean("chatEnabled");
                }

                if (document.containsKey("announcementsEnabled")) {
                    announcementsEnabled = document.getBoolean("announcementsEnabled");
                }

                if (document.containsKey("ignoredPlayers")) {
                    List<String> uuidNameIgnoredPlayers = (List<String>) document.get("ignoredPlayers");

                    for (String s : uuidNameIgnoredPlayers)
                        ignoredPlayers.add(UUID.fromString(s));
                }

                if (document.containsKey("dormantQuests"))
                    dormantQuests = (List<String>) document.get("dormantQuests");

                if (document.containsKey("quests"))
                    quests = (List<String>) document.get("quests");

                if (document.containsKey("forcedQuests"))
                    forcedQuests = (List<String>) document.get("forcedQuests");

                if (document.containsKey("professionLevel"))
                    professionLevel = document.getInteger("professionLevel");

                if (document.containsKey("showTab"))
                    showTab = document.getBoolean("showTab");

                if (document.containsKey("professionProgress"))
                    professionProgress = document.getDouble("professionProgress");

                if (document.containsKey("requiredProfessionProgress"))
                    requiredProfessionProgress = document.getDouble("requiredProfessionProgress");

                if (document.containsKey("professionResetCooldown"))
                    professionResetCooldown = document.getLong("professionResetCooldown");

                if (document.containsKey("questsLeft"))
                    questsLeft = document.getInteger("questsLeft");

                if (document.containsKey("skipsLeft"))
                    skipsLeft = document.getInteger("skipsLeft");

                if (document.containsKey("nextQuest"))
                    nextQuest = document.getLong("nextQuest");

                if (document.containsKey("nextSkip"))
                    nextSkip = document.getLong("nextSkip");

                Profile profile = new Profile(uniqueId);
                profile.setUsername(username);
                profile.setKills(kills);
                profile.setCarbyneKills(carbyneKills);
                profile.setDeaths(deaths);
                profile.setCarbyneDeaths(carbyneDeaths);
                profile.setKillStreak(killstreak);
                profile.setKitPoints(kitPoints);
                profile.setShowEffects(showEffects);
                profile.setShowTab(showTab);
                profile.setPlaySounds(playSounds);
                profile.setSafelyLogged(safelyLogged);
                profile.setPin(document.getString("pin") != null ? document.getString("pin") : "");
                profile.setPreviousInventoryContentString(document.getString("previousInventory") != null ? document.getString("previousInventory") : "");
                profile.setProfileChatChannel(Profile.ProfileChatChannel.GLOBAL);
                profile.setIgnoredPlayers(ignoredPlayers);
                profile.setProfessionLevel(professionLevel);
                profile.setProfessionProgress(professionProgress);
                profile.setRequiredProfessionProgress(requiredProfessionProgress);
                profile.setProfessionResetCooldown(professionResetCooldown);
                profile.setDormantQuests(dormantQuests);
                profile.setForcedQuests(forcedQuests);
                profile.setQuestSkipsLeft(skipsLeft);
                profile.setQuestsLeft(questsLeft);
                profile.setQuestNext(nextQuest);
                profile.setSkipTime(nextSkip);
                profile.setQuests(quests);
                profile.setVanishEffect(vanishEffect);
                profile.setChatEnabled(chatEnabled);
                profile.setAnnouncementsEnabled(announcementsEnabled);

                profile.setDailyRewardsSetup(document.getBoolean("dailyRewardSetup"));
                if (profile.isDailyRewardsSetup()) {
                    profile.setDailyRewardDay(document.getInteger("dailyRewardDay"));
                    profile.setDailyRewardDayTime(document.getLong("dailyRewardDayTime"));
                    profile.setHasClaimedDailyReward(document.getBoolean("hasClaimedDailyReward"));

                    Document dailyRewardsDocument = (Document) document.get("dailyRewards");
                    if (dailyRewardsDocument != null && dailyRewardsDocument.keySet().size() > 0)
                        for (String rewardId : dailyRewardsDocument.keySet())
                            profile.getDailyRewards().put(Integer.parseInt(rewardId), dailyRewardsDocument.getBoolean(rewardId));
                }

                if (document.containsKey("pvpTime"))
                    profile.setPvpTime(document.getLong("pvpTime"));

                if (document.containsKey("pvpTimePaused"))
                    profile.setPvpTimePaused(document.getBoolean("pvpTimePaused"));

                if (document.containsKey("timeLeft"))
                    profile.setTimeLeft(document.getLong("timeLeft"));

                if (document.containsKey("hasClaimedRankRewards"))
                    profile.setHasClaimedRankRewards(document.getBoolean("hasClaimedRankRewards"));

                if (document.containsKey("usedKits")) {
                    Document document1 = (Document) document.get("usedKits");

                    if (document1 != null)
                        for (String str : document1.keySet())
                            usedKits.put(str, (long) document1.get(str));

                    profile.setUsedKits(usedKits);
                }

                loadedProfiles.add(profile);
            }

            main.getLogger().log(Level.INFO, "Successfully loaded " + profileCollection.count() + " profiles. Took (" + (System.currentTimeMillis() - startTime) + "ms).");
        }

        System.gc();
    }

    public void save() {
        QuestHandler questHandler = StaticClasses.questHandler;
        for (Profile profile : getLoadedProfiles()) {
            Document document = new Document("uniqueId", profile.getUniqueId().toString());
            document.append("username", profile.getUsername());
            document.append("kills", profile.getKills());
            document.append("carbyneKills", profile.getCarbyneKills());
            document.append("deaths", profile.getDeaths());
            document.append("carbyneDeaths", profile.getCarbyneDeaths());
            document.append("killStreak", profile.getKillStreak());
            document.append("kitPoints", profile.getKitPoints());
            document.append("usedKits", profile.getUsedKits());
            document.append("pvpTime", profile.getPvpTime());
            document.append("pvpTimePaused", profile.isPvpTimePaused());
            document.append("showEffects", profile.hasEffectsToggled());
            document.append("showTab", profile.isShowTab());
            document.append("playSounds", profile.hasSoundsToggled());
            document.append("safelyLogged", profile.isSafelyLogged());
            document.append("pin", profile.getPin());
            document.append("previousInventory", profile.getPreviousInventoryContentString());
            document.append("hasClaimedRankRewards", profile.isHasClaimedRankRewards());
            document.append("dailyRewardSetup", profile.isDailyRewardsSetup());
            document.append("dormantQuests", profile.getDormantQuests());
            document.append("forcedQuests", profile.getForcedQuests());
            document.append("questsLeft", profile.getQuestsLeft());
            document.append("skipsLeft", profile.getQuestSkipsLeft());
            document.append("nextQuest", profile.getQuestNext());
            document.append("nextSkip", profile.getSkipTime());
            document.append("vanishEffect", profile.isVanishEffect());
            document.append("chatEnabled", profile.isChatEnabled());
            document.append("announcementsEnabled", profile.isAnnouncementsEnabled());

            List<String> quests = new ArrayList<>();
            for (Quest quest : questHandler.getQuests(profile.getUniqueId())) {
                StringBuilder builder = new StringBuilder(quest.getName() + ":" + quest.getPlayers().get(profile.getUniqueId()) + "!");
                for (Task task : quest.getTasks())
                    builder.append(task.getName()).append(":").append(task.getPlayers().get(profile.getUniqueId())).append(",");

                //questName!task1:10,task2:20
                quests.add(builder.toString());
            }

            document.append("quests", quests);

            if (profile.isDailyRewardsSetup()) {
                document.append("dailyRewardDay", profile.getDailyRewardDay());
                document.append("dailyRewardDayTime", profile.getDailyRewardDayTime());
                document.append("hasClaimedDailyReward", profile.hasClaimedDailyReward());

                Document dailyRewardsDocument = new Document();
                for (int rewardId : profile.getDailyRewards().keySet())
                    dailyRewardsDocument.put("" + rewardId, profile.getDailyRewards().get(rewardId));
                document.append("dailyRewards", dailyRewardsDocument);
            }

            if (!profile.getIgnoredPlayers().isEmpty()) {
                List<String> uuids = new ArrayList<>();

                for (UUID id : profile.getIgnoredPlayers())
                    uuids.add(id.toString());

                document.append("ignoredPlayers", uuids);
            }

            document.append("professionLevel", profile.getProfessionLevel());
            document.append("professionProgress", profile.getProfessionProgress());
            document.append("requiredProfessionProgress", profile.getRequiredProfessionProgress());
            document.append("professionResetCooldown", profile.getProfessionResetCooldown());

            if (profile.getTimeLeft() > 1)
                document.append("timeLeft", profile.getTimeLeft());

            profileCollection.replaceOne(Filters.eq("uniqueId", profile.getUniqueId().toString()), document, new UpdateOptions().upsert(true));
        }
    }

    public void saveProfiles(boolean async) {
        if (getLoadedProfiles().size() > 0) {
            main.getLogger().log(Level.INFO, "Preparing to save " + getLoadedProfiles().size() + " profiles.");

            long startTime = System.currentTimeMillis();

            if (async) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        save();
                        main.getLogger().log(Level.INFO, "Successfully saved " + getLoadedProfiles().size() + " profiles. Took (" + (System.currentTimeMillis() - startTime) + "ms).");
                    }
                }.runTaskAsynchronously(main);
            } else {
                save();
                main.getLogger().log(Level.INFO, "Successfully saved " + getLoadedProfiles().size() + " profiles. Took (" + (System.currentTimeMillis() - startTime) + "ms).");
            }
        }
    }

    public void createProfile(Player player) {
        if (!hasProfile(player.getUniqueId())) {
            Profile profile = new Profile(player.getUniqueId());
            profile.setUsername(player.getName());
            profile.setKills(0);
            profile.setCarbyneKills(0);
            profile.setDeaths(0);
            profile.setCarbyneDeaths(0);
            profile.setKillStreak(0);
            profile.setKitPoints(0);
            profile.setUsedKits(new HashMap<>());
            profile.setShowEffects(true);
            profile.setShowTab(true);
            profile.setPlaySounds(true);
            profile.setSafelyLogged(false);
            profile.setProfileChatChannel(Profile.ProfileChatChannel.GLOBAL);
            profile.setPvpTime(System.currentTimeMillis() + ((60 * 30) * 1000));
            profile.setTimeLeft(60 * 30 * 1000);
            profile.setPvpTimePaused(true);
            profile.setPin("");
            profile.setPreviousInventoryContentString("");
            profile.setDailyRewardsSetup(false);
            profile.setDormantQuests(new ArrayList<>());
            profile.setForcedQuests(StaticClasses.questHandler.getForcedQuestsByName());

            Document document = new Document("uniqueId", profile.getUniqueId().toString());
            document.append("username", profile.getUsername());
            document.append("kills", profile.getKills());
            document.append("carbyneKills", profile.getCarbyneKills());
            document.append("deaths", profile.getDeaths());
            document.append("carbyneDeaths", profile.getCarbyneDeaths());
            document.append("killStreak", profile.getKillStreak());
            document.append("kitPoints", profile.getKitPoints());
            document.append("usedKits", profile.getUsedKits());
            document.append("pvpTime", profile.getPvpTime());
            document.append("pvpTimePaused", profile.isPvpTimePaused());
            document.append("showEffects", profile.hasEffectsToggled());
            document.append("playSounds", profile.hasSoundsToggled());
            document.append("safelyLogged", profile.isSafelyLogged());
            document.append("pin", profile.getPin());
            document.append("previousInventory", profile.getPreviousInventoryContentString());
            document.append("hasClaimedRankRewards", profile.isHasClaimedRankRewards());
            document.append("dailyRewardSetup", profile.isDailyRewardsSetup());

            if (!profile.getIgnoredPlayers().isEmpty()) {
                List<String> uuids = new ArrayList<>();

                for (UUID id : profile.getIgnoredPlayers())
                    uuids.add(id.toString());

                document.append("ignoredPlayers", uuids);
            }

            document.append("professionLevel", profile.getProfessionLevel());
            document.append("professionProgress", profile.getProfessionProgress());
            document.append("requiredProfessionProgress", profile.getRequiredProfessionProgress());
            document.append("professionResetCooldown", profile.getProfessionResetCooldown());
            document.append("dormantQuests", profile.getDormantQuests());
            document.append("forcedQuests", profile.getForcedQuests());

            new BukkitRunnable() {
                @Override
                public void run() {
                    profileCollection.insertOne(document);
                }
            }.runTaskAsynchronously(main);

            getLoadedProfiles().add(profile);
            Bukkit.getServer().getLogger().log(Level.INFO, "A new profile was created for " + player.getName() + ".");
        }
    }

    public Boolean hasProfile(UUID uniqueId) {
        for (Profile profile : getLoadedProfiles())
            if (profile.getUniqueId().equals(uniqueId))
                return true;

        return false;
    }

    public Profile getProfile(UUID uniqueId) {
        for (Profile profile : getLoadedProfiles())
            if (profile.getUniqueId().equals(uniqueId))
                return profile;

        return null;
    }

    public Profile getProfile(String username) {
        for (Profile profile : getLoadedProfiles())
            if (profile.getUsername().equals(username))
                return profile;

        return null;
    }

    public void startResetting() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Profile profile : loadedProfiles)
                    if (profile.isPvpTimePaused() && (profile.getPvpTime() > 0 && profile.getRemainingPvPTime() > 0)) {
                        long timeLeft = profile.getTimeLeft();

                        if (timeLeft > 1)
                            profile.setPvpTime(System.currentTimeMillis() + timeLeft);
                    }
            }
        }.runTaskTimerAsynchronously(main, 0, 5);
    }
}
