package com.medievallords.carbyne.packages;

import lombok.Getter;
import org.bukkit.ChatColor;

/**
 * Created by WE on 2017-08-03.
 */

@Getter
public enum PackageItemRarity {

    COMMON(ChatColor.GRAY), RARE(ChatColor.BLUE), EPIC(ChatColor.DARK_PURPLE), LEGENDARY(ChatColor.GOLD);

    private ChatColor color;

    PackageItemRarity(ChatColor color) {
        this.color = color;
    }
}
