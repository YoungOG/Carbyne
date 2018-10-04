package com.medievallords.carbyne.quests.config;

import java.util.HashMap;
import java.util.List;

public class QuestLineConfig {

    private HashMap<String, String> keys = new HashMap<>();

    private List<String> lines;

    public QuestLineConfig(List<String> lines) {
        this.lines = lines;
        for (String s : lines) {
            String[] split = s.split("=");
            String key = split[0];
            String value = split[1];
            keys.put(key, value);
        }
    }

    public String getString(String key, String def) {
        if (!keys.containsKey(key))
            return def;

        return keys.get(key);
    }

    public int getInt(String key, int def) {
        if (!keys.containsKey(key))
            return def;

        try {
            return Integer.parseInt(keys.get(key));
        } catch (NumberFormatException e) {
            return def;
        }
    }

    public boolean getBoolean(String key, boolean def) {
        if (!keys.containsKey(key))
            return def;

        try {
            return Boolean.parseBoolean(keys.get(key));
        } catch (NumberFormatException e) {
            return def;
        }
    }

    public double getDouble(String key, double def) {
        if (!keys.containsKey(key))
            return def;

        try {
            return Double.parseDouble(keys.get(key));
        } catch (NumberFormatException e) {
            return def;
        }
    }
}
