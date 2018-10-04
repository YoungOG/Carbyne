package com.medievallords.carbyne.quests.types;

import com.medievallords.carbyne.quests.Task;
import com.medievallords.carbyne.quests.config.QuestLineConfig;
import org.bukkit.entity.Player;

import java.util.List;

public class EnterPortalTask extends Task  {

    private String portal;

    public EnterPortalTask(String name, QuestLineConfig line, List<String> commands) {
        super(name, line, commands);

        this.portal = line.getString("portal", "NOT_A_PORTAL");
    }

    public String getPortal() {
        return portal;
    }
}
