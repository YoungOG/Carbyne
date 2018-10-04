package com.medievallords.carbyne.quests.types;

import com.medievallords.carbyne.quests.Task;
import com.medievallords.carbyne.quests.config.QuestLineConfig;
import org.bukkit.entity.Player;

import java.util.List;

public class DepositMoneyTask extends Task {

    public DepositMoneyTask(String name, QuestLineConfig line, List<String> commands) {
        super(name, line, commands);
    }
}
