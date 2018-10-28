package com.medievallords.carbyne.dungeons.mechanics;


import com.medievallords.carbyne.dungeons.mechanics.targeters.Target;
import com.medievallords.carbyne.dungeons.mechanics.targeters.instances.ITargetEntity;
import com.medievallords.carbyne.utils.DungeonLineConfig;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;

@Getter
public class MechanicMessage extends Mechanic implements ITargetEntity {

    private String message;

    public MechanicMessage(String type, DungeonLineConfig lineConfig) {
        super(type, lineConfig);

        this.message = lineConfig.getString("message", "");
    }

    public MechanicMessage(String type, Target target, String message) {
        super(type, target);

        this.message = message;
    }

    @Override
    public boolean cast(Entity entity) {
        entity.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        return true;
    }
}
