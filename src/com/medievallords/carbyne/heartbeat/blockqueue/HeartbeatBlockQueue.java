package com.medievallords.carbyne.heartbeat.blockqueue;

import java.util.ArrayList;

public class HeartbeatBlockQueue {

    public static ArrayList<BlockType> types = new ArrayList<>();

    public static void handleBlocks() {
        ArrayList<BlockType> typesClone = (ArrayList<BlockType>) types.clone();

        for (BlockType type : typesClone) {
            (type.getLocation()).getBlock().setType(type.getMaterial());
            types.remove(type);
        }
    }
}