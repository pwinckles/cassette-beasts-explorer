package com.pwinckles.cassette.common.model;

import io.avaje.jsonb.Json;

public enum SpeciesType {

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
    WATER;

    public static SpeciesType fromString(String value) {
        var upper = value.toUpperCase();
        for (var entry : values()) {
            if (entry.name().equals(upper)) {
                return entry;
            }
        }
        throw new IllegalArgumentException("Unknown type: " + value);
    }

}
