package com.medievallords.carbyne.quests.types;

import com.medievallords.carbyne.quests.Task;
import com.medievallords.carbyne.quests.config.QuestLineConfig;

import java.util.List;

public class CreateSquadTask extends Task {

    public CreateSquadTask(String name, QuestLineConfig line, List<String> commands) {
        super(name, line, commands);
    }
}
