package com.medievallords.carbyne.events.implementations.enumerations;

import lombok.Getter;

/**
 * Created by Dalton on 7/10/2017.
 */
public enum RaceType {

    FIESTA_BOWL("Fiesta Bowl", "¡Carrera! ¡Con rapidez!");

    @Getter
    private String raceName, startString;

    RaceType(String raceName, String startString) {
        this.raceName = raceName;
        this.startString = startString;
    }

}
