package com.medievallords.carbyne.announcer;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.profiles.Profile;
import com.medievallords.carbyne.utils.JSONMessage;
import com.medievallords.carbyne.utils.PlayerUtility;
import com.medievallords.carbyne.utils.StaticClasses;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AnnouncerManager {

    private int interval = 600, taskID;
    private final List<AnnouncementMessage> messages = new ArrayList<>();

    public AnnouncerManager(Carbyne carbyne) {
        load(carbyne);
    }

    public void reload(Carbyne carbyne) {
        try {
            carbyne.setAnnouncerFileConfiguration(YamlConfiguration.loadConfiguration(carbyne.getAnnouncerFile()));
            carbyne.getAnnouncerFileConfiguration().save(carbyne.getAnnouncerFile());
        } catch (IOException e) {
            e.printStackTrace();
        }

        load(carbyne);
    }

    public void load(Carbyne carbyne) {
        Bukkit.getScheduler().cancelTask(taskID);

        if (carbyne.getAnnouncerFileConfiguration().contains("Interval"))
            this.interval = carbyne.getAnnouncerFileConfiguration().getInt("Interval");

        messages.clear();

        ConfigurationSection section = carbyne.getAnnouncerFileConfiguration().getConfigurationSection("Messages");
        if (section == null) {
            carbyne.getAnnouncerFileConfiguration().createSection("Messages");

            try {
                carbyne.getAnnouncerFileConfiguration().save(carbyne.getAnnouncerFile());
            } catch (IOException e) {
                System.out.println("Could not save announcements file.");
            }

            return;
        }

        for (String key : section.getKeys(false)) {
            String command = "", url = "";
            List<String> toolTip = new ArrayList<>(), message = new ArrayList<>();

            if (section.contains(key + ".Message"))
                message = section.getStringList(key + ".Message");

            if (section.contains(key + ".Command"))
                command = section.getString(key + ".Command");

            if (section.contains(key + ".ToolTip"))
                toolTip = section.getStringList(key + ".ToolTip");

            if (section.contains(key + ".URL"))
                url = section.getString(key + ".URL");

            AnnouncementMessage newMessage = new AnnouncementMessage(message, toolTip, command, url);
            this.messages.add(newMessage);
        }

        run(0);
    }

    public void run(int index) {
        if (!messages.isEmpty()) {
            AnnouncementMessage announcementMessage = messages.get(index);
            if (announcementMessage != null) {
                if (announcementMessage.getMessage() != null && !announcementMessage.getMessage().isEmpty()) {
                    JSONMessage message = JSONMessage.create();
                    StringBuilder messageBuilder = new StringBuilder();
                    for (int i = 0; i < announcementMessage.getMessage().size(); i++) {
                        String s = announcementMessage.getMessage().get(i);
                        messageBuilder.append(s);
                        if (i != announcementMessage.getMessage().size() - 1)
                            messageBuilder.append("\n");
                    }

                    message.then(messageBuilder.toString());

                    if (announcementMessage.getToolTip() != null && !announcementMessage.getToolTip().isEmpty()) {
                        StringBuilder builder = new StringBuilder();
                        for (int i = 0; i < announcementMessage.getToolTip().size(); i++) {
                            String s = announcementMessage.getToolTip().get(i);
                            builder.append(s);
                            if (i != announcementMessage.getToolTip().size() - 1)
                                builder.append("\n");
                        }

                        message.tooltip(builder.toString());
                    }

                    if (announcementMessage.getCommand() != null && !announcementMessage.getCommand().isEmpty())
                        message.suggestCommand(announcementMessage.getCommand());

                    if (announcementMessage.getUrl() != null && !announcementMessage.getUrl().isEmpty())
                        message.openURL(announcementMessage.getUrl());

                    for (Player all : PlayerUtility.getOnlinePlayers()) {
                        Profile other = StaticClasses.profileManager.getProfile(all.getUniqueId());
                        if (other.isAnnouncementsEnabled())
                            message.send(all);
                    }
                }
            }
        }

        index++;
        if (index >= messages.size())
            index = 0;

        final int i = index;

        this.taskID = Bukkit.getScheduler().scheduleAsyncDelayedTask(Carbyne.getInstance(), new Runnable() {
            @Override
            public void run() {
                AnnouncerManager.this.run(i);
            }
        }, interval * 20);
    }
}
