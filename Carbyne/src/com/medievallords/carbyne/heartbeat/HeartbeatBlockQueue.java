package com.medievallords.carbyne.heartbeat;

import java.util.ArrayList;

public class HeartbeatBlockQueue {

    public static ArrayList<BlockType> types = new ArrayList<>();

    public static void handleBlocks() {
        for (BlockType type : types) {
            (type.getLocation()).getBlock().setType(type.getMaterial());
        }
    }
}