package com.medievallords.carbyne.quests.types;

import com.medievallords.carbyne.quests.Task;
import com.medievallords.carbyne.quests.config.QuestLineConfig;
import org.bukkit.entity.Player;

import java.util.List;

public class RepairCarbyneTask extends Task {

    private String gear;

    public RepairCarbyneTask(String name, QuestLineConfig line, List<String> commands) {
        super(name, line, commands);

        this.gear = line.getString("gear", "any");
    }

    public String getGear() {
        return gear;
    }
}
