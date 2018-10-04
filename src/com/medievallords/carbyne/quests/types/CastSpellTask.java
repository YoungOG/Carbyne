package com.medievallords.carbyne.quests.types;

import com.medievallords.carbyne.quests.Task;
import com.medievallords.carbyne.quests.config.QuestLineConfig;
import org.bukkit.entity.Player;

import java.util.List;

public class CastSpellTask extends Task {

    private String spell;

    public CastSpellTask(String name, QuestLineConfig line, List<String> commands) {
        super(name, line, commands);

        this.spell = line.getString("spell", "any");
    }

    public String getSpell() {
        return spell;
    }
}
