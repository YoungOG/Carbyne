package com.medievallords.carbyne.dungeons.triggers;


import com.medievallords.carbyne.dungeons.mechanics.Mechanic;
import com.medievallords.carbyne.dungeons.mechanics.data.MechanicData;
import com.medievallords.carbyne.utils.DungeonLineConfig;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

@Getter
public class MobTrigger extends Trigger {

    private String mobType;
    private int state;
    private int deaths = 0;

    public MobTrigger(String name, Location location, DungeonLineConfig dlc) {
        super(name, location, dlc);

        this.mobType = dlc.getString("mob", "ZOMBIE");
        this.state = dlc.getInt("state", 1);
    }

    public MobTrigger(String name, Location location, String mobType, int state) {
        super(name, location, null);

        this.mobType = mobType;
        this.state = state;
    }

    public void trigger(Entity entity, Location location) {
        if (state == 1 && deaths >= 1) {
            return;
        }

        //location.setWorld(getLocation().getWorld());
        MechanicData data = new MechanicData(entity, location);
        for (Mechanic mechanic : getMechanics()) {
            mechanic.runMechanic(data);
        }

        deaths++;
    }
}
