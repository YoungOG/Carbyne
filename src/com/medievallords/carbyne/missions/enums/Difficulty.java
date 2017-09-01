package com.medievallords.carbyne.missions.enums;

import lombok.Getter;

/**
 * Created by Dalton on 8/8/2017.
 */
public enum Difficulty {

    BABY(0.5, "&b"), EASY(1.0, "&2"), MEDIUM(1.5, "&6"), HARD(2.0, "&4"), INSANE(2.5, "&5"), CRAZY(4, "&a"), BOSS(1, "&c"); // Crazy always needs to be at the end

    @Getter
    private double modifier;
    @Getter
    private String colorCode;

    Difficulty(double modifier, String colorCode) {
        this.modifier = modifier;
        this.colorCode = colorCode;
    }

    @Override
    public String toString() {
        return colorCode + this.name();
    }

}
