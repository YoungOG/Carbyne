package com.medievallords.carbyne.missions.object.implementations.tutorialmissions;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.gear.types.CarbyneGear;
import com.medievallords.carbyne.missions.object.Mission;
import com.medievallords.carbyne.missions.object.interfaces.RepairCarbyneMission;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Williams on 2017-09-06
 * for the Carbyne project.
 */
public class RepairCarbyneClass extends Mission implements RepairCarbyneMission {

    private List<CarbyneGear> gearToRepair = new ArrayList<>();

    public RepairCarbyneClass(String name, String[] description, String timeLimit, int objectiveGoal, double reward, List<String> lootData, List<String> carbyneGear) {
        super(name, description, timeLimit, objectiveGoal, reward, lootData);

        for (String s : carbyneGear) {
            CarbyneGear gear = Carbyne.getInstance().getGearManager().getCarbyneGear(s);
            if (gear != null) {
                gearToRepair.add(gear);
            }
        }
    }

    @Override
    public List<CarbyneGear> getGearToRepair() {
        return gearToRepair;
    }
}
