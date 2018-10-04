package com.medievallords.carbyne.dungeons.player;

import com.medievallords.carbyne.dungeons.dungeons.instance.DungeonInstance;
import com.medievallords.carbyne.utils.Cooldowns;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;


/**
 * Created by WE on 2017-09-27.
 */

@Getter
@Setter
public class DPlayer {

    private Player bukkitPlayer;
    private DungeonInstance instance;

    private boolean ready = false;

    public DPlayer(Player bukkitPlayer, DungeonInstance instance) {
        this.bukkitPlayer = bukkitPlayer;
        this.instance = instance;
    }

    public boolean isInCombat() {
        return Cooldowns.getCooldown(bukkitPlayer.getUniqueId(), "dungeon:combat") >= 0;
    }
}
