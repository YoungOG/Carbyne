package com.medievallords.carbyne.profiles;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.UUID;

/**
 * Created by Calvin on 3/22/2017
 * for the Carbyne project.
 */
@Getter
@Setter
public class Profile {

    private static HashSet<Profile> profiles = new HashSet<>();

    private UUID uniqueId;
    private int kills;
    private int carbyneKills;
    private int deaths;
    private int carbyneDeaths;
    private double kdRatio;
    private int killStreak;
    private int balance;

    public Profile(UUID uniqueId) {
        this.uniqueId = uniqueId;

        profiles.add(this);
    }

    public boolean load() {
        return false;
    }

    public boolean save() {
        return false;
    }

    public static Profile getProfile(UUID uniqueId) {
        for (Profile profile : profiles) {
            if (profile.getUniqueId().equals(uniqueId)) {
                return profile;
            }
        }

        return null;
    }
}
