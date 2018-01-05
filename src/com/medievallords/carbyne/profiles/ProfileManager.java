package com.medievallords.carbyne.profiles;

import com.medievallords.carbyne.Carbyne;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

//import com.medievallords.carbyne.utils.EntityHider;

public class ProfileManager {

    private Carbyne main = Carbyne.getInstance();
    @Getter
    private HashSet<Profile> loadedProfiles = new HashSet<>();
    private MongoCollection<Document> profileCollection = main.getMongoDatabase().getCollection("profiles");
    //@Getter
    //private EntityHider entityHider;

    public ProfileManager() {
        //this.entityHider = new EntityHider(Carbyne.getInstance(), EntityHider.Policy.BLACKLIST);

        loadProfiles();
        //startResetting();

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
                        professionLevel = 1;
                boolean showEffects = document.getBoolean("showEffects"),
                        playSounds = document.getBoolean("playSounds");
                boolean safelyLogged = document.getBoolean("safelyLogged");
                List<UUID> ignoredPlayers = new ArrayList<>();
                long professionResetCooldown = 0;
                double professionProgress = 0,
                        requiredProfessionProgress = 1;

                if (document.containsKey("ignoredPlayers")) {
                    List<String> uuidNameIgnoredPlayers = (List<String>) document.get("ignoredPlayers");

                    for (String s : uuidNameIgnoredPlayers)
                        ignoredPlayers.add(UUID.fromString(s));
                }

                if (document.containsKey("professionLevel"))
                    professionLevel = document.getInteger("professionLevel");

                if (document.containsKey("professionProgress"))
                    professionProgress = document.getDouble("professionProgress");

                if (document.containsKey("requiredProfessionProgress"))
                    requiredProfessionProgress = document.getDouble("requiredProfessionProgress");

                if (document.containsKey("professionResetCooldown"))
                    professionResetCooldown = document.getLong("professionResetCooldown");

                Profile profile = new Profile(uniqueId);
                profile.setUsername(username);
                profile.setKills(kills);
                profile.setCarbyneKills(carbyneKills);
                profile.setDeaths(deaths);
                profile.setCarbyneDeaths(carbyneDeaths);
                profile.setKillStreak(killstreak);
                profile.setShowEffects(showEffects);
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

                profile.setDailyRewardsSetup(document.getBoolean("dailyRewardSetup"));
                if (profile.isDailyRewardsSetup()) {
                    profile.setDailyRewardDay(document.getInteger("dailyRewardDay"));
                    profile.setDailyRewardDayTime(document.getLong("dailyRewardDayTime"));
                    profile.setHasClaimedDailyReward(document.getBoolean("hasClaimedDailyReward"));
                    profile.setHasCompletedDailyChallenge(document.getBoolean("hasCompletedDailyChallenge"));

                    int[] dailyRewardsIndex = new int[8];
                    Document dailyRewardsIndexDocument = (Document) document.get("dailyRewardsIndex");
                    if (dailyRewardsIndexDocument != null && dailyRewardsIndexDocument.keySet().size() > 0)
                        for (String rewardId : dailyRewardsIndexDocument.keySet())
                            dailyRewardsIndex[Integer.parseInt(rewardId)] = dailyRewardsIndexDocument.getInteger(rewardId);
                    profile.setDailyRewardsIndex(dailyRewardsIndex);

                    Document dailyRewardsDocument = (Document) document.get("dailyRewards");
                    if (dailyRewardsDocument != null && dailyRewardsDocument.keySet().size() > 0)
                        for (String rewardId : dailyRewardsDocument.keySet())
                            profile.getDailyRewards().put(Integer.parseInt(rewardId), dailyRewardsDocument.getBoolean(rewardId));
                }

                if (document.containsKey("crateProgression")) {
                    Document crateProgression = (Document) document.get("crateProgression");

                    if (crateProgression != null && crateProgression.keySet().size() > 0)
                        for (String crateName : crateProgression.keySet())
                            profile.getCrateProgression().put(crateName, crateProgression.getDouble(crateName));
                }

                if (document.containsKey("pvpTime"))
                    profile.setPvpTime(document.getLong("pvpTime"));

                if (document.containsKey("pvpTimePaused"))
                    profile.setPvpTimePaused(document.getBoolean("pvpTimePaused"));

                if (document.containsKey("timeLeft"))
                    profile.setTimeLeft(document.getLong("timeLeft"));

                loadedProfiles.add(profile);
            }

            main.getLogger().log(Level.INFO, "Successfully loaded " + profileCollection.count() + " profiles. Took (" + (System.currentTimeMillis() - startTime) + "ms).");
        }

        System.gc();
    }

    public void saveProfiles(boolean async) {
        if (getLoadedProfiles().size() > 0) {
            main.getLogger().log(Level.INFO, "Preparing to save " + getLoadedProfiles().size() + " profiles.");

            long startTime = System.currentTimeMillis();

            for (Profile profile : getLoadedProfiles()) {
                Document document = new Document("uniqueId", profile.getUniqueId().toString());
                document.append("username", profile.getUsername());
                document.append("kills", profile.getKills());
                document.append("carbyneKills", profile.getCarbyneKills());
                document.append("deaths", profile.getDeaths());
                document.append("carbyneDeaths", profile.getCarbyneDeaths());
                document.append("killStreak", profile.getKillStreak());
                document.append("pvpTime", profile.getPvpTime());
                document.append("pvpTimePaused", profile.isPvpTimePaused());
                document.append("showEffects", profile.hasEffectsToggled());
                document.append("playSounds", profile.hasSoundsToggled());
                document.append("safelyLogged", profile.isSafelyLogged());
                document.append("pin", profile.getPin());
                document.append("previousInventory", profile.getPreviousInventoryContentString());
                document.append("dailyRewardSetup", profile.isDailyRewardsSetup());

                if (profile.isDailyRewardsSetup()) {
                    document.append("dailyRewardDay", profile.getDailyRewardDay());
                    document.append("dailyRewardDayTime", profile.getDailyRewardDayTime());
                    document.append("hasClaimedDailyReward", profile.hasClaimedDailyReward());
                    document.append("hasCompletedDailyChallenge", profile.hasCompletedDailyChallenge());

                    Document dailyRewardsIndexDocument = new Document();
                    for (int i = 0; i < 8; i++)
                        dailyRewardsIndexDocument.put("" + i, profile.getDailyRewardsIndex()[i]);
                    document.append("dailyRewardsIndex", dailyRewardsIndexDocument);

                    Document dailyRewardsDocument = new Document();
                    for (int rewardId : profile.getDailyRewards().keySet())
                        dailyRewardsDocument.put("" + rewardId, profile.getDailyRewards().get(rewardId));
                    document.append("dailyRewards", dailyRewardsDocument);
                }

                if (profile.getCrateProgression().keySet().size() > 0) {
                    Document crate = new Document();

                    for (String crateName : profile.getCrateProgression().keySet())
                        crate.put(crateName, profile.getCrateProgression().get(crateName));

                    document.append("crateProgression", crate);
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

                if (!async)
                    profileCollection.replaceOne(Filters.eq("uniqueId", profile.getUniqueId().toString()), document, new UpdateOptions().upsert(true));
                else
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            profileCollection.replaceOne(Filters.eq("uniqueId", profile.getUniqueId().toString()), document, new UpdateOptions().upsert(true));
                        }
                    }.runTaskAsynchronously(main);
            }

            main.getLogger().log(Level.INFO, "Successfully saved " + getLoadedProfiles().size() + " profiles. Took (" + (System.currentTimeMillis() - startTime) + "ms).");
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
            profile.setShowEffects(true);
            profile.setPlaySounds(true);
            profile.setSafelyLogged(false);
            profile.setProfileChatChannel(Profile.ProfileChatChannel.GLOBAL);
            profile.setPvpTime(System.currentTimeMillis() + ((60 * 30) * 1000));
            profile.setTimeLeft(60 * 30 * 1000);
            profile.setPvpTimePaused(true);
            profile.setPin("");
            profile.setPreviousInventoryContentString("");
            profile.setDailyRewardsSetup(false);

            Document document = new Document("uniqueId", profile.getUniqueId().toString());
            document.append("username", profile.getUsername());
            document.append("kills", profile.getKills());
            document.append("carbyneKills", profile.getCarbyneKills());
            document.append("deaths", profile.getDeaths());
            document.append("carbyneDeaths", profile.getCarbyneDeaths());
            document.append("killStreak", profile.getKillStreak());
            document.append("pvpTime", profile.getPvpTime());
            document.append("pvpTimePaused", profile.isPvpTimePaused());
            document.append("showEffects", profile.hasEffectsToggled());
            document.append("playSounds", profile.hasSoundsToggled());
            document.append("safelyLogged", profile.isSafelyLogged());
            document.append("pin", profile.getPin());
            document.append("previousInventory", profile.getPreviousInventoryContentString());
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
