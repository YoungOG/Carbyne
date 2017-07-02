package com.medievallords.carbyne.profiles;

import com.medievallords.carbyne.Carbyne;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.UUID;
import java.util.logging.Level;

public class ProfileManager {

    private Carbyne main = Carbyne.getInstance();
    private HashSet<Profile> loadedProfiles = new HashSet<>();
    private MongoCollection<Document> profileCollection = main.getMongoDatabase().getCollection("profiles");

    public ProfileManager() {
        loadProfiles();
        startResetting();

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
                int kills = document.getInteger("kills");
                int carbyneKills = document.getInteger("carbynekills");
                int deaths = document.getInteger("deaths");
                int carbyneDeaths = document.getInteger("carbynedeaths");
                int killstreak = document.getInteger("killstreak");
                boolean showEffects = document.getBoolean("showeffects");
                boolean safelyLogged = document.getBoolean("safelyLogged");

                Profile profile = new Profile(uniqueId);
                profile.setUsername(username);
                profile.setKills(kills);
                profile.setCarbyneKills(carbyneKills);
                profile.setDeaths(deaths);
                profile.setCarbyneDeaths(carbyneDeaths);
                profile.setKillStreak(killstreak);
                profile.setShowEffects(showEffects);
                profile.setSafelyLogged(safelyLogged);
                profile.setPin(document.getString("pin") != null ? document.getString("pin") : "");
                profile.setPreviousInventoryContentString(document.getString("previous-inventory") != null ? document.getString("previous-inventory") : "");

                if (document.containsKey("pvptime"))
                    profile.setPvpTime(document.getLong("pvptime"));

                if (document.containsKey("pvptimepaused"))
                    profile.setPvpTimePaused(document.getBoolean("pvptimepaused"));

                if (document.containsKey("timeleft"))
                    profile.setTimeLeft(document.getLong("timeleft"));

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

            if (async) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        for (Profile profile : getLoadedProfiles()) {
                            Document document = new Document("uniqueId", profile.getUniqueId().toString());
                            document.append("username", profile.getUsername());
                            document.append("kills", profile.getKills());
                            document.append("carbynekills", profile.getCarbyneKills());
                            document.append("deaths", profile.getDeaths());
                            document.append("carbynedeaths", profile.getCarbyneDeaths());
                            document.append("killstreak", profile.getKillStreak());
                            document.append("pvptime", profile.getPvpTime());
                            document.append("pvptimepaused", profile.isPvpTimePaused());
                            document.append("showeffects", profile.hasEffectsToggled());
                            document.append("safelyLogged", profile.isSafelyLogged());
                            document.append("pin", profile.getPin());
                            document.append("previous-inventory", profile.getPreviousInventoryContentString());
                            if (profile.getTimeLeft() > 1)
                            document.append("timeleft", profile.getTimeLeft());

                            profileCollection.replaceOne(Filters.eq("uniqueId", profile.getUniqueId().toString()), document, new UpdateOptions().upsert(true));
                        }

                        main.getLogger().log(Level.INFO, "Successfully saved " + getLoadedProfiles().size() + " profiles. Took (" + (System.currentTimeMillis() - startTime) + "ms).");
                    }
                }.runTaskAsynchronously(main);
            } else {
                for (Profile profile : getLoadedProfiles()) {
                    Document document = new Document("uniqueId", profile.getUniqueId().toString());
                    document.append("username", profile.getUsername());
                    document.append("kills", profile.getKills());
                    document.append("carbynekills", profile.getCarbyneKills());
                    document.append("deaths", profile.getDeaths());
                    document.append("carbynedeaths", profile.getCarbyneDeaths());
                    document.append("killstreak", profile.getKillStreak());
                    document.append("pvptime", profile.getPvpTime());
                    document.append("pvptimepaused", profile.isPvpTimePaused());
                    document.append("showeffects", profile.hasEffectsToggled());
                    document.append("safelyLogged", profile.isSafelyLogged());
                    document.append("pin", profile.getPin());
                    document.append("previous-inventory", profile.getPreviousInventoryContentString());
                    if (profile.getTimeLeft() > 1)
                    document.append("timeleft", profile.getTimeLeft());

                    profileCollection.replaceOne(Filters.eq("uniqueId", profile.getUniqueId().toString()), document, new UpdateOptions().upsert(true));
                }

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
            profile.setShowEffects(true);
            profile.setSafelyLogged(false);
            //Set remaining time at creation to 30 minutes.
            profile.setPvpTime(System.currentTimeMillis() + ((60 * 30) * 1000));
            profile.setPin("");
            profile.setPreviousInventoryContentString("");

            Document document = new Document("uniqueId", profile.getUniqueId().toString());
            document.append("username", profile.getUsername());
            document.append("kills", profile.getKills());
            document.append("carbynekills", profile.getCarbyneKills());
            document.append("deaths", profile.getDeaths());
            document.append("carbynedeaths", profile.getCarbyneDeaths());
            document.append("killstreak", profile.getKillStreak());
            document.append("pvptime", profile.getPvpTime());
            document.append("pvptimepaused", profile.isPvpTimePaused());
            document.append("showeffects", profile.hasEffectsToggled());
            document.append("safelyLogged", profile.isSafelyLogged());
            document.append("pin", profile.getPin());
            document.append("previous-inventory", profile.getPreviousInventoryContentString());

            new BukkitRunnable() {
                @Override
                public void run() {
                    profileCollection.insertOne(document);
                }
            }.runTaskAsynchronously(main);

            getLoadedProfiles().add(profile);
            Bukkit.getServer().getLogger().log(Level.INFO, "A new profile was created for " + player.getName() + " at " + System.currentTimeMillis() + "time millis");
        }
    }

    public Boolean hasProfile(UUID uniqueId) {
        for (Profile profile : getLoadedProfiles()) {
            if (profile.getUniqueId().equals(uniqueId)) {
                return true;
            }
        }
        return false;
    }

    public Profile getProfile(UUID uniqueId) {
        for (Profile profile : getLoadedProfiles()) {
            if (profile.getUniqueId().equals(uniqueId)) {
                return profile;
            }
        }
        return null;
    }

    public Profile getProfile(String username) {
        for (Profile profile : getLoadedProfiles()) {
            if (profile.getUsername().equals(username)) {
                return profile;
            }
        }
        return null;
    }

    public HashSet<Profile> getLoadedProfiles() {
        return loadedProfiles;
    }

    public void startResetting() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Profile profile : loadedProfiles) {
                    if (profile.isPvpTimePaused() && (profile.getPvpTime() > 0 && profile.getRemainingPvPTime() > 0)) {
                        long timeLeft = profile.getTimeLeft();
                        if (timeLeft > 1) {
                            profile.setPvpTime(System.currentTimeMillis() + timeLeft);
                        }
                    }
                }
            }
        }.runTaskTimerAsynchronously(main, 0, 15);
    }
}
