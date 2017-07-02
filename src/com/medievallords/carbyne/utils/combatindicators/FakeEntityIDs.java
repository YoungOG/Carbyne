package com.medievallords.carbyne.utils.combatindicators;

public class FakeEntityIDs {

    private static int entityID;

    static {
        FakeEntityIDs.entityID = 1000000000;
    }

    public static int next() {
        if (FakeEntityIDs.entityID == Integer.MAX_VALUE) {
            FakeEntityIDs.entityID = 1000000000;
        }

        return FakeEntityIDs.entityID++;
    }
}
