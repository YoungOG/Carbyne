package com.medievallords.carbyne.quests.types;

import com.medievallords.carbyne.quests.Task;
import com.medievallords.carbyne.quests.config.QuestLineConfig;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class InteractWithNPCTask extends Task {

    private List<String> npcs = new ArrayList<>();

    public InteractWithNPCTask(String name, QuestLineConfig line, List<String> commands) {
        super(name, line, commands);

        String split[] = line.getString("npcs", "NOT_AN_NPC,").split(",");
        for (String s : split) {
            npcs.add(s);
        }
    }

    public List<String> getNPCs() {
        return npcs;
    }
}
