package com.pwinckles.cassette.common.model;

public enum MoveType {
    AIR,
    ASTRAL,
    BEAST,
    EARTH,
    FIRE,
    GLASS,
    GLITTER,
    ICE,
    LIGHTNING,
    METAL,
    PLANT,
    PLASTIC,
    POISON,
    TYPELESS,
    WATER;

    public static MoveType fromString(String value) {
        var upper = value.toUpperCase();
        for (var entry : values()) {
            if (entry.name().equals(upper)) {
                return entry;
            }
        }
        throw new IllegalArgumentException("Unknown type: " + value);
    }
}
