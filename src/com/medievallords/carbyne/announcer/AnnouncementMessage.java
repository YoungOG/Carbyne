package com.medievallords.carbyne.announcer;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringEscapeUtils;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class AnnouncementMessage {

    private String command, url;
    private final List<String> toolTip = new ArrayList<>(), message = new ArrayList<>();

    public AnnouncementMessage(List<String> message, List<String> toolTip, String command, String url) {
        for (String s : message)
            this.message.add(ChatColor.translateAlternateColorCodes('&', StringEscapeUtils.unescapeJava(s)));

        for (String s : toolTip)
            this.toolTip.add(ChatColor.translateAlternateColorCodes('&', StringEscapeUtils.unescapeJava(s)));

        this.command = ChatColor.translateAlternateColorCodes('&', command);
        this.url = ChatColor.translateAlternateColorCodes('&', url);
    }
}
