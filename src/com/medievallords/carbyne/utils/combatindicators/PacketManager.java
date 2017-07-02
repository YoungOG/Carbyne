package com.medievallords.carbyne.utils.combatindicators;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

/**
 * Created by Calvin on 6/15/2017
 * for the Carbyne project.
 */
public abstract class PacketManager {

    protected static double VERTICAL_OFFSET_PRE_1_8 = 54.4;
    protected static double VERTICAL_OFFSET = -2.1;

    public abstract void sendDamageIndicator(Entity p0, Location p1, String p2, boolean p3, int p4);
}
