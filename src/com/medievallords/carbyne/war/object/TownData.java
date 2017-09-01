package com.medievallords.carbyne.war.object;

import com.medievallords.carbyne.utils.MessageManager;
import com.palmergames.bukkit.towny.object.Town;
import lombok.Getter;

/**
 * Created by Dalton on 8/8/2017.
 */
public class TownData {

    //private WarManager warManager = Carbyne.getInstance().getWarManager();

    private Town town;
    private int currentDTR, maxDTR;

    @Getter
    private boolean raidable;

    public TownData(Town town) {
        this.town = town;
        //maxDTR = town.getNumResidents() * warManager.getTOWN_DTR_MODIFIER();
        currentDTR = maxDTR;
    }

    public void incrementDTR() {
        if (currentDTR + 1 > maxDTR) return;
        currentDTR++;
    }

    public void decrementDTR() {
        if (currentDTR == 0) return;
        currentDTR--;
        if (currentDTR == 0) {
            raidable = true;
            String msg = new String("&cThe town of &4%town%&c is now raidable!").replace("%town%", town.getName());
            MessageManager.broadcastMessage(msg);
        }
    }

}
