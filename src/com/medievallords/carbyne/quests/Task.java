package com.medievallords.carbyne.quests;

import com.medievallords.carbyne.quests.config.QuestLineConfig;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Getter
public abstract class Task {

    protected String name, displayName, progressDisplay;
    protected HashMap<UUID, Integer> players = new HashMap<>();
    protected int requiredProgress;
    private List<String> commands, condition;

    public Task(String name, QuestLineConfig line, List<String> commands) {
        this.name = name;
        this.requiredProgress = line.getInt("requiredProgress", 10);
        this.progressDisplay = line.getString("progressDisplay", "&aProgress");
        this.commands = commands;
    }

    public String getProgress(Player player) {
        return progressDisplay + "&7: " + players.get(player.getUniqueId()) + "/" + requiredProgress;
    }

    public void incrementProgress(UUID uuid, int progress) {
        int current = players.get(uuid);
        if (current >= requiredProgress) {

        } else if (current + progress >= requiredProgress) {
            players.put(uuid, requiredProgress);
            for (String command : commands) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", Bukkit.getOfflinePlayer(uuid).getName()));
            }
        } else {
            players.put(uuid, current + progress);
        }
    }

    public void addPlayer(Player player) {
        players.put(player.getUniqueId(), 0);
    }

    public void setPlayer(UUID uuid, int progress) {
        players.put(uuid, progress);
    }

    public void removePlayer(Player player) {
        players.remove(player.getUniqueId());
    }

    public boolean isCompleted(UUID uuid) {
        if (!players.containsKey(uuid)) {
            return false;
        } else {
            int current = players.get(uuid);
            return current >= requiredProgress;
        }
    }

}
