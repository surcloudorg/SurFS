package com.surfs.nas.server;

public class UUID {

    public static String makeUUID() {
        java.util.UUID id = java.util.UUID.randomUUID();
        return Long.toHexString(id.getMostSignificantBits()) + Long.toHexString(id.getLeastSignificantBits());
    }
}
