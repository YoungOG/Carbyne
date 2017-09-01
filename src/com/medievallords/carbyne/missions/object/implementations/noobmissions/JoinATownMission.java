package com.medievallords.carbyne.missions.object.implementations.noobmissions;

import com.medievallords.carbyne.missions.enums.Difficulty;
import com.medievallords.carbyne.missions.object.Mission;
import com.medievallords.carbyne.missions.object.interfaces.JoinTownMission;

/**
 * Created by Dalton on 8/14/2017.
 */
public class JoinATownMission extends Mission implements JoinTownMission {

    public JoinATownMission() {
        super(Difficulty.BABY, "&2Join A Town", new String[]{
                "&bMedieval Lords uses Towny to protect land and store possessions.",
                "&bYou can create a town with /t new name or join someone elses town."
        }, "12h", 1, 1, null);
    }
}
