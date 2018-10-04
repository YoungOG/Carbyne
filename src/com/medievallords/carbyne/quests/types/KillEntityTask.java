package com.medievallords.carbyne.quests.types;

import com.medievallords.carbyne.quests.Task;
import com.medievallords.carbyne.quests.config.QuestLineConfig;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Getter
public class KillEntityTask extends Task {

    private List<String> mobsToKill = new ArrayList<>();

    public KillEntityTask(String name, QuestLineConfig line, List<String> commands) {
        super(name, line, commands);

        String[] types = line.getString("types", "CHICKEN,").split(",");
        for (String type : types) {
            this.mobsToKill.add(type);
        }
    }
}
