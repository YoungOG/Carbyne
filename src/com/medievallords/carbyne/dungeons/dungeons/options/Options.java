package com.medievallords.carbyne.dungeons.dungeons.options;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by WE on 2017-09-27.
 */
@Getter
@Setter
public class Options {

    private boolean pvp = false;
    //private boolean spawnOnDeath = true;
    private SpawnMode spawnMode;
}
