package com.medievallords.carbyne.leaderboards.board;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

/**
 * Created by Calvin on 1/24/2017
 * for the Carbyne-Gear project.
 */
@Getter
@Setter
public class BoardLocationSet {

    private String boardSetId;
    private Location primarySignLocation;
    private Location[] signLocations;
    private Location[] headLocations;
}
