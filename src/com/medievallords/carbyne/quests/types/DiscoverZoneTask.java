package com.medievallords.carbyne.quests.types;

import com.medievallords.carbyne.quests.Task;
import com.medievallords.carbyne.quests.config.QuestLineConfig;
import org.bukkit.entity.Player;

import java.util.List;

public class DiscoverZoneTask extends Task {

    private String zone;

    public DiscoverZoneTask(String name, QuestLineConfig line, List<String> commands) {
        super(name, line, commands);

        this.zone = line.getString("zone", "NOT_A_ZONE");
    }

    public String getZone() {
        return zone;
    }
}
