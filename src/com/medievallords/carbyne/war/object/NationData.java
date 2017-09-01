package com.medievallords.carbyne.war.object;

import com.medievallords.carbyne.utils.MessageManager;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Town;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Dalton on 8/8/2017.
 */
public class NationData {

    //private WarManager warManager = Carbyne.getInstance().getWarManager();

    private Nation nation;
    private Map<Town, TownData> townDatas;

    private int currentDTR, maxDTR;

    @Getter
    private boolean raidable;
    public boolean isAttacking;

    public NationData(Nation nation) {
        this.nation = nation;
        townDatas = new HashMap<>();
        setMaxDTR();
        currentDTR = maxDTR;

        for (int i = 0; i < nation.getNumTowns(); i++) {
            Town town = nation.getTowns().get(i);
            townDatas.put(town, new TownData(town));
        }
    }

    public void setMaxDTR() {
        //maxDTR = nation.getNumTowns() * warManager.getNATION_DTR_MODIFIER();
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
            String message = new String("&cThe capital of %nation%&c, &4%town%&c, is now raidable!").replace("%nation%", nation.getName()).replace("%town%", nation.getCapital().getName());
            MessageManager.broadcastMessage(message);
        }
    }

}
