package com.medievallords.carbyne.dungeons.dungeons;

import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.StaticClasses;
import org.bukkit.entity.Player;

import java.util.List;

public class DungeonQueuer {

    public void startDungeon(List<Player> playerList, String dungeonName) {
        Dungeon dungeon = StaticClasses.dungeonHandler.getDungeon(dungeonName);
        if (dungeon == null) {
            playerList.forEach(player -> MessageManager.sendMessage(player, "&cThere's no such dungeon"));
            return;
        }

        dungeon.startDungeon(playerList);
    }
}
