package com.medievallords.carbyne.quests.types;

import com.medievallords.carbyne.quests.Task;
import com.medievallords.carbyne.quests.config.QuestLineConfig;

import java.util.List;

public class EnterDungeonTask extends Task {

    private String dungeon;

    public EnterDungeonTask(String name, QuestLineConfig line, List<String> commands) {
        super(name, line, commands);

        this.dungeon = line.getString("dungeon", "NOT_A_DUNGEON");
    }

    public String getDungeon() {
        return dungeon;
    }
}
