package com.medievallords.carbyne.dungeons.mechanics.targeters;

import com.medievallords.carbyne.dungeons.mechanics.data.MechanicData;
import org.bukkit.entity.Player;

public class PlayerTarget extends Target {

    public PlayerTarget(String params, String type) {
        super(params, type);
    }

    public Player getPlayer(MechanicData data) {
        return (Player) data.getTrigger();
    }
}
