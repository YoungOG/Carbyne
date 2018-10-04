package com.medievallords.carbyne.quests;

import lombok.Getter;
import org.bukkit.ChatColor;

@Getter
public enum Difficulty {

    EASY(10, ChatColor.GREEN), MEDIUM(11, ChatColor.YELLOW), HARD(1, ChatColor.RED), BRUTAL(13, ChatColor.LIGHT_PURPLE);

    private int data;
    private ChatColor color;

    Difficulty(int data, ChatColor color) {
        this.data = data;
        this.color = color;
    }
}
