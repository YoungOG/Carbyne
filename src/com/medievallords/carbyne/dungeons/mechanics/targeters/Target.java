package com.medievallords.carbyne.dungeons.mechanics.targeters;

import com.medievallords.carbyne.utils.DungeonLineConfig;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class Target {

    protected DungeonLineConfig dlc;
    @Getter
    private String type;

    public Target(String params, String type) {
        if (params.contains(";")) {
            String[] split = params.split(";");
            this.dlc = new DungeonLineConfig(Arrays.asList(split));
        } else {
            this.dlc = new DungeonLineConfig(new ArrayList<>());
        }

        this.type = type;
    }

    public static Target getTarget(String name) {
        String finalString = name;
        String rest = name;
        if (finalString.contains("(") && finalString.contains(")")) {
            String[] split = finalString.split("\\(");
            rest = split[0];
            finalString = split[1];
            split = finalString.split("\\)");
            finalString = split[0];
        }

        switch (rest.toUpperCase()) {
            case "PIR":
                return new PIRTarget(finalString, "PIR");
            case "PT":
                return new PlayerTarget(finalString, "PT");
            case "LT":
                return new LocationTarget(finalString, "LT");
        }

        return null;
    }

}
