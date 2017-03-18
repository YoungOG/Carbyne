package com.medievallords.carbyne.controlpoints;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Williams on 2017-03-16.
 * for the Carbyne project.
 */
@Getter
@Setter
public class ControlPoint {

    private Location location;
    private int timer;
    private String name;
    private Player capper;
    private boolean running;
    private List<String> commandRewards = new ArrayList<>();

    public ControlPoint(Location location, String name, int timer){
        this.location = location;
        this.name = name;
        this.timer = timer;
    }
}
