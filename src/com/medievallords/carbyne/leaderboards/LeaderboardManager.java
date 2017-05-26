package com.medievallords.carbyne.leaderboards;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.utils.LocationSerialization;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.logging.Level;

/**
 * Created by Calvin on 1/23/2017
 * for the Carbyne-Gear project.
 */
public class LeaderboardManager {

    private Carbyne main = Carbyne.getInstance();

    private ArrayList<Leaderboard> leaderboards = new ArrayList<>();

    public LeaderboardManager() {
        load();
    }

    public void load() {
        ConfigurationSection section = main.getLeaderboardFileConfiguration().getConfigurationSection("Leaderboards");

        if (section.getKeys(false).size() > 0) {
            main.getLogger().log(Level.INFO, "Preparing to load " + section.getKeys(false).size() + " leaderboards.");

            for (String id : section.getKeys(false)) {
                Location primarySignLocation = null;
                ArrayList<Location> signLocations = new ArrayList<>();
                ArrayList<Location> headLocations = new ArrayList<>();

                if (!section.getString(id + ".PrimarySignLocation").isEmpty()) {
                    primarySignLocation = LocationSerialization.deserializeLocation(section.getString(id + ".PrimarySignLocation"));
                }

                for (String s : section.getStringList(id + ".signLocations")) {
                    signLocations.add(LocationSerialization.deserializeLocation(s));
                }

                for (String s : section.getStringList(id + ".headLocations")) {
                    headLocations.add(LocationSerialization.deserializeLocation(s));
                }

                Leaderboard leaderboard = new Leaderboard(id);
                leaderboard.setPrimarySignLocation(primarySignLocation);
                leaderboard.setSignLocations(signLocations);
                leaderboard.setHeadLocations(headLocations);

                leaderboards.add(leaderboard);
            }

            main.getLogger().log(Level.INFO, "Successfully loaded " + leaderboards.size() + " leaderboards.");
        }
    }

    public void stopAllLeaderboardTasks() {
        for (Leaderboard leaderboard : leaderboards) {
            leaderboard.stop();
        }
    }

    public Leaderboard getLeaderboard(String boardId) {
        for (Leaderboard leaderboard : leaderboards) {
            if (leaderboard.getBoardId().equalsIgnoreCase(boardId)) {
                return leaderboard;
            }
        }

        return null;
    }

    public Leaderboard getLeaderboard(Location location) {
        for (Leaderboard leaderboard : leaderboards) {
            if ((leaderboard.getPrimarySignLocation() != null && leaderboard.getPrimarySignLocation() == location)
                    || leaderboard.getSignLocations().contains(location)
                    || leaderboard.getHeadLocations().contains(location)) {
                return leaderboard;
            }
        }

        return null;
    }

    public ArrayList<Leaderboard> getLeaderboards() {
        return leaderboards;
    }
}