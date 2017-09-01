package com.medievallords.carbyne.missions.object;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.missions.MissionsManager;
import com.medievallords.carbyne.missions.enums.Difficulty;
import lombok.Getter;

/**
 * Created by Dalton on 8/9/2017.
 */
public class PlayerMissionData {

    private static final MissionsManager missionsManager = Carbyne.getInstance().getMissionsManager();

    @Getter
    private Mission[] currentMissions;
    @Getter
    private Mission dailyChallenge;

    public PlayerMissionData(Mission[] currentMissions) {
        this.currentMissions = currentMissions;
        dailyChallenge = generateDailyChallenge();
    }

    /**
     * PRECONDITION: 12 <= slots >= 14
     *
     * @param slot The mission slot from the players inventory
     */
    public Mission getMissionFromSlot(final int slot) {
        switch (slot) {
            case 10:
                return dailyChallenge;
            case 12:
                return currentMissions[0];
            case 13:
                return currentMissions[1];
            case 14:
                return currentMissions[2];
            default:
                return null;
        }
    }

    private Mission generateDailyChallenge() {
        return missionsManager.chooseRandomMission(Difficulty.CRAZY);
    }

    public void setRandomDailyChallenge() {
        dailyChallenge = generateDailyChallenge();
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 37 + currentMissions.hashCode() + dailyChallenge.hashCode();
        return hash;
    }

}
